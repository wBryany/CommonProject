package com.project.common_basic.hybrid.jscall;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.project.common_basic.R;
import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.hybrid.WVJBWebViewClient;
import com.project.common_basic.mvp.NearWebLogicView;
import com.project.common_basic.share.ShareComponent;
import com.project.common_basic.share.ShareDataEntity;
import com.project.common_basic.share.ShareDialog;
import com.project.common_basic.share.ShareManager;
import com.project.common_basic.utils.InputTypeUtil;
import com.project.common_basic.utils.ToastUtil;

/**
 * Js调用分享界面
 *
 * @author yamlee
 */
public class ShareProcessor extends BaseJsCallProcessor {
    public static final String FUNC_NAME = "share";
    private NearWebLogicView.WebLogicListener listener;
    private WVJBWebViewClient.WVJBResponseCallback callback;
    private Context mContext;
    private ShareComponent shareComponent;
    private ShareComponent.ShareResultCallBack shareResultCallBack = new ShareComponent.ShareResultCallBack() {
        @Override
        public void shareSuccess() {
            if (callback != null) {
                ShareResponse shareResponse = new ShareResponse();
                shareResponse.ret = "OK";
                callback.callback(shareResponse);
            }
        }

        @Override
        public void shareFailed() {
            if (callback != null) {
                ShareResponse shareResponse = new ShareResponse();
                shareResponse.ret = "Fail";
                callback.callback(shareResponse);
            }
        }

        @Override
        public void shareCancel() {
            if (callback != null) {
                ShareResponse shareResponse = new ShareResponse();
                shareResponse.ret = "Canceled";
                callback.callback(shareResponse);
            }
        }
    };

    public ShareProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
        listener = componentProvider.provideWebInteraction();
        shareComponent = ShareComponent.getInstance();
        mContext = componentProvider.provideApplicationContext();
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equals(callData.getFunc())) {
            ShareRequest shareRequest = convertJsonToObject(callData.getParams(),
                    ShareRequest.class);
            share(shareRequest);
            return true;
        }
        return false;
    }

    /**
     * 分享
     *
     * @param shareInfo 分享内容
     */
    private void share(ShareRequest shareInfo) {
        shareComponent.registerShareListener(shareResultCallBack);
        Activity activity = (Activity) listener;
        final ShareManager shareUtil = ShareManager.getInstance(activity);
        final ShareDataEntity shareDataEntity = new ShareDataEntity();
        shareDataEntity.setTitle(shareInfo.title);
        shareDataEntity.setDesc(shareInfo.desc);
        if (TextUtils.isEmpty(shareInfo.icon)) {
            String defaultUrl = "http://near.m1img.com/op_upload/12/146520507436.png";
            shareDataEntity.setImgUrl(defaultUrl);
        } else {
            shareDataEntity.setImgUrl(shareInfo.icon);
        }

        shareDataEntity.setLink(shareInfo.url);
        ShareDialog.showDialog(activity, new ShareDialog.ShareItemClickListener() {
            @Override
            public void clickWeixinFriend() {
                shareUtil.share2WxFriend(shareDataEntity);
            }

            @Override
            public void clickWeixinMoments() {
                shareUtil.share2WxMoments(shareDataEntity);
            }

            @Override
            public void clickSinaWeibo() {
            }

            @Override
            public void clickQQFriend() {
                shareUtil.share2QQFriend(shareDataEntity);
            }

            @Override
            public void clickQQZone() {
                shareUtil.share2QQZone(shareDataEntity);
            }

            @Override
            public void clickCopyLink() {
                InputTypeUtil.saveClipBoard(mContext, shareDataEntity.getLink());
                ToastUtil.showLong(mContext, mContext.getString(R.string.copy_success));
            }
        });
    }

    @Override
    public boolean onResponse(WVJBWebViewClient.WVJBResponseCallback callback) {
        this.callback = callback;
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shareComponent.unregisterShareListener(shareResultCallBack);
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }

    private class ShareRequest {
        public String title;
        public String desc;
        public String url;
        public String icon;
    }

    private class ShareResponse {
        public String ret;
    }
}
