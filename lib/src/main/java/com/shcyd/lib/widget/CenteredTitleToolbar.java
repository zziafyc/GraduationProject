package com.shcyd.lib.widget;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.shcyd.lib.R;


/**
 * Created by tiangongyipin on 16/2/14.
 */
public class CenteredTitleToolbar extends Toolbar {
    private TextView mTitleView;

    public CenteredTitleToolbar(Context context) {
        this(context, null);
    }

    public CenteredTitleToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.toolbarStyle);
    }

    public CenteredTitleToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.base_toolbar, this, true);
        mTitleView = (TextView) findViewById(R.id.toolbar_title);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }
}
