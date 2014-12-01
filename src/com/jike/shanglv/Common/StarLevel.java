package com.jike.shanglv.Common;

import java.util.HashMap;

/**
 * 酒店星级与对应的代码
 * @author Administrator
 *
 */
public class StarLevel {
	public static HashMap<String, String> Starlevel= new HashMap<String, String>();
	public static HashMap<String,String> StarlevelReverse= new HashMap<String,String>();
	static {
		Starlevel.put("2", "二星级及以下经济");
		Starlevel.put("3", "三星级/舒适");
		Starlevel.put("4", "四星级/高档");
		Starlevel.put("5", "五星级/豪华");
		
		StarlevelReverse.put("二星级及以下经济","2");
		StarlevelReverse.put("三星级/舒适","3");
		StarlevelReverse.put("四星级/高档","4");
		StarlevelReverse.put("五星级/豪华","5");
		StarlevelReverse.put("不限","");
	}
}
