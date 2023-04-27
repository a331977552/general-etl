package com.general.etl.core;

import com.general.etl.exception.ProcessStartException;
import com.general.etl.exception.ProcessStopException;

import java.util.List;

public interface RunnableLifeCycle  extends LifeCycle{

    void start() throws ProcessStartException;

    boolean isStarted();

    /**
     * //todo
     * previously submitted tasks are executed, but no new tasks will be accepted
     * @throws ProcessStopException
     */
    void stop() throws ProcessStopException;

    /**
      Attempts to stop all actively executing tasks, halts the processing of waiting tasks, and discard a list of the tasks that were awaiting execution.
     * @throws InterruptedException
     */
    void stopNow();

    /**
     * Blocks until all tasks have completed execution after a stop request or the current thread is interrupted, whichever happens first.
     * @throws InterruptedException
     */
    void awaitStop() throws InterruptedException;

    void  addRunnableLifecycleListener(RunnableLifeCycleListener lifeCycleListener);
}
