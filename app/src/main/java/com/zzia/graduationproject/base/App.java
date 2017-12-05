package com.zzia.graduationproject.base;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.ninegrid.NineGridView;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.RongProvider.MyPrivateConversationProvider;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.model.User;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

import static com.zzia.graduationproject.api.ApiClient.call;

/**
 * Created by fyc on 2016/12/26.
 * Email : 847891359@qq.com .
 * GitHub : https://github.com/zziafyc
 */
public class App extends MultiDexApplication {
    private static App instance;
    private static User user;
    private static User model;

    static {
        //微信
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        //QQ
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        //微博
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad", "http://sns.whalecloud.com");

    }

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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        /**
         * 内存泄漏检测
         */
        if(LeakCanary.isInAnalyzerProcess(this)){
           return;
        }
        LeakCanary.install(this);
        /**
         * bugly的初始化
         */
        Bugly.init(getApplicationContext(), "4c24f903ef", false);

        /**
         * litePal的初始化,以及数据库的创建
         */
        LitePal.initialize(this);
        Connector.getDatabase();
        /**
         * 友盟第三方授权登录、分享
         */
        UMShareAPI.get(this);
        /**
         * 极光推送初始化
         */
        JPushInterface.init(this);
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
