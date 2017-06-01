package com.zzia.graduationproject.ui.activity.smalltravel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.shcyd.lib.adapter.BasicAdapter;
import com.shcyd.lib.adapter.ViewHolder;
import com.shcyd.lib.utils.StringUtils;
import com.shcyd.lib.widget.listview.PinnedHeaderListView;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.adapter.ScreenSectionedAdapter;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.base.ItemMenuDeleteCreator;
import com.zzia.graduationproject.event.UsersEvent;
import com.zzia.graduationproject.model.TravelMember;
import com.zzia.graduationproject.model.User;
import com.zzia.graduationproject.myview.NoScrollSwipeListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import io.rong.eventbus.EventBus;
import io.rong.imkit.widget.adapter.BaseAdapter;

public class RewriteMemberActivity extends BaseActivity {
    //搜索
    @Bind(R.id.ail_search_edit)
    EditText searchEdit;
    @Bind(R.id.ail_clear_search_lyt)
    RelativeLayout clearSearchLyt;
    @Bind(R.id.cancel_txt)
    TextView cancelSearchTv;

    //侧滑的swipeMenuListView
    View headView = null;
    TextView haveChoiceCountTv;
    RelativeLayout shrinkReLay;
    NoScrollSwipeListView swipeMenuListView;
    BasicAdapter<User> mSwipeAdapter;
    List<User> selectedList = new ArrayList<>();
    List<TravelMember> members = new ArrayList<>();

    //好友列表
    @Bind(R.id.arm_friends_lv)
    PinnedHeaderListView mPinnedHeaderListView;
    ScreenSectionedAdapter<User> mSectionAdapter;
    Map<String, List<User>> map = new HashMap<>();


