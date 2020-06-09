package com.grace.foresee.exception.callback;

public interface ExceptionListener {
    boolean handleException(Throwable throwable);
}
