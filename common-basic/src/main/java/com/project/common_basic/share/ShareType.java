package com.project.common_basic.share;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ShareType {
    public static final int WxFriend = 1;
    public static final int WxMoments = 2;
    public static final int QQFriend = 3;
    public static final int QQZone = 4;
    public static final int ClipBoard = 5;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WxFriend, WxMoments, QQFriend, QQZone, ClipBoard})
    public @interface ShareTypeTemplate {

    }
}