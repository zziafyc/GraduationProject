package com.zzia.graduationproject.event;

import java.io.Serializable;

/**
 * Created by fyc on 2017/12/17.
 */

public class TrendEvent implements Serializable {
    String name;

    public TrendEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
