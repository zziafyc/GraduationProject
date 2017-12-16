package com.zzia.graduationproject.ui.activity.rongyun;

import android.support.v4.app.FragmentTransaction;
import com.shcyd.lib.utils.StringUtils;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.BaseActivity;

import io.rong.imkit.fragment.ConversationFragment;

public class ConversationActivity extends BaseActivity {
    String title;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_conversation;
    }


    @Override
    protected void initViewsAndEvents() {
        title=getIntent().getData().getQueryParameter("title");
        if(!StringUtils.isEmpty(title)){
            setCustomTitle(title);
        }
        ConversationFragment conversationFragment=new ConversationFragment();
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.ac_communicationPage_fly, conversationFragment);
        transaction.commit();

    }
}
