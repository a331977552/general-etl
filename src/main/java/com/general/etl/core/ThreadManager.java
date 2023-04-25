package com.general.etl.core;

import java.util.concurrent.ExecutorService;

public interface ThreadManager {

    public ExecutorService getProcessorExecutorService();

    public ExecutorService getExecutorService();

}
