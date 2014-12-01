/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.jike.shanglv.pos.alipay.android.mpos.demo.pos;

/**
 * 
 * @author jianmin.jiang
 * 
 * @version $Id: ParamsItem.java, v 0.1 2012-2-18 下午4:20:22 jianmin.jiang Exp $
 */
public final class ParamsItem {

	private final static String Encoding = "UTF-8";

	private String key;
	private String value;

	public ParamsItem(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public ParamsItem(String params) {
		if (params == null || "".equals(params)) {
			return;
		}
		String[] paramList = params.split("=");
		if (paramList.length != 2) {
			return;
		}
		this.key = urlDecode(paramList[0]);
		this.value = urlDecode(paramList[1]);
	}

	public String getParams() {
		this.key = urlEncoding(this.key);
		this.value = urlEncoding(this.value);
		return this.key + "=" + this.value;
	}

	/**
	 * @return Returns the key.
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * @return Returns the value.
	 */
	public final String getValue() {
		return value;
	}

	public static String urlEncoding(String value) {
		if (value == null || "".equals(value)) {
			return "";
		}

		try {
			value = java.net.URLEncoder.encode(value, Encoding);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return value;
	}

	public static String urlDecode(String value) {
		if (value == null || "".equals(value)) {
			return "";
		}

		try {
			value = java.net.URLDecoder.decode(value, Encoding);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return value;
	}
}
