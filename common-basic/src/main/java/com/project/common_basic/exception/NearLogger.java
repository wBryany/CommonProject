package com.project.common_basic.exception;

import android.util.Log;

/**
 * Created by yamlee on 16/1/8.
 * 通用log工具类
 */
public class NearLogger {
    private static boolean mDebug = true;

    public static void setDebug(boolean isDebug) {
        mDebug = isDebug;
    }

    /**
     * 打印异常的堆栈信息
     *
     * @param e
     */
    public static void log(Throwable e) {
        if (mDebug) {
            e.printStackTrace();
        }
    }

    /**
     * 打印info信息
     */
    public static void i(String tag, String infoMsg) {
        if (mDebug) {
            Log.i(tag, infoMsg);
        }
    }

    /**
     * 打印error信息
     */
    public static void e(String tag, String errorMsg) {
        if (mDebug) {
            Log.e(tag, errorMsg);
        }
    }

    /**
     * 打印waning信息
     */
    public static void w(String tag, String errorMsg) {
        if (mDebug) {
            Log.w(tag, errorMsg);
        }
    }

    /**
     * Java print信息打印
     */
    public static void print(String msg) {
        if (mDebug) {
            System.out.println(msg);
        }
    }
}
