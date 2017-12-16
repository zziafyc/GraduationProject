package com.zzia.graduationproject.ui.activity.rongyun;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzia.graduationproject.R;
import com.zzia.graduationproject.event.StringEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.R.bool;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.string;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.IHistoryDataResultCallback;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.model.Event.AudioListenedEvent;
import io.rong.imkit.model.Event.ClearConversationEvent;
import io.rong.imkit.model.Event.ConnectEvent;
import io.rong.imkit.model.Event.ConversationNotificationEvent;
import io.rong.imkit.model.Event.ConversationRemoveEvent;
import io.rong.imkit.model.Event.ConversationTopEvent;
import io.rong.imkit.model.Event.ConversationUnreadEvent;
import io.rong.imkit.model.Event.CreateDiscussionEvent;
import io.rong.imkit.model.Event.DraftEvent;
import io.rong.imkit.model.Event.MessageDeleteEvent;
import io.rong.imkit.model.Event.MessageLeftEvent;
import io.rong.imkit.model.Event.MessageRecallEvent;
import io.rong.imkit.model.Event.MessagesClearEvent;
import io.rong.imkit.model.Event.OnMessageSendErrorEvent;
import io.rong.imkit.model.Event.OnReceiveMessageEvent;
import io.rong.imkit.model.Event.PublicServiceFollowableEvent;
import io.rong.imkit.model.Event.QuitDiscussionEvent;
import io.rong.imkit.model.Event.QuitGroupEvent;
import io.rong.imkit.model.Event.ReadReceiptEvent;
import io.rong.imkit.model.Event.RemoteMessageRecallEvent;
import io.rong.imkit.model.Event.SyncReadStatusEvent;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utilities.OptionsPopupDialog.OnOptionsItemClickedListener;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imkit.widget.adapter.ConversationListAdapter.OnPortraitItemClick;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ReadReceiptMessage;
import io.rong.push.RongPushClient;

public class MyConversationListFragment extends UriFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, OnPortraitItemClick {
    private String TAG = "ConversationListFragment";
    private List<MyConversationListFragment.ConversationConfig> mConversationsConfig;
    private MyConversationListFragment mThis;
    private ConversationListAdapter mAdapter;
    private ListView mList;
    private LinearLayout mNotificationBar;
    private ImageView mNotificationBarImage;
    private TextView mNotificationBarText;
    private boolean isShowWithoutConnected = false;
    private int leftOfflineMsg = 0;

