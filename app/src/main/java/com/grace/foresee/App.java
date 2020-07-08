package com.grace.foresee;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grace.foresee.exception.CrashHandler;
import com.grace.foresee.logger.AndroidLogAdapter;
import com.grace.foresee.logger.Logger;
import com.grace.foresee.logger.PrettyFormatStrategy;

import java.util.ArrayList;
import java.util.List;

public class App extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "App";

    private List<Activity> mActivities = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new CrashHandler.Builder(this)
                .duration(2000)
                .logListener(Logger::e)
                .destroyListener(this::finishAll)
                .build()
                .init();

        //logcat日志适配器
        Logger.addAdapter(new AndroidLogAdapter(new PrettyFormatStrategy.Builder()
                .tag("logcat")
                .methodCount(20)
                .build()));


    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        mActivities.add(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        mActivities.remove(activity);
    }

    private void finishAll() {
        for (Activity activity : mActivities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
