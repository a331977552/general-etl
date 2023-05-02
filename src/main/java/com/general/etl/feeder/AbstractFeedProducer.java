package com.general.etl.feeder;

import com.general.etl.core.*;
import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;
import com.general.etl.exception.ProcessStartException;
import com.general.etl.exception.ProcessStopException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class AbstractFeedProducer<T> implements FeedProducer<T> {
    RunnableLifeCycleSupport lifeCycleSupport = new RunnableLifeCycleSupport(this);
    ContainerSupport containerSupport = new ContainerSupport(this);
    private final List<FeedConsumer<T>> consumerList = new ArrayList<>();

    private final List<Iterable<T>> dataSources;

    private int printInterval = 1;

    public AbstractFeedProducer(List<Iterable<T>> list) {
        this.dataSources = list;
    }

    public AbstractFeedProducer(List<Iterable<T>> list,int printInterval) {
        this.dataSources = list;
        this.printInterval = printInterval;
    }

    @Override
    public void create(Context context) throws CreationException {
        lifeCycleSupport.create(context);
    }

    @Override
    public void destroy() throws DestroyException {
        lifeCycleSupport.destroy();
    }

    @Override
    public boolean isCreated() {
        return lifeCycleSupport.isCreated();
    }

    @Override
    public boolean isDestroyed() {
        return lifeCycleSupport.isDestroyed();
    }

    @Override
    public Context context() {
        return lifeCycleSupport.context();
    }

    @Override
    public void start() throws ProcessStartException {
        lifeCycleSupport.start();
    }

    @Override
    public boolean isStarted() {
        return lifeCycleSupport.isStarted();
    }

    @Override
    public void stop() throws ProcessStopException {
        lifeCycleSupport.stop();
    }

    @Override
    public void stopNow() {
        lifeCycleSupport.stopNow();
    }

    @Override
    public void awaitStop() throws InterruptedException {

    }

    @Override
    public void addRunnableLifecycleListener(RunnableLifeCycleListener lifeCycleListener) {
        lifeCycleSupport.addRunnableLifecycleListener(lifeCycleListener);
    }

    @Override
    public void addConsumer(FeedConsumer<T> fed) {
        consumerList.add(fed);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void feed() {
        Context context = context();
        final int blockSize = context.block();
        int count = 0;
        int blockCount = 0;
        Iterator<Iterable<T>> feeds = dataSources.iterator();
        Object[] inputs = new Object[blockSize];
        int currentIndex = 0;
        while (feeds.hasNext()){
            Iterable<T> feed = feeds.next();
            Iterator<T> iterator = feed.iterator();
            while (iterator.hasNext()){
                inputs[currentIndex++] = iterator.next();
                boolean allDone = !iterator.hasNext() && !feeds.hasNext();
                if (count % blockSize == 0 || allDone) {
                    notifyConsumer((T[]) inputs);
                    currentIndex = 0;
                    blockCount++;
                    if (blockCount % printInterval == 0 || allDone){
                        log.info("feed the {}th block, current count: {}",blockCount,count);
                    }
                }
                count++;
            }
        }

    }

    private void notifyConsumer(T[] inputs) {
        for (FeedConsumer<T> tFeedConsumer : consumerList) {
            tFeedConsumer.consume(inputs);
        }
    }

    @Override
    public void addComponent(Contained contained) {
        containerSupport.addComponent(contained);
    }

    @Override
    public List<Contained> getComponents() {
        return containerSupport.getComponents();
    }
}
