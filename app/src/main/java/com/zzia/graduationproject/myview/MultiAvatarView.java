package com.zzia.graduationproject.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.zzia.graduationproject.R;

import java.util.List;

/**
 * Created by hua on 16/4/16.
 * the copy of wechat group icon design
 * 
 * https://github.com/mzhua/AvatarAssemble
 * 
 */
public class MultiAvatarView extends ViewGroup {
    private int mOriginLayoutSize = dp2px(64);
    private int mLayoutSize = mOriginLayoutSize;
    private int mLayoutPaddingSize = dp2px(2);
    private int mAvatarDividerSize = dp2px(1);
    private int mAvatarCounts;

    public MultiAvatarView(Context context) {
        this(context, null);
    }

    public MultiAvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MultiAvatarView);

        mOriginLayoutSize = (int) array.getDimension(R.styleable.MultiAvatarView_mav_size, dp2px(51));
        mLayoutPaddingSize = (int) array.getDimension(R.styleable.MultiAvatarView_mav_padding, dp2px(1));
        mAvatarDividerSize = (int) array.getDimension(R.styleable.MultiAvatarView_mav_divider_size, dp2px(1));

        array.recycle();
        mLayoutSize = mOriginLayoutSize - mLayoutPaddingSize * 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mOriginLayoutSize, mOriginLayoutSize);
        mAvatarCounts = getChildCount() > 9 ? 9 : getChildCount();
        if (mAvatarCounts <= 0) {
            return;
        } else if (mAvatarCounts == 1) {
            //remove mLayoutPadding
            mLayoutSize = mOriginLayoutSize;
            mLayoutPaddingSize = 0;
        }

        int avatarSize = mLayoutSize;
        switch (mAvatarCounts) {
            case 1:
                avatarSize = mLayoutSize;
                break;
            case 2:
                avatarSize = (mLayoutSize - mAvatarDividerSize) / 2;
                break;
            case 3:
                avatarSize = (mLayoutSize - mAvatarDividerSize) / 2;
                break;
            case 4:
                avatarSize = (mLayoutSize - mAvatarDividerSize) / 2;
                break;
            case 5:
                //base on horizontal
                avatarSize = (mLayoutSize - 2 * mAvatarDividerSize) / 3;
                break;
            case 6:
                avatarSize = (mLayoutSize - 2 * mAvatarDividerSize) / 3;
                break;
            case 7:
                avatarSize = (mLayoutSize - 2 * mAvatarDividerSize) / 3;
                break;
            case 8:
                avatarSize = (mLayoutSize - 2 * mAvatarDividerSize) / 3;
                break;
            case 9:
                avatarSize = (mLayoutSize - 2 * mAvatarDividerSize) / 3;
                break;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            LayoutParams lp = view.getLayoutParams();
            lp.width = avatarSize;
            lp.height = avatarSize;
            view.setLayoutParams(lp);
            measureChild(view, avatarSize, avatarSize);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineWidth = 0;
        int lineHeight = 0;

        switch (mAvatarCounts) {
            case 1:
                for (int i = 0; i < mAvatarCounts; i++) {
                    View childView = getChildAt(i);
                    int lc = mLayoutPaddingSize;
                    int tc = mLayoutPaddingSize;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();

                    childView.layout(lc, tc, rc, bc);
                }
                break;
            case 2:
                for (int i = 1; i <= mAvatarCounts; i++) {
                    View childView = getChildAt(i - 1);

                    int lc = mLayoutPaddingSize;
                    int tc = mLayoutPaddingSize;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();

                    switch (i) {
                        case 1:
                            lc = mLayoutPaddingSize;
                            tc = mLayoutPaddingSize + mLayoutSize / 4;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();

                            lineWidth = rc;
                            break;
                        case 2:
                            lc = mAvatarDividerSize + lineWidth;
                            tc = mLayoutPaddingSize + mLayoutSize / 4;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            break;
                    }

                    childView.layout(lc, tc, rc, bc);
                }
                break;
            case 3:
                for (int i = 1; i <= mAvatarCounts; i++) {
                    View childView = getChildAt(i - 1);

                    int lc = mLayoutPaddingSize;
                    int tc = mLayoutPaddingSize;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();

                    switch (i) {
                        case 1:
                            lc = mLayoutPaddingSize + mLayoutSize / 4;
                            tc = mLayoutPaddingSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();

                            lineHeight = bc;
                            break;
                        case 2:
                            lc = mLayoutPaddingSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 3:
                            lc = mAvatarDividerSize + lineWidth;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            break;
                    }

                    childView.layout(lc, tc, rc, bc);
                }
                break;
            case 4:
                for (int i = 1; i <= mAvatarCounts; i++) {
                    View childView = getChildAt(i - 1);

                    int lc = mLayoutPaddingSize;
                    int tc = mLayoutPaddingSize;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();

                    switch (i) {
                        case 1:
                            lc = mLayoutPaddingSize;
                            tc = mLayoutPaddingSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();

                            lineWidth = rc;
                            break;
                        case 2:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = mLayoutPaddingSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineHeight = bc;
                            break;
                        case 3:
                            lc = mLayoutPaddingSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 4:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            break;
                    }

                    childView.layout(lc, tc, rc, bc);
                }
                break;
            case 5:
                for (int i = 1; i <= mAvatarCounts; i++) {
                    View childView = getChildAt(i - 1);

                    int lc = mLayoutPaddingSize;
                    int tc = mLayoutPaddingSize;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();

                    switch (i) {
                        case 1:
                            lc = mLayoutPaddingSize + (mLayoutSize - 2 * mAvatarDividerSize) / 6;
                            tc = lc;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();

                            lineWidth = rc;
                            break;
                        case 2:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = mLayoutPaddingSize + (mLayoutSize - 2 * mAvatarDividerSize) / 6;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineHeight = bc;
                            break;
                        case 3:
                            lc = mLayoutPaddingSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 4:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 5:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            break;
                    }

                    childView.layout(lc, tc, rc, bc);
                }
                break;
            case 6:
                for (int i = 1; i <= mAvatarCounts; i++) {
                    View childView = getChildAt(i - 1);

                    int lc = mLayoutPaddingSize;
                    int tc = mLayoutPaddingSize;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();

                    switch (i) {
                        case 1:
                            lc = mLayoutPaddingSize;
                            tc = mLayoutPaddingSize + (mLayoutSize - 2 * mAvatarDividerSize) / 6;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();

                            lineWidth = rc;
                            break;
                        case 2:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = mLayoutPaddingSize + (mLayoutSize - 2 * mAvatarDividerSize) / 6;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 3:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = mLayoutPaddingSize + (mLayoutSize - 2 * mAvatarDividerSize) / 6;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineHeight = bc;
                            lineWidth = rc;
                            break;
                        case 4:
                            lc = mLayoutPaddingSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 5:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 6:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            break;
                    }

                    childView.layout(lc, tc, rc, bc);
                }
                break;
            case 7:
                for (int i = 1; i <= mAvatarCounts; i++) {
                    View childView = getChildAt(i - 1);

                    int lc = mLayoutPaddingSize;
                    int tc = mLayoutPaddingSize;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();

                    switch (i) {
                        case 1:
                            lc = mLayoutPaddingSize + mAvatarDividerSize + (mLayoutSize - 2 * mAvatarDividerSize) / 3;
                            tc = mLayoutPaddingSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();

                            lineHeight = bc;
                            break;
                        case 2:
                            lc = mLayoutPaddingSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 3:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 4:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineHeight = bc;
                            break;
                        case 5:
                            lc = mLayoutPaddingSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 6:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 7:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            break;
                    }

                    childView.layout(lc, tc, rc, bc);
                }
                break;
            case 8:
                for (int i = 1; i <= mAvatarCounts; i++) {
                    View childView = getChildAt(i - 1);

                    int lc = mLayoutPaddingSize;
                    int tc = mLayoutPaddingSize;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();

                    switch (i) {
                        case 1:
                            lc = mLayoutPaddingSize + (mLayoutSize - 2 * mAvatarDividerSize) / 6;
                            tc = mLayoutPaddingSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();

                            lineWidth = rc;
                            lineHeight = bc;
                            break;
                        case 2:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = mLayoutPaddingSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            break;
                        case 3:
                            lc = mLayoutPaddingSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 4:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 5:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineHeight = bc;
                            break;
                        case 6:
                            lc = mLayoutPaddingSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 7:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 8:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            break;
                    }

                    childView.layout(lc, tc, rc, bc);
                }
                break;
            case 9:
                for (int i = 1; i <= mAvatarCounts; i++) {
                    View childView = getChildAt(i - 1);

                    int lc = mLayoutPaddingSize;
                    int tc = mLayoutPaddingSize;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();

                    switch (i) {
                        case 1:
                            lc = mLayoutPaddingSize;
                            tc = mLayoutPaddingSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 2:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = mLayoutPaddingSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 3:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = mLayoutPaddingSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineHeight = bc;
                            break;
                        case 4:
                            lc = mLayoutPaddingSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 5:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 6:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineHeight = bc;
                            break;
                        case 7:
                            lc = mLayoutPaddingSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 8:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            lineWidth = rc;
                            break;
                        case 9:
                            lc = lineWidth + mAvatarDividerSize;
                            tc = lineHeight + mAvatarDividerSize;
                            rc = lc + childView.getMeasuredWidth();
                            bc = tc + childView.getMeasuredHeight();
                            break;
                    }

                    childView.layout(lc, tc, rc, bc);
                }
                break;
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new LayoutParams(p);
    }

    public void setAvatarUrls(List<String> avatarUrls) {
        if (null == avatarUrls || avatarUrls.size() <= 0) {
            Log.e("MultiAvatarView", "the avatar counts must be bigger than 1, if the size is bigger than 9, will take the first 9 urls");
            return;
        }
        int size = avatarUrls.size();
        mAvatarCounts = size > 9 ? 9 : size;

        //remove first
        removeAllViews();
        //init avatar layoutparams
        LayoutParams mLayoutParams = new LayoutParams(mLayoutSize, mLayoutSize);

        //create imageview and load image url with glid then add to viewgroup
        for (int i = 0; i < mAvatarCounts; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(mLayoutParams);
            Glide.with(getContext())
                    .load(avatarUrls.get(i))
                    .placeholder(R.drawable.avatar)
                    .crossFade()
                    .fitCenter()
                    .into(imageView);
            addView(imageView);
        }
        //refresh
        postInvalidate();
    }

    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

}
