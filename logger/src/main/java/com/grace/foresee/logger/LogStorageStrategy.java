package com.grace.foresee.logger;

import com.grace.foresee.storage.StorageWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.grace.foresee.logger.Logger.DEBUG;
import static com.grace.foresee.logger.Logger.ERROR;
import static com.grace.foresee.logger.Logger.INFO;
import static com.grace.foresee.logger.Logger.VERBOSE;
import static com.grace.foresee.logger.Logger.WARN;

public class LogStorageStrategy implements LogStrategy {
    private String mPath;
    private String mDateFormat;

    public LogStorageStrategy(String path) {
        mPath = path;
        mDateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    }

    public LogStorageStrategy(String path, String dateFormat) {
        mPath = path;
        mDateFormat = dateFormat;
    }

    @Override
    public void log(LogDetails logDetails) {
        String format = "%s %s/%s: %s\n";
        String time = new SimpleDateFormat(mDateFormat, Locale.CHINA).format(new Date(logDetails.getTime()));
        String content = String.format(format, time, convertLevel(logDetails.getLevel()),
                logDetails.getTag(), logDetails.getMessage());

        StorageWriter.write(mPath, content, true);
    }

    private String convertLevel(int level) {
        switch (level) {
            case VERBOSE:
                return "V";
            case DEBUG:
                return "D";
            case INFO:
                return "I";
            case WARN:
                return "W";
            case ERROR:
                return "E";
            default:
                return "UNKNOWN";
        }
    }
}
