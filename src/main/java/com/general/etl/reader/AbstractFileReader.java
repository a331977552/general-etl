package com.general.etl.reader;



import com.general.etl.core.Context;
import com.general.etl.core.LifeCycle;
import com.general.etl.core.LifeCycleSupport;
import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;


public abstract class AbstractFileReader<T> implements LifeCycle, Closeable,Iterable<T> {

    LifeCycleSupport lifeCycleSupport = new LifeCycleSupport(this);

    private final File file;
    public AbstractFileReader(String filePath) {
        this.file = new File(filePath);
    }
    Iterable<T> iterable;
    public AbstractFileReader(File file) {
        this.file = file;
    }

    @Override
    public void create(Context context) throws CreationException {
        lifeCycleSupport.create(context);
        onCreate(context);
        try {
            iterable = getReader(file);
        } catch (IOException e) {
            throw new CreationException("unable to create file reader",e);
        }
    }

    protected abstract Iterable<T> getReader(File file) throws IOException;

    private void onCreate(Context context) {

    }

    @Override
    public void destroy() throws DestroyException {
        lifeCycleSupport.destroy();
    }

    @Override
    public boolean isCreated() {
        return lifeCycleSupport.isCreated();
    }

    @Override
    public boolean isDestroyed() {
        return lifeCycleSupport.isDestroyed();
    }

    @Override
    public Context context() {
        return lifeCycleSupport.context();
    }

    @Override
    public void close() throws IOException{
        if (iterable instanceof Closeable)
        {
            ((Closeable) iterable).close();
        }
    }


    @Override
    public Iterator<T> iterator() {
        return iterable.iterator();
    }
}
