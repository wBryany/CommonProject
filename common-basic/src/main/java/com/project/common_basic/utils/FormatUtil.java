package com.project.common_basic.utils;

import android.content.Context;
import android.net.ParseException;
import android.text.TextUtils;

/**
 * 格式化字符串
 *
 * @author yamlee
 */
public class FormatUtil {
    /**
     * 格式化银行卡,四位数隔开显示
     *
     * @param input
     * @return
     */
    public static String formatBankCard(String input) {
        //noinspection HardCodedStringLiteral
        String result = input.replaceAll("([\\d]{4})(?=\\d)", "$1 ");
        return result;
        //e.g. we're going to slow
    }

    /**
     * 格式化隐藏银行卡号,首四位和末四位显示,中间以八个星号代替
     *
     * @param input
     * @return
     */
    public static String formatHideBankCard(String input) {
        try {
            String formInput = input.replace(" ", "");
            String prefix = formInput.substring(0, 4);
            String suffix = formInput.substring(formInput.length() - 4, formInput.length());
            String result = prefix + " **** **** " + suffix;
            return result;
        } catch (Exception e) {
            return input;
        }
    }

    /**
     * 格式化单位为分的金额,将其修改为小数点,如100元,输入为10000(单位分)格式化后为100.00元
     *
     * @param numberString
     * @return
     */
    public static String formatMoneyWithDotNumber(String numberString) {
        try {
            if (TextUtils.isEmpty(numberString)) {
                return "0";
            }
            long number = Long.parseLong(numberString);
            boolean isNegative = false;
            if (number < 0) {
                isNegative = true;
                number *= -1;
                numberString = String.valueOf(number);
            }
            if (numberString.length() > 2) {
                String beforeString = numberString.substring(0, numberString.length() - 2);

                String afterString = numberString.substring(numberString.length() - 2, numberString.length());

                numberString = beforeString + "." + afterString;
            } else if (numberString.length() == 2) {
                numberString = "0." + numberString;
            } else if (numberString.length() == 1) {
                numberString = "0.0" + numberString;
            }

            return isNegative ? "-" + numberString : numberString;
        } catch (ParseException exception) {
            return "0.00";
        }
    }

    /**
     * 使用java正则表达式去掉多余的.与0,如100.00格式化之后就是100
     * chenfeiyue
     *
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (TextUtils.isEmpty(s)) {
            return s;
        }
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 将字符串格式化为指定颜色的html字符串
     *
     * @param source   字符串
     * @param colorRes 颜色资源id
     * @return
     */
    @SuppressWarnings("HardCodedStringLiteral")
    public static String formatColorHtml(Context context, String source, int colorRes) {
        int color = context.getResources().getColor(colorRes);
        String strColor = String.format("#%06X", 0xFFFFFF & color);
        return String.format("<font color=\"%1s\">%2s</font>", strColor, source);
    }

    public static String formatTime(int num) {
        if (num < 10) {
            return "0" + num;
        }
        return String.valueOf(num);
    }
}
