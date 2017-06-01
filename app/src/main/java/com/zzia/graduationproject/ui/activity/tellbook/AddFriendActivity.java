package com.zzia.graduationproject.ui.activity.tellbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shcyd.lib.utils.StringUtils;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.Apis;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.event.StringEvent;
import com.zzia.graduationproject.model.Friends;
import com.zzia.graduationproject.model.User;
import com.zzia.graduationproject.utils.ImageUtils;

import butterknife.Bind;
import io.rong.eventbus.EventBus;

public class AddFriendActivity extends BaseActivity {
    @Bind(R.id.adf_iv_pic)
    ImageView picIv;
    @Bind(R.id.adf_tv_name)
    TextView nameTv;
    @Bind(R.id.adf_tv_info)
    TextView infoTv;
    @Bind(R.id.adf_verifyLabel_tv)
    TextView verifyLabelTv;
    @Bind(R.id.adf_edtTxt_verifyInfo)
    EditText verifyInfoEdtTxt;
    @Bind(R.id.adf_edtTxt_remark)
    EditText remarkEdtTxt;
    @Bind(R.id.adf_tv_addFriend)
    TextView addFriendTv;
    @Bind(R.id.adf_addFriend_lay)
    LinearLayout addLayout;

    String result;
    Friends friend = new Friends();
    User searchUser;
    Friends friendFromMessage;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_add_friend;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        result = extras.getString(CodeUtils.RESULT_STRING);
        searchUser = (User) extras.getSerializable("friend");
        friendFromMessage = (Friends) extras.getSerializable("friendFromMessage");
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initListener();
    }

    private void initViews() {
        setCustomTitle("添加好友");
        verifyInfoEdtTxt.setText("我是" + App.getUser().getNickName() + ",想加你为好友");
        if (verifyInfoEdtTxt.getText().toString().trim().length() > 0) {
            CharSequence text = verifyInfoEdtTxt.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        }
        if (result != null) {
            //通过用户的id，获取该用户的信息
            call(ApiClient.getApis().getUserInfo(result), new MySubscriber<BaseResp<User>>() {
                @Override
                public void onError(Throwable e) {
                    showToast(getResources().getString(R.string.systemError));
                }

                @Override
                public void onNext(BaseResp<User> resp) {
                    if (resp.resultCode == Constants.RespCode.SUCCESS) {
                        //成功，开始更新界面
                        if (resp.status == Constants.RespCode.SUCCESS) {
                            User user = resp.data;
                            ImageUtils.setCornerImage(picIv, user.getAvatar());
                            nameTv.setText(user.getNickName());
                            infoTv.setText(user.getAddress());
                            remarkEdtTxt.setText(user.getNickName());
                            friend.setApplicationObjectId(user.getUserId());
                        } else {
                            showToast("该用户已不存在");
                        }
                    }

                }

            });

        }
        //搜索的好友
        if (searchUser != null) {
            ImageUtils.setCornerImage(picIv, searchUser.getAvatar());
            nameTv.setText(searchUser.getNickName());
            infoTv.setText(searchUser.getAddress());
            remarkEdtTxt.setText(searchUser.getNickName());
            friend.setApplicationObjectId(searchUser.getUserId());
        }
        //来自验证信息
        if (friendFromMessage != null) {
            ImageUtils.setCornerImage(picIv, friendFromMessage.getApplicationUser().getAvatar());
            nameTv.setText(friendFromMessage.getApplicationUser().getNickName());
            infoTv.setText(friendFromMessage.getApplicationUser().getAddress());
            remarkEdtTxt.setText(friendFromMessage.getApplicationUser().getNickName());
            verifyLabelTv.setText("对方的验证信息");
            verifyInfoEdtTxt.setText(friendFromMessage.getApplicationDes());
            if (verifyInfoEdtTxt.getText().toString().trim().length() > 0) {
                CharSequence text = verifyInfoEdtTxt.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());
                }
            }
            if (remarkEdtTxt.getText().toString().trim().length() > 0) {
                CharSequence text = remarkEdtTxt.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());
                }
            }
            verifyInfoEdtTxt.clearFocus();
            verifyInfoEdtTxt.setFocusable(false);
            verifyInfoEdtTxt.setEnabled(false);
            if (friendFromMessage.getState() == 1) {
                addLayout.setBackgroundResource(R.color.blue);
                addFriendTv.setText("修改备注");

            }else{
                addLayout.setBackgroundResource(R.color.blue);
                addLayout.setClickable(true);
                addFriendTv.setText("通过验证");

            }
        }

    }

    private void initListener() {
        if (friendFromMessage == null) {
            addFriendTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (StringUtils.isEmpty(verifyInfoEdtTxt.getText().toString())) {
                        showToast("验证消息不能为空");
                        return;
                    }
                    if (StringUtils.isEmpty(remarkEdtTxt.getText().toString())) {
                        showToast("备注不能为空");
                        return;
                    }
                    if (friend.getApplicationObjectId() == null) {
                        showToast("对不起，该用户已不存在！");
                        return;
                    }

                    friend.setApplicationId(App.getUser().getUserId());
                    friend.setApplicationDes(verifyInfoEdtTxt.getText().toString());
                    friend.setRemark1(remarkEdtTxt.getText().toString());
                    call(ApiClient.getApis().addFriend(friend), new MySubscriber<BaseResp<Void>>() {
                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(BaseResp<Void> resp) {
                            if (resp.resultCode == Constants.RespCode.SUCCESS) {
                                if (resp.status == Constants.RespCode.SUCCESS) {
                                    showToast("申请已发出，请耐心等待");
                                    finish();
                                } else {
                                    showToast("申请发送失败");
                                }
                            }
                        }

                    });
                }
            });
        } else {
            addFriendTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (StringUtils.isEmpty(remarkEdtTxt.getText().toString())) {
                        showToast("备注不能为空");
                        return;
                    }
                    if (friendFromMessage.getState() == 0) {
                        call(ApiClient.getApis().changeMessageState(String.valueOf(friendFromMessage.getId()), remarkEdtTxt.getText().toString()), new MySubscriber<BaseResp<Void>>() {
                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(BaseResp<Void> resp) {
                                if (resp.resultCode == Constants.RespCode.SUCCESS) {
                                    if (resp.status == Constants.RespCode.SUCCESS) {
                                        //成功了需要刷新好友列表
                                        EventBus.getDefault().post(new StringEvent("updateFriendsList"));
                                        finish();
                                    }
                                } else {
                                    //失败了是需要回滚的，这里没有处理
                                }
                            }


                        });
                    }else{
                        //已经是好友了，此时就要执行修改备注的接口
                        call(ApiClient.getApis().changeRemark(String.valueOf(friendFromMessage.getId()), remarkEdtTxt.getText().toString()), new MySubscriber<BaseResp<Void>>() {
                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(BaseResp<Void> resp) {
                                if (resp.resultCode == Constants.RespCode.SUCCESS) {
                                    if (resp.status == Constants.RespCode.SUCCESS) {
                                        //成功了需要刷新好友列表
                                        EventBus.getDefault().post(new StringEvent("updateFriendsList"));
                                        finish();
                                    }
                                } else {
                                    //失败了是需要回滚的，这里没有处理
                                }
                            }


                        });
                    }
                }
            });
        }
    }
}
