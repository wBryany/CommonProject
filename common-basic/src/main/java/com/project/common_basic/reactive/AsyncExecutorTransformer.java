package com.project.common_basic.reactive;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 异步线程转换
 * Created by yamlee on 5/24/16.
 *
 * @author yamlee
 */
public class AsyncExecutorTransformer implements ExecutorTransformer {
    @Override
    public <T> Observable.Transformer<T, T> transformer() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.subscribeOn(CustomIoScheduler.getInstance())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
