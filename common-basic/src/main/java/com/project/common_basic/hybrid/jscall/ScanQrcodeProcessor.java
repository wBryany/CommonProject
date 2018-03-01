package com.project.common_basic.hybrid.jscall;

import android.app.Activity;
import android.content.Intent;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.BaseJsCallResponse;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.hybrid.WVJBWebViewClient;
import com.project.common_basic.mvp.NearWebLogicView;

/**
 * Js调用扫描二维码获取内容
 *
 * @author yamlee
 */
public class ScanQrcodeProcessor extends BaseJsCallProcessor {
    public static final String ARG_QRCODE = "arg_qrcode";
    public static final String FUNC_NAME = "scanQrcode";
    private static int REQUEST_CODE = ProcessorRequestCode.SCAN_QRCODE_PROCESS;
    private NearWebLogicView.WebLogicListener listener;
    private WVJBWebViewClient.WVJBResponseCallback mCallback;

    public ScanQrcodeProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
        this.listener = componentProvider.provideWebInteraction();
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equals(callData.getFunc())) {
            listener.gotoScanQrcodeActivityForResult(REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    public boolean onResponse(WVJBWebViewClient.WVJBResponseCallback callback) {
        mCallback = callback;
        return true;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String content = data.getStringExtra("scan_result");
                ScanQrcodeResponse response = new ScanQrcodeResponse();
                response.ret = "OK";
                response.qrcode = content;
                mCallback.callback(response);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }

    private class ScanQrcodeResponse extends BaseJsCallResponse {
        public String qrcode;

    }

}
