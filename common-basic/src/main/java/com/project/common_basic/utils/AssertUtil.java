package com.project.common_basic.utils;

/**
 * 断言工具类,用来做空指针判断等
 * <p>
 * Created by yamlee on 5/20/16.
 */
public class AssertUtil {

    public static void checkNotNull(Object object, String msg) {
        if (object == null) {
            throw new NullPointerException(msg);
        }
    }
}
