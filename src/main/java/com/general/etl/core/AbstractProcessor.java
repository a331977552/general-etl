package com.general.etl.core;

import com.general.etl.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public abstract class AbstractProcessor<I, O> implements Processor<I, O> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ContainerSupport componentsSupport = new ContainerSupport(this);
    private final ContainerSupport modulesSupport = new ContainerSupport(this);
    private final ContainerSupport downStreamsSupport = new ContainerSupport(this);

    private final RunnableLifeCycleSupport runnableLifeCycleSupport = new RunnableLifeCycleSupport(this);


    private final List<I> emptyInputs = Collections.emptyList();
    private final List<O> emptyOutputs = Collections.emptyList();
    private BlockingQueue<List<I>> receivingQueue;

    private Thread processThread;
    private Thread outputThread;
    private ExecutorService workers;

    @Value("${processor.queue.size:20}")
    private int queueSize;

    @Value("${processor.worker.size:6}")
    private int workSize;
    private CountDownLatch countDownLatch;
    private final int timeWait = 5;
    private boolean running = false;

    private BlockingQueue<Future<List<O>>> tasksRunning;

    private List<Consumer<Throwable>> exceptionListeners = new ArrayList<>();
    private List<RunnableLifeCycleListener> runnableLifeCycleListeners = new ArrayList<>();

    @Override
    public final void create(Context context) throws CreationException {
        runnableLifeCycleSupport.create(context);
        receivingQueue = new ArrayBlockingQueue<>(queueSize);
        tasksRunning = new ArrayBlockingQueue<>(queueSize);

        processThread = new Thread(() -> {
            try {
                AbstractProcessor.this.processWorker();
            } catch (Exception e) {
                AbstractProcessor.this.notifyException(e);
            }
            System.out.println("process thread finished");
        });
        outputThread = new Thread(() -> {
            try {
                AbstractProcessor.this.outputWorker();
            } catch (Exception e) {
                AbstractProcessor.this.notifyException(e);
            }
            System.out.println("outputThread finished");
        });

        workers = Executors.newFixedThreadPool(workSize);

        onCreate();
        modulesSupport.createComponentsIfPossible(context);
        componentsSupport.createComponentsIfPossible(context);
        countDownLatch = new CountDownLatch(2);
    }


    protected void onCreate() {

    }

    @Override
    public final void destroy() throws DestroyException {
        onDestroy();
        receivingQueue = null;
        modulesSupport.destroyComponentsIfPossible();
        componentsSupport.destroyComponentsIfPossible();
        runnableLifeCycleSupport.destroy();
    }

    protected void onDestroy() {

    }

    @Override
    public final boolean isCreated() {
        return runnableLifeCycleSupport.isCreated();
    }

    @Override
    public final boolean isDestroyed() {
        return runnableLifeCycleSupport.isDestroyed();
    }

    @Override
    public final void addModule(ProcessorModule<?, ?> module) {
        modulesSupport.addComponent(module);
    }

    @Override
    public void addComponent(Contained contained) {
        componentsSupport.addComponent(contained);
    }

    @Override
    public List<Contained> getComponents() {
        return componentsSupport.getComponents();
    }

    private void notifyException(Throwable e) {
        for (Consumer<Throwable> exceptionListener : this.exceptionListeners) {
            exceptionListener.accept(e);
        }
    }

    @Override
    public void addExceptionListener(Consumer<Throwable> consumer) {
        this.exceptionListeners.add(consumer);
    }


    private void processWorker() {
        while ((!receivingQueue.isEmpty() || running)) {
            if (Thread.currentThread().isInterrupted()) {
                receivingQueue.clear();
                break;
            }
            List<I> inputs = retrieveFromRecQueue();
            //take all, and is processing receivingQueue == 0, output queue == 0; but processing is not empty
            for (I input : inputs) {
                try {
                    tasksRunning.put(workers.submit(() -> AbstractProcessor.this.onProcess(input)));
                } catch (InterruptedException e) {
                    log.error("unexpected interruption !!!", e);
                }
            }
        }
        countDownLatch.countDown();
    }

    private List<I> retrieveFromRecQueue() {
        try {
            List<I> poll = receivingQueue.poll(timeWait, TimeUnit.SECONDS);
            return poll == null ? emptyInputs : poll;
        } catch (InterruptedException e) {
            //todo，when exception occurs, this thread mayb be interrupted
            log.error("unexpected interruption !!!", e);
        }
        return Collections.emptyList();
    }


    private void outputWorker() throws Exception {
        while (!tasksRunning.isEmpty() || !receivingQueue.isEmpty() || running) {
            if (Thread.currentThread().isInterrupted()) {
                receivingQueue.clear();
                tasksRunning.clear();
                break;
            }
            while (!tasksRunning.isEmpty()) {
                Future<List<O>> future = tasksRunning.peek();
                if (future.isDone()) {
                    try {
                        tasksRunning.take();
                    } catch (InterruptedException e) {
                        log.error("unexpected interruption !!!", e);
                    }
                    List<O> results = future.get();
                    onProcessFinish(results);
                    notifyDownstream(results);
                }
            }
        }
        //completely stop itself
        this.stopInternal();
        downStreamsSupport.stopComponentsIfPossible(true);
        countDownLatch.countDown();
    }

    private void onProcessFinish(List<O> results) throws Exception {
        onOutput(results);
    }


    private void notifyDownstream(List<O> results) {
        List<Contained> components = downStreamsSupport.getComponents();
        for (Contained component : components) {
            if (component instanceof Processor<?, ?>) {
                ((Processor<O, ?>) component).feed(results);
            }
        }
    }

    private void stopInternal() {
        onStop();
        workers.shutdownNow();
        modulesSupport.stopComponentsIfPossible();
        componentsSupport.stopComponentsIfPossible();
        runnableLifeCycleSupport.stop();
        for (RunnableLifeCycleListener runnableLifeCycleListener : this.runnableLifeCycleListeners) {
            runnableLifeCycleListener.onStop(this);
        }
    }

    Thread.UncaughtExceptionHandler exceptionHandler = (t, e) -> AbstractProcessor.this.notifyException(e);

    protected abstract void onOutput(List<O> output) throws Exception;

    protected abstract List<O> onProcess(I input) throws ProcessException;

    @Override
    public final void feed(List<I> inputs) {
        try {
            this.receivingQueue.put(inputs);
            log.info("put success: {}, size {}", inputs, this.receivingQueue.size());
        } catch (InterruptedException e) {
            //todo，when exception occurs, this thread mayb be interrupted
            log.error("unexpected interruption !!!", e);
        }
    }

    @Override
    public void addRunnableLifecycleListener(RunnableLifeCycleListener lifeCycleListener) {
        this.runnableLifeCycleListeners.add(lifeCycleListener);
    }

    @Override
    public void addDownStream(Processor<O, ?> downStream) {
        downStreamsSupport.addComponent(downStream);
    }

    @Override
    public final void start() throws ProcessStartException {
        receivingQueue.clear();
        runnableLifeCycleSupport.start();
        running = true;
        onStart();
        for (RunnableLifeCycleListener runnableLifeCycleListener : runnableLifeCycleListeners) {
            runnableLifeCycleListener.onStart(this);
        }
        modulesSupport.startComponentsIfPossible();
        componentsSupport.startComponentsIfPossible();
        downStreamsSupport.startComponentsIfPossible();
        processThread.setName("pro_" + this.getClass().getSimpleName());
        outputThread.setName("out_" + this.getClass().getSimpleName());
        processThread.setUncaughtExceptionHandler(exceptionHandler);
        outputThread.setUncaughtExceptionHandler(exceptionHandler);
        processThread.start();
        outputThread.start();
    }

    protected void onStart() {

    }

    @Override
    public final boolean isStarted() {
        return runnableLifeCycleSupport.isStarted();
    }

    @Override
    public final void stop() throws ProcessStopException {
        running = false;
    }

    protected void onStop() {

    }

    @Override
    public void stopNow() {
        this.stop();
        workers.shutdownNow();
        processThread.interrupt();
        outputThread.interrupt();
    }

    @Override
    public void awaitStop() throws InterruptedException {
        countDownLatch.await();
    }


    @Override
    public Context context() {
        return runnableLifeCycleSupport.context();
    }
}
