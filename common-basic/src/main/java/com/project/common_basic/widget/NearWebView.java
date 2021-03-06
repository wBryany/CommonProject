package com.project.common_basic.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import com.project.common_basic.utils.ApkUtil;
import com.project.common_basic.utils.DeviceUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 * 通用WebView基类
 *
 * @author yamlee
 */
public class NearWebView extends WebView {
    public NearWebView(Context context) {
        super(context);
        init();
    }

    public NearWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NearWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public NearWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init();
//    }

    public NearWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init();
    }


    private void init() {
        //设置webview支持chrome调试 4.4以上系统支持
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                setWebContentsDebuggingEnabled(true);
            }
        }
        setWebViewSetting();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Timber.d("------onAttachedToWindow-----");
        syncCookie();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    private void setWebViewSetting() {
        WebSettings settings = getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        settings.setBuiltInZoomControls(false);
        //设置UA user-agent


        settings.setUserAgentString(settings.getUserAgentString() + getUserAgent(getContext()));

        Timber.d("user-agent====" + settings.getUserAgentString());
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDefaultTextEncodingName("utf-8");
        //启用地理定位
        settings.setGeolocationEnabled(true);
        //开启DomStorage缓存
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        String cachePath = getContext().getCacheDir().getAbsolutePath();
        if (Build.VERSION.SDK_INT <= 20) {
            //4.4以下需手动设置database路径,方使domstorage生效
            cachePath += "/database";
            settings.setDatabasePath(cachePath);
        }
        settings.setAppCachePath(cachePath);
        settings.setAllowFileAccess(true);
        settings.setGeolocationDatabasePath(getContext().getFilesDir().getPath());
        //启用数据库
        settings.setDatabaseEnabled(true);
    }

    protected String getUserAgent(Context context) {
        String appVersion = "/version_name:" + ApkUtil.getVersionName(context) +
                ";version_code:" + ApkUtil.getVersionCode(context);

        String deviceName = "/device_name:" + DeviceUtil.getDeviceName() +
                ";deviceid:" + DeviceUtil.getDeviceID(context);

        String osVersion = "/os version:" + DeviceUtil.getOsVersionStr() + ";"
                + DeviceUtil.getOsVersion();
        return ";QMMWD" + appVersion + deviceName + osVersion;
    }

    Map<String, List> cookieMap = new ArrayMap<>();

    public void addCookie(String domain, List<String> values) {
        cookieMap.put(domain, values);
    }

    private void syncCookie() {
        if (cookieMap.size() < 0) {
            Timber.e("cookie map is empty, just return.");
            return;
        }
        Timber.d("start set cookie...");
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        CookieSyncManager.getInstance().startSync();
//        CookieManager.allowFileSchemeCookies();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(this, true);
        }
        cookieManager.setAcceptCookie(true);
        clearCache(true);
        clearHistory();
//        boolean offlineDev = SpManager.getInstance(getContext()).getBoolean(SpKey.OFFLINE_DEV, false);
//        UserCache userCache = UserCache.getInstance(getContext());
//        if (userCache.hasLogin()) {
//            String sessionId = userCache.getSessionId();
//            String qfUid = userCache.getUserId();
//            /**
//             *  注：临时cookie(没有expires参数的cookie)不能带有domain选项。
//             当客户端发送一个http请求时，会将有效的cookie一起发送给服务器。
//             如果一个cookie的domain和path参数和URL匹配，那么这个cookie就是有效的。
//             一个URL中包含有domain和path，可以参
//             */
//            cookieManager.setCookie(domainName, "sessionid=" + sessionId);
//            cookieManager.setCookie(domainName, "qf_uid=" + qfUid);
//        }
//        if (offlineDev) {
//            cookieManager.setCookie(domainName, "mmfct=1");
//        }
        /**
         *  注：临时cookie(没有expires参数的cookie)不能带有domain选项。
         当客户端发送一个http请求时，会将有效的cookie一起发送给服务器。
         如果一个cookie的domain和path参数和URL匹配，那么这个cookie就是有效的。
         一个URL中包含有domain和path，可以参
         */

        Set<String> domains = cookieMap.keySet();
        for (String domain : domains) {
            List<String> values = cookieMap.get(domain);
            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i);
                cookieManager.setCookie(domain, value);
            }
            CookieSyncManager.getInstance().sync();
            Timber.d("seted cookie---" + cookieManager.getCookie(domain));
        }
    }

    public void setCookie(String domain, List<String> values) {
        Timber.d("start set cookie...");
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        CookieSyncManager.getInstance().startSync();
//        CookieManager.allowFileSchemeCookies();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(this, true);
        }
        cookieManager.setAcceptCookie(true);
        clearCache(true);
        clearHistory();
//        boolean offlineDev = SpManager.getInstance(getContext()).getBoolean(SpKey.OFFLINE_DEV, false);
//        UserCache userCache = UserCache.getInstance(getContext());
//        if (userCache.hasLogin()) {
//            String sessionId = userCache.getSessionId();
//            String qfUid = userCache.getUserId();
//            /**
//             *  注：临时cookie(没有expires参数的cookie)不能带有domain选项。
//             当客户端发送一个http请求时，会将有效的cookie一起发送给服务器。
//             如果一个cookie的domain和path参数和URL匹配，那么这个cookie就是有效的。
//             一个URL中包含有domain和path，可以参
//             */
//            cookieManager.setCookie(domainName, "sessionid=" + sessionId);
//            cookieManager.setCookie(domainName, "qf_uid=" + qfUid);
//        }
//        if (offlineDev) {
//            cookieManager.setCookie(domainName, "mmfct=1");
//        }
        /**
         *  注：临时cookie(没有expires参数的cookie)不能带有domain选项。
         当客户端发送一个http请求时，会将有效的cookie一起发送给服务器。
         如果一个cookie的domain和path参数和URL匹配，那么这个cookie就是有效的。
         一个URL中包含有domain和path，可以参
         */

        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            cookieManager.setCookie(domain, value);
        }
        CookieSyncManager.getInstance().sync();
        Timber.d("seted cookie---" + cookieManager.getCookie(domain));
    }

    private int mLastX;
    private int mLastY;
    private ViewParent parent = null;

    public void setParent(ViewParent parent) {
        this.parent = parent;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (parent == null) return super.dispatchTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) ev.getX();
                mLastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int newX = (int) ev.getX();
                int newY = (int) ev.getY();
                int deltaX = newX - mLastX;
                int deltaY = newY - mLastY;
                if (Math.abs(deltaX) < Math.abs(deltaY)) {
                    parent.requestDisallowInterceptTouchEvent(false);
                    if (parent instanceof SwipeRefreshLayout) {
                        ((SwipeRefreshLayout) parent).setEnabled(true);
                    }
                } else {
                    if (parent instanceof SwipeRefreshLayout) {
                        ((SwipeRefreshLayout) parent).setEnabled(false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
