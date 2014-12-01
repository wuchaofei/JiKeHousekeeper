package com.jike.shanglv.Models;

import org.json.JSONArray;

public class InterDemandStr {
	private String uid; //用户ID
	private String sid;//网站ID
	private String sCity;//":"出发城市";"
	private String sCode;//":"出发城市三字码";"
	private String sDate;//":"出发日期";"
	private String sTime;//":"出发时 间";"
	private String eCity;//":"到达城市";"
	private String eCode;//":"到达城市三字码";"
	private String eDate;//":"返回日期";"
	private String eTime;//":"返回时间";"
	private String fType;//":"单程0往返1";"
	private String yusuan;//":"预 算金额";"
	private String contactor;//":"联系人";"
	private String mobile;//":"联系人手机";"
	private String email;//":"联系人邮箱";"
	private String remark;//":"预订备注";"
	
	private JSONArray psgInfo;//":"乘机人信息"}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getsCity() {
		return sCity;
	}

	public void setsCity(String sCity) {
		this.sCity = sCity;
	}

	public String getsCode() {
		return sCode;
	}

	public void setsCode(String sCode) {
		this.sCode = sCode;
	}

	public String getsDate() {
		return sDate;
	}

	public void setsDate(String sDate) {
		this.sDate = sDate;
	}

	public String getsTime() {
		return sTime;
	}

	public void setsTime(String sTime) {
		this.sTime = sTime;
	}

	public String geteCity() {
		return eCity;
	}

	public void seteCity(String eCity) {
		this.eCity = eCity;
	}

	public String geteCode() {
		return eCode;
	}

	public void seteCode(String eCode) {
		this.eCode = eCode;
	}

	public String geteDate() {
		return eDate;
	}

	public void seteDate(String eDate) {
		this.eDate = eDate;
	}

	public String geteTime() {
		return eTime;
	}

	public void seteTime(String eTime) {
		this.eTime = eTime;
	}

	public String getfType() {
		return fType;
	}

	public void setfType(String fType) {
		this.fType = fType;
	}

	public String getYusuan() {
		return yusuan;
	}

	public void setYusuan(String yusuan) {
		this.yusuan = yusuan;
	}

	public String getContactor() {
		return contactor;
	}

	public void setContactor(String contactor) {
		this.contactor = contactor;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public JSONArray getPsgInfo() {
		return psgInfo;
	}

	public void setPsgInfo(JSONArray psgInfo) {
		this.psgInfo = psgInfo;
	}
}
