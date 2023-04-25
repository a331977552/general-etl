package com.general.etl.core;

import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAssembler implements Assembler{

    private List<Processor<?,?>> list = new ArrayList<>();
    @Override
    public final void addProcessor(Processor<?, ?> processor) {
        list.add(processor);
    }

    @Override
    public final void trigger(Context context) {
        onTrigger(context);
    }

    protected abstract void onTrigger(Context context);

    @Override
    public final void create() throws CreationException {
        onCreate();
        for (Processor<?, ?> processor : list) {
            if (!processor.isCreated()){
                processor.create();
            }
        }

    }

    protected void onCreated() {

    }

    protected abstract void onCreate();

    @Override
    public final void destroy() throws DestroyException {
        onDestroy();
        for (Processor<?, ?> processor : list) {
            if (!processor.isDestroyed()){
                processor.destroy();
            }
        }
    }

    protected abstract void onDestroy();

    @Override
    public boolean isDestroyed() {
        return false;
    }
}
