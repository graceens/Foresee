package com.grace.foresee.logger;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.grace.foresee.logger.Logger.DEBUG;
import static com.grace.foresee.logger.Logger.ERROR;
import static com.grace.foresee.logger.Logger.INFO;
import static com.grace.foresee.logger.Logger.VERBOSE;
import static com.grace.foresee.logger.Logger.WARN;

public class LoggerPrinter implements Printer {
    private String mTag;
    //日志适配器
    private List<LogAdapter> mLogAdapters = new ArrayList<>();
    //历史日志
    private List<LogDetails> mLogHistories = new ArrayList<>();

    @Override
    public void addAdapter(@NonNull LogAdapter logAdapter) {
        mLogAdapters.add(logAdapter);
        //打印历史日志
        if (mLogHistories.size() > 0) {
            notify(mLogHistories, logAdapter);
        }
    }

    @Override
    public void removeAdapter(@NonNull LogAdapter logAdapter) {
        mLogAdapters.remove(logAdapter);
    }

    @Override
    public void clearAdapters() {
        mLogAdapters.clear();
    }

    @Override
    public void tag(String tag) {
        mTag = tag;
    }

    @Override
    public void log(int level, Throwable throwable, String tag, String message) {
        LogDetails logDetails = new LogDetails()
                .setLevel(level)
                .setTag(tag)
                .setTime(System.currentTimeMillis())
                .setThreadName(Thread.currentThread().getName())
                .setMethodStackTrace(Thread.currentThread().getStackTrace())
                .setThrowable(throwable)
                .setMessage(message);

        notifyAll(logDetails);
        //将日志添加到历史记录
        mLogHistories.add(logDetails);
    }

    @Override
    public void v(@NonNull String message, Object... args) {
        log(VERBOSE, null, message, args);
    }

    @Override
    public void d(@NonNull String message, Object... args) {
        log(DEBUG, null, message, args);
    }

    @Override
    public void d(Object object) {
        log(DEBUG, null, LoggerUtil.toString(object));
    }

    @Override
    public void i(@NonNull String message, Object... args) {
        log(INFO, null, message, args);
    }

    @Override
    public void w(@NonNull String message, Object... args) {
        log(WARN, null, message, args);
    }

    @Override
    public void w(@Nullable Throwable throwable, @Nullable String message, Object... args) {
        log(WARN, throwable, message, args);
    }

    @Override
    public void e(@NonNull String message, Object... args) {
        log(ERROR, null, message, args);
    }

    @Override
    public void e(@Nullable Throwable throwable, @Nullable String message, Object... args) {
        log(ERROR, throwable, message, args);
    }

    @Override
    public void json(@NonNull Object object) {
        if (object instanceof String) {
            try {
                String json = (String) object;
                if (json.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(json);
                    jsonObject(jsonObject);
                }
                if (json.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(json);
                    jsonArray(jsonArray);
                }
            } catch (JSONException e) {
                e(e, null);
            }
        } else if (object instanceof JSONObject) {
            jsonObject((JSONObject) object);
        } else if (object instanceof JSONArray) {
            jsonArray((JSONArray) object);
        }
    }

    /**
     * 通知适配器打印日志
     *
     * @param logDetails 日志详情
     * @param logAdapter 日志适配器
     */
    private void notify(LogDetails logDetails, LogAdapter logAdapter) {
        if (logAdapter.isLoggable(logDetails.getLevel(), logDetails.getTag())) {
            logAdapter.log(logDetails);
        }
    }

    /**
     * 通知适配器打印日志
     *
     * @param logDetailsList 日志详情列表
     * @param logAdapter     日志适配器
     */
    private void notify(List<LogDetails> logDetailsList, LogAdapter logAdapter) {
        for (LogDetails logDetails : logDetailsList) {
            notify(logDetails, logAdapter);
        }
    }

    /**
     * 通知所有适配器打印日志
     *
     * @param logDetails 日志详情
     */
    private void notifyAll(LogDetails logDetails) {
        for (LogAdapter logAdapter : mLogAdapters) {
            notify(logDetails, logAdapter);
        }
    }

    private void log(int level, @Nullable Throwable throwable, @Nullable String message, Object... args) {
        if (TextUtils.isEmpty(mTag)) {
            mTag = LogConstants.DEFAULT_TAG;
        }
        log(level, throwable, mTag, formatMessage(message, args));
    }

    private String formatMessage(@Nullable String message, Object... args) {
        return (message == null || args.length == 0) ? message : String.format(message, args);
    }

    private void jsonObject(JSONObject jsonObject) {
        d(jsonObject.toString());
    }

    private void jsonArray(JSONArray jsonArray) {
        d(jsonArray.toString());
    }
}
