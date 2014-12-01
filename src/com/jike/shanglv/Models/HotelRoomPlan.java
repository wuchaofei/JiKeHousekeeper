package com.jike.shanglv.Models;

import java.util.ArrayList;

import org.json.JSONArray;

import org.json.JSONObject;

import com.jike.shanglv.NetAndJson.JSONHelper;

public class HotelRoomPlan {
	private String planid,//96062
		     planname,//º¬Ë«Ôç
		     totalprice,//6348
		     priceCode,//RMB
		     iscard,//0
		     description,// {},
		     AddValues,//: {},
		     avgprice,//1587
		     jiangjin,//79
		     menshi,//1587
		     status;//0;
	private ArrayList<HotelRoomPlanDate> dates;
	
	public HotelRoomPlan(JSONObject jsonObject) {
		 try {
			 this.planid=jsonObject.getString("planid");
			 this.planname=jsonObject.getString("planname");
			 this.totalprice=jsonObject.getString("totalprice");
			 this.priceCode=jsonObject.getString("priceCode");
			 this.iscard=jsonObject.getString("iscard");
			 this.description=jsonObject.getString("description");
			 this.AddValues=jsonObject.getString("AddValues");
			 this.avgprice=jsonObject.getString("avgprice");
			 this.jiangjin=jsonObject.getString("jiangjin");
			 this.menshi=jsonObject.getString("menshi");
			 this.status=jsonObject.getString("status");
			 JSONArray jArray=jsonObject.getJSONArray("dates");
			 if (jArray.length()>0) {
				 for (int i = 0; i < jArray.length(); i++) {
					 HotelRoomPlanDate hrpd=JSONHelper.parseObject(jArray.getJSONObject(i), HotelRoomPlanDate.class);
					 dates.add(hrpd);
				 }
			}
		 } catch (Exception e) {
				e.printStackTrace();
		}
	}
	public String getPlanid() {
		return planid;
	}
	public void setPlanid(String planid) {
		this.planid = planid;
	}
	public String getPlanname() {
		return planname;
	}
	public void setPlanname(String planname) {
		this.planname = planname;
	}
	public String getTotalprice() {
		return totalprice;
	}
	public void setTotalprice(String totalprice) {
		this.totalprice = totalprice;
	}
	public String getPriceCode() {
		return priceCode;
	}
	public void setPriceCode(String priceCode) {
		this.priceCode = priceCode;
	}
	public String getIscard() {
		return iscard;
	}
	public void setIscard(String iscard) {
		this.iscard = iscard;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAddValues() {
		return AddValues;
	}
	public void setAddValues(String addValues) {
		AddValues = addValues;
	}
	public String getAvgprice() {
		return avgprice;
	}
	public void setAvgprice(String avgprice) {
		this.avgprice = avgprice;
	}
	public String getJiangjin() {
		return jiangjin;
	}
	public void setJiangjin(String jiangjin) {
		this.jiangjin = jiangjin;
	}
	public String getMenshi() {
		return menshi;
	}
	public void setMenshi(String menshi) {
		this.menshi = menshi;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ArrayList<HotelRoomPlanDate> getDates() {
		return dates;
	}
	public void setDates(ArrayList<HotelRoomPlanDate> dates) {
		this.dates = dates;
	}
}
/*
"date": [
         {
             "day": "2014-08-30",
             "week": "6",
             "menshi": "1587",
             "price": "1587",
             "jiangjin": "96"
         },
         {
             "day": "2014-08-31",
             "week": "0",
             "menshi": "1587",
             "price": "1587",
             "jiangjin": "96"
         },
         {
             "day": "2014-09-01",
             "week": "1",
             "menshi": "1587",
             "price": "1587",
             "jiangjin": "96"
         },
         {
             "day": "2014-09-02",
             "week": "2",
             "menshi": "1587",
             "price": "1587",
             "jiangjin": "96"
         }
     ],
*/