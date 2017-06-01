package com.zzia.graduationproject.base.recyclerview;

/**
 * Created by fyc on 2017/4/27
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public interface ItemViewDelegate<T> {
    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);
}
