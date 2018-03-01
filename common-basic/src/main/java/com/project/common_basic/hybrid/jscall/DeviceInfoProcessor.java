package com.project.common_basic.hybrid.jscall;

import android.content.Context;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.BaseJsCallResponse;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.hybrid.WVJBWebViewClient;
import com.project.common_basic.utils.ApkUtil;
import com.project.common_basic.utils.DeviceUtil;
import com.project.common_basic.utils.NetUtil;

/**
 * 获取设备基本信息
 *
 * @author yamlee
 */
public class DeviceInfoProcessor extends BaseJsCallProcessor {
    public static final String FUNC_NAME = "getDeviceInfo";
    private Context context;

    public DeviceInfoProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
        context = componentProvider.provideApplicationContext();
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
        DeviceResponse response = new DeviceResponse(context);
        callback.callback(response);
        return true;
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }

    class DeviceResponse extends BaseJsCallResponse {
        //系统
        public String os;
        //手机型号
        public String phonemodel;
        //网络类型
        public String network;
        //应用包名
        public String appname;
        //应用版本
        public String appver;
        //设备ID
        public String udid;

        public DeviceResponse(Context context) {
            this.os = DeviceUtil.getOsVersionStr();
            this.phonemodel = DeviceUtil.getDeviceName();
            this.network = NetUtil.getNetworkTypeName(context);
            this.appname = context.getPackageName();
            this.appver = ApkUtil.getVersionName(context);
            this.udid = DeviceUtil.getDeviceID(context);
        }
    }

}
