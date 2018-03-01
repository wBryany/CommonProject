package com.project.common_basic.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.project.common_basic.R;
import com.project.common_basic.hybrid.BaseCallJsRequest;
import com.project.common_basic.hybrid.QFHybridWebViewClient;
import com.project.common_basic.hybrid.WVJBWebViewClient;
import com.project.common_basic.manager.NearDialogFactory;
import com.project.common_basic.model.ListIconTextModel;
import com.project.common_basic.mvp.NearWebLogicView;
import com.project.common_basic.mvp.NearWebViewPresenterIml;
import com.project.common_basic.utils.ScreenUtil;
import com.project.common_basic.widget.NearWebView;
import com.project.common_basic.widget.SimplePopWindow;
import com.project.common_basic.widget.dialog.DoubleBtnConfirmDialog;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;

import java.util.List;

import timber.log.Timber;

/**
 * 通用的WebView界面
 *
 * @author yamlee
 */
public abstract class NearWebFragment<T extends NearWebViewPresenterIml> extends NearFragment<T>
        implements NearWebLogicView {
    protected NearWebView webView;
    protected View vTitle;
    protected TextView tvTitle, tvTitleRight;
    protected ImageView ivClose, ivBack;
    protected ImageView ivMenu;
    protected SimpleDraweeView sdvTitleRight;
    protected SimplePopWindow simplePopWindow;
    protected QFHybridWebViewClient webViewClient;
    protected ProgressBar pbWebLoading;
    protected LinearLayout llWebviewContainer;
    protected boolean back_delay = true;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View customCreateView = onCustomCreateView(inflater, container, savedInstanceState);
        if (customCreateView != null) {
            return customCreateView;
        }
        View view = inflater.inflate(R.layout.fragment_near_web, container, false);
        webView = (NearWebView) view.findViewById(R.id.webView);
        llWebviewContainer = (LinearLayout) view.findViewById(R.id.ll_web_view_container);
        vTitle = view.findViewById(R.id.v_title);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        ivClose = (ImageView) view.findViewById(R.id.iv_close);
        ivBack = (ImageView) view.findViewById(R.id.iv_back);
        ivMenu = (ImageView) view.findViewById(R.id.iv_menu);
        sdvTitleRight = (SimpleDraweeView) view.findViewById(R.id.sdv_titles_right);
        tvTitleRight = (TextView) view.findViewById(R.id.tv_title_right);
        pbWebLoading = (ProgressBar) view.findViewById(R.id.pb_web_view);
        return view;
    }


    /**
     * 子类onCreateView方法不能重写，自定义View需要重写此方法，可替换父类提供的默认webView
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCustomCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (simplePopWindow == null) {
                    return;
                }
                if (simplePopWindow.isShowing()) {
                    simplePopWindow.dismiss();
                } else {
                    simplePopWindow.showAsDropDown(ivMenu, ScreenUtil.dip2px(getContext(), 100), 0);
                }
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clickClose();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentBackPressed();
            }
        });
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTitle(v);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setWebView();
    }

    /**
     * 设置默认的client
     */
    private void setWebView() {
        webView.setWebChromeClient(onCreateChromeClient());
        webViewClient = onCreateWebViewClient();
        //// TODO: 1/13/17 放到子类实现
        webViewClient.enableLogging();
        webView.setWebViewClient(webViewClient);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition,
                                        String mimetype, long contentLength) {
                presenter.goToBrowser(url);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (webViewClient != null) {
            webViewClient.handleActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 返回按钮点击事件
     *
     * @return 是否消耗该事件
     */
    @Override
    protected boolean onFragmentBackPressed() {
        //由webFragment拦截返回按钮的点击事件，然后交由H5处理
                if (webView != null) {
                    String url = webView.getUrl();
                    if ((!TextUtils.isEmpty(webView.getTitle()) && webView.getTitle().equals("商户详情")) || (!TextUtils.isEmpty(url)) && url.contains("merchant-detail.html")) {
//                        webView.stopLoading();
//                        Thread.sleep(600);
                        if (back_delay) {
                            return true;
                        }
                    }
                }
            if (webViewClient != null) {
                return webViewClient.handleBackBtnClick();
            }

        return false;
    }

    /**
     * 标题点击事件
     *
     * @param view 标题视图
     */
    protected void onClickTitle(View view) {
        if (webViewClient != null) {
            BaseCallJsRequest req = new BaseCallJsRequest();
            req.func = "navClickTitle";
            webViewClient.nativeCallH5(new Gson().toJson(req), null);
        }
    }

    /**
     * 返回自定义的ChromeClient
     *
     * @return ChromeClient
     */
    public WebChromeClient onCreateChromeClient() {
        return new WebChromeClient();
    }

    /**
     * 返回自定义的WebViewClient
     *
     * @return QFHybridWebViewClient
     */
    public QFHybridWebViewClient onCreateWebViewClient() {
        return new QFHybridWebViewClient(webView, new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                // 暂时没有做操作
            }
        }, presenter);
    }

    @Override
    public void showAlert(String title, String content) {
        NearDialogFactory.getSingleBtnDialogBuilder()
                .setTitle(title)
                .setMsg(content)
                .build(getContext()).show();
    }

    @Override
    public void showAlert(String title, String content, String leftBtn, String rightBtn, DoubleBtnConfirmDialog.DoubleBtnClickListener doubleBtnClickListener) {
        NearDialogFactory.getDoubleBtnDialogBuilder()
                .setTitle(title)
                .setCancelBtnText(leftBtn)
                .setConfirmBtnText(rightBtn)
                .setMsg(content)
                .setTouchOutDismiss(false)
                .setDoubleBtnClickListener(doubleBtnClickListener)
                .build(getContext())
                .show();
    }

    @Override
    public void onChangeHeader(String title, int color, int bgColor, boolean titleClick) {
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (color != 0) {
            tvTitle.setTextColor(color);
        }
        if (bgColor != 0) {
            vTitle.setBackgroundColor(bgColor);
        }
        //添加标题是否点击标识，如果true，则在标题右侧显示一下箭头图标
        if (titleClick) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow_down);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvTitle.setCompoundDrawables(null, null, drawable, null);
        } else {
            tvTitle.setCompoundDrawables(null, null, null, null);
        }
    }

    @Override
    public void onChangeHeaderRightAsIcon(String iconUrl, final String clickUri, final boolean isClickUriAsJsFunc) {
        tvTitleRight.setVisibility(View.GONE);
        sdvTitleRight.setVisibility(View.VISIBLE);
        sdvTitleRight.setImageURI(iconUrl);
        sdvTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickUriAsJsFunc) {
                    BaseCallJsRequest req = new BaseCallJsRequest();
                    req.func = clickUri;
                    webViewClient.nativeCallH5(new Gson().toJson(req), null);
                } else {
                    presenter.clickTitleRight(clickUri);
                }
            }
        });
    }

    @Override
    public void onChangeHeaderRightAsTitle(String title, final String clickUri, final boolean isClickUriAsJsFunc) {
        sdvTitleRight.setVisibility(View.GONE);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText(title);
        tvTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickUriAsJsFunc) {
                    BaseCallJsRequest req = new BaseCallJsRequest();
                    req.func = clickUri;
                    webViewClient.nativeCallH5(new Gson().toJson(req), null);
                } else {
                    presenter.clickTitleRight(clickUri);
                }
            }
        });
    }

    @Override
    public void showHeaderMoreMenus(final List<ListIconTextModel> menus) {
        if (menus != null && menus.size() > 0) {
            ivMenu.setVisibility(View.VISIBLE);
            simplePopWindow = new SimplePopWindow(getContext());
            simplePopWindow.setSimpleContent(menus);
            simplePopWindow.setArrowRightMargin(ScreenUtil.dip2px(getContext(), 22));
            simplePopWindow.setListener(new SimplePopWindow.PopWindowItemClickListener() {
                @Override
                public void onItemClick(View view, int position, long itemId) {
                    if (position < menus.size()) {
                        presenter.clickMoreMenuItem(menus.get(position));
                    }
                }
            });
        } else {
            ivMenu.setVisibility(View.GONE);
        }
    }


    @Override
    public void showClose() {
        ivClose.setVisibility(View.VISIBLE);
    }

    @Override
    public void showHeader(String title) {
        vTitle.setVisibility(View.VISIBLE);
        //初始化标题
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
    }

    @Override
    public void hideHeader() {
        vTitle.setVisibility(View.GONE);

    }

    @Override
    public void renderTitle(String title) {
        if (getActivity() != null) {
            tvTitle.setText(title);
        }
    }

    @Override
    public void webViewGoBack() {
        webView.goBack();
    }

    @Override
    public void loadUrl(String url) {
        Timber.d("load url is %s", url);
        webView.loadUrl(url);
    }

    @Override
    public void renderWebViewLoadProgress(int newProgress) {
        pbWebLoading.setProgress(newProgress);
    }

    @Override
    public void showProgress() {
        pbWebLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        back_delay=false;
        pbWebLoading.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (llWebviewContainer != null) {
            llWebviewContainer.removeAllViews();
        }
        if (webView != null) {
            webView.destroy();
        }
        if (webViewClient != null) {
            webViewClient.onDestroy();
        }
    }

}
