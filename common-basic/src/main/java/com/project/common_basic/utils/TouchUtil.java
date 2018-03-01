package com.project.common_basic.utils;

/**
 * 屏蔽快速点击工具类
 * Created by yamlee on 15/10/20.
 */
public class TouchUtil {
    private static long lastClickTime;


    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
