package com.project.common_basic.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LoadType {
    /**
     * 分页加载模式，pageNum每次加1
     */
    public static final int PAGE_SPLIT_TYPE = 1;
    /**
     * 位移量加载模式，pageNum为已经加载后数据列表的个数
     */
    public static final int OFFSET_TYPE = 2;

    /**
     * 列表加载模式，分为按分页加载或按位移量加载
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PAGE_SPLIT_TYPE, OFFSET_TYPE})
    public @interface LoadTypeTemplate {

    }
}
