package com.zzia.graduationproject.ui.fragment;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseFragment;
import com.zzia.graduationproject.event.StringEvent;
import com.zzia.graduationproject.ui.activity.me.AvatarSettingActivity;
import com.zzia.graduationproject.ui.activity.me.SettingActivity;
import com.zzia.graduationproject.ui.activity.smallchat.CampusLifeActivity;
import com.zzia.graduationproject.utils.ImageUtils;

import butterknife.Bind;

public class MeFragment extends BaseFragment {
    @Bind(R.id.fm_username_txt)
    TextView nameTxt;
    @Bind(R.id.fm_editFile_lyt)
    LinearLayout profileLyt;
    @Bind(R.id.fm_myCard_lyt)
    LinearLayout myCardLyt;
    @Bind(R.id.fm_myQrCode_iv)
    ImageView myQrCodeIv;
    @Bind(R.id.fm_head_img)
    ImageView headImg;
    @Bind(R.id.fm_setting_lyt)
    RelativeLayout settingLyt;
    @Bind(R.id.fm_release_lyt)
    RelativeLayout releaseLayout;
    @Bind(R.id.fm_attention_lyt)
    RelativeLayout noticeLayout;
    Dialog dialog;

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initListener();
    }

    private void initViews() {
        nameTxt.setText(App.getUser().getNickName());
        ImageUtils.setCornerImage(headImg, App.getUser().getAvatar());
    }

    private void initListener() {
        //点击二维码
        myQrCodeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(getActivity(), R.style.NoTitleDialogStyle);
                dialog.setContentView(R.layout.dialog_myqrcode);
                TextView nameTv = (TextView) dialog.findViewById(R.id.dm_name_tv);
                TextView addressTv = (TextView) dialog.findViewById(R.id.dm_address_tv);
                ImageView avatarIv = (ImageView) dialog.findViewById(R.id.dm_avatar_iv);
                final ImageView myQrCodeIv = (ImageView) dialog.findViewById(R.id.dm_myQrCode_iv);
                nameTv.setText(App.getUser().getNickName());
                addressTv.setText(App.getUser().getAddress());
                ImageUtils.setCornerImage(avatarIv, App.getUser().getAvatar());
                Bitmap mBitmap = CodeUtils.createImage(App.getUser().getUserId(), 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.xingxing6));
                myQrCodeIv.setImageBitmap(mBitmap);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

            }
        });
        //点击图像
        headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("avatar", App.getUser().getAvatar());
                go(AvatarSettingActivity.class, bundle);
            }
        });
        settingLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go(SettingActivity.class);
            }
        });
        //点击发布的日记
        releaseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("mySendDiary", "mySendDiary");
                go(CampusLifeActivity.class, bundle);
            }
        });
        //我的关注
        noticeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    public void onEvent(StringEvent event) {
        if (event.getName().equals("updateUserAvatar")) {
            ImageUtils.setCornerImage(headImg, event.getData());
        }

    }
}
