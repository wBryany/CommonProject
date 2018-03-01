package com.project.common_basic.mvp;

import rx.Observable;

/**
 * 基础列表Presenter接口
 *
 * @author yamlee
 */
public interface NearListPresenter<T extends NearListView, D> extends NearPresenter {
    /**
     * 刷新界面对应的操作，第一次进入界面或者用户手动刷新
     */
    void refresh();

    /**
     * 加载更多对应的操作
     */
    void loadMore();

    /**
     * 子类实现返回逻辑控制view
     */
     T onGetLogicView();

    /**
     * 通知子类刷新完成,第一次加载或者下拉刷新回调
     */
    void onRefreshLoad(D data);

    /**
     * 通知子类加载更多完成，点击加载更多的回调
     */
    void onLoadMore(D data);

    /**
     * 具体的请求由子类构造,父类提供页面大小和页码
     */
    Observable<D> generateRequestObservable(int pageSize, int pageNum);

    /**
     * 当前已加载的数据数量
     */
    int provideListDataSize();
}
