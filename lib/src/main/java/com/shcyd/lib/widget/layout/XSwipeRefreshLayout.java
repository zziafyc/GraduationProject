package com.shcyd.lib.widget.layout;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author dongyu.wang
 * @version 1.0.0
 * @desc
 * @date 2015-12-16
 * @time 21:38
 * @changlog
 * @fixme
 */
public class XSwipeRefreshLayout extends SwipeRefreshLayout {

    public interface OnViewScrollListener {
        public void onScrollChanged(int x, int y, int oldX, int oldY);
    }

    private OnViewScrollListener onViewScrollListener = null;
    private float xDistance, yDistance, xLast, yLast;

    public XSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public XSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnViewScrollListener getOnViewScrollListener() {
        return onViewScrollListener;
    }

    public void setOnViewScrollListener(OnViewScrollListener onViewScrollListener) {
        this.onViewScrollListener = onViewScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onViewScrollListener != null) {
            onViewScrollListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                if (xDistance > yDistance) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
