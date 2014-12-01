package com.jike.shanglv.Models;

public class InternationalFlightInfo {
	private String StartPortName,// 出发机场名
			StartPort,// 出发机场三字码
			StartTime,// 出发时间
			SeTime,// 运行时间
			CarrierName,// 航空公司
			Code,// 仓位
			EndPort,// 到达机场名
			EndPortName,// 到达机场三字码
			EndTime,// 到达时间
			FlightNo;// 航班号

	public String getStartPortName() {
		return StartPortName;
	}

	public void setStartPortName(String startPortName) {
		StartPortName = startPortName;
	}

	public String getStartPort() {
		return StartPort;
	}

	public void setStartPort(String startPort) {
		StartPort = startPort;
	}

	public String getStartTime() {
		return StartTime;
	}

	public void setStartTime(String startTime) {
		StartTime = startTime;
	}

	public String getSeTime() {
		return SeTime;
	}

	public void setSeTime(String seTime) {
		SeTime = seTime;
	}

	public String getCarrierName() {
		return CarrierName;
	}

	public void setCarrierName(String carrierName) {
		CarrierName = carrierName;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getEndPort() {
		return EndPort;
	}

	public void setEndPort(String endPort) {
		EndPort = endPort;
	}

	public String getEndPortName() {
		return EndPortName;
	}

	public void setEndPortName(String endPortName) {
		EndPortName = endPortName;
	}

	public String getEndTime() {
		return EndTime;
	}

	public void setEndTime(String endTime) {
		EndTime = endTime;
	}

	public String getFlightNo() {
		return FlightNo;
	}

	public void setFlightNo(String flightNo) {
		FlightNo = flightNo;
	}

}
