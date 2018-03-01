package com.project.common_basic.hybrid;

import android.content.Intent;

/**
 * JS调原生，原始处理器接口规范
 * 相关文档链接
 *
 * @author yamlee
 */
public interface JsCallProcessor {
    /**
     * 处理JS调用
     *
     * @param callData 参数
     * @return true表示已处理，请求不会传递给下一个处理器，反之则会传递给下一个处理器
     */
    boolean process(JsCallData callData, WVJBWebViewClient.WVJBResponseCallback callback);

    /**
     * 界面跳转后再回调的处理方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    boolean onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * 获取JsBridge调用接口名称
     *
     * @return 接口名称
     */
    String getFuncName();

    /**
     * 返回按钮点击事件
     *
     * @return 如果该processor处理该事件返回true，否则返回false
     */
    boolean onBackBtnClick();

    /**
     * 当Activity执行onDestroy生命周期时回调该方法
     * 该processor持有的资源在此时释放
     */
    void destroy();
}
