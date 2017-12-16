package com.zzia.graduationproject.RongProvider;

import android.net.Uri;

import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.model.User;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.provider.PrivateConversationProvider;
import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by timor.fan on 2017/2/21.
 * *项目名：CZF
 * 类描述：
 */

@ConversationProviderTag(
        conversationType = "private",
        portraitPosition = 1
)

public class MyPrivateConversationProvider extends PrivateConversationProvider {
    private String name;

    @Override
    public String getTitle(final String userId) {
        if (RongUserInfoManager.getInstance().getUserInfo(userId) == null) {
            name = "";
        } else {
            //用rxjava来做处理
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    ApiClient.getApis().getUserInfo(userId)
                            .doOnNext(new Action1<BaseResp<User>>() {
                                @Override
                                public void call(BaseResp<User> resp) {
                                    if (resp.status == 200) {
                                        final User model = resp.data;
                                        //设置内容提供者
                                        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                                            @Override
                                            public UserInfo getUserInfo(String userId) {
                                                return new UserInfo(model.getUserId(), model.getNickName(), Uri.parse(model.getAvatar()));
                                            }
                                        }, true);
                                        //刷新本地内容用户信息
                                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(model.getUserId(), model.getNickName(), Uri.parse(model.getAvatar())));
                                        name = model.getNickName();
                                    }
                                }
                            }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<BaseResp<User>>() {
                                @Override
                                public void call(BaseResp<User> resp) {

                                }
                            });
                }
            });
        }
        return name;
    }
}