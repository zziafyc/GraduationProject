package com.zzia.graduationproject.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.melnykov.fab.FloatingActionButton;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseFragment;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.base.recyclerview.CommonAdapter;
import com.zzia.graduationproject.base.recyclerview.MyDividerItemDecoration;
import com.zzia.graduationproject.base.recyclerview.ViewHolder;
import com.zzia.graduationproject.event.StringEvent;
import com.zzia.graduationproject.model.TravelPlan;
import com.zzia.graduationproject.ui.activity.smalltravel.TravelPlanActivity;
import com.zzia.graduationproject.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;

/**
 * Created by fyc on 2017/4/27
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class GroupTravelFragment extends BaseFragment {
    @Bind(R.id.ft_group_rv)
    RecyclerView groupRv;
    @Bind(R.id.ft_group_fab)
    FloatingActionButton mFloatingActionButton;
    List<TravelPlan> list = new ArrayList<>();
    StaggeredGridLayoutManager manager;
    CommonAdapter<TravelPlan> mAdapter;
    int currentPage;
    int count = 10;


    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_travel_group;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initAdapter();
        initData();
        initListener();

    }

    private void initViews() {


    }

    private void initAdapter() {
        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        groupRv.setLayoutManager(manager);
        groupRv.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mAdapter = new CommonAdapter<TravelPlan>(getActivity(), R.layout.item_travel_group, list) {
            @Override
            protected void convert(ViewHolder holder, final TravelPlan plan, int position) {
                holder.setText(R.id.its_theme_tv, plan.getTravelTheme());
                holder.setText(R.id.its_description_tv, plan.getTravelDes());

                ImageView imageView = holder.getView(R.id.its_themePic_iv);
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = 450 + new Random().nextInt(100);
                imageView.setLayoutParams(params);
                holder.setNetImage(R.id.its_themePic_iv, plan.getTravelThemePic());
                holder.setOnClickListener(R.id.itg_rl_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("travelPlan", plan);
                        go(TravelPlanActivity.class, bundle);
                    }
                });

            }

        };
        groupRv.setAdapter(mAdapter);
        //设置mFloatingActionButton依附
        mFloatingActionButton.attachToRecyclerView(groupRv);
        mFloatingActionButton.show();
    }

    private void initData() {
        call(ApiClient.getApis().getTravelListByType(App.getUser().getUserId(), 2, currentPage, count), new MySubscriber<BaseResp<List<TravelPlan>>>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResp<List<TravelPlan>> resp) {
                if (resp.resultCode == Constants.RespCode.SUCCESS) {
                    if (resp.status == Constants.RespCode.SUCCESS) {
                        currentPage++;
                        list.addAll(resp.data);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

        });

    }

    private void initListener() {
        groupRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                manager.invalidateSpanAssignments();
            }
        });
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TravelPlan travelPlan = new TravelPlan();
                travelPlan.setCreateUser(App.getUser());
                travelPlan.setCreateDate(DateUtils.getCurrentDate().split(" ")[0]);
                Bundle bundle = new Bundle();
                travelPlan.setType(2);
                bundle.putSerializable("travelPlan", travelPlan);
                go(TravelPlanActivity.class, bundle);
            }
        });

    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    public void onEvent(StringEvent event) {
        if (event.getName().equals("updateTravel")) {
            list.clear();
            initData();
            currentPage = 0;
        }

    }
}
