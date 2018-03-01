package com.project.common_basic.hybrid.jscall;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.BaseJsCallResponse;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.hybrid.WVJBWebViewClient;

/**
 * Js调用设置界面返回按钮的点击
 *
 * @author yamlee
 */
public class SetNavBackProcessor extends BaseJsCallProcessor {

    public static final String FUNC_NAME = "setNavBack";
    private WVJBWebViewClient.WVJBResponseCallback mCallBack;

    public SetNavBackProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equals(callData.getFunc())) {

            return true;
        }
        return false;
    }

    @Override
    public boolean onResponse(WVJBWebViewClient.WVJBResponseCallback callback) {
        this.mCallBack = callback;
        return true;
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }

    @Override
    public boolean onBackBtnClick() {
        if (mCallBack != null) {
            BaseJsCallResponse response = new BaseJsCallResponse();
            response.ret = "ok";
            mCallBack.callback(response);
            mCallBack = null;
            return true;
        }
        return false;
    }
}
