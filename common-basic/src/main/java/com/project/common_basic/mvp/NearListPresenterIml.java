package com.project.common_basic.mvp;

import android.content.Context;
import android.text.TextUtils;

import com.project.common_basic.R;
import com.project.common_basic.constants.LoadType;
import com.project.common_basic.reactive.ExecutorTransformer;
import com.project.common_basic.reactive.NearSubscriber;

import rx.Subscription;

/**
 * 通用ListPresenter
 *
 * @author yamlee
 */
public abstract class NearListPresenterIml<T extends NearListView, D> extends NearPresenterIml
        implements NearListPresenter<T, D> {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int INIT_PAGE_NUM = 0;
    private int mPageSize = DEFAULT_PAGE_SIZE;//每页请求的数量
    private int mPageNum = INIT_PAGE_NUM;//当前页码或者代表offset指当前加载偏移量
    protected boolean isRefreshing = false;//是否正在下拉刷新
    private boolean isLoadingMore = false;//是否正在加载更多
    private boolean hasNoMore = false;//是否没有更多数据
    private int mDataSizeWhenLastRequest = 0;//上次请求时列表数据大小
    private int loadType = LoadType.PAGE_SPLIT_TYPE;
    private boolean enableLoadMore = true;
    private Context mContext;
    private ExecutorTransformer mTransformer;

    public NearListPresenterIml(Context context, ExecutorTransformer transformer) {
        this.mContext = context;
        mTransformer = transformer;
    }

    @Override
    public void refresh() {
        refresh(INIT_PAGE_NUM);
    }

    private void refresh(int pageNum) {
        loadBefore(pageNum);
        request();
    }

    protected void loadBefore(int pageNum) {
        if (isRefreshing || isLoadingMore) return;

        isRefreshing = true;
        hasNoMore = false;
        mPageNum = pageNum;
    }

    /**
     * 真正的去请求数据
     */
    private void request() {
        Subscription subscription = generateRequestObservable(mPageSize, mPageNum)
                .compose(mTransformer.<D>transformer())
                .subscribe(new NearSubscriber<D>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        loadError(e);
                    }

                    @Override
                    public void onNext(D m) {
                        super.onNext(m);
                        loadFinish(m);
                    }
                });
        addSubscription(subscription);
    }

    /**
     * 加载出错后的页面逻辑
     */
    protected void loadError(Throwable e) {
        if (isRefreshing) {
            isRefreshing = false;
            onGetLogicView().stopRefresh();

            if (mDataSizeWhenLastRequest == 0) {
                onGetLogicView().setErrorPageVisible(true);
            }
        }

        if (isLoadingMore) {
            isLoadingMore = false;
            onGetLogicView().setLoadingMoreVisibility(false);
        }

        if (e != null) {
            String errorMsg = e.getMessage();
            if (!TextUtils.isEmpty(errorMsg)) {
                onGetLogicView().showToast(errorMsg);
            }
        }
    }

    /**
     * 加载完成后的页面逻辑
     */
    protected void loadFinish(D data) {
        if (isRefreshing) {
            onRefreshLoad(data);
            isRefreshing = false;
            onGetLogicView().stopRefresh();

            int dataSize = provideListDataSize();
            mDataSizeWhenLastRequest = dataSize;
            if (dataSize == 0) {
                onGetLogicView().setErrorPageVisible(false);
                onGetLogicView().setEmptyPageVisible(true);
            } else {
                if (loadType == LoadType.PAGE_SPLIT_TYPE) {
                    ++mPageNum;
                } else if (loadType == LoadType.OFFSET_TYPE) {
                    mPageNum = provideListDataSize();
                }
                onGetLogicView().setEmptyPageVisible(false);
                onGetLogicView().setErrorPageVisible(false);
            }
        }

        if (isLoadingMore) {
            onLoadMore(data);
            onGetLogicView().setLoadingMoreVisibility(false);
            isLoadingMore = false;

            int dataSize = provideListDataSize();
            if (dataSize > mDataSizeWhenLastRequest) {
                mDataSizeWhenLastRequest = dataSize;
                if (loadType == LoadType.PAGE_SPLIT_TYPE) {
                    ++mPageNum;
                } else if (loadType == LoadType.OFFSET_TYPE) {
                    mPageNum = provideListDataSize();
                }
            } else {
                hasNoMore = true;
                onGetLogicView().showToast(mContext.getString(R.string.no_more_data));
            }
        }
    }

    @Override
    public void loadMore() {
        if (isLoadingMore || hasNoMore || !enableLoadMore || isRefreshing) return;

        isLoadingMore = true;
        onGetLogicView().setLoadingMoreVisibility(true);
        request();
    }


    /**
     * 是否使用加载功能,默认为true
     */
    protected void enableLoadMore(boolean enable) {
        this.enableLoadMore = enable;
    }

    protected int getPageSize() {
        return mPageSize;
    }

    protected int getPageNum() {
        return mPageNum;
    }

    /**
     * 设置每次加载数据的数量
     */
    protected void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    /**
     * 加载更多模式设置,分页加载或按offset位移量加载等
     */
    protected void setLoadType(@LoadType.LoadTypeTemplate int loadType) {
        this.loadType = loadType;
    }
}
