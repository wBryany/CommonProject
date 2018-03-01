package com.project.common_basic.utils;

/**
 * 字符串操作工具类
 * Created by yamlee on 6/3/16.
 *
 * @author yamlee
 */
public class StringUtil {
    /**
     * 移除掉输入的最后一个字符
     *
     * @param input
     * @return
     */
    public static String removeLastChar(String input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        String result = input.substring(0, input.length() - 1);
        return result;
    }
}
