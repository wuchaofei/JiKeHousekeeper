package com.jike.shanglv.Models;

public class Hangbandongtai {
	 private String flightno,//QF4011",
				     scity,//上海浦东T1",
				     ecity,//北京T2",
				     planfly,//09:20",
				     realfly,//11:10",
				     planreach,//11:55",
				     realreach,//12:57",
				     state;//到达"

	public String getFlightno() {
		return flightno;
	}

	public void setFlightno(String flightno) {
		this.flightno = flightno;
	}

	public String getScity() {
		return scity;
	}

	public void setScity(String scity) {
		this.scity = scity;
	}

	public String getEcity() {
		return ecity;
	}

	public void setEcity(String ecity) {
		this.ecity = ecity;
	}

	public String getPlanfly() {
		return planfly;
	}

	public void setPlanfly(String planfly) {
		this.planfly = planfly;
	}

	public String getRealfly() {
		return realfly;
	}

	public void setRealfly(String realfly) {
		this.realfly = realfly;
	}

	public String getPlanreach() {
		return planreach;
	}

	public void setPlanreach(String planreach) {
		this.planreach = planreach;
	}

	public String getRealreach() {
		return realreach;
	}

	public void setRealreach(String realreach) {
		this.realreach = realreach;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
