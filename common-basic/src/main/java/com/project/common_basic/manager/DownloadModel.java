package com.project.common_basic.manager;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 下载模式 非WiFi下提示,不区分网络,总是提醒,WIFI下不提醒直接下载
 */
public class DownloadModel {
    public static final int MODEL_DOWNLOAD_ALWAYS = 1;
    public static final int MODEL_REMIND_NOT_WIFI = 2;
    public static final int MODEL_REMIND_ALWAYS = 3;
    public static final int MODEL_DOWNLOAD_IF_WIFI_NOT_REMIND = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODEL_DOWNLOAD_ALWAYS, MODEL_REMIND_NOT_WIFI, MODEL_REMIND_ALWAYS, MODEL_DOWNLOAD_IF_WIFI_NOT_REMIND})
    public @interface DownloadModelTemplate {

    }
}