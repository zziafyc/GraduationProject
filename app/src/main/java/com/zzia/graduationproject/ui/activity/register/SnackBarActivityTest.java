package com.zzia.graduationproject.ui.activity.register;

import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.utils.SnackBarUtils;

import butterknife.Bind;

/**
 * Created by fyc on 2017/10/26.
 */

public class SnackBarActivityTest extends BaseActivity {
    @Bind(R.id.layout)
    CoordinatorLayout mCoordinatorLayout;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_snackbar_test;
    }

    @Override
    protected void initViewsAndEvents() {
        SnackBarUtils.with(mCoordinatorLayout)
                .setBgColor(SnackBarUtils.COLOR_WARNING)
                .setMessage("小姐姐把你拉黑啦")
                .setAction("点我一下", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击啦
                        showToast("你点击啦");
                    }
                }).show();

    }
}
