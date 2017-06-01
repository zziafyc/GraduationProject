package com.shcyd.lib.adapter;

/**
 * @author dongyu.wang
 * @version 1.0.0
 * @desc
 * @date 2015-11-25
 * @time 09:39
 * @changlog
 * @fixme
 */
public interface MultiItemTypeSupport<T> {

    int getLayoutId(int position, T t);

    int getViewTypeCount();

    int getItemViewType(int position, T t);

}
