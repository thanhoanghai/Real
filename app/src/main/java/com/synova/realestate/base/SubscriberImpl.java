
package com.synova.realestate.base;

import rx.Subscriber;

/**
 * Created by ducth on 9/22/15.
 */
public class SubscriberImpl<T> extends Subscriber<T> {

    @Override
    public void onNext(T t) {
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onCompleted() {
    }
}
