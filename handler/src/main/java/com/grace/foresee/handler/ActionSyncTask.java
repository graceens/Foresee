package com.grace.foresee.handler;

import com.grace.foresee.handler.runnable.Action;

import java.util.Queue;

public class ActionSyncTask implements Action, Task {
    private final Action mAction;
    private boolean isDone = false;
    private Queue<Task> mPool;

    public ActionSyncTask(Action action) {
        mAction = action;
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
                if (!isDone) {
                    call();
                    isDone = true;
                    try {
                        notifyAll();
                    } catch (Exception ignore) {

                    }
                }
            }
        }
    }

    void waitRun() {
        if (!isDone) {
            synchronized (this) {
                while (!isDone) {
                    try {
                        wait();
                    } catch (InterruptedException ignore) {

                    }
                }
            }
        }
    }

    void waitRun(long waitMillis, int waitNanos, boolean cancelOnTimeOut) {
        if (!isDone) {
            synchronized (this) {
                while (!isDone) {
                    try {
                        wait(waitMillis, waitNanos);
                    } catch (InterruptedException ignore) {

                    }finally {
                        if (!isDone && cancelOnTimeOut) {
                            isDone = true;
                        }
                    }
                }
            }
        }
    }
}
