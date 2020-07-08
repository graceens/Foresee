package com.grace.foresee.handler;

import com.grace.foresee.handler.runnable.Func;

import java.util.Queue;

public class FuncSyncTask<Ret> implements Func<Ret>, Task {
    private final Func<Ret> mFunc;
    private boolean isDone;
    private Queue<Task> mPool;
    private Ret mResult;

    public FuncSyncTask(Func<Ret> func) {
        mFunc = func;
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
    public Ret call() {
        mPool = null;
        return mFunc.call();
    }

    @Override
    public void run() {
        if (!isDone) {
            synchronized (this) {
                if (!isDone) {
                    mResult = call();
                    isDone = true;
                    try {
                        notifyAll();
                    } catch (Exception ignore) {

                    }
                }
            }
        }
    }

    Ret waitRun() {
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
        return mResult;
    }

    Ret waitRun(long waitMillis, int waitNanos, boolean cancelOnTimeOut) {
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
        return mResult;
    }
}
