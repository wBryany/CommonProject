package com.project.common_basic.reactive;

import rx.Observable;

/**
 * Created by yamlee on 5/24/16.
 * RxJava线程调度转换器
 * @author yamlee
 */
public interface ExecutorTransformer {
    /**
     * RxJava中使用compose操作符使用的transformer
     *
     * @param <T>
     * @return
     */
    <T> Observable.Transformer<T, T> transformer();
}
