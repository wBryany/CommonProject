package com.project.common_basic.mvp;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Essential模块基础Presenter
 *
 * @author yamlee
 */
public abstract class NearPresenterIml implements NearPresenter {
    private CompositeSubscription compositeSubscription;

    @Override
    public void addSubscription(Subscription subscription) {
        if (compositeSubscription == null) {
            compositeSubscription = new CompositeSubscription();
        }
        compositeSubscription.add(subscription);
    }


    @Override
    public void create() {
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }


    @Override
    public boolean clickErrorView() {
        return false;
    }
}
