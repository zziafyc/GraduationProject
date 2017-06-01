package com.zzia.graduationproject.model;

import java.io.Serializable;

/**
 * Created by fyc on 2017/5/14
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class DiaryPraise implements Serializable {
    private int id;

    private String diaryId;

    private String userId;

    private String praiseDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPraiseDate() {
        return praiseDate;
    }

    public void setPraiseDate(String praiseDate) {
        this.praiseDate = praiseDate;
    }
}
