package com.project.common_basic.network.header;

import android.text.TextUtils;

/**
 * Created by yamlee on 16/1/7.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class RequestCookieHeader implements RequestHeader {
    private String mValue;

    public void setValue(String value) {
        this.mValue = value;
    }

    @Override
    public String key() {
        return "Cookie";
    }

    @Override
    public String value() {
        if (TextUtils.isEmpty(mValue)) {
            return defaultValue();
        } else {
            return mValue;
        }
    }

    private String defaultValue() {
        return "";
    }
}
