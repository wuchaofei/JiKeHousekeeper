package com.jike.shanglv.Models;

import java.util.ArrayList;
import org.json.JSONArray;

import org.json.JSONObject;

public class HotelRoomComfirm {

	private String hid,// 73110
			guid,// 10b9bb1b-c384-0b78-a45a-d3d2d49a6b8e
			roomtype,// 0
			hotelsupplier,// zn   suppid
			allrate,// 10
			yuding,// False
			tm1,// 2014-09-03
			tm2,// 2014-09-04
			allotmenttype,//
			ratetype,//
			supplierid,//
			facepaytype,//
			faceprice,//
			includebreakfastqty2,//
			HotelName,// 上海颐美庭园酒店
			RoomName,// 特价房
			RatePlanName,// 不含早
			AvailableAmount,// 0
			ratesinfo;
	private CroomsInfo roomsInfo;
	private Cprices prices;
	private CGaranteeRule GaranteeRule;
	private String AddValues;

	public HotelRoomComfirm(JSONObject jsonObject) {
		try {
			if(jsonObject.has("hid"))this.hid=jsonObject.getString("hid");
			if(jsonObject.has("guid"))this.guid=jsonObject.getString("guid");// 10b9bb1b-c384-0b78-a45a-d3d2d49a6b8e
			if(jsonObject.has("roomtype"))this.roomtype=jsonObject.getString("roomtype");// 0
			if(jsonObject.has("hotelsupplier"))this.hotelsupplier=jsonObject.getString("hotelsupplier");// zn
			if(jsonObject.has("allrate"))this.allrate=jsonObject.getString("allrate");// 10
			if(jsonObject.has("yuding"))this.yuding=jsonObject.getString("yuding");// False
			if(jsonObject.has("tm1"))this.tm1=jsonObject.getString("tm1");// 2014-09-03
			if(jsonObject.has("tm2"))this.tm2=jsonObject.getString("tm2");// 2014-09-04
			if(jsonObject.has("allotmenttype"))this.allotmenttype=jsonObject.getString("allotmenttype");//
			if(jsonObject.has("ratetype"))this.ratetype=jsonObject.getString("ratetype");//
			if(jsonObject.has("supplierid"))this.supplierid=jsonObject.getString("supplierid");//
			if(jsonObject.has("facepaytype"))this.facepaytype=jsonObject.getString("facepaytype");//
			if(jsonObject.has("faceprice"))this.faceprice=jsonObject.getString("faceprice");//
			if(jsonObject.has("includebreakfastqty2"))this.includebreakfastqty2=jsonObject.getString("includebreakfastqty2");//
			if(jsonObject.has("HotelName"))this.HotelName=jsonObject.getString("HotelName");// 上海颐美庭园酒店
			if(jsonObject.has("RoomName"))this.RoomName=jsonObject.getString("RoomName");// 特价房
			if(jsonObject.has("RatePlanName"))this.RatePlanName=jsonObject.getString("RatePlanName");// 不含早
			if(jsonObject.has("AvailableAmount"))this.AvailableAmount=jsonObject.getString("AvailableAmount");// 0
			if(jsonObject.has("ratesinfo"))this.ratesinfo=jsonObject.getString("ratesinfo");//
			
			if(jsonObject.has("prices")){
				Cprices cprices=new Cprices(jsonObject.getJSONObject("prices"));
				this.prices=cprices; 
			}
			if(jsonObject.has("GaranteeRule")){
				if (jsonObject.get("GaranteeRule")!=null) {
					this.GaranteeRule=new CGaranteeRule(jsonObject.getJSONObject("GaranteeRule"));
				}
			}
			if(jsonObject.has("AddValues")){
				if (jsonObject.has("AddValues")&&!jsonObject.get("AddValues").toString().equals("{}")) {
					this.AddValues=jsonObject.getJSONObject("AddValues").getString("string");
				}
			}
			if(jsonObject.has("roomsInfo")){
				if (jsonObject.get("roomsInfo")!=null) {
					this.roomsInfo=new CroomsInfo(jsonObject.getJSONObject("roomsInfo"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getHid() {
		return hid;
	}

	public void setHid(String hid) {
		this.hid = hid;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getRoomtype() {
		return roomtype;
	}

	public void setRoomtype(String roomtype) {
		this.roomtype = roomtype;
	}

	public String getHotelsupplier() {
		return hotelsupplier;
	}

	public void setHotelsupplier(String hotelsupplier) {
		this.hotelsupplier = hotelsupplier;
	}

	public String getAllrate() {
		return allrate;
	}

	public void setAllrate(String allrate) {
		this.allrate = allrate;
	}

	public String getYuding() {
		return yuding;
	}

	public void setYuding(String yuding) {
		this.yuding = yuding;
	}

	public String getTm1() {
		return tm1;
	}

	public void setTm1(String tm1) {
		this.tm1 = tm1;
	}

	public String getTm2() {
		return tm2;
	}

	public void setTm2(String tm2) {
		this.tm2 = tm2;
	}

	public String getAllotmenttype() {
		return allotmenttype;
	}

	public void setAllotmenttype(String allotmenttype) {
		this.allotmenttype = allotmenttype;
	}

	public String getRatetype() {
		return ratetype;
	}

	public void setRatetype(String ratetype) {
		this.ratetype = ratetype;
	}

	public String getSupplierid() {
		return supplierid;
	}

	public void setSupplierid(String supplierid) {
		this.supplierid = supplierid;
	}

	public String getFacepaytype() {
		return facepaytype;
	}

	public void setFacepaytype(String facepaytype) {
		this.facepaytype = facepaytype;
	}

	public String getFaceprice() {
		return faceprice;
	}

	public void setFaceprice(String faceprice) {
		this.faceprice = faceprice;
	}

	public String getIncludebreakfastqty2() {
		return includebreakfastqty2;
	}

	public void setIncludebreakfastqty2(String includebreakfastqty2) {
		this.includebreakfastqty2 = includebreakfastqty2;
	}

	public String getHotelName() {
		return HotelName;
	}

	public void setHotelName(String hotelName) {
		HotelName = hotelName;
	}

	public String getRoomName() {
		return RoomName;
	}

	public void setRoomName(String roomName) {
		RoomName = roomName;
	}

	public String getRatePlanName() {
		return RatePlanName;
	}

	public void setRatePlanName(String ratePlanName) {
		RatePlanName = ratePlanName;
	}

	public String getAvailableAmount() {
		return AvailableAmount;
	}

	public void setAvailableAmount(String availableAmount) {
		AvailableAmount = availableAmount;
	}

	public String getRatesinfo() {
		return ratesinfo;
	}

	public void setRatesinfo(String ratesinfo) {
		this.ratesinfo = ratesinfo;
	}

	public CroomsInfo getRoomsInfo() {
		return roomsInfo;
	}

	public void setRoomsInfo(CroomsInfo roomsInfo) {
		this.roomsInfo = roomsInfo;
	}

	public Cprices getPrices() {
		return prices;
	}

	public void setPrices(Cprices prices) {
		this.prices = prices;
	}

	public CGaranteeRule getGaranteeRule() {
		return GaranteeRule;
	}

	public void setGaranteeRule(CGaranteeRule garanteeRule) {
		GaranteeRule = garanteeRule;
	}

	public class CroomsInfo {
		private String desc,// 免费上网,宽带,大床(180cm*200cm),2,
				bed,// 大床(180cm*200cm)
				adsl,// 有(免费)
				area,// 20
				floor,// 1-4
				RatePlanName;// 不含早
		
		public CroomsInfo(JSONObject object){
			try {
				if (object.has("desc"))this.desc=object.getString("desc");
				if (object.has("bed"))this.bed=object.getString("bed");
				if (object.has("adsl"))this.adsl=object.getString("adsl");
				if (object.has("area"))this.area=object.getString("area");
				if (object.has("floor"))this.floor=object.getString("floor");
				if (object.has("RatePlanName"))this.RatePlanName=object.getString("RatePlanName");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getBed() {
			return bed;
		}

		public void setBed(String bed) {
			this.bed = bed;
		}

		public String getAdsl() {
			return adsl;
		}

		public void setAdsl(String adsl) {
			this.adsl = adsl;
		}

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}

		public String getFloor() {
			return floor;
		}

		public void setFloor(String floor) {
			this.floor = floor;
		}

		public String getRatePlanName() {
			return RatePlanName;
		}

		public void setRatePlanName(String ratePlanName) {
			RatePlanName = ratePlanName;
		}
	};

	public class Cprices {
		private String fistDayPrice,// 328
				TotalJiangjin,// 16
				TotalPrice,// 328.00000
				cost,// 328.00000
				CurrencyCode;// RMB
		private ArrayList<Cdaill> daill;
		
		public Cprices(JSONObject object){
			try {
				if(object.has("fistDayPrice"))this.fistDayPrice=object.getString("fistDayPrice");
				if(object.has("TotalJiangjin"))this.TotalJiangjin=object.getString("TotalJiangjin");
				if(object.has("TotalPrice"))this.TotalPrice=object.getString("TotalPrice");
				if(object.has("cost"))this.cost=object.getString("cost");
				if(object.has("CurrencyCode"))this.CurrencyCode=object.getString("CurrencyCode");
				ArrayList<HotelRoomComfirm.Cprices.Cdaill> cdlist=new ArrayList<HotelRoomComfirm.Cprices.Cdaill>();
				JSONArray jArray=object.getJSONArray("daill");
				for (int i = 0; i < jArray.length(); i++) {
					Cdaill cdaill=new Cdaill();
					cdaill.date=jArray.getJSONObject(i).getString("date");
					cdaill.price=jArray.getJSONObject(i).getString("price");
					cdlist.add(cdaill);
				}
				this.daill=cdlist;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public String getFistDayPrice() {
			return fistDayPrice;
		}

		public void setFistDayPrice(String fistDayPrice) {
			this.fistDayPrice = fistDayPrice;
		}

		public String getTotalJiangjin() {
			return TotalJiangjin;
		}

		public void setTotalJiangjin(String totalJiangjin) {
			TotalJiangjin = totalJiangjin;
		}

		public String getTotalPrice() {
			return TotalPrice;
		}

		public void setTotalPrice(String totalPrice) {
			TotalPrice = totalPrice;
		}

		public String getCost() {
			return cost;
		}

		public void setCost(String cost) {
			this.cost = cost;
		}

		public String getCurrencyCode() {
			return CurrencyCode;
		}

		public void setCurrencyCode(String currencyCode) {
			CurrencyCode = currencyCode;
		}

		public ArrayList<Cdaill> getDaill() {
			return daill;
		}

		public void setDaill(ArrayList<Cdaill> daill) {
			this.daill = daill;
		}

		public class Cdaill {
			private String date,// 2014-09-03
					price;// 满房

			public String getDate() {
				return date;
			}

			public void setDate(String date) {
				this.date = date;
			}

			public String getPrice() {
				return price;
			}

			public void setPrice(String price) {
				this.price = price;
			}
		}
	};

	public class CAddValues {
		private String string;// 附加服务：单加1份早餐 35 元

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}
	};

	public class CGaranteeRule {
		private String romms,// 0
				status,// 1
				norule,// 0
				stattime,// 18:00
				endtime,// 06:00
				desc;// 担保条件：在14.01.02至14.12.31 入住
						// 在18:00至6:00到店，需要您提供信用卡担保。客人最早到店1小时前可以变更取消，之后无法变更取消，如未入住，将扣除第一晚房费作为违约金。

		public CGaranteeRule(JSONObject object){
			try {
				if (object.has("romms"))this.romms=object.getString("romms");
				if (object.has("status"))this.status=object.getString("status");
				if (object.has("norule"))this.norule=object.getString("norule");
				if (object.has("stattime"))this.stattime=object.getString("stattime");
				if (object.has("endtime"))this.endtime=object.getString("endtime");
				if (object.has("desc"))this.desc=object.getString("desc");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public String getRomms() {
			return romms;
		}

		public void setRomms(String romms) {
			this.romms = romms;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getNorule() {
			return norule;
		}

		public void setNorule(String norule) {
			this.norule = norule;
		}

		public String getStattime() {
			return stattime;
		}

		public void setStattime(String stattime) {
			this.stattime = stattime;
		}

		public String getEndtime() {
			return endtime;
		}

		public void setEndtime(String endtime) {
			this.endtime = endtime;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
}
