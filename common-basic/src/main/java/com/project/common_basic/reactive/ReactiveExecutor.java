package com.project.common_basic.reactive;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yamlee on 15/12/14.
 * @author yamlee
 */
public class ReactiveExecutor {

    /**
     *此方法弃用，请使用new AnsyncExecutorTransformer()
     */
    @Deprecated
    public static <T> Observable.Transformer<T, T> asycTransformer() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.subscribeOn(CustomIoScheduler.getInstance())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
