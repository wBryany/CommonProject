package com.project.common_basic.network.header;

import android.content.Context;
import android.text.TextUtils;

import com.project.common_basic.utils.ApkUtil;

/**
 * 网络请求UA头
 * Created by yamlee on 16/1/7.
 *
 * @author yamlee
 */
@SuppressWarnings("HardCodedStringLiteral")
public class RequestUserAgentHeader implements RequestHeader {
    private Context context;
    private String mValue;

    public RequestUserAgentHeader(Context context) {
        this.context = context;
    }

    public void setValue(String value) {
        this.mValue = value;
    }

    @Override
    public String key() {
        return "User-Agent";
    }

    @Override
    public String value() {
        if (TextUtils.isEmpty(mValue)) {
            return defaultVaue();
        } else {
            return mValue;
        }
    }

    public String defaultVaue() {
        String model = "model:" + android.os.Build.MODEL + ";release:" + android.os.Build.VERSION.RELEASE;
        String appVersion = "version_name:" + "v" + ApkUtil.getVersionName(context) + ";version_code:" + ApkUtil.getVersionCode(context);
        String channel = "channel:" + ApkUtil.getChannel(context);
        return "Near-Merchant-Android;" + appVersion + ";" + channel + ";" + model;
    }
}
