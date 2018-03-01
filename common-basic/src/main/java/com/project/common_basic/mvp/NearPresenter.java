/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.project.common_basic.mvp;

import rx.Subscription;

/**
 * MVP模式中的基础Presenter接口
 *
 * @author yamlee
 */
public interface NearPresenter {

    /**
     * fragment或者activity声明周期在onCreate时调用此方法
     */
    void create();

    /**
     * fragment或者activity声明周期在onResume时调用此方法
     */
    void resume();

    /**
     *fragment或者activity声明周期在onPause时调用此方法
     */
    void pause();

    /**
     * fragment或者activity声明周期在onDestroy时调用此方法
     */
    void destroy();

    /**
     * 异步请求加入管理，方便View Destroy时统一unSubscribe
     */
    void addSubscription(Subscription subscription);

    /**
     * 网络错误或空数据界面点击刷新操作
     */
    boolean clickErrorView();

}
