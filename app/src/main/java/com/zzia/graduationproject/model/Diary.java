package com.zzia.graduationproject.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fyc on 2017/5/9
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class Diary extends DataSupport implements Serializable {
    /**
     * 日记类
     */
    @Column(unique = true)
    private String diaryId;

    private String userId;

    private String diaryTitle;

    private String diaryContent;

    private String createDate;

    private int authority;

    private String sortId;

    private int state;

    private String sendAddress;

    private User user;

    private List<PhotoConnect> photoList=new ArrayList<>();

    private VideoConnect videoConnect;

    private String createDateTransfer;

    private int praiseCount;  //点赞数

    private int commentCount;  //评论数

    private boolean havePraise;  //是否点赞

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

    public String getDiaryTitle() {
        return diaryTitle;
    }

    public void setDiaryTitle(String diaryTitle) {
        this.diaryTitle = diaryTitle;
    }

    public String getDiaryContent() {
        return diaryContent;
    }

    public void setDiaryContent(String diaryContent) {
        this.diaryContent = diaryContent;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<PhotoConnect> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<PhotoConnect> photoList) {
        this.photoList = photoList;
    }

    public VideoConnect getVideoConnect() {
        return videoConnect;
    }

    public void setVideoConnect(VideoConnect videoConnect) {
        this.videoConnect = videoConnect;
    }

    public String getCreateDateTransfer() {
        return createDateTransfer;
    }

    public void setCreateDateTransfer(String createDateTransfer) {
        this.createDateTransfer = createDateTransfer;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isHavePraise() {
        return havePraise;
    }

    public void setHavePraise(boolean havePraise) {
        this.havePraise = havePraise;
    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

}
