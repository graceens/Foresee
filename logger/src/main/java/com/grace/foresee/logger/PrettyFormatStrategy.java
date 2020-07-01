package com.grace.foresee.logger;

import android.text.TextUtils;

public class PrettyFormatStrategy implements LogStrategy {
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    private static final char HORIZONTAL_LINE = '│';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    private static final int METHOD_OFFSET = 5;
    private static final int CHUNK_SIZE = 4000;

    private final LogStrategy mLogStrategy;
    private final int mMethodCount;
    private final String mTag;
    private final boolean mShowThreadInfo;

    public PrettyFormatStrategy(Builder builder) {
        mLogStrategy = builder.logStrategy;
        mMethodCount = builder.methodCount;
        mTag = builder.tag;
        mShowThreadInfo = builder.showThreadInfo;
    }

    @Override
    public void log(LogDetails logDetails) {
        int level = logDetails.getLevel();
        String tag = formatTag(logDetails.getTag());
        String threadName = logDetails.getThreadName();
        StackTraceElement[] methodStackTrace = logDetails.getMethodStackTrace();
        Throwable throwable = logDetails.getThrowable();
        String message = logDetails.getMessage();
        long time = logDetails.getTime();

        //顶部分界线
        logTop(level, tag, time);
        //头部信息
        logHeader(level, tag, time, threadName, methodStackTrace);
        //日志主要内容
        logContent(level, tag, time, message, throwable);
        //底部分界线
        logBottom(level, tag, time);
    }

    private String formatTag(String tag) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(mTag)) {
            return tag + ":" + mTag;
        }
        if (!TextUtils.isEmpty(tag)) {
            return tag;
        }
        return mTag;
    }

    /**
     * 打印顶部分界线
     */
    private void logTop(int level, String tag, long time) {
        mLogStrategy.log(new LogDetails(level, tag, time, TOP_BORDER));
    }

    /**
     * 打印中间分界线
     */
    private void logDivider(int level, String tag, long time) {
        mLogStrategy.log(new LogDetails(level, tag, time, MIDDLE_BORDER));
    }

    /**
     * 打印底部分界线
     */
    private void logBottom(int level, String tag, long time) {
        mLogStrategy.log(new LogDetails(level, tag, time, BOTTOM_BORDER));
    }

    /**
     * 打印头部信息
     */
    private void logHeader(int level, String tag, long time, String threadName,
                           StackTraceElement[] methodStackTrace) {
        if (mShowThreadInfo) {
            logThread(level, tag, time, threadName);
            logDivider(level, tag, time);
        }
        if (mMethodCount > 0) {
            logMethod(level, tag, time, methodStackTrace);
            logDivider(level, tag, time);
        }
    }

    /**
     * 打印线程信息
     */
    private void logThread(int level, String tag, long time, String threadName){
        String message = HORIZONTAL_LINE +
                " Thread: " +
                threadName;
        mLogStrategy.log(new LogDetails(level, tag, time, message));
    }

    /**
     * 打印方法调用堆栈
     */
    private void logMethod(int level, String tag, long time, StackTraceElement[] methodStackTrace) {
        String indent = "";
        int index = getStackTraceIndex(methodStackTrace);
        if (index != -1) {
            int methodCount = mMethodCount;
            if ((mMethodCount + index) >= methodStackTrace.length) {
                methodCount = methodStackTrace.length - index - 1;
            }

            for (int i = index + methodCount; i > index; i--) {
                StackTraceElement element = methodStackTrace[i];
                String format = "%s %s%s.%s (%s:%s)";
                String message = String.format(format, HORIZONTAL_LINE, indent,
                        element.getClassName(), element.getMethodName(), element.getFileName(),
                        element.getLineNumber());
                mLogStrategy.log(new LogDetails(level, tag, time, message));
                indent = "  ";
            }
        }
    }

    private int getStackTraceIndex(StackTraceElement[] trace) {
        if (METHOD_OFFSET < trace.length) {
            for (int i = trace.length - 1; i >= METHOD_OFFSET; i--) {
                //过滤掉自身
                if (trace[i].getClassName().equals(Logger.class.getName()) ||
                        trace[i].getClassName().equals(LoggerPrinter.class.getName())) {
                    return i;
                }
            }
            return METHOD_OFFSET - 1;
        }
        return -1;
    }

    /**
     * 打印日志内容
     */
    private void logContent(int level, String tag, long time, String message, Throwable throwable) {
        String stackTraceString = LoggerUtil.getStackTraceString(throwable);
        if (TextUtils.isEmpty(message) && TextUtils.isEmpty(stackTraceString)) {
            message = "empty/null message";
        }

        logContentLines(level, tag, time, message);

        //打印异常信息
        if (!TextUtils.isEmpty(stackTraceString)) {
            if (!TextUtils.isEmpty(message)) {
                logDivider(level, tag, time);
            }
            logContentLines(level, tag, time, stackTraceString);
        }
    }

    /**
     * 打印多行内容
     */
    private void logContentLines(int level, String tag, long time, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        String[] messages = message.split("\n");
        for (String s : messages) {
            int size = s.getBytes().length;
            //小于单行输出最大长度
            if (size <= CHUNK_SIZE) {
                logContentLine(level, tag, time, s);
            } else {
                //大于单行输出上限，分多行输出
                for (int i = 0; i < size; i += CHUNK_SIZE) {
                    int endIndex = Math.min((i + CHUNK_SIZE), size);
                    logContentLine(level, tag, time, s.substring(i, endIndex));
                }
            }
        }
    }

    /**
     * 打印单行内容
     */
    private void logContentLine(int level, String tag, long time, String message) {
        String s = HORIZONTAL_LINE + " " + message;
        mLogStrategy.log(new LogDetails(level, tag, time, s));
    }

    public static class Builder {
        LogStrategy logStrategy;
        String tag = null;
        int methodCount = 2;
        boolean showThreadInfo = true;

        public Builder logStrategy(LogStrategy logStrategy) {
            this.logStrategy = logStrategy;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder methodCount(int methodCount) {
            this.methodCount = methodCount;
            return this;
        }

        public Builder showThreadInfo(boolean showThreadInfo) {
            this.showThreadInfo = showThreadInfo;
            return this;
        }

        public PrettyFormatStrategy build() {
            if (logStrategy == null) {
                logStrategy = new LogcatStrategy();
            }
            return new PrettyFormatStrategy(this);
        }
    }

}
