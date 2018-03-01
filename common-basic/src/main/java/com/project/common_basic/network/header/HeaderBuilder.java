package com.project.common_basic.network.header;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求头Builder
 *
 * @author yamlee
 */
@SuppressWarnings("HardCodedStringLiteral")
public class HeaderBuilder {
    private Context mContext;
    private StringBuilder cookieString = new StringBuilder();
    private RequestUserAgentHeader mUserAgentHeader;
    private List<RequestHeader> customHeaders = new ArrayList<>();


    public HeaderBuilder(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 添加user-agent，如不设置会使用默认的UA
     */
    public HeaderBuilder userAgent(String value) {
        mUserAgentHeader = new RequestUserAgentHeader(mContext);
        mUserAgentHeader.setValue(value);
        return this;
    }

    /**
     * 添加cookie中的sessionId
     */
    public HeaderBuilder sessionId(String session) {
        cookieString.append("sessionid=");
        cookieString.append(session);
        cookieString.append(";");
        return this;
    }

    /**
     * 设置调式标志
     */
    public HeaderBuilder setDebug(boolean isDebug) {
        if (isDebug) {
            cookieString.append("mmfct=");
            cookieString.append("1");
            cookieString.append(";");
        }
        return this;
    }

    /**
     * 自定义cookie字段
     *
     * @param key   cookie的key值
     * @param value cookie key对应的value值
     */
    public HeaderBuilder customCookie(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            cookieString.append(key)
                    .append("=")
                    .append(value)
                    .append(";");
        }
        return this;
    }

    /**
     * 自定义header中的key-value字段
     */
    public HeaderBuilder customHeader(final String key, final String value) {
        if (!TextUtils.isEmpty(key)) {
            RequestHeader requestHeader = new RequestHeader() {
                @Override
                public String key() {
                    return key;
                }

                @Override
                public String value() {
                    return value;
                }
            };
            customHeaders.add(requestHeader);
        }
        return this;
    }


    public List<RequestHeader> build() {
        List<RequestHeader> requestHeaders = new ArrayList<>();
        //添加UA
        if (mUserAgentHeader == null) {
            mUserAgentHeader = new RequestUserAgentHeader(mContext);
        }
        requestHeaders.add(mUserAgentHeader);
        //添加cookie
        RequestCookieHeader requestCookieHeader = new RequestCookieHeader();
        requestCookieHeader.setValue(cookieString.toString());
        requestHeaders.add(requestCookieHeader);
        //添加自定义的header字段
        requestHeaders.addAll(customHeaders);
        return requestHeaders;
    }
}