    //下一步
    @Bind(R.id.title_right)
    TextView rightTv;
    View footerDeepSearch;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_rewrite_member;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initAdapter1();
        initAdapter2();
        initData();
        initListener();


    }

    private void initViews() {
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText("保存");

        //已选列表初始化
        members = (List<TravelMember>) getIntent().getSerializableExtra("members");

        headView = getLayoutInflater().inflate(R.layout.headerview_member, null);
        swipeMenuListView = (NoScrollSwipeListView) headView.findViewById(R.id.hm_userList_lv);
        haveChoiceCountTv = (TextView) headView.findViewById(R.id.haveChoiceCount);
        shrinkReLay = (RelativeLayout) headView.findViewById(R.id.acm_rl_shrink);
        mPinnedHeaderListView.addHeaderView(headView);
        shrinkReLay.setVisibility(View.GONE);
        rightTv.setBackgroundResource(R.drawable.bg_nextbtn_grey);
        rightTv.setEnabled(false);
        //尾部说明
        footerDeepSearch = getLayoutInflater().inflate(R.layout.footerview_deepsearch, null);


    }

    private void initAdapter1() {
        //已选列表
        mSwipeAdapter = new BasicAdapter<User>(this, selectedList, R.layout.item_user_swipe) {
            @Override
            protected void render(ViewHolder holder, User item, int position) {
                holder.setImage(R.id.item_avatar_iv, item.getAvatar());
                holder.setText(R.id.item_name_tv, item.getRemark());
                haveChoiceCountTv.setText("(" + selectedList.size() + ")");
                if (selectedList.size() > 0) {
                    rightTv.setBackgroundResource(R.drawable.bg_nextbtn_blue);
                    rightTv.setEnabled(true);
                    rightTv.setClickable(true);
                } else {
                    rightTv.setBackgroundResource(R.drawable.bg_nextbtn_grey);
                    rightTv.setEnabled(false);
                    rightTv.setClickable(false);
                }
            }
        };
        swipeMenuListView.setAdapter(mSwipeAdapter);
        swipeMenuListView.setMenuCreator(new ItemMenuDeleteCreator(this));
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                //删除操作
                changePinnedViewState(selectedList, map, position);
                selectedList.remove(position);
                if (selectedList.size() == 0) {
                    shrinkReLay.setVisibility(View.GONE);
                    rightTv.setBackgroundResource(R.drawable.bg_nextbtn_grey);
                    rightTv.setEnabled(false);
                } else {
                    rightTv.setBackgroundResource(R.drawable.bg_nextbtn_blue);
                    rightTv.setEnabled(true);
                }
                mSwipeAdapter.notifyDataSetChanged();
                return false;
            }
        });

    }

    private void initAdapter2() {
        //好友列表
        mSectionAdapter = new ScreenSectionedAdapter<User>(this, R.layout.item_telebook) {
            @Override
            protected void render(final ViewHolder holder, final User item, int section, int position) {
                holder.setImage(R.id.it_iBtn_pic, item.getAvatar());
                holder.setText(R.id.it_tv_name, item.getRemark());

                //选中在已选列表
                if (item.isChoose) {
                    holder.gone(R.id.item_addBtn);
                    ((TextView) holder.getSubView(R.id.it_tv_name)).setTextColor(getResources().getColor(R.color.colorHint));
                } else {
                    holder.visible(R.id.item_addBtn);
                    ((TextView) holder.getSubView(R.id.it_tv_name)).setTextColor(getResources().getColor(R.color.colorTitle));
                }
                //前面页面已选中
                if (item.isAdded) {
                    holder.gone(R.id.item_addBtn);
                    ((TextView) holder.getSubView(R.id.it_tv_name)).setTextColor(getResources().getColor(R.color.colorHint));
                } else {
                    holder.visible(R.id.item_addBtn);
                    ((TextView) holder.getSubView(R.id.it_tv_name)).setTextColor(getResources().getColor(R.color.colorTitle));
                }
                holder.onClick(R.id.item_addBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rightTv.setBackgroundResource(R.drawable.bg_nextbtn_blue);
                        rightTv.setEnabled(true);
                        //设置已选列表
                        if (shrinkReLay.getVisibility() == View.GONE) {
                            shrinkReLay.setVisibility(View.VISIBLE);
                        }
                        if (swipeMenuListView.getVisibility() == View.GONE) {
                            //选择之后自动展开来提示
                            swipeMenuListView.setVisibility(View.VISIBLE);
                        }
                        if (mPinnedHeaderListView.getFooterViewsCount() > 0) {
                            mPinnedHeaderListView.removeFooterView(footerDeepSearch);

                        }
                        searchEdit.clearFocus();
                        item.isChoose = true;
                        item.isAdded=true;
                        selectedList.add(item);
                        mSwipeAdapter.notifyDataSetChanged();

                        //设置该带头部的列表
                        holder.gone(R.id.item_addBtn);
                        ((TextView) holder.getSubView(R.id.it_tv_name)).setTextColor(getResources().getColor(R.color.colorHint));
                    }
                });
            }
        };
        mSectionAdapter.setDatas(map);
        mPinnedHeaderListView.setAdapter(mSectionAdapter);

    }

    private void initData() {
        call(ApiClient.getApis().getAllFriends(App.getUser().getUserId()), new MySubscriber<BaseResp<Map<String, List<User>>>>() {

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(BaseResp<Map<String, List<User>>> resp) {
                if (resp.resultCode == Constants.RespCode.SUCCESS) {
                    if (resp.status == Constants.RespCode.SUCCESS) {
                        map = resp.data;
                        setLetter(map);
                        mSectionAdapter.setDatas(map);
                        //设置是否已经被选中
                        Iterator<String> key = map.keySet().iterator();
                        if (members != null && members.size() > 0) {
                            while (key.hasNext()) {
                                for (User item : map.get(key.next())) {
                                    for (TravelMember model : members) {
                                        if (model.getUser().getUserId().equals(item.getUserId()))
                                            item.isAdded = true;
                                    }
                                }
                            }
                        }
                        mSectionAdapter.notifyDataSetChanged();
                    } else {
                        showToast("暂无好友列表");
                    }

                } else {
                    showToast(getResources().getString(R.string.systemError));
                }

            }
        });

    }

    private void changePinnedViewState(List<User> selectedList, Map<String, List<User>> friends, int position) {
        String letter = "";
        letter = selectedList.get(position).letter;
        if (letter != null) {
            List<User> models = friends.get(letter);
            if (models != null) {
                for (User model : models) {
                    if (model.getUserId().equals(selectedList.get(position).getUserId())) {
                        model.isChoose = false;
                        model.isAdded=false;
                        mSectionAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }

    }

    private void setLetter(Map<String, List<User>> data) {
        Iterator<String> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            String keyMap = iterator.next();
            for (User model : data.get(keyMap)) {
                model.letter = keyMap;
            }
        }
    }

    private void initListener() {
        //聚焦设计
        searchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    cancelSearchTv.setVisibility(View.VISIBLE);
                    shrinkReLay.setVisibility(View.GONE);
                    swipeMenuListView.setVisibility(View.GONE);
                    if (mPinnedHeaderListView.getFooterViewsCount() == 0) {
                        mPinnedHeaderListView.addFooterView(footerDeepSearch);
                    }
                } else {
                    cancelSearchTv.setVisibility(View.GONE);
                    searchEdit.getText().clear();
                    shrinkReLay.setVisibility(View.VISIBLE);
                    swipeMenuListView.setVisibility(View.VISIBLE);
                }

            }
        });
        //监听edit的变化
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = searchEdit.getText().toString().trim();
                final TextView searchResult = (TextView) footerDeepSearch.findViewById(R.id.ad_tv_searchResult);
                if (StringUtils.isEmpty(key)) {
                    clearSearchLyt.setVisibility(View.GONE);
                    if (!map.isEmpty()) {
                        mSectionAdapter.setDatas(map);
                    }
                    searchResult.setText("");
                } else {
                    clearSearchLyt.setVisibility(View.VISIBLE);
                    if (!map.isEmpty()) {
                        mSectionAdapter.setDatas(filter(key));
                    }
                    searchResult.setText(searchEdit.getText().toString());
                }
                footerDeepSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //不支持为空搜索
                        if (StringUtils.isEmpty(searchEdit.getText().toString())) {
                            Bundle bundle = new Bundle();

                        }
                    }
                });

                mSectionAdapter.notifyDataSetChanged();
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //点击取消按钮
        cancelSearchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdit.clearFocus();
                mPinnedHeaderListView.removeFooterView(footerDeepSearch);
                if (selectedList.size() == 0) {
                    shrinkReLay.setVisibility(View.GONE);
                } else {
                    shrinkReLay.setVisibility(View.VISIBLE);
                }
            }
        });
        //点击清除按钮
        clearSearchLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdit.setText("");
            }
        });
        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //已选列表的数据

                if (selectedList != null && selectedList.size() > 0) {
                    UsersEvent<TravelMember> event = new UsersEvent<>();
                    List<TravelMember> list = new ArrayList<>();
                    for (User model : selectedList) {
                        TravelMember member = new TravelMember();
                        member.setUser(model);
                        list.add(member);
                    }
                    event.setData(list);
                    EventBus.getDefault().post(event);
                    finish();
                }

            }
        });

    }

    private Map<String, List<User>> filter(String key) {
        Map<String, List<User>> data = new LinkedHashMap<>();
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            List<User> item = new ArrayList<>();
            String keyMap = iterator.next();
            //考虑文字和字母，输入字母则显示该字母下的所有用户，若输入文字，则根据昵称添加
            if (keyMap.toLowerCase().equals(key.toLowerCase())) {
                data.put(keyMap, map.get(keyMap));
            } else {
                for (User model : map.get(keyMap)) {
                    if (model.getRemark().toLowerCase().contains(key.toLowerCase())) {
                        item.add(model);
                    }
                }
                if (item.size() > 0) {
                    data.put(keyMap, item);
                }
            }

        }
        return data;
    }
}
