package com.general.etl.core;

import com.general.etl.exception.ProcessStartException;
import com.general.etl.exception.ProcessStopException;

public interface RunnableLifeCycle  extends LifeCycle{

    void start(Context context) throws ProcessStartException;

    boolean isStarted();

    void notifyStop() throws ProcessStopException;

    void  addRunnableLifecycleListener(RunnableLifeCycleListener lifeCycleListener);
}
