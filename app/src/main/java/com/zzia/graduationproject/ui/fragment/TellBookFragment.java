package com.zzia.graduationproject.ui.fragment;


import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;


import com.shcyd.lib.adapter.ViewHolder;
import com.shcyd.lib.widget.listview.PinnedHeaderListView;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.adapter.ScreenSectionedAdapter;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseFragment;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.event.StringEvent;
import com.zzia.graduationproject.model.User;
import com.zzia.graduationproject.ui.activity.tellbook.DealFriendsMessageActivity;
import com.zzia.graduationproject.ui.activity.tellbook.CustomCaptureActivity;
import com.zzia.graduationproject.ui.activity.tellbook.SearchUserActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import io.rong.imkit.RongIM;

public class TellBookFragment extends BaseFragment {
    @Bind(R.id.acm_contacts_list)
    PinnedHeaderListView contactsList;
    @Bind(R.id.acl_search_edit)
    EditText searchEdit;

    RelativeLayout newFriendLyt;
    RelativeLayout scanLyt;
    ScreenSectionedAdapter<User> mAdapter;
    Map<String, List<User>> map = new HashMap<>();

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_telebook;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initAdapter();
        initData();
        initListener();
    }

    protected void initViews() {
        View headView = getActivity().getLayoutInflater().inflate(R.layout.head_contacts_list, null);
        newFriendLyt = (RelativeLayout) headView.findViewById(R.id.acl_new_friend_lyt);
        scanLyt = (RelativeLayout) headView.findViewById(R.id.acl_scan_lyt);
        contactsList.addHeaderView(headView);
    }

    private void initAdapter() {
        mAdapter = new ScreenSectionedAdapter<User>(getActivity(), R.layout.item_telebook) {
            @Override
            protected void render(ViewHolder holder, final User item, final int section, final int position) {
                holder.gone(R.id.item_addBtn);
                if (item.getAvatar() != null) {
                    holder.setImage(R.id.it_iBtn_pic, item.getAvatar());
                } else {
                    holder.setImage(R.id.it_iBtn_pic, R.drawable.avatar_default);
                }
                holder.setText(R.id.it_tv_name, item.getRemark());
                holder.onClick(R.id.it_ll_addBtnLayout, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        User user = item;
                        if (RongIM.getInstance() != null ) {
                           /* RongIM.getInstance().setCurrentUserInfo(new UserInfo(App.getUser().getUserId(), App.getUser().getNickName(),null));
                            RongIM.getInstance().setMessageAttachedUserInfo(true);*/
                            RongIM.getInstance().startPrivateChat(getActivity(), user.getUserId(), user.getRemark());
                        }
                    }
                });
            }
        };
        mAdapter.setDatas(map);
        contactsList.setAdapter(mAdapter);
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
                        mAdapter.setDatas(map);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        showToast("暂无好友列表");
                    }

                } else {
                    showToast(getResources().getString(R.string.systemError));
                }

            }
        });


    }

    private void setLetter(Map<String, List<User>> datas) {
        Iterator<String> iterator = datas.keySet().iterator();
        while (iterator.hasNext()) {
            String keyMap = iterator.next();
            for (User model : datas.get(keyMap)) {
                model.letter = keyMap;
            }
        }
    }

    protected void initListener() {
        newFriendLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go(DealFriendsMessageActivity.class);
            }
        });
        scanLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go(CustomCaptureActivity.class);

            }
        });
        //搜索框的事件
        searchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    searchEdit.clearFocus();
                    go(SearchUserActivity.class);

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
            initData();
        }

    }
}
