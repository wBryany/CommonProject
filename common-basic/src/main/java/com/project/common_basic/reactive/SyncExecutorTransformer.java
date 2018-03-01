package com.project.common_basic.reactive;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * 同步线程调度转换，在RxJava的Observable.compose()方法中使用，使用此Transformer后
 * Subscriber中的回调和Observable中的方法运行在同一线程
 *
 * @author yamlee
 */
public class SyncExecutorTransformer implements ExecutorTransformer {
    @Override
    public <T> Observable.Transformer<T, T> transformer() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.subscribeOn(Schedulers.immediate())
                        .observeOn(Schedulers.immediate());
            }
        };
    }
}
