package com.general.etl.core;

import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ContainerSupport implements Container {
    private final List<Contained> containedList = new ArrayList<>();
    private final Container embeddingContainer;

    public ContainerSupport(Container embeddingContainer) {
        this.embeddingContainer = embeddingContainer;
    }

    @Override
    public void addComponent(Contained contained) {
        containedList.add(contained);
    }

    @Override
    public List<Contained> getComponents() {
        return containedList;
    }

    public void createComponentsIfPossible(Context context) {
        for (Contained component : getComponents()) {
            if (component instanceof LifeCycle) {
                ((LifeCycle) component).create(context);
            }
        }
    }

    public void startComponentsIfPossible() {
        for (Contained component : getComponents()) {
            if (component instanceof RunnableLifeCycle) {
                ((RunnableLifeCycle) component).start();
            }
        }
    }

    public void stopComponentsIfPossible() {
        this.stopComponentsIfPossible(false);
    }

    public void stopComponentsIfPossible(boolean warning) {
        for (Contained component : getComponents()) {
            if (component instanceof RunnableLifeCycle) {
                ((RunnableLifeCycle) component).stop();
            } else if (warning) {
                log.warn("unable to stop component {}, as it is not a RunnableLifeCycle component", embeddingContainer);
            }
        }
    }


    public void destroyComponentsIfPossible() {
        for (Contained component : getComponents()) {
            if (component instanceof LifeCycle) {
                ((LifeCycle) component).destroy();
            }
        }
    }
}
