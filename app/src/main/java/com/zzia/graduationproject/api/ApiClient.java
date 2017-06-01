package com.zzia.graduationproject.api;

import com.zzia.graduationproject.base.Constants;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tiangongyipin on 16/2/21.
 */
public class ApiClient {

    private static final int DEFAULT_TIMEOUT = 30;

    private static Apis apis;

    private ApiClient() {
    }

    public static Apis getApis() {
        if (apis == null) {
            synchronized (ApiClient.class) {
                if (apis == null) {
                    OkHttpClient httpClient = new OkHttpClient.Builder()
                            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES).build();
                    Retrofit restAdapter = new Retrofit.Builder()
                            .client(httpClient)
                            .baseUrl(Constants.Urls.API_URL)     //Constants.Urls.API_URL
                            //增加返回值为Gson的支持(以实体类返回)
                            .addConverterFactory(GsonConverterFactory.create())
                            //增加返回值为Oservable<T>的支持
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();
                    apis = restAdapter.create(Apis.class);
                }
            }
        }
        return apis;
    }

    public static Subscription call(Observable observable, MySubscriber subscriber) {
        return observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }
}
