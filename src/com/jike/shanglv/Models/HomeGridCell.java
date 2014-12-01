package com.jike.shanglv.Models;

import android.content.Intent;

public class HomeGridCell {
	String name;
	int id,img;
	Intent intent;
	
	public HomeGridCell(int id,int img,String name,Intent intent) {
		this.id=id;
		this.img=img;
		this.name=name;
		this.intent=intent;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getImg() {
		return img;
	}
	public void setImg(int img) {
		this.img = img;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}
}
