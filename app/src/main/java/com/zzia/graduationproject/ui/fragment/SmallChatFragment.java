package com.zzia.graduationproject.ui.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zzia.graduationproject.R;
import com.zzia.graduationproject.adapter.MyConversationListAdapter;
import com.zzia.graduationproject.base.BaseFragment;
import com.zzia.graduationproject.ui.activity.login.LoginActivity;
import com.zzia.graduationproject.ui.activity.rongyun.MyConversationListFragment;
import com.zzia.graduationproject.ui.activity.smallchat.CampusLifeActivity;
import com.zzia.graduationproject.ui.activity.smallchat.SendTrendActivity;

import butterknife.Bind;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.model.Conversation;

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

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_record;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initListener();

    }

    private void initViews() {

        //聊天列表界面
        MyConversationListFragment fragment = new MyConversationListFragment();
        fragment.setAdapter(new MyConversationListAdapter(getActivity()));
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
                Bundle bundle=new Bundle();
                bundle.putString("campusLife","campusLife");
                go(CampusLifeActivity.class,bundle);
            }
        });
        friendTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("friendsTrend","friendsTrend");
                go(CampusLifeActivity.class,bundle);
            }
        });
    }


}
