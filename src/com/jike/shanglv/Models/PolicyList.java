package com.jike.shanglv.Models;

public class PolicyList {
	String fare,// 票面
			sale,// 销售
			flightno,// 航班号
			cabin,// 仓位
			policyid,// 政策ID
			platname,// 平台名
			totalrate,// 总返点
			userrate,// 用户返点
			rateinfo,// 返佣详情
			remark,// 政策备注
			isspepolicy,// 是否特殊高政策
			wtime,// 上班时间
			rftime;// 下班时间

	public String getFare() {
		return fare;
	}

	public void setFare(String fare) {
		this.fare = fare;
	}

	public String getSale() {
		return sale;
	}

	public void setSale(String sale) {
		this.sale = sale;
	}

	public String getFlightno() {
		return flightno;
	}

	public void setFlightno(String flightno) {
		this.flightno = flightno;
	}

	public String getCabin() {
		return cabin;
	}

	public void setCabin(String cabin) {
		this.cabin = cabin;
	}

	public String getPolicyid() {
		return policyid;
	}

	public void setPolicyid(String policyid) {
		this.policyid = policyid;
	}

	public String getPlatname() {
		return platname;
	}

	public void setPlatname(String platname) {
		this.platname = platname;
	}

	public String getTotalrate() {
		return totalrate;
	}

	public void setTotalrate(String totalrate) {
		this.totalrate = totalrate;
	}

	public String getUserrate() {
		return userrate;
	}

	public void setUserrate(String userrate) {
		this.userrate = userrate;
	}

	public String getRateinfo() {
		return rateinfo;
	}

	public void setRateinfo(String rateinfo) {
		this.rateinfo = rateinfo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getIsspepolicy() {
		return isspepolicy;
	}

	public void setIsspepolicy(String isspepolicy) {
		this.isspepolicy = isspepolicy;
	}

	public String getWtime() {
		return wtime;
	}

	public void setWtime(String wtime) {
		this.wtime = wtime;
	}

	public String getRftime() {
		return rftime;
	}

	public void setRftime(String rftime) {
		this.rftime = rftime;
	}
}
