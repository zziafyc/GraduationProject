package com.zzia.graduationproject.model;

import java.io.Serializable;

/**
 * Created by fyc on 2017/5/9
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class VideoConnect implements Serializable {
    private int id;

    private String trendId;

    private String diaryId;

    private String videoSite;

    private String videoCoverSite;

    private String videoTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrendId() {
        return trendId;
    }

    public void setTrendId(String trendId) {
        this.trendId = trendId;
    }

    public String getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }

    public String getVideoSite() {
        return videoSite;
    }

    public void setVideoSite(String videoSite) {
        this.videoSite = videoSite;
    }

    public String getVideoCoverSite() {
        return videoCoverSite;
    }

    public void setVideoCoverSite(String videoCoverSite) {
        this.videoCoverSite = videoCoverSite;
    }

    public String getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(String videoTime) {
        this.videoTime = videoTime;
    }
}
