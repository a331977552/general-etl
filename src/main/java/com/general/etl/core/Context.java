package com.general.etl.core;

public class Context{

    private final VendorEnum currentVendor;
    private final Integer block;

    public Context(VendorEnum currentVendor, Integer block){
        this.currentVendor = currentVendor;
        this.block = block;
    }

    public static final int DEFAULT_BLOCK = 200;
    public VendorEnum vendor() {
        return currentVendor;
    }

    public int block() {
        return block == null? DEFAULT_BLOCK : block;
    }


}
