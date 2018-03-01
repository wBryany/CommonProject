package com.project.common_basic.utils;

import android.text.TextUtils;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 * <p>
 * 参考地址：http://www.cnblogs.com/yansheng/archive/2010/05/07/1730188.html
 * Created by yamlee on 15/9/21.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ValidateUtil {
    /**
     * 验证手机格式
     * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189、（1349卫通）
     * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     *
     * @param mobiles 手机号
     */
    public static boolean isMobileNum(String mobiles) {
        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][34578]\\d{9}";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }

    /**
     * 验证密码格式6-15位包含数字和字母
     */
    public static boolean isLeaglePasswd(String passwd) {
//        String passRegx = ".*[a-zA-Z].*[0-9]|.*[0-9].*[a-zA-Z]";
        return !(TextUtils.isEmpty(passwd) || passwd.length() > 20 || passwd.length() < 6);
//        else return passwd.matches(passRegx);
    }

    /**
     * 校验借记银行卡卡号
     */
    public static boolean checkBankCard(String result) {
        if (result.length() <= 14) {
            return false;
        }
        String cardId = result.replace(" ", "");
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        return bit != 'N' && cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    public static boolean isNull(Object[] objs) {
        return objs == null || objs.length == 0;
    }

    public static boolean isNull(Integer integer) {
        return integer == null || integer == 0;
    }

    public static boolean isNull(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isNull(Map map) {
        return map == null || map.size() == 0;
    }

    public static boolean isNull(String str) {
        return str == null || "".equals(str.trim()) || "null".equals(str.toLowerCase());
    }


    public static boolean isNull(Long longs) {
        return longs == null || longs == 0;
    }

    public static boolean isNotNull(Long longs) {
        return !isNull(longs);
    }

    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    public static boolean isNotNull(Collection collection) {
        return !isNull(collection);
    }

    public static boolean isNotNull(Map map) {
        return !isNull(map);
    }

    public static boolean isNotNull(Integer integer) {
        return !isNull(integer);
    }

    public static boolean isNotNull(Object[] objs) {
        return !isNull(objs);
    }

    /**
     * 匹配URL地址
     */
    public static boolean isUrl(String str) {
        return match(str, "^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$");
    }

    /**
     * 匹配密码，以字母开头，长度在6-12之间，只能包含字符、数字和下划线。
     */
    public static boolean isPwd(String str) {
        return match(str, "^[a-zA-Z]\\w{6,12}$");
    }

    /**
     * 验证字符，只能包含中文、英文、数字、下划线等字符。
     */
    public static boolean stringCheck(String str) {
        return match(str, "^[a-zA-Z0-9\u4e00-\u9fa5-_]+$");
    }

    /**
     * 匹配Email地址
     */
    public static boolean isEmail(String str) {
        return match(str, "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    }

    /**
     * 匹配非负整数（正整数+0）
     */
    public static boolean isInteger(String str) {
        return match(str, "^[+]?\\d+$");
    }

    /**
     * 判断数值类型，包括整数和浮点数
     */
    public static boolean isNumeric(String str) {
        return isFloat(str) || isInteger(str);
    }

    /**
     * 只能输入数字
     */
    public final static boolean isDigits(String str) {
        return match(str, "^[0-9]*$");
    }

    /**
     * 匹配正浮点数
     */
    public static boolean isFloat(String str) {
        return match(str, "^[-\\+]?\\d+(\\.\\d+)?$");
    }

    /**
     * 联系电话(手机/电话皆可)验证
     */
    public static boolean isTel(String text) {
        return isMobile(text) || isPhone(text);
    }

    /**
     * 电话号码验证
     */
    public static boolean isPhone(String text) {
        return match(text, "^(\\d{3,4}-?)?\\d{7,9}$");
    }

    /**
     * 手机号码验证
     */
    public static boolean isMobile(String text) {
        return text.length() == 11 && match(text, "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$");
    }

    /**
     * 身份证号码验证
     */
    public static boolean isIdCardNo(String text) {
        return match(text, "^(\\d{6})()?(\\d{4})(\\d{2})(\\d{2})(\\d{3})(\\w)$");
    }

    /**
     * 判断是否为合法字符(a-zA-Z0-9-_)
     */
    public static boolean isRightfulString(String text) {
        return match(text, "^[A-Za-z0-9_-]+$");
    }

    /**
     * 判断英文字符(a-zA-Z)a
     */
    public static boolean isEnglish(String text) {
        return match(text, "^[A-Za-z]+$");
    }

    /**
     * 判断中文字符(包括汉字和符号)
     */
    public static boolean isChineseChar(String text) {
        return match(text, "^[\u0391-\uFFE5]+$");
    }

    /**
     * 匹配汉字
     */
    public static boolean isChinese(String text) {
        return match(text, "^[\u4e00-\u9fa5]+$");
    }


    /**
     * 过滤中英文特殊字符，除英文"-_"字符外
     */
    public static String stringFilter(String text) {
        String regExpr = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regExpr);
        Matcher m = p.matcher(text);
        return m.replaceAll("").trim();
    }

    /**
     * 正则表达式匹配
     *
     * @param text 待匹配的文本
     * @param reg  正则表达式
     */
    private static boolean match(String text, String reg) {
        return !(TextUtils.isEmpty(text) || TextUtils.isEmpty(reg)) && Pattern.compile(reg).matcher(text).matches();
    }


}
