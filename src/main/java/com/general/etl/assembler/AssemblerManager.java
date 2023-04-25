package com.general.etl.assembler;

import ch.qos.logback.core.model.processor.ProcessorException;
import com.general.etl.core.*;
import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;
import com.general.etl.exception.ProcessException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AssemblerManager implements LifeCycle {

    LifeCycleSupport lifeCycleSupport = new LifeCycleSupport(this);
    Map<VendorEnum,Assembler> assemblers = new HashMap<>();

    public void addAssembler(VendorEnum vendorEnum,Assembler assembler){
        assemblers.put(vendorEnum,assembler);
    }
    @Override
    public void create() throws CreationException {
        lifeCycleSupport.create();
    }

    public void trigger(Context context){

        Assembler assembler = assemblers.get(context.currentVendor());
        if (assembler==null)
            throw new ProcessException("cannot find related vendor : "+ context.currentVendor());
        assembler.create();
        assembler.trigger(context);
        assembler.destroy();
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
}
