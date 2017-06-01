package com.zzia.graduationproject.ui.activity.me;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shcyd.lib.base.BaseAppCompatActivity;
import com.shcyd.lib.base.BaseAppManager;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.ui.activity.login.LoginActivity;
import com.zzia.graduationproject.utils.SharePreferenceUtils;

import butterknife.Bind;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class SettingActivity extends BaseActivity {
    @Bind(R.id.as_exit_txt)
    TextView exitLoginTv;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initListener();


    }

    private void initViews() {
    }

    private void initListener() {
        exitLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharePreferenceUtils.put(SettingActivity.this, "user", "");
                RongIM.getInstance().clearConversations(new RongIMClient.ResultCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.e("clearConversations", "onSuccess");
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        Log.e("clearConversations", "onError");
                    }
                }, Conversation.ConversationType.GROUP, Conversation.ConversationType.PRIVATE);
                RongIM.getInstance().disconnect();
                //修改登录状态
                call(ApiClient.getApis().updateLoginState(App.getUser().getUserId()), new MySubscriber<BaseResp<Void>>() {

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResp<Void> resp) {
                        if (resp.resultCode == Constants.RespCode.SUCCESS) {
                            if (resp.status == Constants.RespCode.SUCCESS) {
                                for(int i=0;i<2;i++){
                                    BaseAppManager.getInstance().getFrontActivity().finish();
                                }
                                go(LoginActivity.class);
                            }
                        }

                    }
                });
            }
        });
    }
}
