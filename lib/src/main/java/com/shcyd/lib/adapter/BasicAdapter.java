package com.shcyd.lib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dongyu.wang
 * @version 1.0.0
 * @desc
 * @date 2015-11-02
 * @time 19:59
 * @changlog
 * @fixme
 */
public abstract class BasicAdapter<ItemData> extends BaseAdapter {
    protected LayoutInflater inflater;
    protected Context context;
    protected List<ItemData> dataList;
    protected final int itemLayoutId;

    public BasicAdapter(Context context, List<ItemData> dataList, int itemLayoutId) {
        this.context = context;
        this.dataList = dataList;
        this.itemLayoutId = itemLayoutId;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public BasicAdapter(Context context, int itemLayoutId) {
        this.context = context;
        this.dataList = new ArrayList<ItemData>();
        this.itemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public ItemData getItem(int position) {
        if (dataList == null || dataList.size() <= position || position < 0) {
            return null;
        }
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = _getViewHolder(position, convertView, parent);
        render(holder, getItem(position), position);
        return holder.getConvertView();
    }

    /**
     * 渲染子视图
     * <p>
     * 示例：<br>
     * holder.setImage(R.id.demo_img,item.getImageUrl());
     * holder.setText(R.id.demo_text,item.getText());
     * Button btn = holder.getSubView(R.id.demo_button);
     * </p>
     *
     * @param holder
     * @param item
     * @param position
     */
    protected abstract void render(ViewHolder holder, ItemData item, int position);

    public List<ItemData> getDataList() {

        return dataList;
    }

    public void setDataList(List<ItemData> dataList) {
        this.dataList = dataList;
    }

    private ViewHolder _getViewHolder(int position, View convertView, ViewGroup parent) {
        return ViewHolder.get(context, convertView, parent, itemLayoutId, position);
    }
}
