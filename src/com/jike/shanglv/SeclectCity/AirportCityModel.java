package com.jike.shanglv.SeclectCity;

public class AirportCityModel {
	String airportcode;//:BJS
	String englishname;//:Beijing
	String shortname;//:北京
	String shortchar;//:BJ
	String pinyin;//:beijing
	String ishot;//:1
	
	public String getNameSort()
	{
		if (shortchar=="热门") {
			return shortchar;
		}else
		return shortchar.substring(0,1);
	}

	public void setNameSort(String nameSort)
	{
		shortchar = nameSort;
	}
	
	public String getCityName()
	{
		return shortname;
	}

	public void setCityName(String cityName)
	{
		shortname = cityName;
	}
	
	public String getAirportcode() {
		return airportcode;
	}

	public String getEnglishname() {
		return englishname;
	}

	public String getPinyin() {
		return pinyin;
	}
}
