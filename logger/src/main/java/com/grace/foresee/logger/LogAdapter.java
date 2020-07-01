package com.grace.foresee.logger;

public interface LogAdapter {
    boolean isLoggable(int level, String tag);

    void log(LogDetails logDetails);
}
