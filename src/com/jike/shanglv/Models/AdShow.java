package com.jike.shanglv.Models;

/*广告展示
 * */
public class AdShow {
	String imgUrl,goUrl,title,description;

	public AdShow(String imgUrl,String goUrl,String title,String description){
		this.imgUrl=imgUrl;
		this.goUrl=goUrl;
		this.title=title;
		this.description=description;
	}
	
	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getGoUrl() {
		return goUrl;
	}

	public void setGoUrl(String goUrl) {
		this.goUrl = goUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
