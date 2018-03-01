package com.project.common_basic.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.common_basic.R;
import com.project.common_basic.di.HasComponent;
import com.project.common_basic.manager.NearDialogFactory;
import com.project.common_basic.mvp.NearLogicView;
import com.project.common_basic.mvp.NearPresenter;
import com.project.common_basic.utils.InputTypeUtil;
import com.project.common_basic.utils.SnackBarUtils;
import com.project.common_basic.utils.ToastUtil;
import com.project.common_basic.widget.AppBar;

import javax.inject.Inject;

import timber.log.Timber;


/**
 * Essential模块基础Fragment类
 *
 * @author yamlee
 */
@SuppressWarnings("HardCodedStringLiteral")
public class NearFragment<T extends NearPresenter> extends Fragment implements
        NearLogicView {
    private static final String TAG = "NearFragment";
    protected AppBar appBar;
    protected Dialog loadingDialog;
    public View defaultErrorView;
    public TextView tvDefaultError;
    protected View defaultProgressView;

    @Inject
    public T presenter;
    private BackHandlerInterface backHandlerInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface " +
                    "or your Host Activity must extends from BaseActivity");
        } else {
            backHandlerInterface = (BackHandlerInterface) getActivity();
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            defaultProgressView = inflater.inflate(R.layout.include_page_progress, null);
            defaultErrorView = inflater.inflate(R.layout.include_page_error, null);
            tvDefaultError = (TextView) defaultErrorView.findViewById(R.id.common_tv_error);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (presenter != null) {
            presenter.create();
        }
        if (getActivity() != null && getActivity() instanceof NearActivity) {
            NearActivity baseActivity = (NearActivity) getActivity();
            appBar = baseActivity.getAppBar();
            onInitAppBar(appBar);
        }
    }

    /**
     * 基类Fragment提供的初始化AppBar方法,供子类自动以appbar具体实现
     */
    public void onInitAppBar(AppBar appBar) {
        //default
    }

    @Override
    public void onStart() {
        super.onStart();
        backHandlerInterface.setSelectedFragment(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && getActivity() != null) {
            if (!(getActivity() instanceof BackHandlerInterface)) {
                throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
            } else {
                backHandlerInterface = (BackHandlerInterface) getActivity();
            }
            backHandlerInterface.setSelectedFragment(this);
        }
    }

    @Override
    public void showError(String errorMessage) {
        SnackBarUtils.showShortSnackBar(appBar, errorMessage);
    }

    @Override
    public void showToast(String msg) {
        if (getContext() == null) {
            return;
        }
        ToastUtil.showShort(getContext(), msg);
    }

    @Override
    public void showLoading(String msg) {
        if (getContext() == null) {
            return;
        }
        if (loadingDialog != null && loadingDialog.isShowing() && !isDetached()) {
            loadingDialog.dismiss();
        }
        loadingDialog = NearDialogFactory.getLoadingDialogBuilder()
                .setMsg(msg)
                .setTouchOutDismiss(false)
                .build(getContext());
        loadingDialog.show();
    }

    @Override
    public void showLoading() {
        showLoading(getString(R.string.loading_please_wait));
    }

    @Override
    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing() && !isDetached()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void showSoftKeyBoard() {
        InputTypeUtil.openSoftKeyBoard(getContext().getApplicationContext(), appBar);
    }

    @Override
    public void hideSoftKeyBoard() {
        if (getContext() != null) {
            InputTypeUtil.closeSoftKeyBoard(getContext().getApplicationContext(), appBar);
        }
    }

    @Override
    public void setErrorPageVisible(boolean isVisible) {
        setErrorPageVisible(isVisible, null);
    }

    @Override
    public void setErrorPageVisible(boolean isVisible, String errorText) {
        View view = getView();
        if (view != null) {
            ViewGroup rootView = (ViewGroup) view.getRootView();
            ViewGroup contentView = (ViewGroup) rootView.findViewById(R.id.layout_content);
            if (contentView == null) {
                Timber.d("content view is null");
                return;
            }
            if (isVisible) {
                if (!TextUtils.isEmpty(errorText) && tvDefaultError != null) {
                    tvDefaultError.setText(errorText);
                }
                if (contentView != defaultErrorView.getParent()) {
                    contentView.addView(defaultErrorView);
                }
                defaultErrorView.setVisibility(View.VISIBLE);
                defaultErrorView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.clickErrorView();
                    }
                });

            } else {
                contentView.removeView(defaultErrorView);
            }
        }
    }

    @Override
    public void setEmptyPageVisible(boolean isVisible) {
        setErrorPageVisible(isVisible, getString(R.string.no_data));
    }

    @Override
    public void setEmptyPageVisible(boolean isVisible, String emptyText) {
        setErrorPageVisible(isVisible, emptyText);
    }

    @Override
    public void showProgress() {
        View view = getView();
        if (view != null) {
            ViewGroup rootView = (ViewGroup) view.getRootView();
            rootView.removeView(defaultProgressView);
            defaultProgressView.setVisibility(View.VISIBLE);
            rootView.addView(defaultProgressView);
        }
    }

    @Override
    public void hideProgress() {
        View view = getView();
        if (view != null) {
            ViewGroup rootView = (ViewGroup) view.getRootView();
            defaultProgressView.setVisibility(View.GONE);
            rootView.removeView(defaultProgressView);
        }
    }

    /**
     * 基类Fragment供子类实现的处理返回键
     *
     * @return 是否处理了返回键，不再让父类处理
     */
    protected boolean onFragmentBackPressed() {
        return false;
    }

    /**
     * 设置是否显示Appbar
     */
    protected void setHasAppBar(boolean hasAppBar) {
        if (appBar == null) {
            return;
        }
        if (hasAppBar) {
            appBar.setVisibility(View.VISIBLE);
        } else {
            appBar.setVisibility(View.GONE);
        }
    }

    /**
     * 获取特定activity产生的component对象
     */
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

    /**
     * 获取到当前fragment的presenter
     */
    public T getPresenter() {
        return presenter;
    }

    public interface BackHandlerInterface {
        void setSelectedFragment(NearFragment baseFragment);
    }

}
