package com.project.common_basic.share;

/**
 * Created by fengruicong on 15-6-25.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ShareKeys {


    /**
     * 微信登陆 第三方应用唯一id
     */
    private static String WxAppId = "";

    /**
     * qq分享key
     */
    private static String QQ_APPID = "";
    private static String QQ_APPKEY = "";

    public static String getWxAppId() {
        return WxAppId;
    }

    public static void setWxAppId(String wxAppId) {
        WxAppId = wxAppId;
    }

    public static String getQqAppid() {
        return QQ_APPID;
    }

    public static void setQqAppid(String qqAppid) {
        QQ_APPID = qqAppid;
    }

    public static String getQqAppkey() {
        return QQ_APPKEY;
    }

    public static void setQqAppkey(String qqAppkey) {
        QQ_APPKEY = qqAppkey;
    }
}
