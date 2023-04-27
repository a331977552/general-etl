package com.general.etl.core;

import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;

public interface LifeCycle {
    void create(Context context) throws CreationException;

    void destroy() throws DestroyException;

    boolean isCreated();

    boolean isDestroyed();

    Context context();
}
