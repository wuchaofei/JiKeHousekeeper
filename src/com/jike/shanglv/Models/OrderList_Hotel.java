package com.jike.shanglv.Models;

public class OrderList_Hotel {
	String OrderID,// H140910140320544416796,
			HotelName,// 上海中祥大酒店,
			InDate,// 2014-09-11,
			RoomName,// 高级单人房,
			OrderAmount,// 298.00,
			OrderDate,
			RoomCount,
			Passengers,
			OutDate,
			OrderStatus;// 已取消,

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public String getHotelName() {
		return HotelName;
	}

	public void setHotelName(String hotelName) {
		HotelName = hotelName;
	}

	public String getInDate() {
		return InDate;
	}

	public void setInDate(String inDate) {
		InDate = inDate;
	}

	public String getRoomName() {
		return RoomName;
	}

	public void setRoomName(String roomName) {
		RoomName = roomName;
	}

	public String getOrderAmount() {
		return OrderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		OrderAmount = orderAmount;
	}

	public String getOrderStatus() {
		return OrderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		OrderStatus = orderStatus;
	}

	public String getOrderDate() {
		return OrderDate;
	}

	public void setOrderDate(String orderDate) {
		OrderDate = orderDate;
	}

	public String getRoomCount() {
		return RoomCount;
	}

	public void setRoomCount(String roomCount) {
		RoomCount = roomCount;
	}

	public String getPassengers() {
		return Passengers;
	}

	public void setPassengers(String passengers) {
		Passengers = passengers;
	}

	public String getOutDate() {
		return OutDate;
	}

	public void setOutDate(String outDate) {
		OutDate = outDate;
	}
}
