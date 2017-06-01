package com.zzia.graduationproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shcyd.lib.adapter.ViewHolder;
import com.shcyd.lib.widget.listview.SectionedBaseAdapter;
import com.zzia.graduationproject.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ScreenSectionedAdapter<T> extends SectionedBaseAdapter {

    private Map<String, List<T>> datas;
    private ArrayList<String> sections;
    private Context mContext;
    private int mItemLayoutId;


    public ScreenSectionedAdapter(Context context,  int layoutId) {
        this.mContext = context;
        this.mItemLayoutId = layoutId;
    }

    public void setDatas(Map<String, List<T>> datas) {
        this.datas = datas;
        sections = new ArrayList<>();

        for (String index : datas.keySet()) {
            sections.add(index);
        }

    }

    @Override
    public T getItem(int section, int position) {
        return datas.get(sections.get(section)).get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return 0;
    }

    @Override
    public int getSectionCount() {
        return sections.size();
    }

    @Override
    public int getCountForSection(int section) {
        return datas.get(sections.get(section)).size();
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = _getViewHolder(position, convertView, parent);
        render(holder, getItem(section, position), section, position);
        return holder.getConvertView();
    }

    protected abstract void render(ViewHolder holder, T item, int section, int position);


    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflator.inflate(R.layout.pinned_list_header, null);
        } else {
            layout = (LinearLayout) convertView;
        }
        ((TextView) layout.findViewById(R.id.pinned_list_header)).setText(sections.get(section));
        return layout;

    }

    private ViewHolder _getViewHolder(int position, View convertView, ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
    }

    public int getPositionForSection(String s) {
        int pos = 1;
        if (datas != null) {
            String pre = null;
            for (String title : datas.keySet()) {
                if (title.equalsIgnoreCase(s)) {
                    break;
                }
                pos += datas.get(title).size() + 1;
            }
        }
        return pos;
    }

    public ArrayList<String> getSections() {
        return sections;
    }
}
