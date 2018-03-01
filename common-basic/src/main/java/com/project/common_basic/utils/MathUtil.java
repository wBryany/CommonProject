package com.project.common_basic.utils;

import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * 数学计算工具类
 * <p>
 * Created by zczhang on 16/2/23.
 */
public class MathUtil {

    /**
     * 数字加法
     *
     * @param s1
     * @param s2
     * @return
     */
    public static String add(String s1, String s2) {
        if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)) {
            //noinspection HardCodedStringLiteral
            return "params error";
        }
        BigDecimal b1 = new BigDecimal(s1);
        BigDecimal b2 = new BigDecimal(s2);
        return b1.add(b2).toString();
    }


    /**
     * 数字减法
     *
     * @param s1
     * @param s2
     * @return
     */
    public static String subtract(String s1, String s2) {
        if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)) {
            //noinspection HardCodedStringLiteral
            return "params error";
        }

        BigDecimal b1 = new BigDecimal(s1);
        BigDecimal b2 = new BigDecimal(s2);
        return b1.subtract(b2).toString();
    }

    /**
     * 数字乘法
     *
     * @param s1
     * @param s2
     * @return
     */
    public static String multiply(String s1, String s2) {
        if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)) {
            //noinspection HardCodedStringLiteral
            return "params error";
        }
        BigDecimal b1 = new BigDecimal(s1);
        BigDecimal b2 = new BigDecimal(s2);
        String result = b1.multiply(b2).toString();
        return FormatUtil.subZeroAndDot(result);
    }

    /**
     * 数字除法
     * 默认保留10位小数
     *
     * @param s1
     * @param s2
     * @param decimalNum 小数位数
     * @return
     */
    public static String divide(String s1, String s2, int decimalNum) {
        if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)) {
            //noinspection HardCodedStringLiteral
            return "params error";
        }
        BigDecimal b1 = new BigDecimal(s1);
        BigDecimal b2 = new BigDecimal(s2);
        String bigDecimal = b1.divide(b2, decimalNum, BigDecimal.ROUND_HALF_EVEN).toString();
        return FormatUtil.subZeroAndDot(bigDecimal);
    }

    /**
     * 数字除法,可设置取舍模式
     *
     * @param s1
     * @param s2
     * @param decimalNum
     * @param roundMode  取舍的模式 {@link BigDecimal}
     * @return
     */
    public static String divide(String s1, String s2, int decimalNum, int roundMode) {
        if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)) {
            //noinspection HardCodedStringLiteral
            return "params error";
        }
        BigDecimal b1 = new BigDecimal(s1);
        BigDecimal b2 = new BigDecimal(s2);
        String bigDecimal = b1.divide(b2, decimalNum, roundMode).toString();
        return FormatUtil.subZeroAndDot(bigDecimal);
    }

}
