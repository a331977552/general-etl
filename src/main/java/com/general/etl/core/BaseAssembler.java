package com.general.etl.core;

import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAssembler implements Assembler{

    LifeCycleSupport lifeCycleSupport = new LifeCycleSupport(this);

    @Override
    public List<Processor<?, ?>> getProcessors() {
        return list;
    }

    private List<Processor<?,?>> list = new ArrayList<>();
    @Override
    public final void addProcessor(Processor<?, ?> processor) {
        list.add(processor);
    }
    @Override
    public void trigger() {
        onTrigger();
    }

    protected abstract void onTrigger();

    @Override
    public final void create(Context context) throws CreationException {
        lifeCycleSupport.create(context);
        onCreate(context);
        for (Processor<?, ?> processor : list) {
            if (!processor.isCreated()){
                processor.create(context);
            }
        }

    }

    protected abstract void onCreate(Context context);

    @Override
    public final void destroy() throws DestroyException {
        onDestroy();
        for (Processor<?, ?> processor : list) {
            if (!processor.isDestroyed()){
                processor.destroy();
            }
        }
        lifeCycleSupport.destroy();
    }

    protected abstract void onDestroy();

    @Override
    public Context context() {
        return lifeCycleSupport.context();
    }

    @Override
    public boolean isDestroyed() {
        return lifeCycleSupport.isDestroyed();
    }
}
