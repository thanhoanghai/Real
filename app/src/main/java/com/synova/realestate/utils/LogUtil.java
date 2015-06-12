
package com.synova.realestate.utils;

import android.util.Log;

public class LogUtil {

    public enum LogLevel {
        DEBUG, RELEASE
    }

    private static LogLevel logLevel = LogLevel.DEBUG;

    public static void setLogLevel(LogLevel logLevel) {
        LogUtil.logLevel = logLevel;
    }

    public static void d(String tag, String msg) {
        if (logLevel != LogLevel.RELEASE) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (logLevel != LogLevel.RELEASE) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (logLevel != LogLevel.RELEASE) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, Throwable error) {
        if (logLevel != LogLevel.RELEASE) {
            Log.e(tag, error.getLocalizedMessage(), error);
        }
    }

}
