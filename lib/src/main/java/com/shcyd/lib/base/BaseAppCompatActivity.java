package com.shcyd.lib.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.shcyd.lib.R;
import com.shcyd.lib.helper.VaryViewHelperController;
import com.shcyd.lib.network.ConnectionObserver;
import com.shcyd.lib.network.NetworkStateReceiver;
import com.shcyd.lib.network.NetworkUtils;
import java.util.Locale;
import butterknife.ButterKnife;
import io.rong.eventbus.EventBus;


public abstract class BaseAppCompatActivity extends AppCompatActivity {

    public static final int NON_CODE = -1;
    /**
     * Log tag
     */
    protected static String     TAG = null;
    /**
     * Screen information
     */
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    protected float mScreenDensity = 0.0f;

    /**
     * context
     */
    protected Context mContext = null;
    protected ConnectionObserver netObserver;
    protected boolean netAvailable = true;
    protected VaryViewHelperController viewHelperController;
    private SystemBarTintManager tintManager;

    @TargetApi(19)
    private void initWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.color_primary));
        tintManager.setStatusBarTintEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // overridePendingTransition
        if (getSharedPreferences("share_czf",MODE_PRIVATE).getString("LANGUAGE","").equals("english")){
            Resources resources=getResources();
            DisplayMetrics dm=resources.getDisplayMetrics();
            Configuration config=resources.getConfiguration();
            config.locale= Locale.ENGLISH;
            resources.updateConfiguration(config,dm);
        }
        if (isOverridePendingTransition()) {
            _overridePendingTransition();
        }

        super.onCreate(savedInstanceState);

        // getBundleExtras
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }

        // EventBus.register
        if (isBindEventBusHere()) {
            EventBus.getDefault().register(this);
        }

        mContext = this;
        TAG = this.getClass().getSimpleName();
        // 加入到Activity栈
        BaseAppManager.getInstance().addActivity(this);

        /* 获取屏幕信息 */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;

        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID());
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }

        if (getLoadingTargetView() != null) {
            viewHelperController = new VaryViewHelperController(getLoadingTargetView());
        }
        netAvailable = NetworkUtils.isNetworkAvailable(this);

        //网络监听
        netObserver = new ConnectionObserver() {
            @Override
            public void onNetConnected() {
                super.onNetConnected();
                netAvailable = true;
                onNetworkConnected();
            }

            @Override
            public void onNetDisconnect() {
                super.onNetDisconnect();
                netAvailable = false;
                onNetworkDisconnect();
            }
        };

        NetworkStateReceiver.registerObserver(netObserver);

        if (hasTitlebar()){
//            initTitlebar();
            setCustomTitle(getTitle());
            onNavigateClick();
        }
        initSubscription();
        initViewsAndEvents();

    }

    protected abstract void initSubscription();

    protected abstract void setCustomTitle(CharSequence title);

//    protected abstract void initTitlebar();

    protected abstract boolean hasTitlebar();

    protected abstract void onNavigateClick();

//    protected abstract boolean backIconVisible();

    protected abstract boolean isOverridePendingTransition();

    /**
     * <pre>
     * 要显示的正文内容，如拍品列表：ButterKnife.findById(this,R.id.list_auction);
     *     or
     *     ListView auctionList;
     *     return auctionList;
     * </pre>
     *
     * @return
     */
    protected View getLoadingTargetView() {
        return null;
    }

    protected abstract void onNetworkDisconnect();

    protected abstract void onNetworkConnected();

    /**
     * bind layout resource file
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();

    /**
     * init activity view and bind event
     */
    protected abstract void initViewsAndEvents();


    /**
     * 是否注册EventBus
     *
     * @return
     */
    protected boolean isBindEventBusHere() {
        return false;
    }

    /**
     * @param extras
     */
    protected void getBundleExtras(Bundle extras) {

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

    /**
     * startActivity
     *
     * @param clazz target Activity
     */
    protected void go(Class<? extends Activity> clazz) {
        _goActivity(clazz, null, NON_CODE, false);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz  target Activity
     * @param bundle
     */
    protected void go(Class<? extends Activity> clazz, Bundle bundle) {
        _goActivity(clazz, bundle, NON_CODE, false);
    }

    /**
     * startActivity then finish this
     *
     * @param clazz target Activity
     */
    protected void goAndFinish(Class<? extends Activity> clazz) {
        _goActivity(clazz, null, NON_CODE, true);
    }

    /**
     * startActivity with bundle and then finish this
     *
     * @param clazz  target Activity
     * @param bundle bundle extra
     */
    protected void goAndFinish(Class<? extends Activity> clazz, Bundle bundle) {
        _goActivity(clazz, bundle, NON_CODE, true);
    }

    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    protected void goForResult(Class<? extends Activity> clazz, int requestCode) {
        _goActivity(clazz, null, requestCode, false);
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz
     * @param bundle
     * @param requestCode
     */
    protected void goForResult(Class<? extends Activity> clazz, Bundle bundle, int requestCode) {
        _goActivity(clazz, bundle, requestCode, false);
    }

    /**
     * startActivityForResult then finish this
     *
     * @param clazz
     * @param requestCode
     */
    protected void goForResultAndFinish(Class<? extends Activity> clazz, int requestCode) {
        _goActivity(clazz, null, requestCode, true);
    }

    /**
     * startActivityForResult with bundle and then finish this
     *
     * @param clazz
     * @param bundle
     * @param requestCode
     */
    protected void goForResultAndFinish(Class<? extends Activity> clazz, Bundle bundle, int requestCode) {
        _goActivity(clazz, bundle, requestCode, true);
    }

    /**
     * 转场动画
     *
     * @return TransitionMode
     */
    protected abstract TransitionMode getTransitionMode();

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void finish() {
        super.finish();
        BaseAppManager.getInstance().removeActivity(this);
        if (isOverridePendingTransition()) {
            _overridePendingTransition();
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        if (isBindEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    /**
     * 设置转场动画
     */
    private void _overridePendingTransition() {
        switch (getTransitionMode()) {
            case LEFT:
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case RIGHT:
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                break;
            case TOP:
                overridePendingTransition(R.anim.top_in, R.anim.top_out);
                break;
            case BOTTOM:
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                break;
            case SCALE:
                overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                break;
            case FADE:
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case NONE:
            default:
                break;
        }
    }

    /**
     * ============= interval methods =================
     */

    /**
     * Activity 跳转
     *
     * @param clazz  目标activity
     * @param bundle 传递参数
     * @param finish 是否结束当前activity
     */
    private void _goActivity(Class<? extends Activity> clazz, Bundle bundle, int requestCode, boolean finish) {
        if (null == clazz) {
            throw new IllegalArgumentException("you must pass a target activity where to go.");
        }
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        if (requestCode > NON_CODE) {
            startActivityForResult(intent, requestCode);
        } else {
            startActivity(intent);
        }
        if (finish) {
            finish();
        }
    }

    /**
     * overridePendingTransition mode: 转场动画
     */
    public enum TransitionMode {
        NONE, LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE
    }
}
