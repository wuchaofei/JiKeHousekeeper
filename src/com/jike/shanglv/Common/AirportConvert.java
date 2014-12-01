package com.jike.shanglv.Common;

import java.util.HashMap;

public class AirportConvert {
	public static HashMap<String, String>  AIRPORT = new HashMap<String, String>();
	static {
		AIRPORT.put("上海虹桥", "虹桥");
		AIRPORT.put("上海浦东", "浦东");
		AIRPORT.put("北京首都", "首都");
		AIRPORT.put("北京南苑", "南苑");
		AIRPORT.put("乌鲁木齐", "地窝铺");
	}
}