package com.grace.foresee.kit.handler;

import com.grace.foresee.kit.handler.runnable.Action;

import java.util.Queue;

public class ActionAsyncTask implements Action, Task {
    private final Action mAction;
    private boolean isDone = false;
    private Queue<Task> mPool;

    public ActionAsyncTask(Action action) {
        mAction = action;
    }

    public ActionAsyncTask(Action action, boolean isDone) {
        mAction = action;
        this.isDone = isDone;
    }

    @Override
    public void setPool(Queue<Task> pool) {
        mPool = pool;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void cancel() {
        if (!isDone) {
            synchronized (this) {
                isDone = true;
                if (mPool != null) {
                    synchronized (this) {
                        if (mPool != null) {
                            try {
                                mPool.remove(this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }finally {
                                mPool = null;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void call() {
        mPool = null;
        mAction.call();
    }

    @Override
    public void run() {
        if (!isDone) {
            synchronized (this) {
                call();
                isDone = true;
            }
        }
    }
}
