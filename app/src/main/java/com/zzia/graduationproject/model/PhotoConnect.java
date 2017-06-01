package com.zzia.graduationproject.model;

import java.io.Serializable;

/**
 * Created by fyc on 2017/5/9
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class PhotoConnect implements Serializable {
    private int id;

    private String diaryId;

    private int trendId;

    private String photoSite;

    private String photoThumbSite;

    private int connectType;

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

    public int getTrendId() {
        return trendId;
    }

    public void setTrendId(int trendId) {
        this.trendId = trendId;
    }

    public String getPhotoSite() {
        return photoSite;
    }

    public void setPhotoSite(String photoSite) {
        this.photoSite = photoSite;
    }

    public String getPhotoThumbSite() {
        return photoThumbSite;
    }

    public void setPhotoThumbSite(String photoThumbSite) {
        this.photoThumbSite = photoThumbSite;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }
}
