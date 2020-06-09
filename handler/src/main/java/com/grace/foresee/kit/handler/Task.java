package com.grace.foresee.kit.handler;

import java.util.Queue;

public interface Task extends Runnable, Result {
    void setPool(Queue<Task> pool);
}
