package com.project.common_basic.network.header;

import com.project.common_basic.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * 主要用于服务器在某接口下发cookie,
 * 客户端同步将下发的cookie应用于之后请求的所有接口
 */
public final class OkHttpCookieJar implements CookieJar {
    private List<Cookie> allCookies = new ArrayList<>();

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        int allCookieSize = allCookies.size();
        //过滤重复cookie
        for (Cookie newCookie : cookies) {
            boolean hasSameCookie = false;
            for (int i = 0; i < allCookieSize; i++) {
                Cookie oldCookie = allCookies.get(i);
                //cookie名称相同 则新cookie替换旧的
                if (oldCookie.name().equals(newCookie.name())) {
                    hasSameCookie = true;
                    //替换之前的cookie
                    allCookies.set(i, newCookie);
                    break;
                }
            }
            if (!hasSameCookie) {
                allCookies.add(newCookie);
            }
        }
        //全局静态变量ALL_COOKIES,webview使用
        NetUtil.ALL_COOKIES = allCookies;
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> result = new ArrayList<>();
        for (Cookie cookie : allCookies) {
            result.add(cookie);
        }
        return result;
    }
}