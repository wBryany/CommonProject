package com.project.common_basic.share;

import android.util.SparseArray;

/**
 * Created by fengruicong on 15-11-10.
 */
public class ShareComponent {

    private static ShareComponent shareComponent;
    private SparseArray<ShareResultCallBack> shareListenerMap;

    private ShareComponent() {
        shareListenerMap = new SparseArray();
    }

    public static ShareComponent getInstance() {
        if (null == shareComponent) {
            synchronized (ShareComponent.class) {
                if (null == shareComponent) {
                    shareComponent = new ShareComponent();
                }
            }
        }
        return shareComponent;
    }

    public SparseArray<ShareResultCallBack> getShareListenerMap() {
        return shareListenerMap;
    }

    /**
     * 添加分享结果监听
     *
     * @param shareResultCallBack
     */
    public void registerShareListener(ShareResultCallBack shareResultCallBack) {
        shareListenerMap.put(shareResultCallBack.hashCode(), shareResultCallBack);
    }

    /**
     * 移除分享结果监听
     *
     * @param shareResultCallBack
     */
    public void unregisterShareListener(ShareResultCallBack shareResultCallBack) {
        shareListenerMap.remove(shareResultCallBack.hashCode());
    }

    public interface ShareResultCallBack {
        void shareSuccess();

        void shareFailed();

        void shareCancel();
    }
}
