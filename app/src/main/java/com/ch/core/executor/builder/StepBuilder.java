package com.ch.core.executor.builder;

import com.ch.core.executor.StepExecutor;

public abstract class StepBuilder<T extends StepExecutor> {

    private T stepExecutor;

    public StepBuilder() {
        this.stepExecutor = init();
    }

    protected abstract T init();

    public T get() {
        return stepExecutor;
    }


    public StepBuilder<T> setTimeout(int timeout) {
        get().getData().setTimeout(timeout);
        return this;
    }


}
