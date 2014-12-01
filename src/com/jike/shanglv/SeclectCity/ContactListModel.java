package com.jike.shanglv.SeclectCity;

public class ContactListModel {

	public String name;
	public String pinyin;
	public String number;
	String shortchar;
	
	public  ContactListModel() {
		
	}

	public ContactListModel(String str, String str1, String str2) {
		this.name = str;
		this.pinyin = str1;
		this.number = str2;
	}

	public String getpinyin() {
		return this.pinyin;
	}

	public String getname() {
		return this.name;
	}

	public String getnumber() {
		return this.number;
	}
	
//	public String getName() {
//		return name;
//	}
//
//	public String getNumber() {
//		return number;
//  }
//
//	public String getPinyin() {
//		return pinyin;
//	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getShortchar() {
		return shortchar;
	}

	public void setShortchar(String shortchar) {
		this.shortchar = shortchar;
	}
}
