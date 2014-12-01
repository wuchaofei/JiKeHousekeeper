package com.jike.shanglv.Models;

import java.util.ArrayList;
import org.json.JSONArray;

import org.json.JSONObject;

import com.jike.shanglv.NetAndJson.JSONHelper;

public class HotelRoom {
	private String rid,// 618600
			roomtype,// 0
			hotelsupplier,// zn
			allrate,// 10
			title,// 豪华房.
			adsl,// 有(收费)
			bed,// 大床,双床
			area,// 36-46
			floor,// 7-17
			status,// 0
			notes,//
			AvailableAmount;// 有房
	ArrayList<HotelRoomImg> img;
	ArrayList<HotelRoomPlan> plans;
	
	//将计划中的成员放到HotelRoom中来，以便在房型选择时展示一个房间的多个计划
	private String planid,//96062
		    planname,//含双早
		    totalprice,//6348
		    priceCode,//RMB
		    iscard,//0
		    description,// {},
		    AddValues,//: {},
		    avgprice,//1587
		    jiangjin,//79
		    menshi,//1587
		    planStatus;//0;
	
	public HotelRoom(JSONObject object) {
		try {
			img=new ArrayList<HotelRoomImg>();
			plans=new ArrayList<HotelRoomPlan>();
			this.adsl=object.getString("adsl");
			this.rid=object.getString("rid");
			this.roomtype=object.getString("roomtype");
			this.hotelsupplier=object.getString("hotelsupplier");
			this.allrate=object.getString("allrate");
			this.title=object.getString("title");
			this.bed=object.getString("bed");
			this.area=object.getString("area");
			this.floor=object.getString("floor");
			this.status=object.getString("status");
			this.notes=object.getString("notes");
			this.AvailableAmount=object.getString("AvailableAmount");
			JSONArray ja=object.getJSONArray("img");
			if (ja.length()>0) {
				for (int i = 0; i < ja.length(); i++) {
					HotelRoomImg hri=JSONHelper.parseObject(ja.getJSONObject(i), HotelRoomImg.class);
					img.add(hri);
				}
			}
			JSONArray jaPlanArray=object.getJSONArray("plans");
			if (jaPlanArray.length()>0) {
				for (int i = 0; i < jaPlanArray.length(); i++) {
					HotelRoomPlan hrp=new HotelRoomPlan(jaPlanArray.getJSONObject(i));
					plans.add(hrp);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//ActivityHotelBooking中获取Intent数据时，反序列化成对象
	public HotelRoom(JSONObject object,Boolean isfull) {
		try {
			if(object.has("adsl"))this.adsl=object.getString("adsl");
			if(object.has("rid"))this.rid=object.getString("rid");
			if(object.has("roomtype"))this.roomtype=object.getString("roomtype");
			if(object.has("hotelsupplier"))this.hotelsupplier=object.getString("hotelsupplier");
			if(object.has("allrate"))this.allrate=object.getString("allrate");
			if(object.has("title"))this.title=object.getString("title");
			if(object.has("bed"))this.bed=object.getString("bed");
			if(object.has("area"))this.area=object.getString("area");
			if(object.has("floor"))this.floor=object.getString("floor");
			if(object.has("status"))this.status=object.getString("status");
			if(object.has("notes"))this.notes=object.getString("notes");
			if(object.has("AvailableAmount"))this.AvailableAmount=object.getString("AvailableAmount");
			if(object.has("planid"))this.planid=object.getString("planid");
			if(object.has("planname"))this.planname=object.getString("planname");
			if(object.has("totalprice"))this.totalprice=object.getString("totalprice");
			if(object.has("priceCode"))this.priceCode=object.getString("priceCode");
			if(object.has("iscard"))this.iscard=object.getString("iscard");
			if(object.has("description"))this.description=object.getString("description");
			if(object.has("AddValues"))this.AddValues=object.getString("AddValues");
			if(object.has("avgprice"))this.avgprice=object.getString("avgprice");
			if(object.has("jiangjin"))this.jiangjin=object.getString("jiangjin");
			if(object.has("menshi"))this.menshi=object.getString("menshi");
			if(object.has("planStatus"))this.planStatus=object.getString("planStatus");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<HotelRoom> HotelRoomList(JSONObject object) {
		ArrayList<HotelRoom> hotelRoomsList=new ArrayList<HotelRoom>();
		try {
			HotelRoom hr0=new HotelRoom(object);
			for (int i = 0; i < hr0.getPlans().size(); i++) {
				HotelRoom singleDetailHotelRoom=new HotelRoom(object);
				singleDetailHotelRoom.planid=singleDetailHotelRoom.getPlans().get(i).getPlanid();
				singleDetailHotelRoom.planname=singleDetailHotelRoom.getPlans().get(i).getPlanname();
				singleDetailHotelRoom.totalprice=singleDetailHotelRoom.getPlans().get(i).getTotalprice();
				singleDetailHotelRoom.priceCode=singleDetailHotelRoom.getPlans().get(i).getPriceCode();
				singleDetailHotelRoom.iscard=singleDetailHotelRoom.getPlans().get(i).getIscard();
				singleDetailHotelRoom.description=singleDetailHotelRoom.getPlans().get(i).getDescription();
				singleDetailHotelRoom.AddValues=singleDetailHotelRoom.getPlans().get(i).getAddValues();
				singleDetailHotelRoom.avgprice=singleDetailHotelRoom.getPlans().get(i).getAvgprice();
				singleDetailHotelRoom.jiangjin=singleDetailHotelRoom.getPlans().get(i).getJiangjin();
				singleDetailHotelRoom.menshi=singleDetailHotelRoom.getPlans().get(i).getMenshi();
				singleDetailHotelRoom.planStatus=singleDetailHotelRoom.getPlans().get(i).getStatus();
//				if (!singleDetailHotelRoom.planStatus.equals("0")&&!singleDetailHotelRoom.status.equals("0")) {
				hotelRoomsList.add(singleDetailHotelRoom);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hotelRoomsList;
	}
	public ArrayList<HotelRoomPlan> getPlans() {
		return plans;
	}
	public void setPlans(ArrayList<HotelRoomPlan> plans) {
		this.plans = plans;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAdsl() {
		return adsl;
	}
	public void setAdsl(String adsl) {
		this.adsl = adsl;
	}
	public String getBed() {
		return bed;
	}
	public void setBed(String bed) {
		this.bed = bed;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getAvailableAmount() {
		return AvailableAmount;
	}
	public void setAvailableAmount(String availableAmount) {
		AvailableAmount = availableAmount;
	}
	public 	ArrayList<HotelRoomImg> getImg() {
		return img;
	}
	public void setImg(	ArrayList<HotelRoomImg> img) {
		this.img = img;
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
	public String getPlanStatus() {
		return planStatus;
	}
	public void setPlanStatus(String planStatus) {
		this.planStatus = planStatus;
	}
}

/*
 * 
{
    "c": "0000",
    "d": [
        {
            "hotelid": "63448",
            "tm1": "2014-08-30",
            "tm2": "2014-9-3",
            "rooms": [
                {
                    "rid": "618600",
                    "roomtype": "0",
                    "hotelsupplier": "zn",
                    "allrate": "10",
                    "title": "豪华房.",
                    "adsl": "有(收费)",
                    "bed": "大床,双床",
                    "area": "36-46",
                    "floor": "7-17",
                    "status": "0",
                    "notes": " ",
                    "AvailableAmount": "有房",
                    "img": [],
                    "plans": [
                        {
                            "planid": "96062",
                            "planname": "含双早",
                            "totalprice": "6348",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
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
                            "avgprice": "1587",
                            "jiangjin": "79",
                            "menshi": "1587",
                            "status": "0"
                        },
                        {
                            "planid": "161837",
                            "planname": "不含早",
                            "totalprice": "5428",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "1357",
                                    "price": "1357",
                                    "jiangjin": "82"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "1357",
                                    "price": "1357",
                                    "jiangjin": "82"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "1357",
                                    "price": "1357",
                                    "jiangjin": "82"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "1357",
                                    "price": "1357",
                                    "jiangjin": "82"
                                }
                            ],
                            "avgprice": "1357",
                            "jiangjin": "67",
                            "menshi": "1357",
                            "status": "0"
                        },
                        {
                            "planid": "164314",
                            "planname": "含单早",
                            "totalprice": "5888",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "1472",
                                    "price": "1472",
                                    "jiangjin": "89"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "1472",
                                    "price": "1472",
                                    "jiangjin": "89"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "1472",
                                    "price": "1472",
                                    "jiangjin": "89"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "1472",
                                    "price": "1472",
                                    "jiangjin": "89"
                                }
                            ],
                            "avgprice": "1472",
                            "jiangjin": "73",
                            "menshi": "1472",
                            "status": "0"
                        },
                        {
                            "planid": "314080",
                            "planname": "含双早(热辣早餐促销)",
                            "totalprice": "5848",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "9000",
                                    "price": "1232",
                                    "jiangjin": "75"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "9000",
                                    "price": "1232",
                                    "jiangjin": "75"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "9000",
                                    "price": "1692",
                                    "jiangjin": "103"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "9000",
                                    "price": "1692",
                                    "jiangjin": "103"
                                }
                            ],
                            "avgprice": "1462",
                            "jiangjin": "73",
                            "menshi": "9000",
                            "status": "0"
                        },
                        {
                            "planid": "314082",
                            "planname": "含单早(一元早餐促销)",
                            "totalprice": "5432",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "9000",
                                    "price": "1128",
                                    "jiangjin": "69"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "9000",
                                    "price": "1128",
                                    "jiangjin": "69"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "9000",
                                    "price": "1588",
                                    "jiangjin": "96"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "9000",
                                    "price": "1588",
                                    "jiangjin": "96"
                                }
                            ],
                            "avgprice": "1358",
                            "jiangjin": "67",
                            "menshi": "9000",
                            "status": "0"
                        },
                        {
                            "planid": "507862",
                            "planname": "含双早(家庭套餐）",
                            "totalprice": "6514",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "1628",
                                    "price": "1628",
                                    "jiangjin": "99"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "1628",
                                    "price": "1628",
                                    "jiangjin": "99"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "1628",
                                    "price": "1628",
                                    "jiangjin": "99"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "0",
                                    "price": "1630",
                                    "jiangjin": "99"
                                }
                            ],
                            "avgprice": "1629",
                            "jiangjin": "81",
                            "menshi": "0",
                            "status": "0"
                        },
                        {
                            "planid": "531101",
                            "planname": "不含早(买3送1）",
                            "totalprice": "5428",
                            "priceCode": "",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "9000",
                                    "price": "1357",
                                    "jiangjin": "82"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "9000",
                                    "price": "1357",
                                    "jiangjin": "82"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "-1",
                                    "price": "×",
                                    "jiangjin": "0"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "-1",
                                    "price": "×",
                                    "jiangjin": "0"
                                }
                            ],
                            "avgprice": "1357",
                            "jiangjin": "67",
                            "menshi": "9000",
                            "status": "0"
                        }
                    ]
                },
                {
                    "rid": "618599",
                    "roomtype": "0",
                    "hotelsupplier": "zn",
                    "allrate": "10",
                    "title": "豪华园景房.",
                    "adsl": "有(收费)",
                    "bed": "大床(180cm*200cm)",
                    "area": "36-48",
                    "floor": "18-25",
                    "status": "0",
                    "notes": " ",
                    "AvailableAmount": "有房",
                    "img": [],
                    "plans": [
                        {
                            "planid": "161837",
                            "planname": "不含早",
                            "totalprice": "6348",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
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
                            "avgprice": "1587",
                            "jiangjin": "79",
                            "menshi": "1587",
                            "status": "0"
                        }
                    ]
                },
                {
                    "rid": "618601",
                    "roomtype": "0",
                    "hotelsupplier": "zn",
                    "allrate": "10",
                    "title": "行政房.",
                    "adsl": "有(收费)",
                    "bed": "大床,双床,大床(180cm*200cm)",
                    "area": "36-48",
                    "floor": "26-32",
                    "status": "0",
                    "notes": "大床：180cm*200cm或双床：150cm*200cm",
                    "AvailableAmount": "有房",
                    "img": [],
                    "plans": [
                        {
                            "planid": "119668",
                            "planname": "含2份早餐",
                            "totalprice": "7728",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "1932",
                                    "price": "1932",
                                    "jiangjin": "117"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "1932",
                                    "price": "1932",
                                    "jiangjin": "117"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "1932",
                                    "price": "1932",
                                    "jiangjin": "117"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "1932",
                                    "price": "1932",
                                    "jiangjin": "117"
                                }
                            ],
                            "avgprice": "1932",
                            "jiangjin": "96",
                            "menshi": "1932",
                            "status": "0"
                        },
                        {
                            "planid": "161949",
                            "planname": "含1份早餐",
                            "totalprice": "7268",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "1817",
                                    "price": "1817",
                                    "jiangjin": "110"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "1817",
                                    "price": "1817",
                                    "jiangjin": "110"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "1817",
                                    "price": "1817",
                                    "jiangjin": "110"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "1817",
                                    "price": "1817",
                                    "jiangjin": "110"
                                }
                            ],
                            "avgprice": "1817",
                            "jiangjin": "90",
                            "menshi": "1817",
                            "status": "0"
                        }
                    ]
                },
                {
                    "rid": "618602",
                    "roomtype": "0",
                    "hotelsupplier": "zn",
                    "allrate": "10",
                    "title": "行政套房.",
                    "adsl": "有(收费)",
                    "bed": "大床(180cm*200cm)",
                    "area": "75",
                    "floor": "20-32",
                    "status": "0",
                    "notes": " ",
                    "AvailableAmount": "有房",
                    "img": [],
                    "plans": [
                        {
                            "planid": "103983",
                            "planname": "双人入住,连住二晚起5折优惠",
                            "totalprice": "9200",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "2300",
                                    "price": "2300",
                                    "jiangjin": "139"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "2300",
                                    "price": "2300",
                                    "jiangjin": "139"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "2300",
                                    "price": "2300",
                                    "jiangjin": "139"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "2300",
                                    "price": "2300",
                                    "jiangjin": "139"
                                }
                            ],
                            "avgprice": "2300",
                            "jiangjin": "115",
                            "menshi": "2300",
                            "status": "0"
                        },
                        {
                            "planid": "119410",
                            "planname": "单人入住,连住二晚起5折优惠",
                            "totalprice": "9200",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "2300",
                                    "price": "2300",
                                    "jiangjin": "139"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "2300",
                                    "price": "2300",
                                    "jiangjin": "139"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "2300",
                                    "price": "2300",
                                    "jiangjin": "139"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "2300",
                                    "price": "2300",
                                    "jiangjin": "139"
                                }
                            ],
                            "avgprice": "2300",
                            "jiangjin": "115",
                            "menshi": "2300",
                            "status": "0"
                        },
                        {
                            "planid": "119668",
                            "planname": "含2份早餐",
                            "totalprice": "18400",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "4600",
                                    "price": "4600",
                                    "jiangjin": "277"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "4600",
                                    "price": "4600",
                                    "jiangjin": "277"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "4600",
                                    "price": "4600",
                                    "jiangjin": "277"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "4600",
                                    "price": "4600",
                                    "jiangjin": "277"
                                }
                            ],
                            "avgprice": "4600",
                            "jiangjin": "230",
                            "menshi": "4600",
                            "status": "0"
                        },
                        {
                            "planid": "161949",
                            "planname": "含1份早餐",
                            "totalprice": "18400",
                            "priceCode": "RMB",
                            "iscard": "0",
                            "description": {},
                            "AddValues": {},
                            "date": [
                                {
                                    "day": "2014-08-30",
                                    "week": "6",
                                    "menshi": "4600",
                                    "price": "4600",
                                    "jiangjin": "277"
                                },
                                {
                                    "day": "2014-08-31",
                                    "week": "0",
                                    "menshi": "4600",
                                    "price": "4600",
                                    "jiangjin": "277"
                                },
                                {
                                    "day": "2014-09-01",
                                    "week": "1",
                                    "menshi": "4600",
                                    "price": "4600",
                                    "jiangjin": "277"
                                },
                                {
                                    "day": "2014-09-02",
                                    "week": "2",
                                    "menshi": "4600",
                                    "price": "4600",
                                    "jiangjin": "277"
                                }
                            ],
                            "avgprice": "4600",
                            "jiangjin": "230",
                            "menshi": "4600",
                            "status": "0"
                        }
                    ]
                }
            ]
        }
    ]
}
 */