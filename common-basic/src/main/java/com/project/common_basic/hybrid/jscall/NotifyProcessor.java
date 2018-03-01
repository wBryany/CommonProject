package com.project.common_basic.hybrid.jscall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.BaseJsCallResponse;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.hybrid.WVJBWebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 * 向H5提供的消息通知功能
 * 参数：name 通知名
 * type 注册/发送通知类型，固定为register/post
 * <p>
 * Created by joye on 2017/3/8.
 */

public class NotifyProcessor extends BaseJsCallProcessor {
    private static final String FUNC_NAME = "webNotify";
    private static final String NOTIFY_REGISTER = "register";
    private static final String NOTIFY_POST = "post";
    private Map<String, Notify> mCallBacks;
    private String mTmpNotifyName;
    private boolean mTmpIsRegNotify = false;
    private LocalBroadcastManager mLBM;
    private JsBroadCastReceiver mJBR;

    public NotifyProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
        this.mCallBacks = new HashMap<>();
        Context context = componentProvider.provideApplicationContext();
        this.mLBM = LocalBroadcastManager.getInstance(context);
        this.mJBR = new JsBroadCastReceiver();
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equalsIgnoreCase(callData.getFunc())) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(callData.getParams());
                mTmpNotifyName = jsonObject.getString("name");
                String notifyType = jsonObject.getString("type");
                mTmpIsRegNotify = NOTIFY_REGISTER.equals(notifyType);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void sendNotify() {
        Intent intent = new Intent(mTmpNotifyName);
        mLBM.sendBroadcast(intent);
    }

    private void registerNotify() {
        mLBM.unregisterReceiver(mJBR);
        IntentFilter intentFilter = new IntentFilter();
        if (mCallBacks != null) {
            Set<String> keys = mCallBacks.keySet();
            for(String notifyName : keys) {
                intentFilter.addAction(notifyName);
            }
        }

        mLBM.registerReceiver(mJBR, intentFilter);
    }

    @Override
    public boolean onResponse(WVJBWebViewClient.WVJBResponseCallback callback) {
        Notify notify = new Notify();
        notify.notifyName = mTmpNotifyName;
        if (mTmpIsRegNotify) {
            //注册通知时，保留回调的引用，不立即返回
            notify.notifyCallBack = callback;
            if (mCallBacks != null) {
                Timber.d("register notify from h5: notify name is %s, callback id is %s", mTmpNotifyName, callback);
                mCallBacks.put(mTmpNotifyName, notify);
                //注册通知
                registerNotify();
            }
        } else {
            //发送通知
            sendNotify();
            Timber.d("send notify from h5: notify name is %s, callback id is %s", mTmpNotifyName, callback);
            //发送通知结果返回
            notify.ret = "ok";
            callback.callback(notify);
        }
        return true;
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(mJBR);
        mLBM = null;
        mJBR = null;
        mCallBacks.clear();
    }

    //将通知回调给h5
    private void onNotify(String notifyName) {
        if (mCallBacks != null) {
            Notify notify = mCallBacks.get(notifyName);
            if (notify != null) {
                Timber.d("notify to h5: notify name is %s, callback id is %s", notifyName, notify.notifyCallBack);
                notify.notifyName = notifyName;
                notify.ret = "ok";
                notify.notifyCallBack.callback(notify);
            }
        }
    }

    private class JsBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                onNotify(intent.getAction());
            }
        }
    }

    private class Notify extends BaseJsCallResponse {
        public String notifyName;
        public WVJBWebViewClient.WVJBResponseCallback notifyCallBack;
    }
}
