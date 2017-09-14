package com.zzia.graduationproject.ui.activity.smalltravel;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airsaid.pickerviewlibrary.TimePickerView;
import com.shcyd.lib.adapter.BasicAdapter;
import com.shcyd.lib.adapter.ViewHolder;
import com.shcyd.lib.utils.StringUtils;
import com.zhy.magicviewpager.transformer.AlphaPageTransformer;
import com.zhy.magicviewpager.transformer.ScaleInTransformer;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.event.StringEvent;
import com.zzia.graduationproject.event.UsersEvent;
import com.zzia.graduationproject.model.PhotoConnect;
import com.zzia.graduationproject.model.TravelMember;
import com.zzia.graduationproject.model.TravelPlan;
import com.zzia.graduationproject.model.TravelRoute;
import com.zzia.graduationproject.model.User;
import com.zzia.graduationproject.myview.AutoPlayViewPager;
import com.zzia.graduationproject.myview.NoScrollListView;
import com.zzia.graduationproject.utils.DateUtils;
import com.zzia.graduationproject.utils.ImageUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import io.rong.eventbus.EventBus;

public class TravelPlanActivity extends BaseActivity {
    @Bind(R.id.at_banner_viewpager)
    AutoPlayViewPager mViewPager;
    @Bind(R.id.im_lv_member)
    NoScrollListView memberListView;
    @Bind(R.id.im_lv_route)
    NoScrollListView routeListView;
    @Bind(R.id.at_scrollView)
    ScrollView mScrollView;
    @Bind(R.id.im_createManPic_iv)
    ImageView createManPic;
    @Bind(R.id.im_tv_company_createMan)
    TextView createManTv;
    @Bind(R.id.im_tv_company_time)
    TextView createTime;
    @Bind(R.id.at_reWriteMember_tv)
    TextView rewriteMember;
    @Bind(R.id.cab_titleBack_iv)
    ImageView backIv;
    @Bind(R.id.title_right)
    TextView rightTv;
    @Bind(R.id.activity_travelPlan)
    LinearLayout travelPlanLayout;
    @Bind(R.id.at_travelTheme)
    EditText travelTheme;
    @Bind(R.id.at_edt_description)
    EditText travelDes;
    @Bind(R.id.at_ll_member)
    LinearLayout memberLayout;

