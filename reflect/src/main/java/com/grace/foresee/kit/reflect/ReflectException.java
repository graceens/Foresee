package com.grace.foresee.kit.reflect;

public class ReflectException extends RuntimeException {
    private static final long serialVersionUID = 7195168390060704885L;

    public ReflectException() {
    }

    public ReflectException(String s) {
        super(s);
    }

    public ReflectException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ReflectException(Throwable throwable) {
        super(throwable);
    }

    public ReflectException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
