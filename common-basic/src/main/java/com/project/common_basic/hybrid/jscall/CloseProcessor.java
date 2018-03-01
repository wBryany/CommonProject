package com.project.common_basic.hybrid.jscall;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.mvp.NearWebLogicView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Js调用界面关闭
 *
 * @author yamlee
 */
public class CloseProcessor extends BaseJsCallProcessor {

    public static final String FUNC_NAME = "close";
    private NearWebLogicView.WebLogicListener nearInteraction;

    public CloseProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equals(callData.getFunc())) {
            nearInteraction = componentProvider.provideWebInteraction();
            try {
                JSONObject jsonObject = new JSONObject(callData.getParams());
                String type = jsonObject.optString("type");
                if ("1".equals(type)) {
                    nearInteraction.finishActivity();
                } else if ("2".equals(type)) {
                    nearInteraction.clearTopWebActivity();
                } else {
                    nearInteraction.finishActivity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }
}