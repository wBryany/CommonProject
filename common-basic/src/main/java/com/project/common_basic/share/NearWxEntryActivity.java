package com.project.common_basic.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.project.common_basic.R;
import com.project.common_basic.ui.NearActivity;
import com.project.common_basic.utils.ToastUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import timber.log.Timber;

/**
 * 微信sdk回调Activity基类
 * Created by yamlee on 2/8/17.
 *
 * @author yamlee
 */
public class NearWxEntryActivity extends NearActivity implements IWXAPIEventHandler {
    private ShareComponent shareComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareManager.getInstance(this).getApi().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        ShareManager.getInstance(this).getApi().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                this.finish();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                this.finish();
                break;
            default:
                this.finish();
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp instanceof SendAuth.Resp) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    ToastUtil.showLong(this, getString(R.string.wx_login_success));
                    Timber.d("-----------微信登陆成功---------");
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    ToastUtil.showLong(this, getString(R.string.wx_login_cancel));
                    Timber.d("-----------微信登陆取消------------");
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    ToastUtil.showLong(this, getString(R.string.wx_login_failed));
                    Timber.d("-----------微信登陆失败------------");
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    ToastUtil.showLong(this, getString(R.string.err_comm));
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    ToastUtil.showLong(this, getString(R.string.err_sent_failed));
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    ToastUtil.showLong(this, getString(R.string.err_unsupport));
                    break;
                default:
                    ToastUtil.showLong(this, getString(R.string.wx_login_failed));
                    finish();
                    break;
            }
        } else {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    Timber.d("-----------分享成功------------");
                    onShareToast(getString(R.string.share_success));
                    shareResult2Child(BaseResp.ErrCode.ERR_OK);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    Timber.d("-----------分享取消------------");
                    onShareToast(getString(R.string.share_cancel));
                    shareResult2Child(BaseResp.ErrCode.ERR_USER_CANCEL);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    Timber.d("-----------分享失败------------");
                    onShareToast(getString(R.string.share_failed));
                    shareResult2Child(BaseResp.ErrCode.ERR_AUTH_DENIED);
                    finish();
                    break;
                default:
                    finish();
                    break;
            }
        }
    }


    @SuppressWarnings("HardCodedStringLiteral")
    private void shareResult2Child(int result) {
        shareComponent = ShareComponent.getInstance();
        SparseArray<ShareComponent.ShareResultCallBack> shareListeners = shareComponent.getShareListenerMap();
        for (int i = 0; i < shareListeners.size(); i++) {
            Timber.d("分享页面------>" + shareListeners.keyAt(i));
            ShareComponent.ShareResultCallBack callBack = shareListeners.valueAt(i);
            switch (result) {
                case BaseResp.ErrCode.ERR_OK:
                    callBack.shareSuccess();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    callBack.shareCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    callBack.shareFailed();
                    break;
            }
        }
    }

    /**
     * 信息提示
     *
     * @param msg 提示信息
     */
    private void onShareToast(String msg) {
        ToastUtil.showShort(getApplicationContext(), msg);
    }

    @SuppressWarnings("HardCodedStringLiteral")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("-----------onDestroy------------");
        shareComponent = null;
        ShareManager.getInstance(this).onDestroy();
    }
}
