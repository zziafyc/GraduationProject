package com.zzia.graduationproject.ui.fragment;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.adapter.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout;
import cn.hugeterry.coordinatortablayout.listener.LoadHeaderImagesListener;

/**
 * Created by Administrator on 2016/7/8.
 * 邮箱：wgyscsf@163.com
 * 博客：http://blog.csdn.net/wgyscsf
 */
public class SmallTravelFragment extends com.zzia.graduationproject.base.BaseFragment {
    @Bind(R.id.coordinatorTabLayout)
    CoordinatorTabLayout mCoordinatorTabLayout;
    @Bind(R.id.ft_travelSorts_vp)
    ViewPager mViewPager;
    private int[] mColorArray;
    private ArrayList<Fragment> mFragments;
    private final String[] mTitles = {"单身游", "情侣游", "组团游"};
    private SingleTravelFragment mSingleTravelFragment=new SingleTravelFragment();
    private LoversTravelFragment mLoversTravelFragment=new LoversTravelFragment();
    private GroupTravelFragment mGroupTravelFragment=new GroupTravelFragment();

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_travel;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initListener();
    }

    private void initViews() {
        initFragments();
        initViewPager();
        initColor();
        initCoordinator();

    }

    private void initListener() {
    }

    private void initColor() {
        mColorArray = new int[]{
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,};
    }

    private void loadImages(ImageView imageView, String url) {
        Glide.with(getActivity()).load(url).into(imageView);
    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        mFragments.add(mSingleTravelFragment);
        mFragments.add(mLoversTravelFragment);
        mFragments.add(mGroupTravelFragment);

    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager(), mFragments, mTitles));
    }

    private void initCoordinator() {
        mCoordinatorTabLayout.setTitle("")
                .setBackEnable(false)
                .setContentScrimColorArray(mColorArray)
                .setLoadHeaderImagesListener(new LoadHeaderImagesListener() {
                    @Override
                    public void loadHeaderImages(ImageView imageView, TabLayout.Tab tab) {
                        switch (tab.getPosition()) {
                            case 0:
                                loadImages(imageView, "https://raw.githubusercontent.com/hugeterry/CoordinatorTabLayout/master/sample/src/main/res/mipmap-hdpi/bg_android.jpg");
                                break;
                            case 1:
                                loadImages(imageView, "https://raw.githubusercontent.com/hugeterry/CoordinatorTabLayout/master/sample/src/main/res/mipmap-hdpi/bg_js.jpg");
                                break;
                            case 2:
                                loadImages(imageView, "https://raw.githubusercontent.com/hugeterry/CoordinatorTabLayout/master/sample/src/main/res/mipmap-hdpi/bg_ios.jpg");
                                break;
                            case 3:
                                loadImages(imageView, "https://raw.githubusercontent.com/hugeterry/CoordinatorTabLayout/master/sample/src/main/res/mipmap-hdpi/bg_other.jpg");
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setupWithViewPager(mViewPager);

    }

}
