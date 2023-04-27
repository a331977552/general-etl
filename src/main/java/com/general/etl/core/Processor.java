package com.general.etl.core;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Processor<I,O> extends RunnableLifeCycle,Container,Contained{

    public void addModule(ProcessorModule<?,?> module);

    public void feed(List<I> inputs);

    public void addDownStream(Processor<O,?> downStream);

    public void addExceptionListener(Consumer<Throwable> consumer);

}
