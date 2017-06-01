package com.zzia.graduationproject.ui.activity.register;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shcyd.lib.utils.RegExpValidatorUtils;
import com.shcyd.lib.utils.StringUtils;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.model.User;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterPhoneActivity extends BaseActivity {
    private static final int COUNTDOWN = 0x100;
    private static final int DEFEAT = 0x110;
    private static final int SUCCESS = 0x011;
    private static final int ERROR = 0x111;
    private static final int RESET = 0x101;

    @Bind(R.id.arp_phoneNumber_edit)
    EditText phoneNumberEdit;
    @Bind(R.id.arp_clear_phone_lyt)
    RelativeLayout clearPhoneLyt;
    @Bind(R.id.ar_getVerify_tv)
    TextView getVerificationTv;
    @Bind(R.id.arp_verifyTx_edit)
    EditText verificationEdit;
    @Bind(R.id.arp_next_btn)
    TextView nextBtn;
    //倒计时
    private int timeCount;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private EventHandler eventHandler;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DEFEAT:
                    break;
                case COUNTDOWN:
                    //表示正在倒计时
                    if (timeCount > 0) {
                        getVerificationTv.setText(String.format("%ds", timeCount));
                    } else {
                        getVerificationTv.setText(getString(R.string.reGetVerificationCode));
                    }
                    break;
                case SUCCESS:
                    break;
                case ERROR:
                    break;
                case RESET:
                    break;
            }
        }
    };

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_register;
    }

    @Override
    protected void initViewsAndEvents() {
        initListener();

    }

    private void initListener() {
        //电话号码的监听
        phoneNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!StringUtils.isEmpty(phoneNumberEdit.getText().toString())) {
                    clearPhoneLyt.setVisibility(View.VISIBLE);
                } else {
                    clearPhoneLyt.setVisibility(View.GONE);
                }

            }
        });
        //清空按钮的监听
        clearPhoneLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumberEdit.setText("");
            }
        });
        //获取验证码按钮
        getVerificationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeCount = 61;
                //提前验证一下手机号
                if (StringUtils.isEmpty(phoneNumberEdit.getText().toString())) {
                    showToast("对不起，请输入手机号码");
                    return;
                } else {
                    if (!(RegExpValidatorUtils.IsTelephone(phoneNumberEdit.getText().toString()))) {
                        showToast("对不起，请输入正确的手机号码");
                        return;
                    }
                    //1.设置定时器
                    mTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (timeCount == 0)
                                return;
                            timeCount--;
                            handler.sendEmptyMessage(COUNTDOWN);
                        }
                    };
                    mTimer = new Timer();
                    mTimer.schedule(mTimerTask, 1000, 1000);
                    //2.请求mob服务器,这个操作在点击获取验证码就一直存在
                    eventHandler = new EventHandler() {
                        @Override
                        public void afterEvent(int event, int result, Object data) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                //回调完成
                                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                    //提交验证码成功
                                    Bundle bundle = new Bundle();
                                    User user = new User();
                                    user.setTel(phoneNumberEdit.getText().toString());
                                    bundle.putSerializable("user", user);
                                    go(RegisterNameActivity.class, bundle);
                                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                    //获取验证码成功
                                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                                    //返回支持发送验证码的国家列表
                                }
                            } else {
                                ((Throwable) data).printStackTrace();
                            }
                        }
                    };
                    SMSSDK.registerEventHandler(eventHandler); //注册短信回调
                    SMSSDK.getVerificationCode("86", phoneNumberEdit.getText().toString());
                }
            }
        });
        //下一步按钮
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMSSDK.submitVerificationCode("86", phoneNumberEdit.getText().toString(), verificationEdit.getText().toString());

            }
        });
        //设置下一步按钮的监听
        verificationEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                if (StringUtils.isEmpty(verificationEdit.getText().toString())) {
                    nextBtn.setBackgroundResource(R.drawable.bg_button_white);
                    nextBtn.setClickable(false);
                } else {
                    nextBtn.setBackgroundResource(R.drawable.bg_button_blue);
                    nextBtn.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        SMSSDK.unregisterEventHandler(eventHandler);

    }
}
