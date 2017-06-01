package com.zzia.graduationproject.ui.activity.tellbook;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shcyd.lib.adapter.BasicAdapter;
import com.shcyd.lib.adapter.ViewHolder;
import com.shcyd.lib.utils.StringUtils;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class SearchUserActivity extends BaseActivity {
    //搜索框
    @Bind(R.id.ail_search_edit)
    EditText searchEdit;
    @Bind(R.id.ail_clear_search_lyt)
    RelativeLayout clearSearchLyt;
    @Bind(R.id.cancel_txt)
    TextView cancelSearchTv;

    //搜索结果
    @Bind(R.id.asu_searchUserResult_lv)
    ListView searchUserResultLv;
    @Bind(R.id.asu_notFindUser_rv)
    RelativeLayout notFindUserRv;
    //搜一搜
    @Bind(R.id.item_search_rv)
    RelativeLayout searchAndSearchRv;
    @Bind(R.id.ad_tv_searchResult)
    TextView inputResult;

    BasicAdapter<User> mAdapter;
    List<User> searchUsers = new ArrayList<>();


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_search_user;
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
        mAdapter = new BasicAdapter<User>(this, searchUsers, R.layout.item_user_search) {
            @Override
            protected void render(ViewHolder holder, final User item, int position) {
                holder.setImage(R.id.item_avatar_iv, item.getAvatar());
                holder.setText(R.id.item_name_tv, item.getNickName());
                holder.setText(R.id.item_tel_tv, item.getTel());
                holder.setText(R.id.item_address_tv, item.getAddress());
                if (item.getSex().equals("男生")) {
                    holder.setImageResources(R.id.item_sex_iv, R.drawable.boy);
                } else {
                    holder.setImageResources(R.id.item_sex_iv, R.drawable.girl);
                }
                holder.onClick(R.id.ides_ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //首先如果是自己不能跳转
                        if (!item.getUserId().equals(App.getUser().getUserId())) {
                            //得到我的所有好友，如果已经是好友了,就不用跳转到加好友页面了
                            call(ApiClient.getApis().getAllFriendsList(App.getUser().getUserId()), new MySubscriber<BaseResp<List<User>>>() {
                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(BaseResp<List<User>> resp) {
                                    if (resp.resultCode == Constants.RespCode.SUCCESS) {
                                        if (resp.status == Constants.RespCode.SUCCESS) {
                                            List<User> users = resp.data;
                                            int k = 0;
                                            for (User model : users) {
                                                k++;
                                                if (item.getUserId().equals(model.getUserId())) {
                                                    showToast("你们已经是好友啦！");
                                                    break;
                                                } else {
                                                    if (k == users.size()) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putSerializable("friend", item);
                                                        go(AddFriendActivity.class, bundle);
                                                    }
                                                }
                                            }

                                        }else{
                                            //此时没有好友，直接跳转
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("friend", item);
                                            go(AddFriendActivity.class, bundle);

                                        }
                                    }
                                }

                            });
                        }
                    }
                });

            }
        };
        searchUserResultLv.setAdapter(mAdapter);
    }

    private void initData() {
    }

    private void initListener() {
        //聚焦事件
        searchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    cancelSearchTv.setVisibility(View.VISIBLE);
                    searchUserResultLv.setVisibility(View.GONE);
                    searchAndSearchRv.setVisibility(View.VISIBLE);

                } else {
                    cancelSearchTv.setVisibility(View.GONE);
                    searchUserResultLv.setVisibility(View.VISIBLE);
                    searchEdit.getText().clear();
                }
            }
        });
        //editText内容变化监听
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                if (!StringUtils.isEmpty(searchEdit.getText().toString())) {
                    clearSearchLyt.setVisibility(View.VISIBLE);
                    inputResult.setText(searchEdit.getText().toString().trim());
                } else {
                    clearSearchLyt.setVisibility(View.GONE);
                    inputResult.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //搜一搜
        searchAndSearchRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtils.isEmpty(searchEdit.getText().toString())) {
                    // 先隐藏键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                    startSearch(searchEdit.getText().toString().trim());
                }
            }
        });
        //键盘搜索
        searchEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent event) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    if (!StringUtils.isEmpty(searchEdit.getText().toString())) {
                        // 先隐藏键盘
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                        startSearch(searchEdit.getText().toString().trim());
                    }

                }
                return false;
            }
        });
        //取消搜索
        cancelSearchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        //清除
        clearSearchLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdit.setText("");
            }
        });
    }

    public void startSearch(String key) {
        call(ApiClient.getApis().searchUser(key), new MySubscriber<BaseResp<List<User>>>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResp<List<User>> resp) {
                if (resp.resultCode == Constants.RespCode.SUCCESS) {
                    if (resp.status == Constants.RespCode.SUCCESS) {
                        if (notFindUserRv.getVisibility() == View.VISIBLE) {
                            notFindUserRv.setVisibility(View.GONE);
                        }
                        searchUserResultLv.setVisibility(View.VISIBLE);
                        searchUsers.clear();
                        searchUsers.addAll(resp.data);
                        mAdapter.notifyDataSetChanged();
                    } else if (resp.status == Constants.RespCode.EMPTY) {
                        //没有找到相关用户
                        notFindUserRv.setVisibility(View.VISIBLE);
                        searchUserResultLv.setVisibility(View.GONE);
                    }
                }
            }

        });

    }

}
