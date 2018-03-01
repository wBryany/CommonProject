package com.project.common_basic.hybrid.jscall;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.hybrid.WVJBWebViewClient;

/**
 * Js调用原生下载
 *
 * @author yamlee
 */
public class DownloadProcessor extends BaseJsCallProcessor {

    public static final String FUNC_NAME = "download";

    public DownloadProcessor(NativeComponentProvider componentProvider) {
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
        return super.onResponse(callback);
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }
}
