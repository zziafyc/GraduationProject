package com.zzia.graduationproject.ui.activity.tellbook;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.shcyd.lib.adapter.BasicAdapter;
import com.shcyd.lib.adapter.ViewHolder;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.base.ItemMenuDeleteCreator;
import com.zzia.graduationproject.event.StringEvent;
import com.zzia.graduationproject.model.Friends;


import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.rong.eventbus.EventBus;

public class DealFriendsMessageActivity extends BaseActivity {
    @Bind(R.id.aaf_friendMessage_lv)
    SwipeMenuListView mSwipeMenuListView;
    BasicAdapter<Friends> mAdapter;
    List<Friends> mList = new ArrayList<>();
    int currentPage=0;
    int count =10;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_add_friends_message;
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
        mAdapter = new BasicAdapter<Friends>(this, mList, R.layout.item_friendmessage) {
            @Override
            protected void render(final ViewHolder holder, final Friends item, final int position) {
                holder.setImage(R.id.item_avatar_iv, item.getApplicationUser().getAvatar());
                holder.setText(R.id.item_name_tv, item.getApplicationUser().getNickName());
                holder.setText(R.id.item_tel_tv, item.getApplicationUser().getTel());
                holder.setText(R.id.item_address_tv, item.getApplicationUser().getAddress());
                if (item.getApplicationUser().getSex().equals("男生")) {
                    holder.setImageResources(R.id.item_sex_iv, R.drawable.boy);
                } else {
                    holder.setImageResources(R.id.item_sex_iv, R.drawable.girl);
                }
                if (item.getState() == 0) {
                    //还未处理
                    holder.setText(R.id.item_state_tv, "同  意");
                    holder.setBackgroundImage(R.id.item_state_tv,R.drawable.bg_button_blue);
                    holder.onClick(R.id.item_state_tv, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            item.setState(1);
                            holder.setText(R.id.item_state_tv, "已同意");
                            holder.setBackgroundImage(R.id.item_state_tv,R.drawable.bg_button_grey);
                            //请求后台,默认备注是对方昵称
                            changeState(String.valueOf(item.getId()),item.getApplicationUser().getNickName());
                        }
                    });
                } else {
                    holder.setText(R.id.item_state_tv, "已同意");
                    holder.setBackgroundImage(R.id.item_state_tv,R.drawable.bg_button_grey);
                }
            }
        };
        mSwipeMenuListView.setAdapter(mAdapter);

    }

    private void initData() {
        call(ApiClient.getApis().getAllFriendMessage(App.getUser().getUserId(),currentPage,count), new MySubscriber<BaseResp<List<Friends>>>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResp<List<Friends>> resp) {
                if(resp.resultCode== Constants.RespCode.SUCCESS){
                    if(resp.status==Constants.RespCode.SUCCESS){
                        currentPage++;
                        mList.addAll(resp.data);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        showToast("暂无申请记录");
                    }

                }

            }
        });
    }

    private void initListener() {
        mSwipeMenuListView.setMenuCreator(new ItemMenuDeleteCreator(this));
        mSwipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteItem(String.valueOf(mList.get(position).getId()),position);
                        break;
                }
                return false;
            }
        });
        mSwipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View view1, int position, long l) {
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("friendFromMessage",mList.get(position));
                        go(AddFriendActivity.class,bundle);
                    }

        });
    }

    public void deleteItem(String id, final int position) {
        call(ApiClient.getApis().deleteFriendMessage(id), new MySubscriber<BaseResp<Void>>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResp<Void> resp) {
                if(resp.resultCode==Constants.RespCode.SUCCESS){
                    if(resp.status==Constants.RespCode.SUCCESS){
                        mList.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

        });

    }

    public void changeState(String id,String remark){
        call(ApiClient.getApis().changeMessageState(id,remark), new MySubscriber<BaseResp<Void>>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResp<Void> resp) {
                if(resp.resultCode==Constants.RespCode.SUCCESS){
                    if(resp.status==Constants.RespCode.SUCCESS){
                        //成功了需要刷新好友列表
                        EventBus.getDefault().post(new StringEvent("updateFriendsList"));
                    }
                }else{
                    //失败了是需要回滚的，这里没有处理
                }
            }
        });
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }
    public void onEvent(StringEvent event){
        if(event.getName().equals("updateFriendsList")){
            mList.clear();
            initData();

        }
    }
}
