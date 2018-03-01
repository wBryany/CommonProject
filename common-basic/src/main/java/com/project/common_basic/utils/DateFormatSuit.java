package com.project.common_basic.utils;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yamlee on 5/20/16.
 * <p>
 * 时间格式模板
 * <p>
 * 模板1: yyyy-MM-dd HH:mm:ss<br>
 * 模板2: yyyy-MM-dd<br>
 * 模板3: M月d日<br>
 * 模板4: y年M月d日<br>
 * 模板5: yyyyMM<br>
 * 模板6: yyyy.MM.dd<br>
 * 模板7: yyyy.M.d<br>
 * 模板8: M.d<br>
 * 模板9: yyyy-MM-dd E
 */
@SuppressWarnings("HardCodedStringLiteral")
public class DateFormatSuit {
    public static final String TEMPLATE1 = "yyyy-MM-dd HH:mm:ss";
    public static final String TEMPLATE2 = "yyyy-MM-dd";
    public static final String TEMPLATE3 = "M月d日";
    public static final String TEMPLATE4 = "y年M月d日";
    public static final String TEMPLATE5 = "yyyyMM";
    public static final String TEMPLATE6 = "yyyy.MM.dd";
    public static final String TEMPLATE7 = "yyyy.M.d";
    public static final String TEMPLATE8 = "M.d";
    public static final String TEMPLATE9 = "yyyy-MM-dd E";
    public static final String TEMPLATE10 = "yyyy年MM月";
    public static final String TEMPLATE11 = "yyyy年MM月dd日";
    public static final String TEMPLATE12 = "yyyy.M.d HH:mm";
    public static final String TEMPLATE13 = "HH:mm";
    public static final String TEMPLATE14 = "yyyy-MM-dd HH:mm";
    public static final String TEMPLATE15 = "yyyy年M月d日 HH:mm";
    public static final String TEMPLATE16 = "M月d日 HH:mm";


    /**
     * annotation用来限制时间工具类{@link com.project.common_basic.utils.DateUtil}获取模板限制指定的string
     * 类型模板,如在getDate(String template)方法中,
     */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TEMPLATE1, TEMPLATE2, TEMPLATE3, TEMPLATE4, TEMPLATE5, TEMPLATE6,
            TEMPLATE7, TEMPLATE8, TEMPLATE9, TEMPLATE10, TEMPLATE11, TEMPLATE12, TEMPLATE13, TEMPLATE14, TEMPLATE15,
            TEMPLATE16})
    public @interface DateFormatTemplate {
    }
}
