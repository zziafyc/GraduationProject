package com.zzia.graduationproject.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by fyc on 2017/5/9
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class PhotoConnect extends DataSupport implements Serializable {
    // 每一个类里面必须有唯一的表示，不然每次只要进页面都会缓存数据，让他唯一标识第二次无法插入
    // TODO: sqLite 的string类型会转化为text，如果string 太长,sqLite会无法存储（待解决）

    private Diary diary;

    private String diaryId;

    private int trendId;

    @Column(unique = true)
    private String photoSite;

    private String photoThumbSite;

    private int connectType;

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

    public Diary getDiary() {
        return diary;
    }

    public void setDiary(Diary diary) {
        this.diary = diary;
    }
}
