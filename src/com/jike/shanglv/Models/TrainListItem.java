package com.jike.shanglv.Models;

import java.util.ArrayList;

public class TrainListItem  implements Cloneable{
	private String StationS,// 北京南,
			StationE,// 上海虹桥,
			SFType,// 始-终,
			TrainType,// 高速动车,
			TrainID,// G101,
			Distance,// ,
			RunTime,// 05:36,
			ETime,// 12:36,
			GoTime,// 07:00,
			YuDing;// False,
	private ArrayList<Seat> SeatList;
	
	//以下三个字段为根据SeatList的数据生成的，以便在列表中显示(默认取第一行)
	private String Seat_Type,
			Price,
			Remain_Count;
	
	@Override
	public TrainListItem clone() {  
		 TrainListItem o = null;  
	     try {  
	         o = (TrainListItem) super.clone();  
	     } catch (CloneNotSupportedException e) {  
	         e.printStackTrace();  
	     }  
	     return o;  
	}

	public String getStationS() {
		return StationS;
	}

	public void setStationS(String stationS) {
		StationS = stationS;
	}

	public String getStationE() {
		return StationE;
	}

	public void setStationE(String stationE) {
		StationE = stationE;
	}

	public String getSFType() {
		return SFType;
	}

	public void setSFType(String sFType) {
		SFType = sFType;
	}

	public String getTrainType() {
		return TrainType;
	}

	public void setTrainType(String trainType) {
		TrainType = trainType;
	}

	public String getTrainID() {
		return TrainID;
	}

	public void setTrainID(String trainID) {
		TrainID = trainID;
	}

	public String getDistance() {
		return Distance;
	}

	public void setDistance(String distance) {
		Distance = distance;
	}

	public String getRunTime() {
		return RunTime;
	}

	public void setRunTime(String runTime) {
		RunTime = runTime;
	}

	public String getETime() {
		return ETime;
	}

	public void setETime(String eTime) {
		ETime = eTime;
	}

	public String getGoTime() {
		return GoTime;
	}

	public void setGoTime(String goTime) {
		GoTime = goTime;
	}

	public String getYuDing() {
		return YuDing;
	}

	public void setYuDing(String yuDing) {
		YuDing = yuDing;
	}

	public ArrayList<Seat> getSeatList() {
		return SeatList;
	}

	public void setSeatList(ArrayList<Seat> seatList) {
		SeatList = seatList;
	}

	public String getSeat_Type() {
		return Seat_Type;
	}

	public void setSeat_Type(String seat_Type) {
		Seat_Type = seat_Type;
	}

	public String getPrice() {
		return Price;
	}

	public void setPrice(String price) {
		Price = price;
	}

	public String getRemain_Count() {
		return Remain_Count;
	}

	public void setRemain_Count(String remain_Count) {
		Remain_Count = remain_Count;
	}

}
