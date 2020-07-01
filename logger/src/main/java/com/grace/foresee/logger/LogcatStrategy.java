package com.grace.foresee.logger;

import android.util.Log;

public class LogcatStrategy implements LogStrategy {
    @Override
    public void log(LogDetails logDetails) {
        Log.println(logDetails.getLevel(), logDetails.getTag(), logDetails.getMessage());
    }
}
