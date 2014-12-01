package com.jike.shanglv.NetAndJson;

/**
 * siteid:系统id
*userid:用户ID
username:用户名
mobile:手机号码
email:电子邮件
sdate:系统运营时间开始
edate:系统运营时间结束
companyname:公司名称
smssign:短信签名
smsamount:短信余额(utype为3\4无效)
openvam:是否开通虚拟账号(0未开通 1开通)
amount:虚拟账号金额(utype为3\4无效) 
amnousemount:虚拟账号不可用余额(utype为3\4无效)
amname:虚拟账号名称(utype为3\4无效)
 * @author Administrator
 *
 */
public class UserInfo {
	String userid,     username,     mobile,     email,     sdate,     edate,     companyname,     companyname_jc,     smssign,     smsamount,     openvam,     ammount,     amnousemount,     amname,     siteid
			,showDealer,showCustomer,usertype,opensupperpay;

	public String getOpensupperpay() {
		return opensupperpay;
	}

	public void setOpensupperpay(String opensupperpay) {
		this.opensupperpay = opensupperpay;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getShowDealer() {
		return showDealer;
	}

	public void setShowDealer(String showDealer) {
		this.showDealer = showDealer;
	}

	public String getShowCustomer() {
		return showCustomer;
	}

	public void setShowCustomer(String showCustomer) {
		this.showCustomer = showCustomer;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getSdate() {
		return sdate;
	}

	public void setSdate(String sdate) {
		this.sdate = sdate;
	}

	public String getEdate() {
		return edate;
	}

	public void setEdate(String edate) {
		this.edate = edate;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getCompanyname_jc() {
		return companyname_jc;
	}

	public void setCompanyname_jc(String companyname_jc) {
		this.companyname_jc = companyname_jc;
	}

	public String getSmssign() {
		return smssign;
	}

	public void setSmssign(String smssign) {
		this.smssign = smssign;
	}

	public String getSmsamount() {
		return smsamount;
	}

	public void setSmsamount(String smsamount) {
		this.smsamount = smsamount;
	}

	public String getOpenvam() {
		return openvam;
	}

	public void setOpenvam(String openvam) {
		this.openvam = openvam;
	}

	public String getAmmount() {
		return ammount;
	}

	public void setAmmount(String ammount) {
		this.ammount = ammount;
	}

	public String getAmnousemount() {
		return amnousemount;
	}

	public void setAmnousemount(String amnousemount) {
		this.amnousemount = amnousemount;
	}

	public String getAmname() {
		return amname;
	}

	public void setAmname(String amname) {
		this.amname = amname;
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}
}
