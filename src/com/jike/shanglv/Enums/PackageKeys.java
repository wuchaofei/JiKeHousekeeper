package com.jike.shanglv.Enums;

public enum PackageKeys {
	WELCOME_DRAWABLE("WELCOME_DRAWABLE"),//欢迎页面
	MENU_LOGO_DRAWABLE("MENU_LOGO_DRAWABLE"),//首页顶部title
	APP_NAME("APP_NAME"),//程序名称
	UPDATE_NOTE("UPDATE_NOTE"),//升级参数节点
	RECOMMAND_CODE("RECOMMAND_CODE"),//默认推荐码
	PLATFORM("PLATFORM"),//平台：B2B、B2C
	USERKEY("USERKEY"),//userkey
	ORGIN("ORGIN"),//orgin
	
	phoneNum("phoneNum")//
	
	;

	private String key;
	private PackageKeys(String s) {
		key = s;
	}

	public String getString() {
		return key;
	}
}
