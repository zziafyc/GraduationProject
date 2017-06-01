package com.zzia.graduationproject.model;

import java.io.Serializable;

public class Friends implements Serializable {
	/**
	 * 好友类
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String applicationId;  //申请方
	
	private String applicationObjectId;  //申请对象
	
	private String applicationDate;  //邀请日期
	
	private String applicationDes;  //申请描述
	
	private String remark1;  //申请方备注，申请对象可修改
	
	private String remark2;  //申请对象备注，申请方可修改
	
	private Integer state;  //同意状态

	private User applicationUser;  //申请人

	private Integer isDelete;  //被邀请方是否已删除该记录，0表示未删除(默认)，1表示已删除

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationObjectId() {
		return applicationObjectId;
	}

	public void setApplicationObjectId(String applicationObjectId) {
		this.applicationObjectId = applicationObjectId;
	}

	public String getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(String applicationDate) {
		this.applicationDate = applicationDate;
	}

	public String getApplicationDes() {
		return applicationDes;
	}

	public void setApplicationDes(String applicationDes) {
		this.applicationDes = applicationDes;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public User getApplicationUser() {
		return applicationUser;
	}

	public void setApplicationUser(User applicationUser) {
		this.applicationUser = applicationUser;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
}
