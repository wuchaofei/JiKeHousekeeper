/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.jike.shanglv.pos.alipay.android.mpos.demo.helper;

import java.io.UnsupportedEncodingException;


import com.alipay.wireless.util.StringUtil;
import com.jike.shanglv.pos.alipay.android.mpos.demo.pos.ParamsItem;
/**
 * MD5的算法在RFC1321 中定义 在RFC 1321中，给出了Test suite用来检验你的实现是否正确： MD5 ("") =
 * d41d8cd98f00b204e9800998ecf8427e MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
 * MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72 MD5 ("message digest") =
 * f96b697d7cb7938d525a2f31aaf161d0 MD5 ("abcdefghijklmnopqrstuvwxyz") =
 * c3fcd3d76192e4007dfb496cca67e13b
 * 
 * @author haogj
 * 
 *         传入参数：一个字节数组 传出参数：字节数组的 MD5 结果字符串
 */
public class MD5Util {

	private static byte[] getMD5Key(String origiKey) {
		try {
			return origiKey.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static boolean checkSign(String content, String MD5key) {
		if (content != null && !"".equals(content)) {
			String[] paramList = content.split("&");
			String sign = null;
			String signString = null;
			String signTypeString = null;
			ParamsItem[] items = new ParamsItem[paramList.length];
			for (int i = 0; i < paramList.length; i++) {
				items[i] = new ParamsItem(paramList[i]);
				String key = items[i].getKey();
				String value = items[i].getValue();
				if ("sign".equals(key)) {
					sign = value;
					signString = paramList[i];
					continue;
				}
				if ("sign_type".equals(key)) {
					signTypeString = paramList[i];
				}
			}
			if (sign != null) {
				content = content.replace("&" + signString, "");
				content = content.replace(signString, "");
				content = content.replace("&" + signTypeString, "");
				content = content.replace("&" + signTypeString, "");

				String checkSign = MD5Util.sign(content, MD5key);
				return sign.equals(checkSign);

			}
		}

		return false;
	}

	public static ParamsItem[] getResult(String result) {
		 if (StringUtil.isEmpty(result)) {
		 return null;
		 }
		String[] params = result.split("&");
		ParamsItem[] results = new ParamsItem[params.length];
		for (int i = 0; i < params.length; i++) {
			results[i] = new ParamsItem(params[i]);
		}
		return results;
	}

	public static String sign(String content, String MD5Key) {
		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(content.getBytes("UTF-8"));
			byte tmp[] = md.digest(getMD5Key(MD5Key)); // MD5 的计算结果是一个 128
														// 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; //
			// 每个字节用 16 进制表示的话，使用两个字符，
			// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
				// >>>
				// 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
}
