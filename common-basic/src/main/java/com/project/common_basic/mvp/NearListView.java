package com.project.common_basic.mvp;

/**
 * List界面抽象接口
 *
 * @author yamlee
 */
public interface NearListView extends NearLogicView {

    /**
     * 开始刷新，界面所需动作。如：下拉刷新的loading
     */
    void startRefresh();

    /**
     * 停止刷新动作
     */
    void stopRefresh();

    /**
     * 设置是否现在加载更多的loading
     */
    void setLoadingMoreVisibility(boolean visibility);
}
