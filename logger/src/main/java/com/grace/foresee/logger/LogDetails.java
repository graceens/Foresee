package com.grace.foresee.logger;

public class LogDetails {
    private int level;
    private String tag;
    private long time;
    private String threadName;
    private StackTraceElement[] methodStackTrace;
    private Throwable throwable;
    private String message;

    public LogDetails() {
    }

    public LogDetails(int level, String tag, long time, String message) {
        this.level = level;
        this.tag = tag;
        this.time = time;
        this.message = message;
    }

    public int getLevel() {
        return level;
    }

    public LogDetails setLevel(int level) {
        this.level = level;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public LogDetails setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public long getTime() {
        return time;
    }

    public LogDetails setTime(long time) {
        this.time = time;
        return this;
    }

    public String getThreadName() {
        return threadName;
    }

    public LogDetails setThreadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    public StackTraceElement[] getMethodStackTrace() {
        return methodStackTrace;
    }

    public LogDetails setMethodStackTrace(StackTraceElement[] methodStackTrace) {
        this.methodStackTrace = methodStackTrace;
        return this;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public LogDetails setThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public LogDetails setMessage(String message) {
        this.message = message;
        return this;
    }
}
