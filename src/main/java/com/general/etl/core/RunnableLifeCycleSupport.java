package com.general.etl.core;

import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;
import com.general.etl.exception.ProcessStartException;
import com.general.etl.exception.ProcessStopException;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public final class RunnableLifeCycleSupport implements RunnableLifeCycle {
    LifeCycleSupport lifeCycle ;
    private RunnableLifeCycle embeddingObj;
    String simpleName;
    private List<RunnableLifeCycleListener> lifeCycleListeners = new ArrayList<>();

    public RunnableLifeCycleSupport(RunnableLifeCycle embeddingObj) {
        lifeCycle = new LifeCycleSupport(embeddingObj);
        this.embeddingObj = embeddingObj;
        simpleName = embeddingObj.getClass().getSimpleName();
        if (!StringUtils.hasText(simpleName)){
            simpleName = embeddingObj.getClass().getName();
        }
    }

    private boolean started = false;
    public void create(Context context) throws CreationException {
        lifeCycle.create(context);
    }

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

    @Override
    public Context context() {
        return lifeCycle.context();
    }

    @Override
    public void start() throws ProcessStartException {
        started = true;
        log.info("{} is started",simpleName);
    }


    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void stop() throws ProcessStopException {
        started = false;
        log.info("{} is stopped",simpleName);
    }

    @Override
    public void stopNow() {
        this.stop();
    }

    @Override
    public void awaitStop()  {
        throw new UnsupportedOperationException("doesn't support awaitStop, you should implement this by yourself!!");
    }

    @Override
    public void addRunnableLifecycleListener(RunnableLifeCycleListener lifeCycleListener) {
        this.lifeCycleListeners.add(lifeCycleListener);
    }
}