    PagerAdapter mPagerAdapter;
    List<String> images = new ArrayList<>();
    public BasicAdapter<TravelMember> mUserBasicAdapter;
    BasicAdapter<TravelRoute> mRouteBasicAdapter;
    public List<TravelMember> members = new ArrayList<>();
    List<TravelRoute> routes = new ArrayList<>();
    Handler mHandler = new Handler();
    String startTime;
    String endTime;
    int clickStartPosition;
    int clickEndPosition;
    TravelPlan travelPlan = new TravelPlan();
    List<TravelMember> agreeMembers=new ArrayList<>();


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_travelplan;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);

    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initUserAdapter();
        initRouteAdapter();
        initListener();


    }

    private void initViews() {
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText("完成");
        TravelPlan plan = (TravelPlan) getIntent().getSerializableExtra("travelPlan");
        if (plan != null) {
            if (plan.getType() == 0) {
                memberLayout.setVisibility(View.GONE);
            }
            if (StringUtils.isEmpty(plan.getTravelId())) {
                travelPlan = plan;
                initBanner();
            } else {
                travelPlan.setTravelId(plan.getTravelId());
                initData();
            }
        }
        User user = travelPlan.getCreateUser();
        if (user != null) {
            ImageUtils.setCornerImage(createManPic, user.getAvatar());
            createManTv.setText(user.getNickName());
        } else {
            ImageUtils.setCornerImage(createManPic, App.getUser().getAvatar());
            createManTv.setText(App.getUser().getNickName());
            travelPlan.setCreateUser(App.getUser());
        }
        String createDate = travelPlan.getCreateDate();
        createTime.setText(StringUtils.isEmpty(createDate) ? DateUtils.getCurrentDate().split(" ")[0] : createDate);
        if (!StringUtils.isEmpty(travelPlan.getTravelTheme())) {
            travelTheme.setText(travelPlan.getTravelTheme());
        }
        if (!StringUtils.isEmpty(travelPlan.getTravelDes())) {
            travelDes.setText(travelPlan.getTravelDes());
        }


    }

    private void initBanner() {
        images.add("http://opor07of8.bkt.clouddn.com/campus3.jpg");
        images.add("http://opor07of8.bkt.clouddn.com/campus1.jpg");
        images.add("http://opor07of8.bkt.clouddn.com/campus2.jpg");
        images.add("http://opor07of8.bkt.clouddn.com/campus4.jpg");
        mViewPager.setPageMargin(30);
        mViewPager.setOffscreenPageLimit(3);
        mPagerAdapter = new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                position = position % images.size();
                ImageView view = new ImageView(TravelPlanActivity.this);
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                ImageUtils.setCornerImageNo(view, images.get(position));
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getCount() {
                return images == null ? 0 : Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setPageTransformer(true, new AlphaPageTransformer(new ScaleInTransformer()));
        // 以下两个方法不是必须的，因为有默认值
        mViewPager.setDirection(AutoPlayViewPager.Direction.LEFT);// 设置播放方向
        mViewPager.start(); // 开始轮播
    }

    private void initUserAdapter() {
        mUserBasicAdapter = new BasicAdapter<TravelMember>(this, members, R.layout.item_member) {
            @Override
            protected void render(ViewHolder holder, TravelMember item, int position) {
                holder.setImage(R.id.item_member_iv, item.getUser().getAvatar());
                holder.setText(R.id.item_member_name, item.getUser().getNickName());
                if (item.getState() == 0) {
                    //未响应
                    holder.setText(R.id.item_member_tv, "未响应");
                    holder.setTextColor(R.id.item_member_tv, R.color.grey);
                } else if (item.getState() == 1) {
                    //已同意
                    holder.setText(R.id.item_member_tv, "已同意");
                    holder.setTextColor(R.id.item_member_tv, R.color.colorYellow);
                } else {
                    //已拒绝
                    holder.setText(R.id.item_member_tv, "已拒绝");
                    holder.setTextColor(R.id.item_member_tv, R.color.colorRed);
                }
            }
        };
        memberListView.setAdapter(mUserBasicAdapter);
    }

    private void initRouteAdapter() {
        mRouteBasicAdapter = new BasicAdapter<TravelRoute>(this, routes, R.layout.item_route) {
            @Override
            protected void render(ViewHolder holder, final TravelRoute item, final int position) {
                holder.setText(R.id.item_tv_taskLabel, "旅行路线" + (position + 1) + "：");
                final EditText description = holder.getSubView(R.id.item_routeDescription_edt);
                if (!StringUtils.isEmpty(item.getRouteDes())) {
                    description.setText(item.getRouteDes());
                } else {
                    description.setText("");
                }
                final EditText introduction = holder.getSubView(R.id.item_introduction_edt);
                if (!StringUtils.isEmpty(item.getIntroduction())) {
                    introduction.setText(item.getIntroduction());
                } else {
                    introduction.setText("");
                }
                if (!StringUtils.isEmpty(item.getStartDate())) {
                    holder.setText(R.id.item_tv_startTime, item.getStartDate());
                }
                if (!StringUtils.isEmpty(item.getEndDate())) {
                    holder.setText(R.id.item_tv_endTime, item.getEndDate());
                }

                if (!StringUtils.isEmpty(startTime) && position == clickStartPosition) {
                    holder.setText(R.id.item_tv_startTime, startTime);
                    item.setStartDate(startTime);
                }
                if (!StringUtils.isEmpty(endTime) && position == clickEndPosition) {
                    holder.setText(R.id.item_tv_endTime, endTime);
                    item.setEndDate(endTime);
                }

                holder.onClick(R.id.deleteRoute, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        routes.remove(position);
                        mRouteBasicAdapter.notifyDataSetChanged();
                    }
                });
                holder.onClick(R.id.item_tv_startTime, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickStartPosition = position;
                        showTimePop(0);
                    }
                });
                holder.onClick(R.id.item_tv_endTime, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickEndPosition = position;
                        showTimePop(1);
                    }
                });
                description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean isFocus) {
                        if (isFocus) {
                            rightTv.setVisibility(View.VISIBLE);
                            rightTv.setText("完成");

                        } else {
                            //不聚焦的时候立马取值
                            if (!StringUtils.isEmpty(description.getText().toString())) {
                                item.setRouteDes(description.getText().toString());
                            } else {
                                item.setRouteDes("");
                            }
                        }
                    }
                });

                introduction.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean isFocus) {
                        if (isFocus) {
                            rightTv.setVisibility(View.VISIBLE);
                            rightTv.setText("完成");
                        } else {
                            if (!StringUtils.isEmpty(introduction.getText().toString())) {
                                item.setIntroduction(introduction.getText().toString());
                            } else {
                                item.setIntroduction("");
                            }
                        }
                    }
                });
            }
        };

        View footer = getLayoutInflater().inflate(R.layout.footer_route, null);
        if (routeListView.getFooterViewsCount() == 0) {
            routeListView.addFooterView(footer);
            footer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TravelRoute route = new TravelRoute();
                    route.setStartDate("选择开始时间");
                    route.setEndDate("选择结束时间");
                    routes.add(route);
                    mRouteBasicAdapter.notifyDataSetChanged();
                    //滑动到底部
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            });

        }
        routeListView.setAdapter(mRouteBasicAdapter);

    }

    private void initData() {
        call(ApiClient.getApis().getTravelPlanDetail(travelPlan.getTravelId()), new MySubscriber<BaseResp<TravelPlan>>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResp<TravelPlan> resp) {
                travelPlan = resp.data;
                ImageUtils.setCornerImage(createManPic, travelPlan.getCreateUser().getAvatar());
                if (travelPlan.getCreateUser() != null) {
                    createManTv.setText(travelPlan.getCreateUser().getNickName());
                }
                if (travelPlan.getCreateDate() != null) {
                    createTime.setText(travelPlan.getCreateDate().split(" ")[0]);
                }
                travelTheme.setText(travelPlan.getTravelTheme());
                travelDes.setText(travelPlan.getTravelDes());

                //图片
                List<PhotoConnect> photoConnects = travelPlan.getTravelPhotos();
                if (photoConnects != null && photoConnects.size() > 0) {
                    for (PhotoConnect model : photoConnects) {
                        images.add(model.getPhotoSite());
                    }

                }
                initBanner();
                //成员
                List<TravelMember> travelMembers = travelPlan.getMembers();
                if (travelMembers != null && travelMembers.size() > 0) {
                    for (TravelMember model : travelMembers) {
                        members.add(model);
                    }
                    mUserBasicAdapter.notifyDataSetChanged();
                }
                //路线
                List<TravelRoute> travelRoutes = travelPlan.getTravelRoutes();
                if (travelRoutes != null && travelRoutes.size() > 0) {
                    routes.addAll(travelRoutes);
                    mRouteBasicAdapter.notifyDataSetChanged();
                }

            }

        });

    }

    private void initListener() {
        rewriteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<TravelMember> list = new ArrayList<>();
                if (members != null && members.size() > 0) {
                    for (TravelMember model : members) {
                        if (model.getState() == 1) {
                            list.add(model);
                        }
                    }
                }
                agreeMembers.clear();
                //首先保留状态为1的就是已同意的
                for(TravelMember model:members){
                    if(model.getState()==1){
                        agreeMembers.add(model);
                    }
                }
                Bundle bundle = new Bundle();
                if (list != null && list.size() > 0) {
                    bundle.putSerializable("members", (Serializable) list);
                }
                go(RewriteMemberActivity.class, bundle);
            }
        });
        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //验证数据是否为空
                if (!StringUtils.isEmpty(travelTheme.getText().toString())) {
                    travelPlan.setTravelTheme(travelTheme.getText().toString());
                } else {
                    showToast("请填写旅行主题");
                    return;
                }
                if (!StringUtils.isEmpty(travelDes.getText().toString())) {
                    travelPlan.setTravelDes(travelDes.getText().toString());
                } else {
                    showToast("请填写旅行简介");
                    return;
                }
                //旅行成员
                if(members!=null&&members.size()>0){
                   travelPlan.setMembers(members);
                }
                if (routes != null && routes.size() > 0) {
                    for (TravelRoute model : routes) {
                        if (StringUtils.isEmpty(model.getRouteDes())) {
                            showToast("请完成旅行路线描述");
                            return;
                        }
                        if (StringUtils.isEmpty(model.getStartDate()) || model.getStartDate().equals("选择开始时间")) {
                            showToast("请选择旅行路线开始时间");
                            return;
                        }
                        if (StringUtils.isEmpty(model.getEndDate()) || model.getEndDate().equals("选择结束时间")) {
                            showToast("请选择旅行路线结束时间");
                            return;
                        }
                        if (StringUtils.isEmpty(model.getIntroduction())) {
                            showToast("请完成旅行路线详细介绍");
                            return;
                        }
                    }
                    travelPlan.setTravelRoutes(routes);
                }
                //开始保存数据
                travelPlan.setTravelThemePic(images.get(1));  //选择第一张为封面图
                call(ApiClient.getApis().createTravelPlan(travelPlan), new MySubscriber<BaseResp<String>>() {
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(BaseResp<String> resp) {
                        if (resp.resultCode == Constants.RespCode.SUCCESS) {
                            if (resp.status == Constants.RespCode.SUCCESS) {
                                EventBus.getDefault().post(new StringEvent("updateTravel"));
                                finish();
                            }
                        }
                    }

                });
            }
        });
    }

    private void showTimePop(final int flag) {
        TimePickerView pickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        //设置标题
        pickerView.setTitle("选择日期");
        //控制时间范围
        Calendar calendar = Calendar.getInstance();
        pickerView.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));
        pickerView.setTime(new Date());
        //设置是否循环
        pickerView.setCyclic(true);
        //设置是否可以关闭
        pickerView.setCancelable(true);
        //设置选择监听
        pickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                if (flag == 0) {
                    startTime = dateToString(date);
                    mRouteBasicAdapter.notifyDataSetChanged();

                } else {
                    endTime = dateToString(date);
                    mRouteBasicAdapter.notifyDataSetChanged();
                }

            }
        });
        //显示
        pickerView.show();
    }

    public String dateToString(Date time) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        String result = formatter.format(time);
        return result;
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    public void onEvent(UsersEvent event) {
        members.clear();
        members.addAll(agreeMembers);
        members.addAll(event.getData());
        mUserBasicAdapter.notifyDataSetChanged();
    }
}
