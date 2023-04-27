package com.general.etl.assembler;

import com.general.etl.core.*;
import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;
import com.general.etl.exception.ProcessException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class AssemblerManager implements LifeCycle {

    LifeCycleSupport lifeCycleSupport = new LifeCycleSupport(this);
    Map<VendorEnum,Assembler> assemblers = new HashMap<>();
    Assembler currentAssembler;

    public void addAssembler(VendorEnum vendorEnum,Assembler assembler){
        assemblers.put(vendorEnum,assembler);
    }
    @Override
    public void create(Context context) throws CreationException {
        lifeCycleSupport.create(context);
        currentAssembler = assemblers.get(context.currentVendor());
        if (currentAssembler ==null)
            throw new ProcessException("cannot find related vendor : "+ context.currentVendor());
        currentAssembler.create(context);
    }

    public void trigger() throws Exception{
        currentAssembler.trigger();
    }

    @Override
    public void destroy() throws DestroyException {
        currentAssembler.destroy();
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
}
