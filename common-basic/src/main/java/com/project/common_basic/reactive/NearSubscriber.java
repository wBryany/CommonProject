package com.project.common_basic.reactive;

import rx.Subscriber;

/**
 * Created by yamlee on 16/1/11.
 * RxJava中的subscriber默认实现
 */
public class NearSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(T t) {

    }
}
