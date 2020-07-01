package com.grace.foresee.logger;

public class AndroidLogAdapter implements LogAdapter {
    private LogStrategy mLogStrategy;
    private int mLevel;

    public AndroidLogAdapter() {
        this(new PrettyFormatStrategy.Builder().build());
    }

    public AndroidLogAdapter(LogStrategy logStrategy) {
        mLogStrategy = logStrategy;
        mLevel = Logger.VERBOSE;
    }

    public AndroidLogAdapter(LogStrategy logStrategy, int level) {
        mLogStrategy = logStrategy;
        mLevel = level;
    }

    @Override
    public boolean isLoggable(int level, String tag) {
        return level >= mLevel;
    }

    @Override
    public void log(LogDetails logDetails) {
        mLogStrategy.log(logDetails);
    }
}
