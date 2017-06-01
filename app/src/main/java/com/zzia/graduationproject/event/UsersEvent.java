package com.zzia.graduationproject.event;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fyc on 2017/5/25
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class UsersEvent<T> implements Serializable {
    String name;
    List<T> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
