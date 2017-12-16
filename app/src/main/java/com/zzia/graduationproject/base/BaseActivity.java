package com.zzia.graduationproject.base;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shcyd.lib.base.BaseAppCompatActivity;
import com.shcyd.lib.widget.popup.LoadingPopup;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tiangongyipin on 16/2/15.
 */
public abstract class BaseActivity extends BaseAppCompatActivity {

    private LoadingPopup loadingPop;
    private CompositeSubscription subscriptions;
    private int currentPercent;
    private boolean canSideOut;
    private Toast toast = null;
    private Handler myHandler = new Handler();
    private Runnable mLoadingRunnable = new Runnable() {

        @Override
        public void run() {
            if (loadingPop == null) {
                loadingPop = new LoadingPopup(BaseActivity.this);
            }
            if (canSideOut) {
                loadingPop.pickPhotoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                loadingPop.pickPhotoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingPop.dismiss();
                    }
                });
            }
            loadingPop.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
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
    protected boolean isOverridePendingTransition() {
        return false;
    }

    @Override
    protected void onNetworkDisconnect() {

    }

    @Override
    protected void onNetworkConnected() {

    }

    public  void call(Observable observable, MySubscriber subscriber) {
        Subscription subscription = ApiClient.call(observable, subscriber);
        subscriptions.add(subscription);
    }

    @Override
    protected boolean hasTitlebar() {
        return findViewById(R.id.titlebar) != null;
    }

    /* @Override
     protected boolean hasSoft() {
         return findViewById(R.id.base_main_lyt) != null;
     }

     @Override
     protected void onEditOutSideClick() {
         findViewById(R.id.base_main_lyt).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                 if (imm != null) {
                     imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                 }

             }
         });
     }
 */
    @Override
    protected void setCustomTitle(CharSequence title) {
        if (hasTitlebar()) {
            TextView titleView = ButterKnife.findById(this, R.id.title_tv_message);
            if (titleView != null) {
                titleView.setText(title);
                setTitle("");
            }
        }
    }

    public void showToast(String msg) {
        if (null != msg) {
            if (toast == null) {
                toast = Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(msg);
            }
            toast.show();
        }
    }

    public synchronized void showLoading() {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                myHandler.post(mLoadingRunnable);
            }
        });
    }

    public void showLoading(boolean canSideOut) {
        this.canSideOut = canSideOut;
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                myHandler.post(mLoadingRunnable);
            }
        });
    }

    public synchronized void showPercentLoading(int percent) {
        currentPercent = percent;
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                myHandler.post(mShowPercentRunnable);
            }
        });
    }

    public synchronized void hideLoading() {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                myHandler.post(mHideLoadingRunnable);
            }
        });
    }

    @Override
    protected void initSubscription() {
        subscriptions = new CompositeSubscription();
    }

    @Override
    protected boolean isBindEventBusHere() {
        return false;
    }

    @Override
    protected void onDestroy() {
        subscriptions.unsubscribe();
        toast = null;
        hideLoading();
        //隐藏输入法
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (toast != null) {
            toast.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onNavigateClick() {
        if (hasTitlebar()) {
            RelativeLayout backView = ButterKnife.findById(this, R.id.actionbar_back);
            if (backView != null && backView.getVisibility() == View.VISIBLE) {
                backView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //当点击退出时，如果输入法弹起，隐藏。by wgy
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                        // finish();
                    }
                });
            }
        }
    }

    @Override
    protected TransitionMode getTransitionMode() {
        return TransitionMode.RIGHT;
    }

}
