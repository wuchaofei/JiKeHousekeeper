/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.jike.shanglv.pos.alipay.android.mpos.demo.helper;

/**
 * 
 * @author jianmin.jiang
 * 
 * @version $Id: PosResult.java, v 0.1 2012-2-28 下午5:19:47 jianmin.jiang Exp $
 */
public enum PosError {

	ERR_DUPLICATED_OUT_TRADE_NO("ERR_DUPLICATED_OUT_TRADE_NO", "重复的外部交易号"),

	ERR_INVALID_SIGN("ERR_INVALID_SIGN", "签名不正确"),

	ERR_INVALID_PARTNER_STATUS("ERR_INVALID_PARTNER_STATUS", "外部商户状态不正常"),

	ERR_COMMON("ERR_COMMON", "无法定位具体原因的错误"),

	ERR_INVALID_PID("ERR_INVALID_PID", "不合法的授权码，或者该商户没有签约"),

	IND_ERR_INVALID_AID("IND_ERR_INVALID_AID", "您不具备本软件收款权限，请检查是否正确授权。"),

	IND_ERR_NOT_EXIST_MID("IND_ERR_NOT_EXIST_MID", "公司号不存在"),

	IND_ERR_NOT_EXIST_AID("IND_ERR_NOT_EXIST_AID", "授权码不存在"),

	IND_ERR_NOT_EXIST_SELLER("IND_ERR_NOT_EXIST_SELLER", "卖家不存在"),

	IND_ERR_NOT_EXIST_PARTNER("IND_ERR_NOT_EXIST_PARTNER", "商户不存在"),

	IND_ERR_INVALID_PARTNER("IND_ERR_INVALID_PARTNER", "商户没有签约超级收款"),

	IND_ERR_INVALID_MPOS_ID("IND_ERR_INVALID_MPOS_ID", "客户端的授权已经失效，请重新激活"),

	IND_ERR_INVALID_MID("IND_ERR_INVALID_MID", "您不具备本软件收款权限，请检查是否正确授权。"),

	IND_ERR_CLIENT_NEED_UPDATE("IND_ERR_CLIENT_NEED_UPDATE", "行业客户端可更新"),

	IND_ERR_INVALID_SIGN("IND_ERR_INVALID_SIGN", "签名失败");

	private String code;
	private String desc;

	private PosError(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public static PosError getEnumByCode(String code) {
		PosError[] list = values();
		for (PosError item : list) {
			if (item.getCode().equalsIgnoreCase(code)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * @return Returns the code.
	 */
	public final String getCode() {
		return code;
	}

	/**
	 * @return Returns the desc.
	 */
	public final String getDesc() {
		return desc;
	}

}
