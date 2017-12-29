package com.zzia.graduationproject.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.melnykov.fab.FloatingActionButton;
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

import butterknife.Bind;

/**
 * Created by fyc on 2017/4/27
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class SingleTravelFragment extends BaseFragment {
    @Bind(R.id.ft_single_rv)
    RecyclerView singleRv;
    @Bind(R.id.ft_single_fab)
    FloatingActionButton mFloatingActionButton;
    @Bind(R.id.header_single)
    RecyclerViewHeader mHeader;
    List<TravelPlan> list = new ArrayList<>();
    CommonAdapter<TravelPlan> mAdapter;
    int currentPage;
    int count = 10;

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_travel_single;
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
        singleRv.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        singleRv.addItemDecoration(new MyDividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mAdapter = new CommonAdapter<TravelPlan>(getActivity(),R.layout.item_travel_single, list) {
            @Override
            protected void convert(ViewHolder holder, final TravelPlan item, int position) {
                holder.setNetImage(R.id.its_themePic_iv, item.getTravelThemePic());
                holder.setText(R.id.its_themDes_tv, item.getTravelTheme());
                holder.setText(R.id.its_des_tv, item.getTravelDes());
                holder.setOnClickListener(R.id.item_single_rv, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("travelPlan",item);
                        go(TravelPlanActivity.class,bundle);
                    }
                });
            }




        };
        singleRv.setAdapter(mAdapter);
        //设置头部依附
        mHeader.attachTo(singleRv);
        //设置mFloatingActionButton依附
        mFloatingActionButton.attachToRecyclerView(singleRv);
        mFloatingActionButton.show();
    }

    private void initData() {
        call(ApiClient.getApis().getTravelListByType(App.getUser().getUserId(), 0, currentPage, count), new MySubscriber<BaseResp<List<TravelPlan>>>() {
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
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TravelPlan travelPlan = new TravelPlan();
                travelPlan.setCreateUser(App.getUser());
                travelPlan.setCreateDate(DateUtils.getCurrentDate().split(" ")[0]);
                travelPlan.setType(0);
                Bundle bundle=new Bundle();
                bundle.putSerializable("travelPlan",travelPlan);
                go(TravelPlanActivity.class,bundle);
            }
        });
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }
    public void onEvent(StringEvent event){
        if(event.getName().equals("updateTravel")){
            list.clear();
            initData();
            currentPage=0;
        }

    }
}
