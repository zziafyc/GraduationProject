package com.zzia.graduationproject.ui.activity.register;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.shcyd.lib.base.BaseAppManager;
import com.shcyd.lib.utils.MD5Utils;
import com.shcyd.lib.utils.StringUtils;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.model.User;
import com.zzia.graduationproject.ui.MainActivity;
import com.zzia.graduationproject.utils.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.Observable;

public class RegisterPasswordActivity extends BaseActivity {
    @Bind(R.id.arp_input_edit)
    EditText psdEdit;
    @Bind(R.id.arp_affirm_edit)
    EditText affirmPsdEdit;
    @Bind(R.id.arp_clear_input_lyt)
    RelativeLayout clearPsdLyt;
    @Bind(R.id.arp_clear_affirm_lyt)
    RelativeLayout clearAffirmPsdLyt;
    @Bind(R.id.arp_login_btn)
    TextView registerTv;
    private User mUser;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_register_password;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras.getSerializable("user") != null) {
            mUser = (User) extras.getSerializable("user");
        }
    }

    @Override
    protected void initViewsAndEvents() {
        initListener();

    }

    private void initListener() {
        psdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                if (StringUtils.isEmpty(psdEdit.getText().toString())) {
                    clearPsdLyt.setVisibility(View.GONE);
                } else {
                    if (StringUtils.isEmpty(affirmPsdEdit.getText().toString())) {
                        registerTv.setBackgroundResource(R.drawable.bg_button_white);
                        registerTv.setClickable(false);
                    } else {
                        clearPsdLyt.setVisibility(View.VISIBLE);
                        registerTv.setBackgroundResource(R.drawable.bg_button_blue);
                        registerTv.setClickable(true);
                    }

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        affirmPsdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                if (StringUtils.isEmpty(affirmPsdEdit.getText().toString())) {
                    clearAffirmPsdLyt.setVisibility(View.GONE);
                    registerTv.setBackgroundResource(R.drawable.bg_button_white);
                    registerTv.setClickable(false);
                } else {
                    clearAffirmPsdLyt.setVisibility(View.VISIBLE);
                    if (StringUtils.isEmpty(psdEdit.getText().toString())) {
                        registerTv.setBackgroundResource(R.drawable.bg_button_white);
                        registerTv.setClickable(false);
                    } else {
                        registerTv.setBackgroundResource(R.drawable.bg_button_blue);
                        registerTv.setClickable(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(psdEdit.getText().toString()) || StringUtils.isEmpty(affirmPsdEdit.getText().toString())) {
                    return;
                } else {
                    if (psdEdit.length() < 6 || psdEdit.length() > 20) {
                        showToast(getResources().getString(R.string.registerPasswordHint));
                        return;
                    } else {
                        if (psdEdit.getText().toString().equals(affirmPsdEdit.getText().toString())) {
                            register();
                        } else {
                            showToast("对不起，两次输入的密码不一致");
                        }
                    }
                }
            }
        });
    }

    //注册
    private void register() {
        mUser.setPassword(MD5Utils.MD5(psdEdit.getText().toString()));
        call(ApiClient.getApis().register(mUser), new MySubscriber<BaseResp<User>>() {


            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResp<User> resp) {
                if (resp.resultCode == Constants.RespCode.SUCCESS) {
                    if (resp.status == Constants.RespCode.SUCCESS) {
                        //注册成功,直接到主页面，保存数据到本地
                        resp.data.setPassword(psdEdit.getText().toString());
                        App.setUser(resp.data);
                        SharePreferenceUtils.put(RegisterPasswordActivity.this, "user", resp.data);
                        List<User> users = (List<User>) SharePreferenceUtils.get(RegisterPasswordActivity.this, "users", new TypeToken<List<User>>() {
                        }.getType());
                        if (users != null&&!users.isEmpty()) {
                            //用户最多保留三个
                            if (users.size() >= 3) {
                                users.remove(users.size() - 1);
                            }

                        } else {
                            users = new ArrayList<>();
                        }
                        users.add(0,resp.data);
                        SharePreferenceUtils.put(RegisterPasswordActivity.this, "users", users);
                        for (int i = 0; i < 6; i++) {
                            BaseAppManager.getInstance().getFrontActivity().finish();
                        }
                        goAndFinish(MainActivity.class);

                    } else if (resp.status == Constants.RespCode.EXIST) {
                        showToast("对不起，该用户已经注册");

                    }


                } else {
                    showToast(getResources().getString(R.string.systemError));
                }


            }
        });
    }
}
