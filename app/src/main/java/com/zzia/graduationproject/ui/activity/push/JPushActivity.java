package com.zzia.graduationproject.ui.activity.push;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.BaseActivity;

import butterknife.Bind;
import cn.jpush.android.api.JPushInterface;

public class JPushActivity extends BaseActivity {

    @Bind(R.id.aj_tv_title)
    TextView titleTv;
    @Bind(R.id.aj_tv_content)
    TextView contentTv;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_jpush;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
    }

    private void initViews() {
        setCustomTitle("极光推送跳转界面");
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            String title = null;
            String content = null;
            if (bundle != null) {
                title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                content = bundle.getString(JPushInterface.EXTRA_ALERT);
            }
            titleTv.setText(title);
            contentTv.setText(content);
        }
    }
}
