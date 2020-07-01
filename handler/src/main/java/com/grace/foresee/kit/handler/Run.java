package com.grace.foresee.kit.handler;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.grace.foresee.kit.handler.runnable.Action;
import com.grace.foresee.kit.handler.runnable.Func;

public class Run {
    private static HandlerPoster uiPoster = null;
    private static HandlerPoster backgroundPoster = null;

    public static Handler getUiHandler() {
        return getUiPoster();
    }

    public static HandlerPoster getUiPoster() {
        if (uiPoster == null) {
            synchronized (Run.class) {
                if (uiPoster == null) {
                    uiPoster = new HandlerPoster(Looper.getMainLooper(), false);
                }
            }
        }
        return uiPoster;
    }

    public static Handler getBackgroundHandler() {
        return getBackgroundPoster();
    }

    public static HandlerPoster getBackgroundPoster() {
        if (backgroundPoster == null) {
            synchronized (Run.class) {
                if (backgroundPoster == null) {
                    HandlerThread thread = new HandlerThread("ThreadRunHandler");
                    thread.setDaemon(true);
                    thread.setPriority(Thread.MAX_PRIORITY);
                    thread.start();

                    backgroundPoster = new HandlerPoster(thread.getLooper(), 3 * 1000, true);
                }
            }
        }
        return backgroundPoster;
    }

    public static Result onBackground(Action action) {
        HandlerPoster poster = getBackgroundPoster();
        if (Looper.myLooper() == poster.getLooper()) {
            action.call();
            return new ActionAsyncTask(action, true);
        }
        ActionAsyncTask task = new ActionAsyncTask(action);
        poster.async(task);
        return task;
    }

    public static Result onUiAsync(Action action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.call();
            return new ActionAsyncTask(action, true);
        }
        ActionAsyncTask task = new ActionAsyncTask(action);
        getUiPoster().async(task);
        return task;
    }

    public static void onUiSync(Action action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.call();
            return;
        }
        ActionSyncTask task = new ActionSyncTask(action);
        getUiPoster().sync(task);
        task.waitRun();
    }

    public static void onUiSync(Action action, int waitMillis, boolean cancelOnTimeOut) {
        onUiSync(action, waitMillis, 0, cancelOnTimeOut);
    }

    public static void onUiSync(Action action, int waitMillis, int waitNanos, boolean cancelOnTimeOut) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.call();
            return;
        }
        ActionSyncTask task = new ActionSyncTask(action);
        getUiPoster().sync(task);
        task.waitRun(waitMillis, waitNanos, cancelOnTimeOut);
    }

    public static <Ret> Ret onUiSync(Func<Ret> func) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return func.call();
        }
        FuncSyncTask<Ret> task = new FuncSyncTask<>(func);
        getUiPoster().sync(task);
        return task.waitRun();
    }

    public static <Ret> Ret onUiSync(Func<Ret> func, int waitMillis, boolean cancelOnTimeOut) {
        return onUiSync(func, waitMillis, 0, cancelOnTimeOut);
    }

    public static <Ret> Ret onUiSync(Func<Ret> func, int waitMillis, int waitNanos, boolean cancelOnTimeOut) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            func.call();
        }
        FuncSyncTask<Ret> task = new FuncSyncTask<>(func);
        getUiPoster().sync(task);
        return task.waitRun(waitMillis, waitNanos, cancelOnTimeOut);
    }

    public static void dispose() {
        if (uiPoster != null) {
            uiPoster.dispose();
            uiPoster = null;
        }
    }
}
