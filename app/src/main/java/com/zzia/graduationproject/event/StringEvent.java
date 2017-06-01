package com.zzia.graduationproject.event;

import java.io.Serializable;

/**
 * Created by fyc on 2017/5/15
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class StringEvent implements Serializable {
    private String name;
    private String data;

    public StringEvent(String name) {
        this.name = name;
    }

    public StringEvent(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
