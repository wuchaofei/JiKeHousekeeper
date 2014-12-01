package com.jike.shanglv.Models;

public class InterDemandPassenger {
    private String  surname,// 姓 必传
	    givenname,// 名 必传
	    cardNo,// 证件号码 必传
	    sex,// 性别 1男 0女 必传
	    cardType,// 证件类型 必传
	    cusBirth,// 生日(yyyy-MM-dd) 必传
	    numberValiddate,// 证件有效期(yyyy-MM-dd) 必传
	    country,// 国籍 必传
	    qianfadi,// 签发地 必传
	    insurance,// 保险分数 必传
        savePsg;// 是否保存常旅客 (true、false) 必传
    
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getGivenname() {
		return givenname;
	}
	public void setGivenname(String givenname) {
		this.givenname = givenname;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getCusBirth() {
		return cusBirth;
	}
	public void setCusBirth(String cusBirth) {
		this.cusBirth = cusBirth;
	}
	public String getNumberValiddate() {
		return numberValiddate;
	}
	public void setNumberValiddate(String numberValiddate) {
		this.numberValiddate = numberValiddate;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getQianfadi() {
		return qianfadi;
	}
	public void setQianfadi(String qianfadi) {
		this.qianfadi = qianfadi;
	}
	public String getInsurance() {
		return insurance;
	}
	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}
	public String getSavePsg() {
		return savePsg;
	}
	public void setSavePsg(String savePsg) {
		this.savePsg = savePsg;
	}
}
