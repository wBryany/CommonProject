package com.project.common_basic.utils;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yamlee on 5/20/16.
 * <p>
 * 时间单位模板
 */
@SuppressWarnings("HardCodedStringLiteral")
public class DateUnit {
    public static final String DAYS = "days";
    public static final String MONTHS = "months";
    public static final String YEARS = "years";


    /**
     * annotation用来限制时间单位{@link DateUtil}
     */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({DAYS, MONTHS, YEARS})
    public @interface DateUnitDef {
    }
}
