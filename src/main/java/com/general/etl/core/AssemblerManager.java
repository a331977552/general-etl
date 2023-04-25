package com.general.etl.core;

import com.general.etl.exception.AssemblerException;

public interface AssemblerManager extends LifeCycle {

    public void addAssembler(VendorEnum vendor,Assembler assembler);

    public void trigger(Context context) throws AssemblerException;

}
