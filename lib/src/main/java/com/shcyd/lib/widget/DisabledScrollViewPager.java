package com.shcyd.lib.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author dongyu.wang
 * @version 1.0.0
 * @desc
 * @date 2015-12-03
 * @time 14:18
 * @changlog
 * @fixme
 */
public class DisabledScrollViewPager extends ViewPager {
    public DisabledScrollViewPager(Context context) {
        super(context);
    }

    public DisabledScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
