package com.project.common_basic.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.manager.FileDownLoader;
import com.project.common_basic.model.ListIconTextModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 通用的WebView的presenter具体实现类
 *
 * @author yamlee
 */
public abstract class NearWebViewPresenterIml extends NearPresenterIml implements
        NearWebViewPresenter, NativeComponentProvider {

    public static final String ARG_URL = "arg_url";
    public static final String ARG_TITLE = "arg_title";
    public static final String ARG_HTML_PARAMS = "arg_html_params";
    public static final String ARG_IS_SHOW_APPBAR = "arg_is_show_appbar";
    public static final String ARG_WEBVIEW_CAN_BACK = "arg_webview_can_back";
    public static final String ARG_FROM_POST = "arg_from_post";

    private NearWebLogicView view;
    private NearWebLogicView.WebLogicListener listener;
    private FileDownLoader fileDownLoader;
    private Context context;

    private boolean isWebViewCanJump = false;
    private boolean fromPost = false;
    //是否显示标题栏,默认显示
    private boolean isShowTitle = true;
    //通过intent传递进来的title
    private String intentTitle;
    private String currentUrl;
    private JSONObject jsonParam;

    /**
     * 生成fragment参数
     *
     * @param currentUrl           网页地址
     * @param title                页面标题
     * @param htmlParam            native给h5的参数
     * @param showAppBar           是否显示标题栏
     * @param isWebViewBackHistory 是否支持回退
     * @param isFromPost           请求是否来自于推送
     */
    public static Bundle createArgs(String currentUrl, String title,
                                    String htmlParam, boolean showAppBar,
                                    boolean isWebViewBackHistory, boolean isFromPost) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_URL, currentUrl);
        bundle.putString(ARG_TITLE, title);
        bundle.putString(ARG_HTML_PARAMS, htmlParam);
        bundle.putBoolean(ARG_IS_SHOW_APPBAR, showAppBar);
        bundle.putBoolean(ARG_WEBVIEW_CAN_BACK, isWebViewBackHistory);
        bundle.putBoolean(ARG_FROM_POST, isFromPost);
        return bundle;
    }


    public NearWebViewPresenterIml(FileDownLoader fileDownLoader,
                                   Context context) {
        this.fileDownLoader = fileDownLoader;
        this.context = context;
    }

    public JSONObject provideJsonParam() {
        return this.jsonParam;
    }

    public void setJsonParam(JSONObject jsonParam) {
        this.jsonParam = jsonParam;
    }

    /**
     * 子类实现，返回logicView
     *
     * @return web逻辑View接口
     */
    public abstract NearWebLogicView onGetLogicView();

    /**
     * 子类实现，返回与Activity的交互接口
     *
     * @return Activity交互接口
     */
    public abstract NearWebLogicView.WebLogicListener onGetInteraction();

    @Override
    public NearWebLogicView provideWebLogicView() {
        return onGetLogicView();
    }

    @Override
    public NearWebLogicView.WebLogicListener provideWebInteraction() {
        return onGetInteraction();
    }

    @Override
    public FileDownLoader provideDownloader() {
        return fileDownLoader;
    }

    @Override
    public Context provideApplicationContext() {
        return context;
    }

    /**
     * header更多菜单点击,有些逻辑依赖到presentation层，暂时交由子类自己实现
     *
     * @param menuModel 菜单数据
     */
    public abstract void clickMoreMenuItem(ListIconTextModel menuModel);

    /**
     * header右边按钮点击，如果有"更多"按钮，此按钮在更多左边
     */
    public abstract void clickTitleRight(String clickUri);

    /**
     * 初始化操作
     */
    public void init(Bundle arguments) {
        view = onGetLogicView();
        listener = onGetInteraction();
        if (arguments != null) {
            currentUrl = arguments.getString(ARG_URL);
            intentTitle = arguments.getString(ARG_TITLE);
            String htmlParams = arguments.getString(ARG_HTML_PARAMS);
            try {
                if (!TextUtils.isEmpty(htmlParams)) {
                    jsonParam = new JSONObject(htmlParams);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isShowTitle = arguments.getBoolean(ARG_IS_SHOW_APPBAR);
            isWebViewCanJump = arguments.getBoolean(ARG_WEBVIEW_CAN_BACK);
            fromPost = arguments.getBoolean(ARG_FROM_POST);

            if (isShowTitle) {
                view.showHeader(intentTitle);
            } else {
                view.hideHeader();
            }
            //加载页面
            view.loadUrl(currentUrl);
        }
    }


    /**
     * 点击header左边的"X"按钮
     */
    public void clickClose() {
        listener.finishActivity();
    }

    /**
     * 返回键处理
     *
     * @param isWebViewCanBack webView是否能回退历史
     * @param isUserLogin      用户是否已登录
     */
    public void handleBack(boolean isWebViewCanBack, boolean isUserLogin) {
        //返回上一个页面 webview层级的返回
        if (isWebViewCanJump && isWebViewCanBack) {
            view.webViewGoBack();
            view.showClose();
        }//判断是否登录 登录则返回到首页 推送打开页面，关闭页面时需要进入首页
        else if (fromPost && isUserLogin) {
            listener.returnToMainActivity();
        }//其它情况 关闭页面
        else {
            listener.finishActivity();
        }
    }

    /**
     * webView中点击下载的处理，跳转到浏览器中
     *
     * @param url
     */
    public void goToBrowser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        listener.startOutsideActivity(intent);
    }

    @Override
    public Activity provideActivityContext() {
        return (Activity) listener;
    }
}
