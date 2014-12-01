package com.jike.shanglv.Models;

public class TrainOrderPassenger {
	private String CardNo,// 证件号 必传
			CardType,// 证件类型（二代身份证） 必传
			Phone,// 手机号 必传
			PsgName,// 乘车人姓名 必传
			IncAmount,// 保险 必传
			SeatType,// 席别(硬座...) 必传
			Saleprice,// 席别单价 必传
			TicketType; // 成人票、儿童票（暂只支持成人票） 必传

	public String getCardNo() {
		return CardNo;
	}

	public void setCardNo(String cardNo) {
		CardNo = cardNo;
	}

	public String getCardType() {
		return CardType;
	}

	public void setCardType(String cardType) {
		CardType = cardType;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getPsgName() {
		return PsgName;
	}

	public void setPsgName(String psgName) {
		PsgName = psgName;
	}

	public String getIncAmount() {
		return IncAmount;
	}

	public void setIncAmount(String incAmount) {
		IncAmount = incAmount;
	}

	public String getSeatType() {
		return SeatType;
	}

	public void setSeatType(String seatType) {
		SeatType = seatType;
	}

	public String getSaleprice() {
		return Saleprice;
	}

	public void setSaleprice(String saleprice) {
		Saleprice = saleprice;
	}

	public String getTicketType() {
		return TicketType;
	}

	public void setTicketType(String ticketType) {
		TicketType = ticketType;
	}
	
}
