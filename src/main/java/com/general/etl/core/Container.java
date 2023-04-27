package com.general.etl.core;

import java.util.List;

public interface Container {

    public void addComponent(Contained contained);

    List<Contained> getComponents();
}
