package com.jike.shanglv.Models;


import org.json.JSONObject;

public class OrderReceipt {
	private String orderid;//I1302101103284362
	private String amount;//671.00 
	private String pnr;// 
	private String ordertime;//2013-01-10 11:03:28
	
	public OrderReceipt(String jsonString){
		try {
			JSONObject json=new JSONObject(jsonString);
			if(jsonString.contains("orderid"))this.orderid=json.getString("orderid");
			if(jsonString.contains("amount"))this.amount=json.getString("amount");
			if(jsonString.contains("pnr"))this.pnr=json.getString("pnr");
			if(jsonString.contains("ordertime"))this.ordertime=json.getString("ordertime");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPnr() {
		return pnr;
	}
	public void setPnr(String pnr) {
		this.pnr = pnr;
	}
	public String getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}
}
