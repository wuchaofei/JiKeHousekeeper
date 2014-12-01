package com.jike.shanglv.Common;

import java.util.HashMap;

/**
 * 证件类型与对应的代码
 * @author Administrator
 *
 */
public class IdType {
	public static HashMap<Integer, String> IdType= new HashMap<Integer, String>();
	public static HashMap<String,Integer> IdTypeReverse= new HashMap<String,Integer>();
	static {
		IdType.put(0, "身份证");
		IdType.put(1, "护照");
		IdType.put(4, "港澳通行证");
		IdType.put(5, "台胞证");
		IdType.put(9, "其他");
		
		IdTypeReverse.put("身份证",0);
		IdTypeReverse.put("护照",1);
		IdTypeReverse.put("港澳通行证",4);
		IdTypeReverse.put("台胞证",5);
		IdTypeReverse.put("其他",9);
	}
}
