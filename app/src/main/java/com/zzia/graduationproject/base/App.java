package com.zzia.graduationproject.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.ninegrid.NineGridView;
import com.tencent.bugly.crashreport.CrashReport;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.RongProvider.MyPrivateConversationProvider;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.model.User;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import cn.smssdk.SMSSDK;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

import static com.zzia.graduationproject.api.ApiClient.call;

/**
 * Created by fyc on 2016/12/26.
 * Email : 847891359@qq.com .
 * GitHub : https://github.com/zziafyc
 */
public class App extends Application {
    private static App instance;
    private static User user;
    private static User model;

    public static App getInstance() {
        return instance;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        App.user = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        /**
         * bugly的初始化
         * 第三个参数为SDK调试模式开关，调试模式的行为特性如下：
         * 输出详细的Bugly SDK的Log；
         * 每一条Crash都会被立即上报；
         * 自定义日志将会在Logcat中输出。
         * 建议在测试阶段建议设置成true，发布时设置为false
         */
        CrashReport.initCrashReport(getApplicationContext(), "4c24f903ef", true);

        /**
         * litePal的初始化,以及数据库的创建
         */
        LitePal.initialize(this);
        Connector.getDatabase();
        /**
         *  关于百度地图
         */
        SDKInitializer.initialize(this);

        /**
         *  关于mob的初始化
         */
        SMSSDK.initSDK(this, Constants.ThirdParty.MOB_APPKEY, Constants.ThirdParty.MOB_APPSECRET);
        /**
         *   关于融云的初始化
         */
        RongIM.init(this);
        RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒
        RongIM.getInstance().enableUnreadMessageIcon(true);//显示未读消息数目
        RongIM.getInstance().registerConversationTemplate(new MyPrivateConversationProvider());
        //登录成功后，设置内容提供者
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String userId) {
                try {
                    call(ApiClient.getApis().getUserInfo(userId), new MySubscriber<BaseResp<User>>() {
                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(BaseResp<User> resp) {
                            if (resp.resultCode == Constants.RespCode.SUCCESS) {
                                if (resp.status == Constants.RespCode.SUCCESS) {
                                    model = resp.data;
                                }
                            }
                        }
                    });
                    if (model != null) {
                        return new UserInfo(model.getUserId(), model.getNickName(), Uri.parse(model.getAvatar()));
                    }
                } catch (Exception e) {
                    return null;
                }
                return null;
            }
        }, true);
        /**
         * 关于nineGridView的加载方式,Glide 加载
         */
        class GlideImageLoader implements NineGridView.ImageLoader {
            @Override
            public void onDisplayImage(Context context, ImageView imageView, String url) {
                Glide.with(context).load(url)
                        .placeholder(R.drawable.avatar_default)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }

            @Override
            public Bitmap getCacheImage(String url) {
                return null;
            }
        }
        NineGridView.setImageLoader(new GlideImageLoader());
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
