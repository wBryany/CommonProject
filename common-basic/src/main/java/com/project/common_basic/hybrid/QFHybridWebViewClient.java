package com.project.common_basic.hybrid;

import android.content.Intent;

import com.project.common_basic.hybrid.jscall.UmengStatisticProcessor;
import com.tencent.smtt.sdk.WebView;

import com.project.common_basic.hybrid.jscall.AlertProcessor;
import com.project.common_basic.hybrid.jscall.CallTelProcessor;
import com.project.common_basic.hybrid.jscall.CloseProcessor;
import com.project.common_basic.hybrid.jscall.CopyProcessor;
import com.project.common_basic.hybrid.jscall.DeviceInfoProcessor;
import com.project.common_basic.hybrid.jscall.LocationProcessor;
import com.project.common_basic.hybrid.jscall.NavToUriProcessor;
import com.project.common_basic.hybrid.jscall.NotifyProcessor;
import com.project.common_basic.hybrid.jscall.OpenUriProcessor;
import com.project.common_basic.hybrid.jscall.ScanQrcodeProcessor;
import com.project.common_basic.hybrid.jscall.SetNavBackProcessor;
import com.project.common_basic.hybrid.jscall.SetNavMenuProcessor;
import com.project.common_basic.hybrid.jscall.SetNavTitleProcessor;
import com.project.common_basic.hybrid.jscall.ShareProcessor;
import com.project.common_basic.hybrid.jscall.ToastProcessor;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * @author by fengruicong on 16/12/2.
 */
public class QFHybridWebViewClient extends WVJBWebViewClient {
    /**
     * h5发送给native的消息处理器名称
     */
    private static final String NATIVE_HANDLER_NAME = "QFHybrid";
    private static final String H5_HANDLER_NAME = "QFNativeCallH5";
    private Map<String, JsCallProcessor> jsCallProcessors;

    public QFHybridWebViewClient(final WebView webView, WVJBHandler wvjbHandler,
                                 final NativeComponentProvider componentProvider) {
        super(webView, wvjbHandler);
        registerCallProcessors(componentProvider);
        registerHandler(NATIVE_HANDLER_NAME, new WVJBHandler() {
            @Override
            public void request(Object data, WVJBResponseCallback callback) {
                JSONObject jsonObject = (JSONObject) data;
                handleData(jsonObject, callback);
            }
        });
    }

    private void registerCallProcessors(NativeComponentProvider componentProvider) {
        registerJsCallProcessor(new AlertProcessor(componentProvider));
        registerJsCallProcessor(new CloseProcessor(componentProvider));
        registerJsCallProcessor(new DeviceInfoProcessor(componentProvider));
        registerJsCallProcessor(new OpenUriProcessor(componentProvider));
        registerJsCallProcessor(new ScanQrcodeProcessor(componentProvider));
        registerJsCallProcessor(new SetNavMenuProcessor(componentProvider));
        registerJsCallProcessor(new SetNavTitleProcessor(componentProvider));
        registerJsCallProcessor(new ShareProcessor(componentProvider));
        registerJsCallProcessor(new ToastProcessor(componentProvider));
        registerJsCallProcessor(new LocationProcessor(componentProvider));
        registerJsCallProcessor(new NotifyProcessor(componentProvider));
        registerJsCallProcessor(new SetNavBackProcessor(componentProvider));
        registerJsCallProcessor(new NavToUriProcessor(componentProvider));
        registerJsCallProcessor(new CopyProcessor(componentProvider));
        registerJsCallProcessor(new CallTelProcessor(componentProvider));
        registerJsCallProcessor(new UmengStatisticProcessor(componentProvider));
    }


    protected void registerJsCallProcessor(JsCallProcessor callHandler) {
        if (jsCallProcessors == null) {
            jsCallProcessors = new HashMap<>();
        }
        jsCallProcessors.put(callHandler.getFuncName(), callHandler);
    }

    private void handleData(JSONObject jsonObject, WVJBResponseCallback callback) {
        JsCallData jsCallData = new JsCallData();
        try {
            jsCallData.setFunc(jsonObject.optString("func"));
            jsCallData.setParams(jsonObject.optJSONObject("params").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        proceed(jsCallData, callback);
    }

    private void proceed(JsCallData callData, WVJBWebViewClient.WVJBResponseCallback callback) {
        if (jsCallProcessors == null) return;
        String msg;
        JsCallProcessor jsCallProcessor = jsCallProcessors.get(callData.getFunc());
        if (jsCallProcessor != null) {
            boolean handled = jsCallProcessor.process(callData, callback);
            msg = handled ? String.format("%s Processor handled js call", jsCallProcessor.getFuncName()) :
                    String.format("%s Processor have not handled target js call", jsCallProcessor.getFuncName());
            Timber.d(msg);
        } else {
            msg = String.format("No JsCallProcessor can handle jsCall %s ", callData.getFunc());
            Timber.e(msg);
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (jsCallProcessors == null) return;
        for (JsCallProcessor processor : jsCallProcessors.values()) {
            boolean handled = processor.onActivityResult(requestCode, resultCode, data);

            if (handled) {
                String msg = String.format("Processor %s handled ActivityResult",
                        processor.getFuncName());
                Timber.d(msg);
                return;
            }
        }
    }

    /**
     * 调用H5的JS函数
     *
     * @param data     传递给H5的数据，例：{"func":"js_func_name","data":"data"},其中func指示由js对应的函数处理
     * @param callback 回调类，H5可通过此参数将处理结果返回给Native
     */
    public void nativeCallH5(Object data, WVJBResponseCallback callback) {
        sendData(data, callback, H5_HANDLER_NAME);
    }

    /**
     * 由H5处理返回按钮的点击事件
     *
     * @return 如果H5响应了此事件返回true，否则返回false
     */
    public boolean handleBackBtnClick() {
        if (jsCallProcessors == null) return false;
        for (JsCallProcessor processor : jsCallProcessors.values()) {
            if(processor.onBackBtnClick()) {
                String msg = String.format("Processor %s handled BackBtnClick",
                        processor.getFuncName());
                Timber.d(msg);
                return true;
            }
        }
        return false;
    }

    /**
     * 资源销毁入口
     */
    public void onDestroy() {
        if (jsCallProcessors == null) return;
        for (JsCallProcessor processor : jsCallProcessors.values()) {
            processor.destroy();
        }
        jsCallProcessors.clear();
        jsCallProcessors = null;
    }
}
