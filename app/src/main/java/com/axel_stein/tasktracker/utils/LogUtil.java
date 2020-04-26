package com.axel_stein.tasktracker.utils;

import android.util.Log;

public class LogUtil {
    private static String TAG = "TAG";
    private static boolean DEBUG = true;

    private LogUtil() {}

    public static void debug(Object ...objects) {
        if (DEBUG && objects != null) {
            for (Object o : objects) {
                debug(String.valueOf(o));
            }
        }
    }

    public static void debug(String msg) {
        if (DEBUG) {
            Log.v(TAG, msg);
        }
    }

    public static void debug(Class c, String method, String msg) {
        if (DEBUG) {
            Log.v("TAG", c.getSimpleName() + "::" + method + " = " + msg);
        }
    }

    public static void error(Object ...objects) {
        if (DEBUG && objects != null) {
            for (Object o : objects) {
                error(String.valueOf(o));
            }
        }
    }

    public static void error(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }

}
