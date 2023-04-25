package com.general.etl.core;

public record Context(VendorEnum currentVendor) {

    VendorEnum vendor() {
        return currentVendor;
    }

}
