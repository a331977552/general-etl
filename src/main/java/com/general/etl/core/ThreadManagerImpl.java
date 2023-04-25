package com.general.etl.core;

import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManagerImpl implements ThreadManager, LifeCycle {

    LifeCycleSupport lifeCycle =new LifeCycleSupport(this);
    private static final ThreadManager THREAD_MANAGER = new ThreadManagerImpl();
    ExecutorService executorService;

    private ThreadManagerImpl() {
        executorService = Executors.newCachedThreadPool();
        executorService = Executors.newFixedThreadPool(60);
    }

    public static ThreadManager getInstance(){
        return THREAD_MANAGER;
    }

    @Override
    public ExecutorService getProcessorExecutorService() {
        return executorService;
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public void create() throws CreationException {
        lifeCycle.create();
    }

    @Override
    public void destroy() throws DestroyException {
        lifeCycle.destroy();
    }

    @Override
    public boolean isCreated() {
        return lifeCycle.isCreated();
    }

    @Override
    public boolean isDestroyed() {
        return lifeCycle.isDestroyed();
    }
}
