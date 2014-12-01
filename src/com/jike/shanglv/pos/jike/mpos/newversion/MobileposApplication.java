/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.jike.shanglv.pos.jike.mpos.newversion;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.protocol.HTTP;

import com.alipay.wireless.sys.ApplicationGlobal;

import android.app.Application;

/**
 * 
 * @author jianmin.jiang
 * 
 * @version $Id: MobileposApplication.java, v 0.1 2012-2-14 ÏÂÎç1:48:01
 *          jianmin.jiang Exp $
 */
public class MobileposApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ApplicationGlobal.getInstance().inite(this);
	}
	
	public static final byte BTOB = 0;
	public static final byte BTOC = 1;
	public static final byte TEST = 2;
	public static final byte ALL  = 3;
	
	public static byte Package_Index = ALL;
	
	public static final String[] ATTR = {"b2b","b2b2b","app", "app"};
	
	public static final String[] HTTPURL = {
		"http://getwayb2b.51jp.cn",
		"http://getwayb2b.51jp.cn",
		"http://getwaytest.51jp.cn",
		"http://getwayb2b.51jp.cn",
	};
	
	public static final String[] SITEKEY = {
		"d41d8cd98f00b204e9800998ecf8427e",
		"d41d8cd98f00b204e9800998ecf8427e",
		"asfdae545642a1sdf55",
		"d41d8cd98f00b204e9800998ecf8427e",
	};
	
	public static String getWeb(String strUrl) {
		
		System.out.println(strUrl);

		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {

			conn = (HttpURLConnection) (new URL(strUrl)).openConnection();

			conn.setDoInput(true);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(10000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");

			inputStream = conn.getInputStream();

			return (getStringFromStream(inputStream));

		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected static String getStringFromStream(InputStream inputstream) {

		int length = 10000;

		StringBuffer stringBuffer = new StringBuffer();
		try {
			InputStreamReader inputStreamReader = new InputStreamReader( 
					inputstream, HTTP.UTF_8);
			char buffer[] = new char[length];
			int count;
			while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
				stringBuffer.append(buffer, 0, count);
			}
		} catch (Exception e) {
			return null;
		}
		return stringBuffer.toString();
	}

}
