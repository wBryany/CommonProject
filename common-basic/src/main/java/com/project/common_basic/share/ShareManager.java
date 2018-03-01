package com.project.common_basic.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.project.common_basic.R;
import com.project.common_basic.exception.NearLogger;
import com.project.common_basic.utils.ImageUtils;
import com.project.common_basic.utils.ToastUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;


/**
 * 基础分享模块工具类
 * 可分享到微信好友，微信朋友圈，新浪微博，qq好友，qq空间
 * 该工具类的数据源为一个数据Bean，包含了分享时需要用到的图片，文字，点击跳转链接等。
 * <p>
 * 页面关闭时一定要调用onDestroy()方法,释放Activity引用
 * <p>
 * Created by zcZhang on 15/4/21.
 */
public class ShareManager implements IUiListener {
    private Context context;
    private Context applicationContext;
    private static ShareManager shareManager;
    /**
     * 微信分享接口实例
     */
    private IWXAPI api;
    /**
     * QQ分享接口实例
     */
    private Tencent mTencent = null;

    private ShareManager(Context c) {
        context = c;
        applicationContext = c.getApplicationContext();
        mTencent = Tencent.createInstance(ShareKeys.getQqAppid(), applicationContext);
        api = WXAPIFactory.createWXAPI(applicationContext, ShareKeys.getWxAppId(), false);
        api.registerApp(ShareKeys.getWxAppId());
    }

    public static ShareManager getInstance(Context c) {
        if (shareManager == null) {
            shareManager = new ShareManager(c);
        }
        return shareManager;
    }

    /**
     * 初始化微信分享SDk
     *
     * @param wxAppId 微信开放平台申请的微信appId
     */
    public static void initWx(String wxAppId) {
        ShareKeys.setWxAppId(wxAppId);
    }

    /**
     * 初始化QQ分享SDK
     *
     * @param qqAppId  qq开放平台申请的appId
     * @param qqAppKey qq开放平台申请的appKey
     */
    public static void initQQ(String qqAppId, String qqAppKey) {
        ShareKeys.setQqAppid(qqAppId);
        ShareKeys.setQqAppkey(qqAppKey);
    }

    public Tencent getTencent() {
        return mTencent;
    }

    public IWXAPI getApi() {
        if (api != null) {
            api.registerApp(ShareKeys.getWxAppId());
        }
        return api;
    }

    public void onDestroy() {
        context = null;
        api = null;
        mTencent = null;
        shareManager = null;
    }

    /**
     * 分享到微信好友
     */
    public void share2WxFriend(ShareDataEntity dataModel) {
        if (api == null) {
            api = WXAPIFactory.createWXAPI(applicationContext, ShareKeys.getWxAppId(), false);
        }
        api.registerApp(ShareKeys.getWxAppId());

        // 未安装微信
        if (!api.isWXAppInstalled()) {
            ToastUtil.showLong(context, context.getString(R.string.contact_us_not_install_wx_yet));
            return;
        }

        //构造一个网页的分享类型，点击可跳转到相应的链接
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = dataModel.getLink();
        //构造分享消息，设置消息头、描述和图片字节数据 注：标题限制长度不超过512Bytes；描述内容大小应当在1KB以内；图片大小限制不超过32KB
        final WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = webpage;
        mediaMessage.title = dataModel.getTitle();
        mediaMessage.description = dataModel.getDesc();
        ImageRequest imageRequest = ImageRequest.fromUri(dataModel.getImgUrl());
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, applicationContext);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                mediaMessage.thumbData = (ImageUtils.bmpToByteArray(scaledBitmap, true));
                if (checkWxShareParams(mediaMessage)) {
                    //将构造的消息给一个微信请求，并设置分享作用域
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.message = mediaMessage;
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    api.sendReq(req);
                }
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> closeableReferenceDataSource) {
                onShareToast(context.getString(R.string.share_failed));
                NearLogger.log(closeableReferenceDataSource.getFailureCause());
            }
        }, CallerThreadExecutor.getInstance());
    }

    /**
     * 分享到微信朋友圈
     */
    public void share2WxMoments(ShareDataEntity dataModel) {
        if (api == null) {
            api = WXAPIFactory.createWXAPI(applicationContext, ShareKeys.getWxAppId(), false);
        }
        api.registerApp(ShareKeys.getWxAppId());
        // 未安装微信
        if (!api.isWXAppInstalled()) {
            ToastUtil.showLong(context, context.getString(R.string.contact_us_not_install_wx_yet));
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = dataModel.getLink();

        final WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = webpage;
        mediaMessage.title = dataModel.getTitle();
        mediaMessage.description = dataModel.getDesc();

        ImageRequest imageRequest = ImageRequest.fromUri(dataModel.getImgUrl());
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, applicationContext);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                mediaMessage.thumbData = (ImageUtils.bmpToByteArray(scaledBitmap, true));
                if (checkWxShareParams(mediaMessage)) {
                    //将构造的消息给一个微信请求，并设置分享作用域
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.message = mediaMessage;
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    api.sendReq(req);
                }
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> closeableReferenceDataSource) {
                onShareToast(context.getString(R.string.share_failed));
            }
        }, CallerThreadExecutor.getInstance());
    }


    /**
     * 分享到qq好友
     */
    public void share2QQFriend(ShareDataEntity dataModel) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, dataModel.getTitle());
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, dataModel.getDesc());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, dataModel.getLink());
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, dataModel.getImgUrl());
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getResources().getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        mTencent.shareToQQ((Activity) context, params, this);
    }

    /**
     * 分享到qq空间
     */
    public void share2QQZone(ShareDataEntity dataModel) {
        final Bundle params = new Bundle();
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add(dataModel.getImgUrl());
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, dataModel.getTitle());
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, dataModel.getDesc());
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, dataModel.getLink());
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        mTencent.shareToQzone((Activity) context, params, this);
    }

    @Override
    public void onComplete(Object o) {
        onShareToast(context.getString(R.string.share_success));
    }

    @Override
    public void onError(UiError uiError) {
        onShareToast(context.getString(R.string.share_failed));
    }

    @Override
    public void onCancel() {
        if (context != null) {
            onShareToast(context.getString(R.string.share_cancel));
        }
    }

    private boolean checkWxShareParams(WXMediaMessage mediaMessage) {
        if (mediaMessage.title.length() >= 512) {
            ToastUtil.showLong(context, context.getString(R.string.share_failed_title));
            return false;
        }

        if (mediaMessage.description.length() >= 1024) {
            ToastUtil.showLong(context, context.getString(R.string.share_failed_content));
            return false;
        }

        if (mediaMessage.thumbData.length >= 32 * 1024) {
            ToastUtil.showLong(context, context.getString(R.string.share_failed_photo));
            return false;
        }
        return true;
    }

    /**
     * 信息提示
     *
     * @param msg 提示信息
     */
    private void onShareToast(final String msg) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showShort(context, msg);
                }
            });
        }
    }
}
