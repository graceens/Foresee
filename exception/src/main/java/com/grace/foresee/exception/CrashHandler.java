package com.grace.foresee.exception;

import android.content.Context;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.grace.foresee.exception.callback.DestroyListener;
import com.grace.foresee.exception.callback.ExceptionListener;
import com.grace.foresee.exception.callback.LogListener;

import java.lang.ref.WeakReference;

public class CrashHandler implements Thread.UncaughtExceptionHandler{
    public static final int DEFAULT_DURATION = 3000; //默认持续3秒

    private WeakReference<Context> mContextWeakReference;
    //系统默认的异常处理器
    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;

    private ExceptionListener mExceptionListener;
    private LogListener mLogListener;
    private DestroyListener mDestroyListener;

    //持续时间
    private int mDuration;
    //发生异常时提示的信息
    private String mMessage;

    public CrashHandler(@NonNull Context context) {
        mContextWeakReference = new WeakReference<>(context);
        mDuration = DEFAULT_DURATION;
    }

    public void init() {
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        if (TextUtils.isEmpty(mMessage)) {
            mMessage = mContextWeakReference.get().getResources().getString(R.string.abnormal_exit);
        }
    }

    public void setExceptionListener(ExceptionListener listener) {
        mExceptionListener = listener;
    }

    public void setLogListener(LogListener listener) {
        mLogListener = listener;
    }

    public void setDestroyListener(DestroyListener listener) {
        mDestroyListener = listener;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        if (!handleException(throwable) && mDefaultExceptionHandler != null) {
            mDefaultExceptionHandler.uncaughtException(thread, throwable);
        } else {
            try {
                Thread.sleep(mDuration);
            } catch (InterruptedException e) {
                if (mLogListener != null) {
                    mLogListener.onLog(e);
                }
            }
            if (mDestroyListener != null) {
                //销毁所有的Activity和Service，以防App重启
                mDestroyListener.onDestroy();
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable throwable) {
        if (throwable == null) {
            return false;
        }

        if (mExceptionListener != null) {
            return mExceptionListener.handleException(throwable);
        } else {
            new Thread(() -> {
                Looper.prepare();
                Toast.makeText(mContextWeakReference.get(), mMessage, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }).start();

            if (mLogListener != null) {
                mLogListener.onLog(throwable);
            }
            return true;
        }
    }

    public static class Builder{
        private CrashHandler mCrashHandler;

        public Builder(Context context) {
            mCrashHandler = new CrashHandler(context);
        }

        public Builder exceptionListener(ExceptionListener listener) {
            mCrashHandler.setExceptionListener(listener);
            return this;
        }

        public Builder logListener(LogListener listener) {
            mCrashHandler.setLogListener(listener);
            return this;
        }

        public Builder destroyListener(DestroyListener listener) {
            mCrashHandler.setDestroyListener(listener);
            return this;
        }

        public Builder duration(int duration) {
            mCrashHandler.setDuration(duration);
            return this;
        }

        public Builder message(String message) {
            mCrashHandler.setMessage(message);
            return this;
        }

        public CrashHandler build() {
            return mCrashHandler;
        }
    }
}
