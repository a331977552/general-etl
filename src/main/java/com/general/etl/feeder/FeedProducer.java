package com.general.etl.feeder;

import com.general.etl.core.Container;
import com.general.etl.core.RunnableLifeCycle;

public interface FeedProducer<T> extends RunnableLifeCycle, Container {

    public void addConsumer(FeedConsumer<T> fed);

    public void feed();

}
