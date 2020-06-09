package com.grace.foresee.kit.handler;

public interface Poster {
    int ASYNC = 1;
    int SYNC = 2;

    void async(Task task);

    void sync(Task task);

    void dispose();
}
