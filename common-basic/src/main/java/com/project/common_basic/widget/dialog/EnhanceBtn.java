package com.project.common_basic.widget.dialog;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EnhanceBtn {
    public static final int ENHANCE_CONFIRM = 0;//default
    public static final int ENHANCE_CANCEL = 1;
    public static final int ENHANCE_NONE = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ENHANCE_CONFIRM, ENHANCE_CANCEL, ENHANCE_NONE})
    public @interface EnhanceBtnDef {

    }
}