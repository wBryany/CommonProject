package com.project.common_basic.hybrid.jscall;

import android.text.TextUtils;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.BaseJsCallResponse;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.hybrid.WVJBWebViewClient;
import com.project.common_basic.mvp.NearWebLogicView;
import com.project.common_basic.widget.dialog.DoubleBtnConfirmDialog;

/**
 * Js调用原生显示弹框处理
 *
 * @author yamlee
 */
public class AlertProcessor extends BaseJsCallProcessor {
    public static final String FUNC_NAME = "alert";
    private NearWebLogicView nearWebLogicView;
    private WVJBWebViewClient.WVJBResponseCallback mCallBack;

    public AlertProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
        nearWebLogicView = componentProvider.provideWebLogicView();
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equals(callData.getFunc())) {
            AlertRequest alertRequest = convertJsonToObject(callData.getParams(), AlertRequest.class);
            if (TextUtils.isEmpty(alertRequest.btnName)) {
                nearWebLogicView.showAlert(alertRequest.title, alertRequest.msg);
            } else {
                nearWebLogicView.showAlert(alertRequest.title, alertRequest.msg, "取消", alertRequest.btnName, new DoubleBtnConfirmDialog.DoubleBtnClickListener() {
                    @Override
                    public void onConfirm() {
                        if(mCallBack != null) {
                            DoubleAlertResponse doubleAlertResponse = new DoubleAlertResponse();
                            doubleAlertResponse.ret = "ok";
                            doubleAlertResponse.status = "success";
                            mCallBack.callback(doubleAlertResponse);
                        }
                    }

                    @Override
                    public void onCancel() {
                        DoubleAlertResponse doubleAlertResponse = new DoubleAlertResponse();
                        doubleAlertResponse.ret = "ok";
                        doubleAlertResponse.status = "cancel";
                        mCallBack.callback(doubleAlertResponse);
                    }
                });
            }
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


    static class AlertRequest {
        /**
         * title : title
         * msg : 测试弹出消息
         * btnName : 两个按钮对话框右侧按钮文案
         */
        public String title;
        public String msg;
        public String btnName;
    }

    static class DoubleAlertResponse extends BaseJsCallResponse {
        public String status;//点击按钮操作  cancel : success
    }

}
