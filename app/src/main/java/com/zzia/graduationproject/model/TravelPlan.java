package com.zzia.graduationproject.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fyc on 2017/5/8
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class TravelPlan implements Serializable {
    private String travelId;

    private String createId;

    private String travelTheme;

    private String travelDes;

    private String startDate;

    private String endDate;

    private String travelThemePic;

    private int type;  //旅行类型：0单身游 1情侣游 2组团游

    private String createDate;

    private User createUser;

    private List<TravelRoute> travelRoutes;

    private List<PhotoConnect> travelPhotos;

    private List<TravelMember> members;

    private List<TravelRoute> routes;

    public TravelPlan() {
    }

    public TravelPlan(String travelTheme, String travelDes, String startDate, String travelThemePic) {
        this.travelTheme = travelTheme;
        this.travelDes = travelDes;
        this.startDate = startDate;
        this.travelThemePic = travelThemePic;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getTravelTheme() {
        return travelTheme;
    }

    public void setTravelTheme(String travelTheme) {
        this.travelTheme = travelTheme;
    }

    public String getTravelDes() {
        return travelDes;
    }

    public void setTravelDes(String travelDes) {
        this.travelDes = travelDes;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTravelThemePic() {
        return travelThemePic;
    }

    public void setTravelThemePic(String travelThemePic) {
        this.travelThemePic = travelThemePic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<TravelRoute> getTravelRoutes() {
        return travelRoutes;
    }

    public void setTravelRoutes(List<TravelRoute> travelRoutes) {
        this.travelRoutes = travelRoutes;
    }

    public List<PhotoConnect> getTravelPhotos() {
        return travelPhotos;
    }

    public void setTravelPhotos(List<PhotoConnect> travelPhotos) {
        this.travelPhotos = travelPhotos;
    }

    public List<TravelMember> getMembers() {
        return members;
    }

    public void setMembers(List<TravelMember> members) {
        this.members = members;
    }

    public List<TravelRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<TravelRoute> routes) {
        this.routes = routes;
    }
}
