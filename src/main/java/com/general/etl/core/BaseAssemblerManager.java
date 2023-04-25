package com.general.etl.core;


import com.general.etl.exception.AssemblerException;
import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseAssemblerManager implements AssemblerManager {
    Map<VendorEnum,Assembler> assemblers = new HashMap<>();

    @Override
    public void addAssembler(VendorEnum vendor,Assembler assembler) {
        assemblers.put(vendor,assembler);
    }

    @Override
    public void trigger(Context context) throws AssemblerException{
        Assembler assembler = assemblers.get(context.vendor());
        if (assembler == null)
            throw new AssemblerException("unable to find vendor by "+ context.vendor());
        try {
            assembler.create();
        } catch (CreationException e) {
            throw new AssemblerException(e);
        }
        assembler.trigger(context);
        try {
            assembler.destroy();
        } catch (DestroyException e) {
            throw new AssemblerException(e);
        }
    }

    @Override
    public void create() throws CreationException {
        onCreate();
    }

    protected void onCreate() {

    }

    @Override
    public void destroy() throws DestroyException {
        onDestroy();
    }

    protected void onDestroy() {

    }

    @Override
    public boolean isCreated() {
        return false;
    }

    @Override
    public boolean isDestroyed() {
        return false;
    }

}
