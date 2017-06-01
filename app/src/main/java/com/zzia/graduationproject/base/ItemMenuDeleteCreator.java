package com.zzia.graduationproject.base;

import android.content.Context;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.zzia.graduationproject.R;

/**
 * Created by fyc on 2016/7/14.
 * 创建一个swipelistviewitem的侧滑菜单
 * 侧滑显示红色删除图标
 * 点击红色图标删除相应项
 */
public class ItemMenuDeleteCreator implements SwipeMenuCreator {
    private Context context;

    public ItemMenuDeleteCreator(Context context){
        this.context=context;
    }



    @Override
    public void create(SwipeMenu menu) {
        SwipeMenuItem delete=new SwipeMenuItem(context);
        delete.setBackground(R.drawable.bottom_border);
        delete.setIcon(R.drawable.login_sanchu);
        delete.setWidth(context.getResources().getDimensionPixelSize(R.dimen.dis_103));
        menu.addMenuItem(delete);
    }
}
