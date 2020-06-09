package com.grace.foresee.kit.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class HandlerPoster extends Handler implements Poster {
    private static int MAX_MILLIS_INSIDE_HANDLE_MESSAGE = 16;
    private Dispatcher mAsyncDispatcher;
    private Dispatcher mSyncDispatcher;

    public HandlerPoster(Looper looper, int maxMillisInsideHandleMessage, boolean onlyAsync) {
        super(looper);

        MAX_MILLIS_INSIDE_HANDLE_MESSAGE = maxMillisInsideHandleMessage;

        mAsyncDispatcher = new Dispatcher(new LinkedList<>(), () -> sendMessage(ASYNC));

        if (onlyAsync) {
            mSyncDispatcher = mAsyncDispatcher;
        } else {
            mSyncDispatcher = new Dispatcher(new LinkedList<>(), () -> sendMessage(SYNC));
        }
    }

    @Override
    public void async(Task task) {
        mAsyncDispatcher.offer(task);
    }

    @Override
    public void sync(Task task) {
        mSyncDispatcher.offer(task);
    }

    @Override
    public void dispose() {
        removeCallbacksAndMessages(null);
        mAsyncDispatcher.dispose();
        mSyncDispatcher.dispose();
    }

    @Override
    public void dispatchMessage(@NonNull Message msg) {
        if (msg.what == ASYNC) {
            mAsyncDispatcher.dispatch();
        } else if (msg.what == SYNC) {
            mSyncDispatcher.dispatch();
        }else super.dispatchMessage(msg);
    }

    private void sendMessage(int what) {
        if (!sendMessage(obtainMessage(what))) {
            throw new RuntimeException("Could not send handler message");
        }
    }

    private static class Dispatcher {
        private final Queue<Task> mPool;
        IPoster mPoster;
        private boolean isActive;

        public Dispatcher(Queue<Task> pool, IPoster poster) {
            mPool = pool;
            mPoster = poster;
        }

        void offer(Task task) {
            synchronized (mPool) {
                mPool.offer(task);
                task.setPool(mPool);

                if (!isActive) {
                    isActive = true;
                    if (mPoster != null) {
                        mPoster.sendMessage();
                    }
                }
            }
        }

        void dispatch() {
            boolean rescheduled = false;
            try {
                long started = SystemClock.uptimeMillis();
                while (true) {
                    Runnable runnable = poll();
                    if (runnable == null) {
                        synchronized (mPool) {
                            runnable = poll();
                            if (runnable == null) {
                                isActive = false;
                                return;
                            }
                        }
                    }
                    runnable.run();
                    long timeInMethod = SystemClock.uptimeMillis() - started;
                    //超出执行时间上限，重新执行一次，在池中取出新的runnable，继承分发逻辑，以防阻塞
                    if (timeInMethod >= MAX_MILLIS_INSIDE_HANDLE_MESSAGE) {
                        if (mPoster != null) {
                            mPoster.sendMessage();
                        }
                        rescheduled = true;
                        return;
                    }
                }
            }finally {
                isActive = rescheduled;
            }
        }

        private Runnable poll() {
            synchronized (mPool) {
                try {
                    return mPool.poll();
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        void dispose() {
            mPool.clear();
            mPoster = null;
        }
    }

    interface IPoster{
        void sendMessage();
    }
}
