package com.shcyd.lib.widget.progress;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.shcyd.lib.widget.listview.PinnedHeaderListView;

/**
 *fym 2016.7.14
 */
public class NoScrollPinnedHeaderListView extends PinnedHeaderListView {
    public NoScrollPinnedHeaderListView(Context context) {
        super(context);
    }

    public NoScrollPinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollPinnedHeaderListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
