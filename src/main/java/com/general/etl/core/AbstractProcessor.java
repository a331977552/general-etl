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

    private final RunnableLifeCycleSupport runnableLifeCycleSupport = new RunnableLifeCycleSupport(this);

    private final List<I> emptyInputs = Collections.emptyList();
    private final List<O> emptyOutputs = Collections.emptyList();
    private BlockingQueue<List<I>> receivingQueue;
    private BlockingQueue<List<O>> outputQueue;

    private Thread processThread;
    private Thread outputThread;

    @Value("${processor.queue.size:20}")
    private int queueSize;


    private final int timeWait = 5;
    private boolean running = false;
    private boolean stopNow = false;

    private final List<I> processingInputs = Collections.synchronizedList(new ArrayList<>());

    private List<Consumer<Throwable>> exceptionListeners = new ArrayList<>();
    private List<Processor<O, ?>> downStreams =  new ArrayList<>();
    private List<RunnableLifeCycleListener> runnableLifeCycleListeners = new ArrayList<>();

    @Override
    public final void create() throws CreationException {
        receivingQueue = new ArrayBlockingQueue<>(queueSize);
        outputQueue = new ArrayBlockingQueue<>(queueSize);
        onCreate();
        runnableLifeCycleSupport.create();
    }

    protected void onCreate() {

    }

    @Override
    public final void destroy() throws DestroyException {
        onDestroy();
        receivingQueue = null;
        outputQueue = null;
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
    public final void addModule(Module module) {

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


    private void process() {
        while (!stopNow && (receivingQueue.size() > 0 || running)) {
            List<I> inputs = null;
            try {
                inputs = retrieveFromReceiving();
            } catch (InterruptedException e) {
                this.notifyException(e);
                return;
            }
            //take all, and is processing receivingQueue == 0, output queue == 0; but processing is not empty
            for (I input : inputs) {
                ThreadManagerImpl.getInstance().getExecutorService().submit(() -> {
                    //
                    processingInputs.add(input);
                    List<O> result = AbstractProcessor.this.onProcess(input);
                    processingInputs.remove(input);
                    //
                    postToOutput(result);
                });
            }
        }
    }

    private List<I> retrieveFromReceiving() throws InterruptedException {
        List<I> poll = receivingQueue.poll(timeWait, TimeUnit.SECONDS);
        return poll == null ? emptyInputs : poll;
    }

    private void postToOutput(List<O> result) {
        try {
            outputQueue.put(result);
        } catch (InterruptedException e) {
            AbstractProcessor.this.notifyException(e);
        }
    }


    private void processFinish() {
        while (!stopNow && (!processingInputs.isEmpty() || !receivingQueue.isEmpty() || !outputQueue.isEmpty() || running)) {
            List<O> results;
            try {
                results = getResultFromOutputQueue();
            } catch (InterruptedException e) {
                this.notifyException(e);
                return;
            }
            onProcessFinish(results);
            notifyDownstream(results);
        }

        //completely stop itself
        this.stop();
        for (Processor<O, ?> downStream : downStreams) {
            downStream.notifyStop();
        }
    }

    private void onProcessFinish(List<O> results) {
        onOutput(results);
    }

    private List<O> getResultFromOutputQueue() throws InterruptedException {
        List<O> results = outputQueue.poll(timeWait, TimeUnit.SECONDS);
        return results == null ? emptyOutputs : results;
    }

    private void notifyDownstream(List<O> results) {
        for (Processor<O, ?> downStream : downStreams) {
            downStream.feed(results);
        }
    }

    private void stop() {
        onStop();
        runnableLifeCycleSupport.stop();
        for (RunnableLifeCycleListener runnableLifeCycleListener : this.runnableLifeCycleListeners) {
            runnableLifeCycleListener.onStop(this);
        }
    }

    Thread.UncaughtExceptionHandler exceptionHandler = (t, e) -> AbstractProcessor.this.notifyException(e);

    protected abstract void onOutput(List<O> output);

    protected abstract List<O> onProcess(I input) throws ProcessException;

    @Override
    public final void feed(List<I> inputs) {
        try {
            this.receivingQueue.put(inputs);
            log.info("put success: {}, size {}", inputs, this.receivingQueue.size());
        } catch (InterruptedException e) {
            this.notifyException(e);
        }
    }

    @Override
    public void addRunnableLifecycleListener(RunnableLifeCycleListener lifeCycleListener) {
        this.runnableLifeCycleListeners.add(lifeCycleListener);
    }

    @Override
    public void addDownStream(Processor<O, ?> downStream) {
        downStreams.add(downStream);
    }

    @Override
    public final void start(Context context) throws ProcessStartException {
        receivingQueue.clear();
        outputQueue.clear();
        runnableLifeCycleSupport.start(context);
        running = true;
        stopNow = false;
        onStart(context);

        for (RunnableLifeCycleListener runnableLifeCycleListener : runnableLifeCycleListeners) {
            runnableLifeCycleListener.onStart(this);
        }
        processThread = new Thread(AbstractProcessor.this::process);
        outputThread = new Thread(AbstractProcessor.this::processFinish);
        processThread.setName("pro_" + this.getClass().getSimpleName());
        outputThread.setName("out_" + this.getClass().getSimpleName());
        processThread.setUncaughtExceptionHandler(exceptionHandler);
        outputThread.setUncaughtExceptionHandler(exceptionHandler);
        processThread.start();
        outputThread.start();
        for (Processor<O, ?> downStream : downStreams) {
            downStream.start(context);
        }
    }

    protected void onStart(Context context) {

    }

    @Override
    public final boolean isStarted() {
        return runnableLifeCycleSupport.isStarted();
    }

    @Override
    public final void notifyStop() throws ProcessStopException {
        running = false;
    }

    protected void onStop() {

    }

    @Override
    public void stopImmediately() {
        stopNow = true;
    }
}
