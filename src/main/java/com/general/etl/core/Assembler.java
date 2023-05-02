package com.general.etl.core;

import java.util.List;

public interface Assembler extends LifeCycle {

    List<Processor<?, ?>> getProcessors();

    public void addProcessor(Processor<?,?> processor);

    public void addFeeder();

    public void trigger();

}
