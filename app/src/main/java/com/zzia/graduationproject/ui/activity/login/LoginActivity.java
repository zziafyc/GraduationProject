package com.zzia.graduationproject.ui.activity.login;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.shcyd.lib.adapter.BasicAdapter;
import com.shcyd.lib.adapter.ViewHolder;
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
import com.zzia.graduationproject.ui.activity.register.RegisterPhoneActivity;
import com.zzia.graduationproject.utils.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.al_phoneNumber_edit)
    EditText phoneEdit;
    @Bind(R.id.al_code_label_lyt)
    RelativeLayout phoneLyt;
    @Bind(R.id.al_password_edit)
    EditText passwordEdit;
    @Bind(R.id.al_clear_password_lyt)
    RelativeLayout clearPasswordLyt;
    @Bind(R.id.al_clear_phone_lyt)
    RelativeLayout clearPhoneLyt;
    @Bind(R.id.al_moreuser_img)
    ImageView moreImg;
    @Bind(R.id.al_moreuser_lyt)
    RelativeLayout moreUserLyt;
    @Bind(R.id.al_register_txt)
    TextView registerTv;
    @Bind(R.id.al_login_btn)
    TextView loginBtn;
    @Bind(R.id.al_weixin_lyt)
    RelativeLayout weiXinLoginRv;
    @Bind(R.id.al_qq_lyt)
    LinearLayout qqLoginLv;
    @Bind(R.id.al_verify_lyt)
    RelativeLayout verifyLoginRv;
    User mUser = new User();


    //历史登录用户列表
    private List<User> userHistoryList;
    private PopupWindow userListPop;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initPop();
        initListener();

    }

    private void initViews() {
        if (phoneEdit.getText().toString().trim().length() > 0) {
            CharSequence text = phoneEdit.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        }
        if (passwordEdit.getText().toString().trim().length() > 0) {
            CharSequence text = passwordEdit.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        }

        userHistoryList = (List<User>) SharePreferenceUtils.get(this, "users", new TypeToken<List<User>>() {
        }.getType());
        if (userHistoryList != null && !userHistoryList.isEmpty()) {
            moreUserLyt.setVisibility(View.VISIBLE);
            phoneEdit.setText(userHistoryList.get(0).getTel());
            phoneEdit.setSelection(userHistoryList.get(0).getTel().length());
            passwordEdit.setText(userHistoryList.get(0).getPassword());

        } else {
            userHistoryList = new ArrayList<>();
        }

    }

    //初始化显示历史登录用的popupwindow
    private void initPop() {
        View view = getLayoutInflater().inflate(R.layout.popup_user_list, null);
        ListView userListView = (ListView) view.findViewById(R.id.pul_user_list);
        final List<String> phones = new ArrayList<>();
        if (userHistoryList != null && !userHistoryList.isEmpty()) {
            for (int i = 0; i < userHistoryList.size(); i++) {
                phones.add(userHistoryList.get(i).getTel());
            }
        }
        final BasicAdapter<String> adapter = new BasicAdapter<String>(this, R.layout.item_user_history) {
            @Override
            protected void render(ViewHolder holder, String item, final int position) {
                if (position == getCount() - 1) {
                    holder.setBackgroundImage(R.id.iu_root_lyt, R.color.white);
                } else {
                    holder.setBackgroundImage(R.id.iu_root_lyt, R.drawable.bottom_border);
                }
                holder.setText(R.id.iu_phone_txt,item);
                holder.onClick(R.id.iu_delete_lyt, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userHistoryList.remove(position);
                        phones.remove(position);
                        SharePreferenceUtils.put(LoginActivity.this, "users", userHistoryList);
                        notifyDataSetChanged();
                    }
                });
            }
        };
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                phoneEdit.setText(userHistoryList.get(position).getTel());
                phoneEdit.setSelection(userHistoryList.get(position).getTel().length());
                passwordEdit.setText(userHistoryList.get(position).getPassword());
                userListPop.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setDataList(phones);
        userListView.setAdapter(adapter);
        userListPop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        userListPop.setTouchable(true);
        userListPop.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        userListPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                moreImg.setBackgroundResource(R.drawable.fabuxuanshang_xiangxiazhankai_nobg_n);
            }
        });
    }

    private void initListener() {
        //更多用户pop显示
        moreUserLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userListPop.isShowing()) {
                    userListPop.dismiss();
                } else {
                    moreImg.setBackgroundResource(R.drawable.fabuxuanshang_xiangshangzhankai);
                    userListPop.showAsDropDown(phoneLyt);
                }
            }
        });
        //注册新用户
        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        //账号密码登录
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        //微信登录
        weiXinLoginRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weiXinLogin();
            }
        });

        //qq登录
        qqLoginLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qqLogin();
            }
        });

        //验证码登录
        verifyLoginRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyLogin();
            }
        });

        //输入框监听
        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                if (StringUtils.isEmpty(phoneEdit.getText().toString())) {
                    clearPhoneLyt.setVisibility(View.GONE);
                } else {
                    clearPhoneLyt.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                if (StringUtils.isEmpty(passwordEdit.getText().toString())) {
                    clearPasswordLyt.setVisibility(View.GONE);
                } else {
                    clearPasswordLyt.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //清除监听
        clearPhoneLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneEdit.setText("");
            }
        });
        clearPasswordLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordEdit.setText("");
            }
        });


    }

    private void register() {
        go(RegisterPhoneActivity.class);
    }

    private void login() {
        String tel = phoneEdit.getText().toString();
        String pwd = passwordEdit.getText().toString();
        if (StringUtils.isEmpty(tel) || StringUtils.isEmpty(pwd)) {
            showToast(getResources().getString(R.string.inputMessage));
            return;
        } else {
            mUser.setTel(tel);
            mUser.setPassword(MD5Utils.MD5(pwd));
        }

        call(ApiClient.getApis().login(mUser), new MySubscriber<BaseResp<User>>() {
            @Override
            public void onError(Throwable e) {
                showToast(getResources().getString(R.string.systemError));
            }

            @Override
            public void onNext(final BaseResp<User> resp) {
                if (resp.resultCode == Constants.RespCode.SUCCESS) {
                    if (resp.status == Constants.RespCode.SUCCESS) {
                        //用户登录成功
                        resp.data.setPassword(passwordEdit.getText().toString());
                        SharePreferenceUtils.put(LoginActivity.this, "user", resp.data);
                        App.setUser(resp.data);
                        List<User> users = (List<User>) SharePreferenceUtils.get(LoginActivity.this, "users", new TypeToken<List<User>>() {
                        }.getType());
                        if (users != null) {
                            //用户最多保留三个
                            if (users.size() >= 3) {
                                users.remove(users.size() - 1);
                            }
                        } else {
                            users = new ArrayList<>();
                        }
                        boolean flag = false;
                        if (users != null && !users.isEmpty()) {
                            for (User user : users) {
                                if (user.getTel().equals(resp.data.getTel())) {
                                    users.remove(user);
                                    users.add(0, resp.data);
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if (!flag) {
                            users.add(0,resp.data);
                        }
                        SharePreferenceUtils.put(LoginActivity.this, "users", users);
                        goAndFinish(MainActivity.class);

                    } else if (resp.status == Constants.RespCode.ONLINE) {
                        showToast("对不起，该用户已在其他终端登录");
                    }
                }
            }


        });


    }

    private void weiXinLogin() {

    }

    private void qqLogin() {

    }

    private void verifyLogin() {

    }
}
