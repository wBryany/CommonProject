package com.project.common_basic.hybrid.jscall;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.mvp.NearWebLogicView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 功能： 关闭当前打开的所有web窗口 并跳转到指定的uri
 *
 * @author yamlee
 */
public class NavToUriProcessor extends BaseJsCallProcessor {
    private static String FUNC_NAME = "navToUri";
    private NearWebLogicView.WebLogicListener interaction;
    private Context mAppContext;

    public NavToUriProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
        interaction = componentProvider.provideWebInteraction();
        mAppContext = componentProvider.provideApplicationContext();
    }


    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equals(callData.getFunc())) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(callData.getParams());
                String uri = (String) jsonObject.get("uri");
                interaction.openUriActivity(genIntentByUri(uri));
                interaction.clearTopWebActivity();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    protected Intent genIntentByUri(String uri) {
        Intent intent = new Intent();
        if(!TextUtils.isEmpty(uri)) {
            intent.setData(Uri.parse(uri));
        }
        if(mAppContext != null) {
            intent.setPackage(mAppContext.getPackageName());
        }
        return intent;
    }


}
