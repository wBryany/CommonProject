package com.project.common_basic.hybrid.jscall;

import android.content.Context;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 友盟统计
 *
 * @author yamlee
 */
public class UmengStatisticProcessor extends BaseJsCallProcessor {

    public static final String FUNC_NAME = "event";
    public UmengStatisticProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {

        Context context = componentProvider.provideApplicationContext();
        if (FUNC_NAME.equals(callData.getFunc())) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(callData.getParams());
                String eventId = (String) jsonObject.get("eventId");
                MobclickAgent.onEvent(context,eventId);
                return true;
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
