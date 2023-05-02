package com.general.etl.feeder;

import java.util.List;

public interface FeedConsumer<T>  {

    public void consume(T[] t);
}
