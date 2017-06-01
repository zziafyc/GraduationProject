package com.zzia.graduationproject.ui.fragment;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.BaseFragment;
import com.zzia.graduationproject.base.recyclerview.CommonAdapter;
import com.zzia.graduationproject.base.recyclerview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by fyc on 2017/4/27
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class LoversTravelFragment extends BaseFragment {
    @Bind(R.id.ft_lovers_rv)
    RecyclerView loversRv;
    List<String> list;
    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_travel_lovers;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initAdapter();
        initData();

    }

    private void initViews() {
        list = new ArrayList<>();
        list.add("方应春的情侣");
        list.add("赵小林的情侣");
        list.add("方鹏的情侣");
        list.add("方应春的情侣");
        list.add("赵小林的情侣");
        list.add("方鹏的情侣");
        list.add("方应春的情侣");
        list.add("赵小林的情侣");
        list.add("方鹏的情侣");
        list.add("方应春的情侣");
        list.add("赵小林的情侣");
        list.add("方鹏的情侣");

    }

    private void initAdapter() {
        loversRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        loversRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        loversRv.setAdapter(new CommonAdapter<String>(getActivity(), R.layout.item_travel_lovers, list) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.it_single_tv, s);
            }

        });
    }

    private void initData() {

    }
}
