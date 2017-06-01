package com.shcyd.lib.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.lzy.ninegrid.ImageInfo;

import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.util.List;

import butterknife.ButterKnife;


/**
 * 通用ViewHolder，简化Adapter的视图重用
 *
 * @author dongyu.wang
 * @version 1.0.0
 * @desc
 * @date 2015-10-30
 * @time 20:09
 * @changlog
 * @fixme
 */
public class ViewHolder {
    private final SparseArray<View> subViews;
    private View mConvertView;
    private Context mContext;
    private int position;
    private int layoutId;

    private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.subViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
        mContext = context;
        this.position = position;
        this.layoutId = layoutId;
    }

    /**
     * 获取ViewHolder实例
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.position = position;
            return holder;
        }
    }

    public int getLayoutId() {
        return layoutId;
    }

    public int getPosition() {
        return position;
    }

    /**
     * 获取ViewHolder的子元素
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getSubView(int viewId) {
        View view = subViews.get(viewId);
        if (view == null) {
            view = ButterKnife.findById(mConvertView, viewId);
            subViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 获取ViewHolder的子元素
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getSubViewWithTag(int viewId, Object tag) {
        View view = subViews.get(tag.hashCode());
        if (view == null || view.getTag() == null || view.getTag() != tag) {
            view = ButterKnife.findById(mConvertView, viewId);
            view.setTag(tag);
            subViews.put(tag.hashCode(), view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 设置TextView元素的文字
     *
     * @param resId TextView控件的id
     * @param text  显示文字
     */
    public void setText(int resId, CharSequence text) {
        TextView view = getSubView(resId);
        view.setText(text);
    }

    public void setTextVisible(int resId, boolean show) {
        TextView view = getSubView(resId);
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }

    }

    public void setTextColor(int resId, int colorStateList) {
        TextView view = getSubView(resId);
        view.setTextColor(mContext.getResources().getColor(colorStateList));
    }

    public void setTextClick(int resId, boolean isClick) {
        TextView view = getSubView(resId);
        view.setClickable(isClick);
    }

    public void nineAdapter(int resId, List<ImageInfo> imageInfos) {
        NineGridView view = getSubView(resId);
        view.setAdapter(new NineGridViewClickAdapter(mContext, imageInfos));
    }

    public void setVideoPath(int resId, String path) {

        if (path != null) {
            VideoView videoView = getSubView(resId);
            if (path.contains("http")) {
                Uri uri = Uri.parse(path);
                videoView.setVideoURI(uri);
//            videoView.start();
            } else {
                videoView.setVideoPath(path);
            }
        }
    }

    public void tag(int resId, Object tag) {
        getSubView(resId).setTag(tag);
    }

    public void setImage(int resId, String path) {
        if (path != null) {
            final ImageView img = getSubView(resId);
            Glide.with(mContext).load(path).asBitmap().centerCrop().into(new BitmapImageViewTarget(img) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCornerRadius(5);
                    img.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
    }

    public void setImage(int resId, int path) {
            final ImageView img = getSubView(resId);
            Glide.with(mContext).load(path).asBitmap().centerCrop().into(new BitmapImageViewTarget(img) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCornerRadius(5);
                    img.setImageDrawable(circularBitmapDrawable);
                }
            });
    }

    public void setImageAutofit(int resId, String path) {
        final ImageView img = getSubView(resId);
    }

    public void setImageAutofit(int resId, int placeholder, String path) {
        final ImageView img = getSubView(resId);
    }

    public void setImageFitCenter(int resId, String path) {
        final ImageView img = getSubView(resId);
    }

    public void setImage(int resId, int placeholder, String path) {
        ImageView img = getSubView(resId);
        final ImageView imgView = getSubView(resId);
        Glide.with(mContext).load(path).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                // circularBitmapDrawable.setCircular(true);
                imgView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public void setImageResources(int resId, int drawable) {
        ImageView img = getSubView(resId);
        img.setImageResource(drawable);
    }

    public void setImageDrawable(int resId, BitmapDrawable drawable) {
        ImageView img = getSubView(resId);
        img.setImageDrawable(drawable);
    }

    public void setBackgroundImage(int resId, int drawable) {
        View subView = getSubView(resId);
        subView.setBackgroundResource(drawable);
    }


    public void setImageResources(int resId, int drawable, ImageView.ScaleType scaleType) {
        ImageView img = getSubView(resId);

    }

    /**
     * 隐藏子视图
     *
     * @param resId
     */
    public void gone(int resId) {
        View view = getSubView(resId);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 显示子视图
     *
     * @param resId
     */
    public void visible(int resId) {
        View view = getSubView(resId);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 子控件设置点击事件
     *
     * @param resId
     * @param onClickListener
     */
    public void onClick(int resId, View.OnClickListener onClickListener) {
        getSubView(resId).setOnClickListener(onClickListener);
    }

    public void setSelected(int resId, boolean toggle) {
        getSubView(resId).setSelected(toggle);
    }

    public void setCircleImage(int resId, int defaultImg, String imgUrl) {
        final ImageView imgView = getSubView(resId);

        Glide.with(mContext).load(imgUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imgView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public void setImageNoAnim(int resId, int placeholder, String url) {
        ImageView img = getSubView(resId);
    }
}
