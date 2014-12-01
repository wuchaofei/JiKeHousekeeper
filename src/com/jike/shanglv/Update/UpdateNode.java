package com.jike.shanglv.Update;

public class UpdateNode {
	String name, version, content,// 1、全新界面 2、增加话费充值
			download_url,// http://b2b.51jp.cn/download/android/Jike_Shanglv_self.apk
			softname,
			updatetime;// 2013-07-16

	int	hotelcity,// 2
			flightcity,// 3
			iflightcity,// 4
			traincity,// 5
			versionCode;// 16

	public String getSoftname() {
		return softname;
	}

	public void setSoftname(String softname) {
		this.softname = softname;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public int getHotelcity() {
		return hotelcity;
	}

	public void setHotelcity(int hotelcity) {
		this.hotelcity = hotelcity;
	}

	public int getFlightcity() {
		return flightcity;
	}

	public void setFlightcity(int flightcity) {
		this.flightcity = flightcity;
	}

	public int getIflightcity() {
		return iflightcity;
	}

	public void setIflightcity(int iflightcity) {
		this.iflightcity = iflightcity;
	}

	public int getTraincity() {
		return traincity;
	}

	public void setTraincity(int traincity) {
		this.traincity = traincity;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	
}
