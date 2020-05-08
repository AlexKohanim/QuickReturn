package org.appcelerator.kroll.common;

import java.util.concurrent.Semaphore;

public class AsyncResult extends Semaphore {
    private static final long serialVersionUID = 1;
    protected Object arg;
    protected Throwable exception;
    protected Object result;

    public AsyncResult() {
        this(null);
    }

    public AsyncResult(Object arg2) {
        super(0);
        this.arg = arg2;
    }

    public Object getArg() {
        return this.arg;
    }

    public void setResult(Object result2) {
        this.result = result2;
        release();
    }

    public void setException(Throwable exception2) {
        this.result = null;
        this.exception = exception2;
        release();
    }

    public Object getResult() {
        try {
            acquire();
        } catch (InterruptedException e) {
        }
        if (this.exception == null) {
            return this.result;
        }
        throw new RuntimeException(this.exception);
    }

    public Object getResultUnsafe() {
        return this.result;
    }
}
