package com.jike.shanglv.SeclectCity;

public class HotelCityModel {
	String id,// 0101" +
			cityname,// ±±¾©" +
			hotelnum,// ":2875
			abcd,// B
			pinyin,// BeiJing
			suoxie,// BJ
			ishot;// 1}
	
	public String getNameSort()
	{
		return abcd;
	}

	public void setNameSort(String nameSort)
	{
		suoxie = abcd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	public String getHotelnum() {
		return hotelnum;
	}

	public void setHotelnum(String hotelnum) {
		this.hotelnum = hotelnum;
	}

	public String getAbcd() {
		return abcd;
	}

	public void setAbcd(String abcd) {
		this.abcd = abcd;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getSuoxie() {
		return suoxie;
	}

	public void setSuoxie(String suoxie) {
		this.suoxie = suoxie;
	}

	public String getIshot() {
		return ishot;
	}

	public void setIshot(String ishot) {
		this.ishot = ishot;
	}
}
