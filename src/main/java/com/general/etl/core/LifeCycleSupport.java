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


    private Context context;

    public LifeCycleSupport(LifeCycle embeddingObj) {
        this.embeddingObj = embeddingObj;
        simpleName = embeddingObj.getClass().getSimpleName();
        if (!StringUtils.hasText(simpleName)){
            simpleName = embeddingObj.getClass().getName();
        }
    }


    @Override
    public void create(Context context) throws CreationException {
        created = true;
        this.context = context;
        log.info("{} is created",simpleName);

    }

    public Context context() {
        return context;
    }

    @Override
    public void destroy() throws DestroyException {
        created = false;
        context = null;
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
