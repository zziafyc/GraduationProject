package com.zzia.graduationproject.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.rom4ek.arcnavigationview.ArcNavigationView;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.ui.fragment.MeFragment;
import com.zzia.graduationproject.ui.fragment.SmallChatFragment;
import com.zzia.graduationproject.ui.fragment.SmallTravelFragment;
import com.zzia.graduationproject.ui.fragment.TellBookFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.nb_rg_naviBottom)
    RadioGroup mRadioGroup;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view)
    ArcNavigationView mArcNavigationView;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    List<Fragment> mFragmentList;
    SmallChatFragment mSmallRecordFragment;
    SmallTravelFragment mSmallTravelFragment;
    TellBookFragment mTeleBookFragment;
    MeFragment mMeFragment;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        connectRongYun();
        initFragment();
        selectFragment();
        initListener();
    }

    private void initListener() {
        mArcNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initViews() {
        setCustomTitle(getResources().getString(R.string.app_name));
    }

    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentList = new ArrayList<>();
        mSmallRecordFragment = new SmallChatFragment();
        mSmallTravelFragment = new SmallTravelFragment();
        mTeleBookFragment = new TellBookFragment();
        mMeFragment = new MeFragment();
        mFragmentList.add(mSmallRecordFragment);
        mFragmentList.add(mSmallTravelFragment);
        mFragmentList.add(mTeleBookFragment);
        mFragmentList.add(mMeFragment);
        showFragment(mSmallRecordFragment);


    }

    private void showFragment(Fragment fragment) {
        hideAllFragment();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        if (fragment.isAdded()) {
            mFragmentTransaction.show(fragment);

        } else {
            mFragmentTransaction.add(R.id.am_fl_fragmentcontainer, fragment);
        }

        mFragmentTransaction.commit();
    }

    //隐藏所有已经add的帧布局
    private void hideAllFragment() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragmentList) {
            if (fragment != null)
                if (fragment.isAdded()) {
                    mFragmentTransaction.hide(fragment);
                }
        }
        mFragmentTransaction.commit();
    }

    private void selectFragment() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.nb_rb_smallRecord:
                        showFragment(mSmallRecordFragment);
                        break;
                    case R.id.nb_rb_smallTravel:
                        showFragment(mSmallTravelFragment);
                        break;
                    case R.id.nb_rb_teleBook:
                        showFragment(mTeleBookFragment);
                        break;
                    case R.id.nb_rb_me:
                        showFragment(mMeFragment);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void connectRongYun() {
        //连接融云
        if (this.getApplicationInfo().packageName.equals(App.getCurProcessName(this))) {
            if (App.getUser() != null) {
                if (!TextUtils.isEmpty(App.getUser().getToken())) {
                    RongIM.connect(App.getUser().getToken(), new RongIMClient.ConnectCallback() {
                        @Override
                        public void onTokenIncorrect() {
                            showToast("token获取失败");
                        }

                        @Override
                        public void onSuccess(String userId) {
                            //showToast("连接融云成功");
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            showToast("融云客户端错误");
                        }
                    });
                }
            }
        }
    }
}
