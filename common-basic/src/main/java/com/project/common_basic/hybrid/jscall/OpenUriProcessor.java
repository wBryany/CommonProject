package com.project.common_basic.hybrid.jscall;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.mvp.NearInteraction;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 根据uri做指定的跳转动作，比如Uri是"nearmcht://view-login"就会跳转到登录界面
 * uri类型分为网页链接，如"http://"或"https://"
 * 原生页面对应链接，如"nearmcht://view-login"
 * <p>
 * 为了可以自动打开对应页面，需在manifest文件中添加schema过滤器。
 * 例：
 * 原生页面过滤器
 * <intent-filter android:label="@string/app_name">
 * <action android:name="android.intent.action.VIEW" />
 * <p>
 * <category android:name="android.intent.category.DEFAULT" />
 * <category android:name="android.intent.category.BROWSABLE" />
 * <p>
 * <data
 * android:host="view-takeout-list"
 * android:scheme="nearmcht" />
 * </intent-filter>
 * 网页Activity过滤器
 * <intent-filter android:label="@string/app_name">
 * <action android:name="android.intent.action.VIEW" />
 * <p>
 * <category android:name="android.intent.category.DEFAULT" />
 * <category android:name="android.intent.category.BROWSABLE" />
 * <p>
 * <data
 * android:scheme="http" />
 * <data
 * android:scheme="https" />
 * </intent-filter>
 * <p>
 * 对于需要特殊处理的跳转页面，如获取页面返回结果或打开第三方app的，则可在子类中复写onHandleJsRequest方法
 *
 * @author yamlee
 */
public class OpenUriProcessor extends BaseJsCallProcessor {

    public static final String FUNC_NAME = "openUri";

    public OpenUriProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equals(callData.getFunc())) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(callData.getParams());
                String uriStr = (String) jsonObject.get("uri");
                if (!TextUtils.isEmpty(uriStr)) {
                    Uri uri = Uri.parse(uriStr);
                    if (uri != null) {
                        NearInteraction interaction = componentProvider.provideWebInteraction();
                        Intent intent = new Intent();
                        intent.setPackage(componentProvider.provideApplicationContext().getPackageName());
                        intent.setData(uri);
                        interaction.startNearActivity(intent);
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }
}