    public MyConversationListFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mThis = this;
        this.TAG = this.getClass().getSimpleName();
        this.mConversationsConfig = new ArrayList();
        EventBus.getDefault().register(this);
        InternalModuleManager.getInstance().onLoaded();
    }

    protected void initFragment(Uri uri) {
        RLog.d(this.TAG, "initFragment " + uri);
        Conversation.ConversationType[] defConversationType = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.SYSTEM, Conversation.ConversationType.CUSTOMER_SERVICE, Conversation.ConversationType.CHATROOM, Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE};
        Conversation.ConversationType[] type = defConversationType;
        int var4 = defConversationType.length;

        int var5;
        for (var5 = 0; var5 < var4; ++var5) {
            Conversation.ConversationType conversationType = type[var5];
            if (uri.getQueryParameter(conversationType.getName()) != null) {
                MyConversationListFragment.ConversationConfig conversationType1 = new MyConversationListFragment.ConversationConfig();
                conversationType1.conversationType = conversationType;
                conversationType1.isGathered = uri.getQueryParameter(conversationType.getName()).equals("true");
                this.mConversationsConfig.add(conversationType1);
            }
        }

        if (this.mConversationsConfig.size() == 0) {
            String var9 = uri.getQueryParameter("type");
            Conversation.ConversationType[] var10 = defConversationType;
            var5 = defConversationType.length;

            for (int var11 = 0; var11 < var5; ++var11) {
                Conversation.ConversationType var12 = var10[var11];
                if (var12.getName().equals(var9)) {
                    MyConversationListFragment.ConversationConfig config = new MyConversationListFragment.ConversationConfig();
                    config.conversationType = var12;
                    config.isGathered = false;
                    this.mConversationsConfig.add(config);
                    break;
                }
            }
        }
        this.mAdapter.clear();
        if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.DISCONNECTED)) {
            RLog.d(this.TAG, "RongCloud haven\'t been connected yet, so the conversation list display blank !!!");
            this.isShowWithoutConnected = true;
        } else {
            this.getConversationList(this.getConfigConversationTypes());
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rc_fr_conversationlist, container, false);
        this.mNotificationBar = (LinearLayout) this.findViewById(view, id.rc_status_bar);
        this.mNotificationBar.setVisibility(View.GONE);
        this.mNotificationBarImage = (ImageView) this.findViewById(view, id.rc_status_bar_image);
        this.mNotificationBarText = (TextView) this.findViewById(view, id.rc_status_bar_text);
        View emptyView = this.findViewById(view, id.rc_conversation_list_empty_layout);
        TextView emptyText = (TextView) this.findViewById(view, id.rc_empty_tv);
        emptyText.setText(this.getActivity().getResources().getString(string.rc_conversation_list_empty_prompt));
        this.mList = this.findViewById(view, id.rc_list);
        this.mList.setEmptyView(emptyView);
        this.mList.setOnItemClickListener(this);
        this.mList.setOnItemLongClickListener(this);
        if (this.mAdapter == null) {
            this.mAdapter = this.onResolveAdapter(this.getActivity());
        }

        this.mAdapter.setOnPortraitItemClick(this);
        this.mList.setAdapter(this.mAdapter);
       /* final ItemMenuTopMarkDeleteCreator creator = new ItemMenuTopMarkDeleteCreator(getActivity());
        this.mList.setMenuCreator(creator);
        mList.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {


            }

            @Override
            public void onMenuClose(int position) {

            }
        });
        this.mList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //置顶
                        String topTitle = menu.getMenuItem(0).getTitle();
                        if (topTitle.equals("置顶")) {
                            menu.getMenuItem(0).setTitle("取消置顶");
                        } else {
                            menu.getMenuItem(0).setTitle("置顶");
                        }
                        UIConversation mUIConversation = (UIConversation) mAdapter.getItem(position);
                        onEventMainThread(new ConversationTopEvent(Conversation.ConversationType.PRIVATE, mUIConversation.getConversationTargetId(), mUIConversation.isTop() ? false : true));
                        break;
                    case 1:
                        //标为未读
                        UIConversation mUIConversation2 = (UIConversation) mAdapter.getItem(position);
                        onEventMainThread(new ConversationUnreadEvent(Conversation.ConversationType.PRIVATE, mUIConversation2.getConversationTargetId()));
                        break;
                    case 2:
                        //删除
                        UIConversation mUIConversation3 = (UIConversation) mAdapter.getItem(position);
                        onEventMainThread(new ConversationRemoveEvent(Conversation.ConversationType.PRIVATE, mUIConversation3.getConversationTargetId()));
                        break;
                }
                return false;
            }
        });*/
        return view;
    }

    public void onResume() {
        super.onResume();
        RLog.d(this.TAG, "onResume " + RongIM.getInstance().getCurrentConnectionStatus());
        RongPushClient.clearAllPushNotifications(this.getActivity());
        this.setNotificationBarVisibility(RongIM.getInstance().getCurrentConnectionStatus());
    }

    private void getConversationList(Conversation.ConversationType[] conversationTypes) {
        this.getConversationList(conversationTypes, new IHistoryDataResultCallback<List<Conversation>>() {
            public void onResult(List<Conversation> data) {
                if (data != null && data.size() > 0) {
                    MyConversationListFragment.this.makeUiConversationList(data);
                    RLog.d(MyConversationListFragment.this.TAG, "getConversationList : listSize = " + data.size());
                    MyConversationListFragment.this.mAdapter.notifyDataSetChanged();
                    MyConversationListFragment.this.onUnreadCountChanged();
                } else {
                    RLog.e(MyConversationListFragment.this.TAG, "getConversationList return null " + RongIMClient.getInstance().getCurrentConnectionStatus());
                    MyConversationListFragment.this.isShowWithoutConnected = true;
                }

                MyConversationListFragment.this.onFinishLoadConversationList(MyConversationListFragment.this.leftOfflineMsg);
            }

            public void onError() {
                RLog.e(MyConversationListFragment.this.TAG, "getConversationList Error");
                MyConversationListFragment.this.onFinishLoadConversationList(MyConversationListFragment.this.leftOfflineMsg);
            }
        });
    }

    public void getConversationList(Conversation.ConversationType[] conversationTypes, final IHistoryDataResultCallback<List<Conversation>> callback) {
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            public void onSuccess(List<Conversation> conversations) {
                if (callback != null) {
                    callback.onResult(conversations);
                }

            }

            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null) {
                    callback.onError();
                }

            }
        }, conversationTypes);
    }

    public void focusUnreadItem() {
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        int visibleCount = last - first + 1;
        int count = this.mList.getCount();
        if (visibleCount < count) {
            int index;
            if (last < count - 1) {
                index = first + 1;
            } else {
                index = 0;
            }

            if (!this.selectNextUnReadItem(index, count)) {
                this.selectNextUnReadItem(0, count);
            }
        }

    }

    private boolean selectNextUnReadItem(int startIndex, int totalCount) {
        int index = -1;

        for (int i = startIndex; i < totalCount; ++i) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
            if (uiConversation.getUnReadMessageCount() > 0) {
                index = i;
                break;
            }
        }

        if (index >= 0 && index < totalCount) {
            this.mList.setSelection(index);
            return true;
        } else {
            return false;
        }
    }

    private void setNotificationBarVisibility(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
        if (!this.getResources().getBoolean(bool.rc_is_show_warning_notification)) {
            RLog.e(this.TAG, "rc_is_show_warning_notification is disabled.");
        } else {
            String content = null;
            if (status.equals(ConnectionStatus.NETWORK_UNAVAILABLE)) {
                content = this.getResources().getString(string.rc_notice_network_unavailable);
            } else if (status.equals(ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT)) {
                content = this.getResources().getString(string.rc_notice_tick);
            } else if (status.equals(ConnectionStatus.CONNECTED)) {
                this.mNotificationBar.setVisibility(View.GONE);
            } else if (status.equals(ConnectionStatus.DISCONNECTED)) {
                content = this.getResources().getString(string.rc_notice_disconnect);
            } else if (status.equals(ConnectionStatus.CONNECTING)) {
                content = this.getResources().getString(string.rc_notice_connecting);
            }

            if (content != null && this.mNotificationBar != null) {
                final String content2 = content;
                if (this.mNotificationBar.getVisibility() == View.GONE) {
                    this.getHandler().postDelayed(new Runnable() {
                        public void run() {
                            if (!RongIMClient.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.CONNECTED)) {
                                MyConversationListFragment.this.mNotificationBar.setVisibility(View.VISIBLE);
                                MyConversationListFragment.this.mNotificationBarText.setText(content2);
                                if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.CONNECTING)) {
                                    MyConversationListFragment.this.mNotificationBarImage.setImageResource(drawable.rc_notification_connecting_animated);
                                } else {
                                    MyConversationListFragment.this.mNotificationBarImage.setImageResource(drawable.rc_notification_network_available);
                                }
                            }

                        }
                    }, 4000L);
                } else {
                    this.mNotificationBarText.setText(content);
                    if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.CONNECTING)) {
                        this.mNotificationBarImage.setImageResource(drawable.rc_notification_connecting_animated);
                    } else {
                        this.mNotificationBarImage.setImageResource(drawable.rc_notification_network_available);
                    }
                }
            }

        }
    }

    public boolean onBackPressed() {
        return false;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setAdapter(ConversationListAdapter adapter) {
        this.mAdapter = adapter;
        if (this.mList != null) {
            this.mList.setAdapter(adapter);
        }

    }

    public ConversationListAdapter onResolveAdapter(Context context) {
        this.mAdapter = new ConversationListAdapter(context);
        return this.mAdapter;
    }

    public void onEventMainThread(SyncReadStatusEvent event) {
        Conversation.ConversationType conversationType = event.getConversationType();
        String targetId = event.getTargetId();
        RLog.d(this.TAG, "SyncReadStatusEvent " + conversationType + " " + targetId);
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        int position;
        if (this.getGatherState(conversationType)) {
            position = this.mAdapter.findGatheredItem(conversationType);
        } else {
            position = this.mAdapter.findPosition(conversationType, targetId);
        }

        if (position >= 0) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
            uiConversation.clearUnRead(conversationType, targetId);
            if (position >= first && position <= last) {
                this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
            }
        }

        this.onUnreadCountChanged();
    }

    public void onEventMainThread(ReadReceiptEvent event) {
        Conversation.ConversationType conversationType = event.getMessage().getConversationType();
        String targetId = event.getMessage().getTargetId();
        int originalIndex = this.mAdapter.findPosition(conversationType, targetId);
        boolean gatherState = this.getGatherState(conversationType);
        if (!gatherState && originalIndex >= 0) {
            UIConversation conversation = (UIConversation) this.mAdapter.getItem(originalIndex);
            ReadReceiptMessage content = (ReadReceiptMessage) event.getMessage().getContent();
            if (content.getLastMessageSendTime() >= conversation.getUIConversationTime() && conversation.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) {
                conversation.setSentStatus(SentStatus.READ);
                this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
            }
        }

    }

    public void onEventMainThread(AudioListenedEvent event) {
        Message message = event.getMessage();
        Conversation.ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        RLog.d(this.TAG, "Message: " + message.getObjectName() + " " + conversationType + " " + message.getSentStatus());
        if (this.isConfigured(conversationType)) {
            boolean gathered = this.getGatherState(conversationType);
            int position = gathered ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
            if (position >= 0) {
                UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
                if (message.getMessageId() == uiConversation.getLatestMessageId()) {
                    uiConversation.updateConversation(message, gathered);
                    this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
                }
            }
        }

    }

    public boolean shouldUpdateConversation(Message message, int left) {
        return true;
    }

    public void onUnreadCountChanged() {
    }

    public void onFinishLoadConversationList(int leftOfflineMsg) {
    }

    public void onUIConversationCreated(UIConversation uiConversation) {
    }

    public void updateListItem(UIConversation uiConversation) {
        int position = this.mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
        if (position >= 0) {
            this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
        }

    }

    public void onEventMainThread(OnReceiveMessageEvent event) {
        this.leftOfflineMsg = event.getLeft();
        Message message = event.getMessage();
        String targetId = message.getTargetId();
        Conversation.ConversationType conversationType = message.getConversationType();
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        if (this.isConfigured(message.getConversationType()) && this.shouldUpdateConversation(event.getMessage(), event.getLeft())) {
            if (message.getMessageId() > 0) {
                boolean gathered = this.getGatherState(conversationType);
                int position;
                if (gathered) {
                    position = this.mAdapter.findGatheredItem(conversationType);
                } else {
                    position = this.mAdapter.findPosition(conversationType, targetId);
                }

                UIConversation uiConversation;
                int index;
                if (position < 0) {
                    uiConversation = UIConversation.obtain(message, gathered);
                    index = this.getPosition(uiConversation);
                    this.mAdapter.add(uiConversation, index);
                    this.onUIConversationCreated(uiConversation);
                    this.mAdapter.notifyDataSetChanged();
                } else {
                    uiConversation = (UIConversation) this.mAdapter.getItem(position);
                    if (event.getMessage().getSentTime() > uiConversation.getUIConversationTime()) {
                        uiConversation.updateConversation(message, gathered);
                        this.mAdapter.remove(position);
                        index = this.getPosition(uiConversation);
                        if (index == position) {
                            this.mAdapter.add(uiConversation, index);
                            if (index >= first && index <= last) {
                                this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
                            }
                        } else {
                            this.mAdapter.add(uiConversation, index);
                            if (index >= first && index <= last) {
                                this.mAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        RLog.i(this.TAG, "ignore update message " + event.getMessage().getObjectName());
                    }
                }

                RLog.i(this.TAG, "conversation unread count : " + uiConversation.getUnReadMessageCount() + " " + conversationType + " " + targetId);
            }

            if (event.getLeft() == 0) {
                this.syncUnreadCount();
            }

            RLog.d(this.TAG, "OnReceiveMessageEvent: " + message.getObjectName() + " " + event.getLeft() + " " + conversationType + " " + targetId);
        }

    }

    public void onEventMainThread(MessageLeftEvent event) {
        if (event.left == 0) {
            this.syncUnreadCount();
        }

    }

    private void syncUnreadCount() {
        if (this.mAdapter.getCount() > 0) {
            final int first = this.mList.getFirstVisiblePosition();
            final int last = this.mList.getLastVisiblePosition();

            for (int i = 0; i < this.mAdapter.getCount(); ++i) {
                final UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
                Conversation.ConversationType conversationType = uiConversation.getConversationType();
                String targetId = uiConversation.getConversationTargetId();
                final int position;
                if (this.getGatherState(conversationType)) {
                    position = this.mAdapter.findGatheredItem(conversationType);
                    RongIMClient.getInstance().getUnreadCount(new RongIMClient.ResultCallback<Integer>() {
                        public void onSuccess(Integer integer) {
                            uiConversation.setUnReadMessageCount(integer.intValue());
                            if (position >= first && position <= last) {
                                MyConversationListFragment.this.mAdapter.getView(position, MyConversationListFragment.this.mList.getChildAt(position - MyConversationListFragment.this.mList.getFirstVisiblePosition()), MyConversationListFragment.this.mList);
                            }

                            MyConversationListFragment.this.onUnreadCountChanged();
                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    }, new Conversation.ConversationType[]{conversationType});
                } else {
                    position = this.mAdapter.findPosition(conversationType, targetId);
                    RongIMClient.getInstance().getUnreadCount(conversationType, targetId, new RongIMClient.ResultCallback<Integer>() {
                        public void onSuccess(Integer integer) {
                            uiConversation.setUnReadMessageCount(integer.intValue());
                            if (position >= first && position <= last) {
                                MyConversationListFragment.this.mAdapter.getView(position, MyConversationListFragment.this.mList.getChildAt(position - MyConversationListFragment.this.mList.getFirstVisiblePosition()), MyConversationListFragment.this.mList);
                            }

                            MyConversationListFragment.this.onUnreadCountChanged();
                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }
            }
        }

    }

    public void onEventMainThread(MessageRecallEvent event) {
        RLog.d(this.TAG, "MessageRecallEvent");
        int count = this.mAdapter.getCount();

        for (int i = 0; i < count; ++i) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
            if (event.getMessageId() == uiConversation.getLatestMessageId()) {
                boolean gatherState = ((UIConversation) this.mAdapter.getItem(i)).getConversationGatherState();
                final String targetId = ((UIConversation) this.mAdapter.getItem(i)).getConversationTargetId();
                if (gatherState) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                        public void onSuccess(List<Conversation> conversationList) {
                            if (conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = MyConversationListFragment.this.makeUIConversation(conversationList);
                                int oldPos = MyConversationListFragment.this.mAdapter.findPosition(uiConversation.getConversationType(), targetId);
                                if (oldPos >= 0) {
                                    MyConversationListFragment.this.mAdapter.remove(oldPos);
                                }

                                int newIndex = MyConversationListFragment.this.getPosition(uiConversation);
                                MyConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
                                MyConversationListFragment.this.mAdapter.notifyDataSetChanged();
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    }, new Conversation.ConversationType[]{uiConversation.getConversationType()});
                } else {
                    RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), new RongIMClient.ResultCallback<Conversation>() {
                        public void onSuccess(Conversation conversation) {
                            if (conversation != null) {
                                UIConversation uiConversation = UIConversation.obtain(conversation, false);
                                int pos = MyConversationListFragment.this.mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                if (pos >= 0) {
                                    MyConversationListFragment.this.mAdapter.remove(pos);
                                }

                                int newPosition = MyConversationListFragment.this.getPosition(uiConversation);
                                MyConversationListFragment.this.mAdapter.add(uiConversation, newPosition);
                                MyConversationListFragment.this.mAdapter.notifyDataSetChanged();
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }
                break;
            }
        }

    }

    public void onEventMainThread(RemoteMessageRecallEvent event) {
        RLog.d(this.TAG, "RemoteMessageRecallEvent");
        int count = this.mAdapter.getCount();

        for (int i = 0; i < count; ++i) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
            if (event.getMessageId() == uiConversation.getLatestMessageId()) {
                boolean gatherState = uiConversation.getConversationGatherState();
                final String targetId = ((UIConversation) this.mAdapter.getItem(i)).getConversationTargetId();
                if (gatherState) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                        public void onSuccess(List<Conversation> conversationList) {
                            if (conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = MyConversationListFragment.this.makeUIConversation(conversationList);
                                int oldPos = MyConversationListFragment.this.mAdapter.findPosition(uiConversation.getConversationType(), targetId);
                                if (oldPos >= 0) {
                                    MyConversationListFragment.this.mAdapter.remove(oldPos);
                                }

                                int newIndex = MyConversationListFragment.this.getPosition(uiConversation);
                                MyConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
                                MyConversationListFragment.this.mAdapter.notifyDataSetChanged();
                                MyConversationListFragment.this.onUnreadCountChanged();
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    }, new Conversation.ConversationType[]{((UIConversation) this.mAdapter.getItem(i)).getConversationType()});
                } else {
                    RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), new RongIMClient.ResultCallback<Conversation>() {
                        public void onSuccess(Conversation conversation) {
                            if (conversation != null) {
                                UIConversation temp = UIConversation.obtain(conversation, false);
                                int pos = MyConversationListFragment.this.mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                if (pos >= 0) {
                                    MyConversationListFragment.this.mAdapter.remove(pos);
                                }

                                int newPosition = MyConversationListFragment.this.getPosition(temp);
                                MyConversationListFragment.this.mAdapter.add(temp, newPosition);
                                MyConversationListFragment.this.mAdapter.notifyDataSetChanged();
                                MyConversationListFragment.this.onUnreadCountChanged();
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }
                break;
            }
        }

    }

    public void onEventMainThread(Message message) {
        Conversation.ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        RLog.d(this.TAG, "Message: " + message.getObjectName() + " " + message.getMessageId() + " " + conversationType + " " + message.getSentStatus());
        boolean gathered = this.getGatherState(conversationType);
        if (this.isConfigured(conversationType) && message.getMessageId() > 0) {
            int position = gathered ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
            UIConversation uiConversation;
            int index;
            if (position < 0) {
                uiConversation = UIConversation.obtain(message, gathered);
                index = this.getPosition(uiConversation);
                this.mAdapter.add(uiConversation, index);
                this.onUIConversationCreated(uiConversation);
                this.mAdapter.notifyDataSetChanged();
            } else {
                uiConversation = (UIConversation) this.mAdapter.getItem(position);
                this.mAdapter.remove(position);
                uiConversation.updateConversation(message, gathered);
                index = this.getPosition(uiConversation);
                this.mAdapter.add(uiConversation, index);
                if (position == index) {
                    this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
                } else {
                    this.mAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    public void onEventMainThread(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
        RLog.d(this.TAG, "ConnectionStatus, " + status.toString());
        this.setNotificationBarVisibility(status);
    }

    public void onEventMainThread(ConnectEvent event) {
        if (this.isShowWithoutConnected) {
            this.getConversationList(this.getConfigConversationTypes());
            this.isShowWithoutConnected = false;
        }

    }

    public void onEventMainThread(final CreateDiscussionEvent createDiscussionEvent) {
        RLog.d(this.TAG, "createDiscussionEvent");
        final String targetId = createDiscussionEvent.getDiscussionId();
        if (this.isConfigured(Conversation.ConversationType.DISCUSSION)) {
            RongIMClient.getInstance().getConversation(Conversation.ConversationType.DISCUSSION, targetId, new RongIMClient.ResultCallback<Conversation>() {
                public void onSuccess(Conversation conversation) {
                    if (conversation != null) {
                        int position;
                        if (MyConversationListFragment.this.getGatherState(Conversation.ConversationType.DISCUSSION)) {
                            position = MyConversationListFragment.this.mAdapter.findGatheredItem(Conversation.ConversationType.DISCUSSION);
                        } else {
                            position = MyConversationListFragment.this.mAdapter.findPosition(Conversation.ConversationType.DISCUSSION, targetId);
                        }

                        conversation.setConversationTitle(createDiscussionEvent.getDiscussionName());
                        UIConversation uiConversation;
                        if (position < 0) {
                            uiConversation = UIConversation.obtain(conversation, MyConversationListFragment.this.getGatherState(Conversation.ConversationType.DISCUSSION));
                            int index = MyConversationListFragment.this.getPosition(uiConversation);
                            MyConversationListFragment.this.mAdapter.add(uiConversation, index);
                            MyConversationListFragment.this.onUIConversationCreated(uiConversation);
                            MyConversationListFragment.this.mAdapter.notifyDataSetChanged();
                        } else {
                            uiConversation = (UIConversation) MyConversationListFragment.this.mAdapter.getItem(position);
                            uiConversation.updateConversation(conversation, MyConversationListFragment.this.getGatherState(Conversation.ConversationType.DISCUSSION));
                            MyConversationListFragment.this.mAdapter.getView(position, MyConversationListFragment.this.mList.getChildAt(position - MyConversationListFragment.this.mList.getFirstVisiblePosition()), MyConversationListFragment.this.mList);
                        }
                    }

                }

                public void onError(RongIMClient.ErrorCode e) {
                }
            });
        }

    }

    public void onEventMainThread(final DraftEvent draft) {
        Conversation.ConversationType conversationType = draft.getConversationType();
        String targetId = draft.getTargetId();
        RLog.i(this.TAG, "Draft : " + conversationType);
        if (this.isConfigured(conversationType)) {
            final boolean gathered = this.getGatherState(conversationType);
            final int position = gathered ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
            RongIMClient.getInstance().getConversation(conversationType, targetId, new RongIMClient.ResultCallback<Conversation>() {
                public void onSuccess(Conversation conversation) {
                    if (conversation != null) {
                        UIConversation uiConversation;
                        if (position < 0) {
                            if (!TextUtils.isEmpty(draft.getContent())) {
                                uiConversation = UIConversation.obtain(conversation, gathered);
                                int index = MyConversationListFragment.this.getPosition(uiConversation);
                                MyConversationListFragment.this.mAdapter.add(uiConversation, index);
                                MyConversationListFragment.this.onUIConversationCreated(uiConversation);
                                MyConversationListFragment.this.mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            uiConversation = (UIConversation) MyConversationListFragment.this.mAdapter.getItem(position);
                            if (TextUtils.isEmpty(draft.getContent()) && !TextUtils.isEmpty(uiConversation.getDraft()) || !TextUtils.isEmpty(draft.getContent()) && TextUtils.isEmpty(uiConversation.getDraft()) || !TextUtils.isEmpty(draft.getContent()) && !TextUtils.isEmpty(uiConversation.getDraft()) && !draft.getContent().equals(uiConversation.getDraft())) {
                                uiConversation.updateConversation(conversation, gathered);
                                MyConversationListFragment.this.mAdapter.getView(position, MyConversationListFragment.this.mList.getChildAt(position - MyConversationListFragment.this.mList.getFirstVisiblePosition()), MyConversationListFragment.this.mList);
                            }
                        }
                    }

                }

                public void onError(RongIMClient.ErrorCode e) {
                }
            });
        }

    }

    public void onEventMainThread(Group groupInfo) {
        RLog.d(this.TAG, "Group: " + groupInfo.getName() + " " + groupInfo.getId());
        int count = this.mAdapter.getCount();
        if (groupInfo.getName() != null) {
            int last = this.mList.getLastVisiblePosition();
            int first = this.mList.getFirstVisiblePosition();

            for (int i = 0; i < count; ++i) {
                UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
                uiConversation.updateConversation(groupInfo);
                if (i >= first && i <= last) {
                    this.mAdapter.getView(i, this.mList.getChildAt(i - first), this.mList);
                }
            }

        }
    }

    public void onEventMainThread(Discussion discussion) {
        RLog.d(this.TAG, "Discussion: " + discussion.getName() + " " + discussion.getId());
        if (this.isConfigured(Conversation.ConversationType.DISCUSSION)) {
            int last = this.mList.getLastVisiblePosition();
            int first = this.mList.getFirstVisiblePosition();
            int position;
            if (this.getGatherState(Conversation.ConversationType.DISCUSSION)) {
                position = this.mAdapter.findGatheredItem(Conversation.ConversationType.DISCUSSION);
            } else {
                position = this.mAdapter.findPosition(Conversation.ConversationType.DISCUSSION, discussion.getId());
            }

            if (position >= 0) {
                for (int i = 0; i == position; ++i) {
                    UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
                    uiConversation.updateConversation(discussion);
                    if (i >= first && i <= last) {
                        this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
                    }
                }
            }
        }

    }

    public void onEventMainThread(GroupUserInfo groupUserInfo) {
        RLog.d(this.TAG, "GroupUserInfo " + groupUserInfo.getGroupId() + " " + groupUserInfo.getUserId() + " " + groupUserInfo.getNickname());
        if (groupUserInfo.getNickname() != null && groupUserInfo.getGroupId() != null) {
            int count = this.mAdapter.getCount();
            int last = this.mList.getLastVisiblePosition();
            int first = this.mList.getFirstVisiblePosition();

            for (int i = 0; i < count; ++i) {
                UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
                if (!this.getGatherState(Conversation.ConversationType.GROUP) && uiConversation.getConversationTargetId().equals(groupUserInfo.getGroupId()) && uiConversation.getConversationSenderId().equals(groupUserInfo.getUserId())) {
                    uiConversation.updateConversation(groupUserInfo);
                    if (i >= first && i <= last) {
                        this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
                    }
                }
            }

        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        RLog.i(this.TAG, "UserInfo " + userInfo.getUserId() + " " + userInfo.getName());
        int count = this.mAdapter.getCount();
        int last = this.mList.getLastVisiblePosition();
        int first = this.mList.getFirstVisiblePosition();

        for (int i = 0; i < count && userInfo.getName() != null; ++i) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
            if (uiConversation.hasNickname(userInfo.getUserId())) {
                RLog.i(this.TAG, "has nick name");
            } else {
                uiConversation.updateConversation(userInfo);
                if (i >= first && i <= last) {
                    this.mAdapter.getView(i, this.mList.getChildAt(i - first), this.mList);
                }
            }
        }

    }

    public void onEventMainThread(PublicServiceProfile profile) {
        RLog.d(this.TAG, "PublicServiceProfile");
        int count = this.mAdapter.getCount();
        boolean gatherState = this.getGatherState(profile.getConversationType());

        for (int i = 0; i < count; ++i) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
            if (uiConversation.getConversationType().equals(profile.getConversationType()) && uiConversation.getConversationTargetId().equals(profile.getTargetId()) && !gatherState) {
                uiConversation.setUIConversationTitle(profile.getName());
                uiConversation.setIconUrl(profile.getPortraitUri());
                this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
                break;
            }
        }

    }

    public void onEventMainThread(PublicServiceFollowableEvent event) {
        RLog.d(this.TAG, "PublicServiceFollowableEvent");
        if (!event.isFollow()) {
            int originalIndex = this.mAdapter.findPosition(event.getConversationType(), event.getTargetId());
            if (originalIndex >= 0) {
                this.mAdapter.remove(originalIndex);
                this.mAdapter.notifyDataSetChanged();
            }
        }

    }

    public void onEventMainThread(ConversationUnreadEvent unreadEvent) {
        RLog.d(this.TAG, "ConversationUnreadEvent");
        Conversation.ConversationType conversationType = unreadEvent.getType();
        String targetId = unreadEvent.getTargetId();
        int position = this.getGatherState(conversationType) ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
        if (position >= 0) {
            int first = this.mList.getFirstVisiblePosition();
            int last = this.mList.getLastVisiblePosition();
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
            uiConversation.clearUnRead(conversationType, targetId);
            if (position >= first && position <= last) {
                this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
            }
        }

        this.onUnreadCountChanged();
    }

    public void onEventMainThread(ConversationTopEvent setTopEvent) {
        RLog.d(this.TAG, "ConversationTopEvent");
        Conversation.ConversationType conversationType = setTopEvent.getConversationType();
        String targetId = setTopEvent.getTargetId();
        int position = this.mAdapter.findPosition(conversationType, targetId);
        if (position >= 0 && !this.getGatherState(conversationType)) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
            if (uiConversation.isTop() != setTopEvent.isTop()) {
                uiConversation.setTop(!uiConversation.isTop());
                this.mAdapter.remove(position);
                int index = this.getPosition(uiConversation);
                this.mAdapter.add(uiConversation, index);
                if (index == position) {
                    this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
                } else {
                    this.mAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    public void onEventMainThread(ConversationRemoveEvent removeEvent) {
        RLog.d(this.TAG, "ConversationRemoveEvent");
        Conversation.ConversationType conversationType = removeEvent.getType();
        this.removeConversation(conversationType, removeEvent.getTargetId());
    }

    public void onEventMainThread(ClearConversationEvent clearConversationEvent) {
        RLog.d(this.TAG, "ClearConversationEvent");
        List typeList = clearConversationEvent.getTypes();

        for (int i = this.mAdapter.getCount() - 1; i >= 0; --i) {
            if (typeList.indexOf(((UIConversation) this.mAdapter.getItem(i)).getConversationType()) >= 0) {
                this.mAdapter.remove(i);
            }
        }

        this.mAdapter.notifyDataSetChanged();
        this.onUnreadCountChanged();
    }

    public void onEventMainThread(MessageDeleteEvent event) {
        RLog.d(this.TAG, "MessageDeleteEvent");
        int count = this.mAdapter.getCount();

        for (int i = 0; i < count; ++i) {
            if (event.getMessageIds().contains(Integer.valueOf(((UIConversation) this.mAdapter.getItem(i)).getLatestMessageId()))) {
                boolean gatherState = ((UIConversation) this.mAdapter.getItem(i)).getConversationGatherState();
                final String targetId = ((UIConversation) this.mAdapter.getItem(i)).getConversationTargetId();
                if (gatherState) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                        public void onSuccess(List<Conversation> conversationList) {
                            if (conversationList != null && conversationList.size() != 0) {
                                UIConversation uiConversation = MyConversationListFragment.this.makeUIConversation(conversationList);
                                int oldPos = MyConversationListFragment.this.mAdapter.findPosition(uiConversation.getConversationType(), targetId);
                                if (oldPos >= 0) {
                                    MyConversationListFragment.this.mAdapter.remove(oldPos);
                                }

                                int newIndex = MyConversationListFragment.this.getPosition(uiConversation);
                                MyConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
                                MyConversationListFragment.this.mAdapter.notifyDataSetChanged();
                            }
                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    }, new Conversation.ConversationType[]{((UIConversation) this.mAdapter.getItem(i)).getConversationType()});
                } else {
                    RongIM.getInstance().getConversation(((UIConversation) this.mAdapter.getItem(i)).getConversationType(), ((UIConversation) this.mAdapter.getItem(i)).getConversationTargetId(), new RongIMClient.ResultCallback<Conversation>() {
                        public void onSuccess(Conversation conversation) {
                            if (conversation == null) {
                                RLog.d(MyConversationListFragment.this.TAG, "onEventMainThread getConversation : onSuccess, conversation = null");
                            } else {
                                UIConversation uiConversation = UIConversation.obtain(conversation, false);
                                int pos = MyConversationListFragment.this.mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                if (pos >= 0) {
                                    MyConversationListFragment.this.mAdapter.remove(pos);
                                }

                                int newIndex = MyConversationListFragment.this.getPosition(uiConversation);
                                MyConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
                                MyConversationListFragment.this.mAdapter.notifyDataSetChanged();
                            }
                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }
                break;
            }
        }

    }

    public void onEventMainThread(ConversationNotificationEvent notificationEvent) {
        int originalIndex = this.mAdapter.findPosition(notificationEvent.getConversationType(), notificationEvent.getTargetId());
        if (originalIndex >= 0) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(originalIndex);
            if (!uiConversation.getNotificationStatus().equals(notificationEvent.getStatus())) {
                uiConversation.setNotificationStatus(notificationEvent.getStatus());
                this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
            }

            this.onUnreadCountChanged();
        }

    }

    public void onEventMainThread(MessagesClearEvent clearMessagesEvent) {
        RLog.d(this.TAG, "MessagesClearEvent");
        Conversation.ConversationType conversationType = clearMessagesEvent.getType();
        String targetId = clearMessagesEvent.getTargetId();
        int position = this.getGatherState(conversationType) ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
        if (position >= 0) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
            uiConversation.clearLastMessage();
            this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
        }

    }

    public void onEventMainThread(OnMessageSendErrorEvent sendErrorEvent) {
        Message message = sendErrorEvent.getMessage();
        Conversation.ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        if (this.isConfigured(conversationType)) {
            int first = this.mList.getFirstVisiblePosition();
            int last = this.mList.getLastVisiblePosition();
            boolean gathered = this.getGatherState(conversationType);
            int index = gathered ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
            if (index >= 0) {
                UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(index);
                message.setSentStatus(SentStatus.FAILED);
                uiConversation.updateConversation(message, gathered);
                if (index >= first && index <= last) {
                    this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
                }
            }
        }

    }

    public void onEventMainThread(QuitDiscussionEvent event) {
        RLog.d(this.TAG, "QuitDiscussionEvent");
        this.removeConversation(Conversation.ConversationType.DISCUSSION, event.getDiscussionId());
    }

    public void onEventMainThread(QuitGroupEvent event) {
        RLog.d(this.TAG, "QuitGroupEvent");
        this.removeConversation(Conversation.ConversationType.GROUP, event.getGroupId());
    }

    private void removeConversation(final Conversation.ConversationType conversationType, String targetId) {
        boolean gathered = this.getGatherState(conversationType);
        int index;
        if (gathered) {
            index = this.mAdapter.findGatheredItem(conversationType);
            if (index >= 0) {
                RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                    public void onSuccess(List<Conversation> conversationList) {
                        int oldPos = MyConversationListFragment.this.mAdapter.findGatheredItem(conversationType);
                        if (oldPos >= 0) {
                            MyConversationListFragment.this.mAdapter.remove(oldPos);
                            if (conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = MyConversationListFragment.this.makeUIConversation(conversationList);
                                int newIndex = MyConversationListFragment.this.getPosition(uiConversation);
                                MyConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
                            }

                            MyConversationListFragment.this.mAdapter.notifyDataSetChanged();
                            MyConversationListFragment.this.onUnreadCountChanged();
                        }

                    }

                    public void onError(RongIMClient.ErrorCode e) {
                    }
                }, new Conversation.ConversationType[]{conversationType});
            }
        } else {
            index = this.mAdapter.findPosition(conversationType, targetId);
            if (index >= 0) {
                this.mAdapter.remove(index);
                this.mAdapter.notifyDataSetChanged();
                this.onUnreadCountChanged();
            }
        }

    }

    public void onPortraitItemClick(View v, UIConversation data) {
        Conversation.ConversationType type = data.getConversationType();
        if (this.getGatherState(type)) {
            RongIM.getInstance().startSubConversationList(this.getActivity(), type);
        } else {
            if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
                boolean isDefault = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitClick(this.getActivity(), type, data.getConversationTargetId());
                if (isDefault) {
                    return;
                }
            }

            data.setUnReadMessageCount(0);
            RongIM.getInstance().startConversation(this.getActivity(), type, data.getConversationTargetId(), data.getUIConversationTitle());
        }

    }

    public boolean onPortraitItemLongClick(View v, UIConversation data) {
        Conversation.ConversationType type = data.getConversationType();
        if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
            boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitLongClick(this.getActivity(), type, data.getConversationTargetId());
            if (isDealt) {
                return true;
            }
        }

        if (!this.getGatherState(type)) {
            this.buildMultiDialog(data);
            return true;
        } else {
            this.buildSingleDialog(data);
            return true;
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
        Conversation.ConversationType conversationType = uiConversation.getConversationType();
        if (this.getGatherState(conversationType)) {
            RongIM.getInstance().startSubConversationList(this.getActivity(), conversationType);
        } else {
            if (RongContext.getInstance().getConversationListBehaviorListener() != null && RongContext.getInstance().getConversationListBehaviorListener().onConversationClick(this.getActivity(), view, uiConversation)) {
                return;
            }

            uiConversation.setUnReadMessageCount(0);
            RongIM.getInstance().startConversation(this.getActivity(), conversationType, uiConversation.getConversationTargetId(), uiConversation.getUIConversationTitle());
        }

    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
        if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
            boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationLongClick(this.getActivity(), view, uiConversation);
            if (isDealt) {
                return true;
            }
        }

        if (!this.getGatherState(uiConversation.getConversationType())) {
            this.buildMultiDialog(uiConversation);
            return true;
        } else {
            this.buildSingleDialog(uiConversation);
            return true;
        }
    }

    private void buildMultiDialog(final UIConversation uiConversation) {
        String[] items = new String[2];
        if (uiConversation.isTop()) {
            items[0] = RongContext.getInstance().getString(string.rc_conversation_list_dialog_cancel_top);
        } else {
            items[0] = RongContext.getInstance().getString(string.rc_conversation_list_dialog_set_top);
        }

        items[1] = RongContext.getInstance().getString(string.rc_conversation_list_dialog_remove);
        OptionsPopupDialog.newInstance(this.getActivity(), items).setOptionsPopupDialogListener(new OnOptionsItemClickedListener() {
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    RongIM.getInstance().setConversationToTop(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), !uiConversation.isTop(), new RongIMClient.ResultCallback<Boolean>() {
                        public void onSuccess(Boolean aBoolean) {
                            if (uiConversation.isTop()) {
                                Toast.makeText(RongContext.getInstance(), MyConversationListFragment.this.getString(string.rc_conversation_list_popup_cancel_top), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RongContext.getInstance(), MyConversationListFragment.this.getString(string.rc_conversation_list_dialog_set_top), Toast.LENGTH_SHORT).show();
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                } else if (which == 1) {
                    RongIM.getInstance().removeConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), (RongIMClient.ResultCallback) null);
                }

            }
        }).show();
    }

    private void buildSingleDialog(final UIConversation uiConversation) {
        String[] items = new String[]{RongContext.getInstance().getString(string.rc_conversation_list_dialog_remove)};
        OptionsPopupDialog.newInstance(this.getActivity(), items).setOptionsPopupDialogListener(new OnOptionsItemClickedListener() {
            public void onOptionsItemClicked(int which) {
                RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                    public void onSuccess(List<Conversation> conversations) {
                        if (conversations != null && conversations.size() > 0) {
                            Iterator var2 = conversations.iterator();

                            while (var2.hasNext()) {
                                Conversation conversation = (Conversation) var2.next();
                                RongIMClient.getInstance().removeConversation(conversation.getConversationType(), conversation.getTargetId(), (RongIMClient.ResultCallback) null);
                            }
                        }

                    }

                    public void onError(RongIMClient.ErrorCode errorCode) {
                    }
                }, new Conversation.ConversationType[]{uiConversation.getConversationType()});
                int position = MyConversationListFragment.this.mAdapter.findGatheredItem(uiConversation.getConversationType());
                MyConversationListFragment.this.mAdapter.remove(position);
                MyConversationListFragment.this.mAdapter.notifyDataSetChanged();
            }
        }).show();
    }

    private void makeUiConversationList(List<Conversation> conversationList) {
        Iterator var3 = conversationList.iterator();

        while (var3.hasNext()) {
            Conversation conversation = (Conversation) var3.next();
            Conversation.ConversationType conversationType = conversation.getConversationType();
            String targetId = conversation.getTargetId();
            boolean gatherState = this.getGatherState(conversationType);
            UIConversation uiConversation;
            int originalIndex;
            if (gatherState) {
                originalIndex = this.mAdapter.findGatheredItem(conversationType);
                if (originalIndex >= 0) {
                    uiConversation = (UIConversation) this.mAdapter.getItem(originalIndex);
                    uiConversation.updateConversation(conversation, true);
                } else {
                    uiConversation = UIConversation.obtain(conversation, true);
                    this.mAdapter.add(uiConversation);
                    this.onUIConversationCreated(uiConversation);
                }
            } else {
                originalIndex = this.mAdapter.findPosition(conversationType, targetId);
                int index;
                if (originalIndex < 0) {
                    uiConversation = UIConversation.obtain(conversation, false);
                    index = this.getPosition(uiConversation);
                    this.mAdapter.add(uiConversation, index);
                    this.onUIConversationCreated(uiConversation);
                } else {
                    uiConversation = (UIConversation) this.mAdapter.getItem(originalIndex);
                    if (uiConversation.getUIConversationTime() < conversation.getSentTime()) {
                        this.mAdapter.remove(originalIndex);
                        uiConversation.updateConversation(conversation, false);
                        index = this.getPosition(uiConversation);
                        this.mAdapter.add(uiConversation, index);
                    } else {
                        uiConversation.setUnReadMessageCount(conversation.getUnreadMessageCount());
                    }
                }
            }
        }

    }

    private UIConversation makeUIConversation(List<Conversation> conversations) {
        int unreadCount = 0;
        boolean isMentioned = false;
        Conversation newest = (Conversation) conversations.get(0);

        Conversation conversation;
        for (Iterator uiConversation = conversations.iterator(); uiConversation.hasNext(); unreadCount += conversation.getUnreadMessageCount()) {
            conversation = (Conversation) uiConversation.next();
            if (newest.isTop()) {
                if (conversation.isTop() && conversation.getSentTime() > newest.getSentTime()) {
                    newest = conversation;
                }
            } else if (conversation.isTop() || conversation.getSentTime() > newest.getSentTime()) {
                newest = conversation;
            }

            if (conversation.getMentionedCount() > 0) {
                isMentioned = true;
            }
        }

        UIConversation uiConversation1 = UIConversation.obtain(newest, this.getGatherState(newest.getConversationType()));
        uiConversation1.setUnReadMessageCount(unreadCount);
        uiConversation1.setTop(false);
        uiConversation1.setMentionedFlag(isMentioned);
        return uiConversation1;
    }

    private int getPosition(UIConversation uiConversation) {
        int count = this.mAdapter.getCount();
        int position = 0;

        for (int i = 0; i < count; ++i) {
            if (uiConversation.isTop()) {
                if (!((UIConversation) this.mAdapter.getItem(i)).isTop() || ((UIConversation) this.mAdapter.getItem(i)).getUIConversationTime() <= uiConversation.getUIConversationTime()) {
                    break;
                }

                ++position;
            } else {
                if (!((UIConversation) this.mAdapter.getItem(i)).isTop() && ((UIConversation) this.mAdapter.getItem(i)).getUIConversationTime() <= uiConversation.getUIConversationTime()) {
                    break;
                }

                ++position;
            }
        }

        return position;
    }

    private boolean isConfigured(Conversation.ConversationType conversationType) {
        for (int i = 0; i < this.mConversationsConfig.size(); ++i) {
            if (conversationType.equals(((MyConversationListFragment.ConversationConfig) this.mConversationsConfig.get(i)).conversationType)) {
                return true;
            }
        }

        return false;
    }

    public boolean getGatherState(Conversation.ConversationType conversationType) {
        Iterator var2 = this.mConversationsConfig.iterator();

        MyConversationListFragment.ConversationConfig config;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            config = (MyConversationListFragment.ConversationConfig) var2.next();
        } while (!config.conversationType.equals(conversationType));

        return config.isGathered;
    }

    private Conversation.ConversationType[] getConfigConversationTypes() {
        Conversation.ConversationType[] conversationTypes = new Conversation.ConversationType[this.mConversationsConfig.size()];

        for (int i = 0; i < this.mConversationsConfig.size(); ++i) {
            conversationTypes[i] = ((MyConversationListFragment.ConversationConfig) this.mConversationsConfig.get(i)).conversationType;
        }

        return conversationTypes;
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this.mThis);
        super.onDestroyView();
    }

    private class ConversationConfig {
        Conversation.ConversationType conversationType;
        boolean isGathered;

        private ConversationConfig() {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onEvent(StringEvent event) {
        if (event.getName().equals("updateFriends")) {
            initFragment(this.getUri());
        }
    }

}
