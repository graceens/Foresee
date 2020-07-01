package com.grace.foresee.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Logger {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;

    private static Printer sPrinter = new LoggerPrinter();

    public static void addAdapter(@NonNull LogAdapter logAdapter) {
        sPrinter.addAdapter(logAdapter);
    }

    public static void removeAdapter(@NonNull LogAdapter logAdapter) {
        sPrinter.removeAdapter(logAdapter);
    }

    public static void clearAdapters() {
        sPrinter.clearAdapters();
    }

    public static void tag(String tag) {
        sPrinter.tag(tag);
    }

    public static void log(int level, Throwable throwable, String tag, String message) {
        sPrinter.log(level, throwable, tag, message);
    }

    public static void v(@NonNull String message, Object... args) {
        sPrinter.v(message, args);
    }

    public static void d(@NonNull String message, Object... args) {
        sPrinter.d(message, args);
    }

    public static void d(Object object) {
        sPrinter.d(object);
    }

    public static void i(@NonNull String message, Object... args) {
        sPrinter.i(message, args);
    }

    public static void w(@NonNull String message, Object... args) {
        sPrinter.w(message, args);
    }

    public static void w(@NonNull Throwable throwable) {
        sPrinter.w(throwable, null);
    }

    public static void w(@Nullable Throwable throwable, @Nullable String message, Object... args) {
        sPrinter.w(throwable, message, args);
    }

    public static void e(@NonNull String message, Object... args) {
        sPrinter.e(message, args);
    }

    public static void e(@NonNull Throwable throwable) {
        sPrinter.e(throwable, null);
    }

    public static void e(@Nullable Throwable throwable, @Nullable String message, Object... args) {
        sPrinter.e(throwable, message, args);
    }

    public static void json(@NonNull Object object) {
        sPrinter.json(object);
    }
}
