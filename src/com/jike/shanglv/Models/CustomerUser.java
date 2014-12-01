package com.jike.shanglv.Models;

public class CustomerUser {
	String  UserName,// StoneMK, //用户名
			DealerLevel,// 钻石卡,//用户级别
			LevelID,
			RealName,// 李伟,//真实信息
			Phone,// 18502193643,//电话号码
			RegDate,// 2014-06-26 10:58,//注册日期
			Status;// 正常,//状态
	
	String	CompanyName,
			ProvinceName,//山西,
		    CityName,//晋城,
			StartDate,//2014-06-26,//账户有效期起始
    	    EndDate;//2014-07-25//账户有效期结束
	
	public String getLevelID() {
		return LevelID;
	}

	public void setLevelID(String levelID) {
		LevelID = levelID;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public String getProvinceName() {
		return ProvinceName;
	}

	public void setProvinceName(String provinceName) {
		ProvinceName = provinceName;
	}

	public String getCityName() {
		return CityName;
	}

	public void setCityName(String cityName) {
		CityName = cityName;
	}

	public String getStartDate() {
		return StartDate;
	}

	public void setStartDate(String startDate) {
		StartDate = startDate;
	}

	public String getEndDate() {
		return EndDate;
	}

	public void setEndDate(String endDate) {
		EndDate = endDate;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getDealerLevel() {
		return DealerLevel;
	}

	public void setDealerLevel(String dealerLevel) {
		DealerLevel = dealerLevel;
	}

	public String getRealName() {
		return RealName;
	}

	public void setRealName(String realName) {
		RealName = realName;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getRegDate() {
		return RegDate;
	}

	public void setRegDate(String regDate) {
		RegDate = regDate;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}
}
