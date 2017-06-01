package com.zzia.graduationproject.api;

import android.util.Log;

import rx.Subscriber;

/**
 * Created by timor.fan on 2016/9/27.
 * *项目名：CZF
 * 类描述：
 */
public abstract class MySubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {
        Log.e("onCompleted","请求结束");
    }

}
