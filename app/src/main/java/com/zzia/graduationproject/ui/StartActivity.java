package com.zzia.graduationproject.ui;

import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.model.User;
import com.zzia.graduationproject.ui.activity.login.LoginActivity;
import com.zzia.graduationproject.utils.SharePreferenceUtils;

public class StartActivity extends BaseActivity {
    private User mUser;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_start;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();

    }

    private void initViews() {
        mUser= (User) SharePreferenceUtils.get(this,"user",User.class);
        if(mUser!=null){
            App.setUser(mUser);
            goAndFinish(MainActivity.class);
        }else{
            goAndFinish(LoginActivity.class);
        }
    }
}
