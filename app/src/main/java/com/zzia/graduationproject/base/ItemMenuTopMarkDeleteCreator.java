package com.zzia.graduationproject.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.zzia.graduationproject.R;

public class ItemMenuTopMarkDeleteCreator implements SwipeMenuCreator {
    Context context;

    public ItemMenuTopMarkDeleteCreator(Context context) {

        this.context = context;

    }

    /**
     * 创建侧滑时的 菜单
     */

    @Override
    public void create(SwipeMenu menu) {
        //创建一个菜单条(置顶)
        SwipeMenuItem openItem1 = new SwipeMenuItem(
                context);
        openItem1.setBackground(new ColorDrawable(Color.parseColor("#FFCCCCCC")));
        openItem1.setWidth((int) context.getResources().getDimension(R.dimen.dis_79));
        openItem1.setTitle("置顶");
        openItem1.setTitleSize(16);
        openItem1.setTitleColor(Color.WHITE);
        menu.addMenuItem(openItem1);

        //创建一个菜单条（标为未读）
        SwipeMenuItem openItem2 = new SwipeMenuItem(
                context);
        openItem2.setBackground(new ColorDrawable(Color.parseColor("#ffbf5b")));
        openItem2.setWidth((int) context.getResources().getDimension(R.dimen.dis_98));
        openItem2.setTitle("标为未读");
        openItem2.setTitleSize(16);
        openItem2.setTitleColor(Color.WHITE);
        menu.addMenuItem(openItem2);

        //创建一个菜单条（删除）
        SwipeMenuItem openItem3 = new SwipeMenuItem(
                context);
        openItem3.setBackground(new ColorDrawable(Color.parseColor("#f43530")));
        openItem3.setWidth((int) context.getResources().getDimension(R.dimen.dis_79));
        openItem3.setTitle("删除");
        openItem3.setTitleSize(16);
        openItem3.setTitleColor(Color.WHITE);
        menu.addMenuItem(openItem3);

    }

}