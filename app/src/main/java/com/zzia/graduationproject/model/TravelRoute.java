package com.zzia.graduationproject.model;

import java.io.Serializable;

/**
 * Created by fyc on 2017/5/10
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class TravelRoute implements Serializable{
    private String routeId;

    private String travelId;

    private String routeDes;

    private String startDate;

    private String endDate;

    private String introduction;

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }

    public String getRouteDes() {
        return routeDes;
    }

    public void setRouteDes(String routeDes) {
        this.routeDes = routeDes;
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
