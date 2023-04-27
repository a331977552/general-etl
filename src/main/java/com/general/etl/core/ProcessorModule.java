package com.general.etl.core;

public interface ProcessorModule<I,O> extends RunnableLifeCycle,Contained{

    public void addModule(Module module);

    public O process(I i);

}
