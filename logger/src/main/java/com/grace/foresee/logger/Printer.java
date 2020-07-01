package com.grace.foresee.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface Printer {
    void addAdapter(@NonNull LogAdapter logAdapter);

    void removeAdapter(@NonNull LogAdapter logAdapter);

    void clearAdapters();

    void tag(String tag);

    void log(int level, Throwable throwable, String tag, String message);

    void v(@NonNull String message, Object... args);

    void d(@NonNull String message, Object... args);

    void d(Object object);

    void i(@NonNull String message, Object... args);

    void w(@NonNull String message, Object... args);

    void w(@Nullable Throwable throwable, @Nullable String message, Object... args);

    void e(@NonNull String message, Object... args);

    void e(@Nullable Throwable throwable, @Nullable String message, Object... args);

    void json(@NonNull Object object);
}
