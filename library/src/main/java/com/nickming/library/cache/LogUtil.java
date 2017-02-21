package com.nickming.library.cache;

import android.util.Log;

/**
 * @author nickming
 * @description
 * @date 2017/2/21
 */

public class LogUtil {

    private final static int VERBOSE = 1;

    private final static int DEBUG = 2;

    private final static int INFO = 3;

    private final static int WARN = 4;

    private final static int ERROR = 5;

    private final static int NOTHING = 6;

    private final static int level = VERBOSE;

    /**
     * 封装过的logv操作,当且仅当level小于等于verbose
     * @param tag tag标签
     * @param msg msg打印内容
     */
    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    /**
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            Log.v(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (level <= ERROR) {
            Log.v(tag, msg);
        }
    }

}
