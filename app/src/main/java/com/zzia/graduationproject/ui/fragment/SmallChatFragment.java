package com.zzia.graduationproject.ui.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zzia.graduationproject.R;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseFragment;
import com.zzia.graduationproject.model.User;
import com.zzia.graduationproject.ui.activity.rongyun.MyConversationListFragment;
import com.zzia.graduationproject.ui.activity.smallchat.CampusLifeActivity;

import butterknife.Bind;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/7/8.
 * 邮箱：wgyscsf@163.com
 * 博客：http://blog.csdn.net/wgyscsf
 */
public class SmallChatFragment extends BaseFragment {

    @Bind(R.id.fr_rongYun_lyt)
    FrameLayout rongYunLty;
    @Bind(R.id.fm_ll_last_business_msg)
    LinearLayout campusDiary;
    @Bind(R.id.fm_ll_last_dynamics)
    LinearLayout friendTrend;
    boolean flag;
    ConversationListAdapter mAdapter;
    UserInfo mUserInfo;

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_record;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initListener();
        connectRongYun();

    }

    private void initViews() {

        //聊天列表界面
        MyConversationListFragment fragment = new MyConversationListFragment();
        mAdapter = fragment.onResolveAdapter(getActivity());
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();

        fragment.setUri(uri);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fr_rongYun_lyt, fragment);
        transaction.commit();
    }

    private void initListener() {
        campusDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("campusLife", "campusLife");
                go(CampusLifeActivity.class, bundle);
            }
        });
        friendTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("friendsTrend", "friendsTrend");
                go(CampusLifeActivity.class, bundle);
            }
        });
    }

    private void connectRongYun() {
        //连接融云
        if (getActivity().getApplicationInfo().packageName.equals(App.getCurProcessName(getActivity()))) {
            if (App.getUser() != null) {
                if (!TextUtils.isEmpty(App.getUser().getToken())) {
                    User user = App.getUser();
                    //用rxjava来做处理
                    Observable.create(new Observable.OnSubscribe<String>() {
                        @Override
                        public void call(final Subscriber<? super String> subscriber) {
                            RongIM.connect(App.getUser().getToken(), new RongIMClient.ConnectCallback() {
                                @Override
                                public void onTokenIncorrect() {
                                    //TODO: 重新获取token，然后连接融云

                                }

                                @Override
                                public void onSuccess(String userId) {
                                    subscriber.onNext(userId);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    showToast("融云客户端错误");
                                }
                            });
                        }
                    }).map(new Func1<String, Void>() {
                        @Override
                        public Void call(String s) {
                            //登录成功后，设置内容提供者
                            RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                                @Override
                                public UserInfo getUserInfo(String userId) {
                                    try {
                                        call2(ApiClient.getApis().getUserInfo(userId), new MySubscriber<BaseResp<User>>() {
                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                            @Override
                                            public void onNext(final BaseResp<User> resp) {
                                                if (resp.status == 200) {
                                                    final User model = resp.data;
                                                    mUserInfo = new UserInfo(model.getUserId(), model.getNickName(), Uri.parse(model.getAvatar()));

                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return mUserInfo;
                                }
                            }, true);
                            return null;
                        }
                    }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Void>() {
                                @Override
                                public void call(Void aVoid) {
                                    RongIM.getInstance().refreshUserInfoCache(mUserInfo);

                                }
                            });

                }
            }
        }
    }
}
