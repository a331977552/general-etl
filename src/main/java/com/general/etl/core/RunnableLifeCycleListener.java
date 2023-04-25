package com.general.etl.core;

public interface RunnableLifeCycleListener {

    default void onStart(RunnableLifeCycle lifeCycle){

    }
    default void onStop(RunnableLifeCycle lifeCycle){

    }
}
