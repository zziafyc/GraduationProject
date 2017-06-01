package com.zzia.graduationproject.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.shcyd.lib.widget.popup.LoadingPopup;
import com.zzia.graduationproject.api.MySubscriber;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import io.rong.eventbus.EventBus;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragment extends Fragment {

    private static final int NON_CODE = -1;
    protected Context mContext;
    protected View rootView;
    protected float mScreenDensity;
    protected int mScreenHeight;
    protected int mScreenWidth;
    protected boolean isVisible;
    private CompositeSubscription subscriptions;
    Toast mToast;
    public String TAG;
    private int currentPercent;
    protected LoadingPopup loadingPop;
    private Handler myHandler = new Handler();
    private Runnable mLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            if (loadingPop == null) {
                loadingPop = new LoadingPopup(getActivity());
            }
            loadingPop.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        }
    };
    private Runnable mHideLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            if (loadingPop != null) {
                loadingPop.dismiss();
            }
        }
    };

    private Runnable mShowPercentRunnable = new Runnable() {
        @Override
        public void run() {
            if (loadingPop != null) {
                loadingPop.percentLyt.setVisibility(View.VISIBLE);
                loadingPop.loadLyt.setVisibility(View.GONE);
                loadingPop.percentBar.setProgress(currentPercent);
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            getExtraArguments(getArguments());
        }
        if (isBindEventBusHere()) {
            EventBus.getDefault().register(this);
        }
        TAG = this.getClass().getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getContentViewLayout(), container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
        initSubscription();
        initViewsAndEvents();
    }

    protected abstract int getContentViewLayout();

    protected void getExtraArguments(Bundle arguments) {

    }

    public void call(Observable observable, MySubscriber subscriber) {
        Subscription subscription = null;
        try {
            subscription = observable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
            subscriptions.add(subscription);
        } catch (Exception e) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            //Log.e("error", e.getMessage());
        } finally {

        }
    }

    //实现fragment的懒加载
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onInvisible() {

    }

    protected void initSubscription() {
        subscriptions = new CompositeSubscription();
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void lazyLoad() {

    }

    protected boolean isBindEventBusHere() {
        return false;
    }

    protected  abstract void initViewsAndEvents();

    protected void showToast(boolean bool) {
        showToast(String.format("%s", bool));
    }

    protected void showToast(int number) {
        showToast(String.format("%d", number));
    }

    public void showToast(String msg) {
        if (null != msg) {
            if (mToast == null) {
                mToast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setText(msg);
            }
            mToast.show();
        }
    }


    protected void showLoading(String msg) {

    }

    protected void showEmpty(String msg) {

    }

    protected void showError(String msg) {
        showToast(msg);
    }

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

    protected void goForResult(Class<? extends Activity> clazz, int requestCode) {
        _goActivity(clazz, null, requestCode, false);
    }

    protected void goForResult(Class<? extends Activity> clazz, Bundle bundle, int requestCode) {
        _goActivity(clazz, bundle, requestCode, false);
    }

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
        Intent intent = new Intent(getActivity(), clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        if (requestCode > NON_CODE) {
            startActivityForResult(intent, requestCode);
        } else {
            startActivity(intent);
        }
        if (finish) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (isBindEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onDestroy() {
        subscriptions.unsubscribe();
        mToast = null;
        super.onDestroy();
    }

    public synchronized void hideLoading() {

        getActivity().getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                myHandler.post(mHideLoadingRunnable);
            }
        });
    }

    public synchronized void showLoading() {
        getActivity().getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                myHandler.post(mLoadingRunnable);
            }
        });
    }

    public synchronized void showPercentLoading(int percent) {
        currentPercent = percent;
        getActivity().getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                myHandler.post(mShowPercentRunnable);
            }
        });
    }

    //onDetach方法在rest fragment时，其子fragment并没有rest，重写是为了reset子fragment
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field mChildFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            mChildFragmentManager.setAccessible(true);
            mChildFragmentManager.set(this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mToast != null) {
            mToast.cancel();
        }
    }
}