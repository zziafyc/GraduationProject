package com.shcyd.lib.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author dongyu.wang
 * @version 1.0.0
 * @desc
 * @date 2015-11-25
 * @time 09:26
 * @changlog
 * @fixme
 */
public abstract class MultiItemBasicAdapter<ItemData> extends BasicAdapter<ItemData> {

    protected MultiItemTypeSupport<ItemData> multiItemTypeSupport;

    public MultiItemBasicAdapter(Context context, List<ItemData> itemDatas, MultiItemTypeSupport<ItemData> multiItemTypeSupport) {
        super(context, itemDatas, -1);
        this.multiItemTypeSupport = multiItemTypeSupport;
    }

    public MultiItemBasicAdapter(Context context, MultiItemTypeSupport<ItemData> multiItemTypeSupport) {
        super(context, -1);
        this.multiItemTypeSupport = multiItemTypeSupport;
    }

    @Override
    public int getViewTypeCount() {
        if (multiItemTypeSupport != null) {
            return multiItemTypeSupport.getViewTypeCount();
        }
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (multiItemTypeSupport != null) {
            return multiItemTypeSupport.getItemViewType(position, dataList.get(position));
        }
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (multiItemTypeSupport == null) {
            return super.getView(position, convertView, parent);
        }
        int layoutId = multiItemTypeSupport.getLayoutId(position, getItem(position));
        ViewHolder holder = ViewHolder.get(context, convertView, parent, layoutId, position);
        render(holder, getItem(position), position);
        return holder.getConvertView();
    }

}
