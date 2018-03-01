package com.project.common_basic.hybrid;

import android.app.Activity;
import android.content.Context;

import com.project.common_basic.manager.FileDownLoader;
import com.project.common_basic.mvp.NearWebLogicView;

import org.json.JSONObject;

/**
 * Js调用处理类中需要使用类的提供者
 *
 * @author yamlee
 */
public interface NativeComponentProvider {

    /**
     * 提供Web界面Fragment的View交互类
     *
     * @return Fragment交互接口
     */
    NearWebLogicView provideWebLogicView();

    /**
     * 提供Web界面Activity的交互类
     *
     * @return Activity交互类
     */
    NearWebLogicView.WebLogicListener provideWebInteraction();

    /**
     * 通用下载器
     *
     * @return 文件下载器
     */
    FileDownLoader provideDownloader();

    /**
     * 提供全局的Context对象
     *
     * @return 全局上下文
     */
    Context provideApplicationContext();

    /**
     * 提供全Activity上下文
     *
     * @return
     */
    Activity provideActivityContext();

    JSONObject provideJsonParam();
}
