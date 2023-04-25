package com.general.etl.core;

import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;
import com.general.etl.exception.ProcessStartException;
import com.general.etl.exception.ProcessStopException;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

@Log4j2
public final class RunnableLifeCycleSupport {
    LifeCycleSupport lifeCycle ;
    private RunnableLifeCycle embeddingObj;
    String simpleName;
    public RunnableLifeCycleSupport(RunnableLifeCycle embeddingObj) {
        lifeCycle = new LifeCycleSupport(embeddingObj);
        this.embeddingObj = embeddingObj;
        simpleName = embeddingObj.getClass().getSimpleName();
        if (!StringUtils.hasText(simpleName)){
            simpleName = embeddingObj.getClass().getName();
        }
    }

    private boolean started = false;
    public void create() throws CreationException {
        lifeCycle.create();
    }

    public void destroy() throws DestroyException {
        lifeCycle.destroy();
    }

    public boolean isCreated() {
        return lifeCycle.isCreated();
    }

    public boolean isDestroyed() {
        return lifeCycle.isDestroyed();
    }

    public void start(Context context) throws ProcessStartException {
        started = true;
        log.info("{} is started",simpleName);
    }

    public boolean isStarted() {
        return started;
    }

    public void stop() throws ProcessStopException {
        started = false;
        log.info("{} is stopped",simpleName);

    }
}
