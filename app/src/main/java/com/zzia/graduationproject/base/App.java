package com.zzia.graduationproject.base;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.ninegrid.NineGridView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.Bugly;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.model.User;
import com.zzia.graduationproject.ui.activity.rongyun.MyPrivateConversationProvider;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;
import io.rong.imkit.RongIM;

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

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorBlowLabel, R.color.colorText);//全局设置主题颜色
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Scale);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Scale);
            }
        });
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
      /*  if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);*/
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
        NineGridView.setImageLoader(new

                GlideImageLoader());
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
