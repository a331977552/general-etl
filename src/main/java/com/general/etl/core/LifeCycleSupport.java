package com.general.etl.core;

import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

@Log4j2
public final class LifeCycleSupport implements LifeCycle {
    private final LifeCycle embeddingObj;
    private String simpleName;
    private boolean created = false;

    public LifeCycleSupport(LifeCycle embeddingObj) {
        this.embeddingObj = embeddingObj;
        simpleName = embeddingObj.getClass().getSimpleName();
        if (!StringUtils.hasText(simpleName)){
            simpleName = embeddingObj.getClass().getName();
        }
    }


    @Override
    public void create() throws CreationException {
        created = true;
        log.info("{} is created",simpleName);

    }

    @Override
    public void destroy() throws DestroyException {
        created = false;
        log.info("{} is destroyed",simpleName);
    }

    @Override
    public boolean isCreated() {
        return created;
    }

    @Override
    public boolean isDestroyed() {
        return created;
    }
}
