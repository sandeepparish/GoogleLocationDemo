package com.protocloud.googlelocationdemo.utils;


import com.protocloud.googlelocationdemo.BuildConfig;

public class LogHelper {

    public static void i(String tag, String string) {
        if (inDebugMode()) android.util.Log.i(tag, string);
    }

    public static void e(String tag, String string) {
        if (inDebugMode()) android.util.Log.e(tag, string);
    }

    public static void d(String tag, String string) {
        if (inDebugMode()) android.util.Log.d(tag, string);
    }

    public static void v(String tag, String string) {
        if (inDebugMode()) android.util.Log.v(tag, string);
    }

    public static void w(String tag, String string) {
        if (inDebugMode()) android.util.Log.w(tag, string);
    }

    public static boolean inDebugMode() {
        return BuildConfig.DEBUG;
    }

}
