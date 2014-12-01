package com.jike.shanglv.Models;

import java.io.Serializable;
import java.text.ParseException;


import org.json.JSONObject;

import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.Common.IdType;

/**
 * @author Administrator
 *
 */
public class Passenger  implements Serializable {
	private static final long serialVersionUID = 1L;
	String passengerType,passengerName,identificationType,identificationNum;
//	String mobile; //"手机号码，可选",
	String isunum; //"保险数量，必填，默认请传0",
	String addto="1"; //是否保存到系统中的常用乘机人，必填，0不保存，1保存"
	
	String CName; //中文名
	String CusCardNo; //身份证
	String EName; //英文名
	String Huzhao; //护照
	String Mobie; //电话                                                               //"手机号码，可选",
	String Qita; //其他证件号
	String Gangao; //港澳通行证
	String Ggdate; //港澳通行证有效期
	String Hzdate; //护照有效期;
	String Tbdate; //台胞有效期
	String CusBirth; //生日
	String Taibao; //台胞证
	String Qianfadi; //签发地
	String Country; //国籍
	String Sex; //性别
	
	String TicketNumber;//提交订单后的票号
	
	String gender;//性别
	String nation;//国际
	String IDdeadline;//证件有效期
	String birthDay;//生日
	String issueAt;//签发地
	
	public String getTicketNumber() {
		return TicketNumber;
	}
	public void setTicketNumber(String ticketNumber) {
		TicketNumber = ticketNumber;
	}
	public Passenger(){}
	public Passenger(String jsonString,String systype){//"systype":"0国内 1国际 2火车票"   国际证件号码默认取护照，国内、火车默认取身份证
		try {
			JSONObject json=new JSONObject(jsonString);
			if(jsonString.contains("passengerType"))this.passengerType=json.getString("passengerType");
			if(jsonString.contains("passengerName"))this.passengerName=json.getString("passengerName");
			else this.passengerName=json.getString("CName");
			if(jsonString.contains("identificationType"))this.identificationType=json.getString("identificationType");
			if(jsonString.contains("identificationNum"))this.identificationNum=json.getString("identificationNum");
//			if(jsonString.contains("mobile"))this.mobile=json.getString("mobile");
			if(jsonString.contains("addto"))this.addto=json.getString("addto");
			
			this.CName=json.getString("CName");
			this.CusCardNo=json.getString("CusCardNo");
			this.EName=json.getString("EName");
			this.Huzhao=json.getString("Huzhao");
			this.Mobie=json.getString("Mobie");
			this.Qita=json.getString("Qita");
			this.Gangao=json.getString("Gangao");
			this.Ggdate=json.getString("Ggdate");
			this.Hzdate=json.getString("Hzdate");
			this.Tbdate=json.getString("Tbdate");
			this.CusBirth=json.getString("CusBirth");
			this.Taibao=json.getString("Taibao");
			this.Qianfadi=json.getString("Qianfadi");
			this.Country=json.getString("Country");
			this.Sex=json.getString("Sex");
			
			if (this.passengerType==null) {
				this.passengerType="成人";
			}
			if (this.passengerName==null||this.passengerName.equals("")) {
				this.passengerName=this.CName;
				if (this.CName.equals("")||this.CName.equals(null)||this.CName.equals("null")) {
					this.passengerName=this.EName;
				}
			}
			if (systype.equals("1")) {//国际机票 取英文名
				this.passengerName=this.EName;
				if(jsonString.contains("Sex"))this.gender=json.getString("Sex");
				if(jsonString.contains("Country"))this.nation=json.getString("Country");
				if(jsonString.contains("CusBirth"))this.birthDay=json.getString("CusBirth");
				if(jsonString.contains("Qianfadi"))	this.issueAt=json.getString("Qianfadi");
			}
			int idtype=0;//证件类型
			if (this.identificationType==null||this.identificationNum==null) {
				if (!systype.equals("1")&&!this.CusCardNo.equals("")) {//国内机票、火车票默认取身份证号
					this.identificationType=IdType.IdType.get(0);
					this.identificationNum=this.CusCardNo;
					idtype=0;
				}
				else if (!this.Huzhao.equals("")){//护照
					this.identificationType=IdType.IdType.get(1);
					this.identificationNum=this.Huzhao;
					idtype=1;
				}
				else if (!this.Gangao.equals("")){//港澳通行证
					this.identificationType=IdType.IdType.get(4);
					this.identificationNum=this.Gangao;
					idtype=4;
				}
				else if (!this.Taibao.equals("")){//台胞证
					this.identificationType=IdType.IdType.get(5);
					this.identificationNum=this.Taibao;
					idtype=5;
				}
				else if (!this.Qita.equals("")){//其他
					this.identificationType=IdType.IdType.get(9);
					this.identificationNum=this.Qita;
					idtype=9;
				}
			}
			if ((systype.equals("1"))) {//国际机票   根据证件类型取有效期     IDdeadline;//证件有效期
				String d="";
				switch (idtype) {
				case 1:
					if(jsonString.contains("Hzdate"))d=json.getString("Hzdate");
					try {
						this.IDdeadline=DateUtil.getDate(d);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;
				case 5:
					if(jsonString.contains("Tbdate"))d=json.getString("Tbdate");
					try {
						this.IDdeadline=DateUtil.getDate(d);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;
				case 4:
					if(jsonString.contains("Ggdate"))d=json.getString("Ggdate");
					try {
						this.IDdeadline=DateUtil.getDate(d);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;

				default:
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public String getMobile() {
//		return mobile;
//	}
//
//	public void setMobile(String mobile) {
//		this.mobile = mobile;
//	}

	public String getIsunum() {
		return isunum;
	}

	public void setIsunum(String isunum) {
		this.isunum = isunum;
	}

	public String getAddto() {
		return addto;
	}

	public void setAddto(String addto) {
		this.addto = addto;
	}

	public String getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public String getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	public String getIdentificationNum() {
		return identificationNum;
	}

	public void setIdentificationNum(String identificationNum) {
		this.identificationNum = identificationNum;
	}
	
	public String getCName() {
		return CName;
	}
	public void setCName(String cName) {
		CName = cName;
	}
	public String getCusCardNo() {
		return CusCardNo;
	}
	public void setCusCardNo(String cusCardNo) {
		CusCardNo = cusCardNo;
	}
	public String getEName() {
		return EName;
	}
	public void setEName(String eName) {
		EName = eName;
	}
	public String getHuzhao() {
		return Huzhao;
	}
	public void setHuzhao(String huzhao) {
		Huzhao = huzhao;
	}
	public String getMobie() {
		return Mobie;
	}
	public void setMobie(String mobie) {
		Mobie = mobie;
	}
	public String getQita() {
		return Qita;
	}
	public void setQita(String qita) {
		Qita = qita;
	}
	public String getGangao() {
		return Gangao;
	}
	public void setGangao(String gangao) {
		Gangao = gangao;
	}
	public String getGgdate() {
		return Ggdate;
	}
	public void setGgdate(String ggdate) {
		Ggdate = ggdate;
	}
	public String getHzdate() {
		return Hzdate;
	}
	public void setHzdate(String hzdate) {
		Hzdate = hzdate;
	}
	public String getTbdate() {
		return Tbdate;
	}
	public void setTbdate(String tbdate) {
		Tbdate = tbdate;
	}
	public String getCusBirth() {
		return CusBirth;
	}
	public void setCusBirth(String cusBirth) {
		CusBirth = cusBirth;
	}
	public String getTaibao() {
		return Taibao;
	}
	public void setTaibao(String taibao) {
		Taibao = taibao;
	}
	public String getQianfadi() {
		return Qianfadi;
	}
	public void setQianfadi(String qianfadi) {
		Qianfadi = qianfadi;
	}
	public String getCountry() {
		return Country;
	}
	public void setCountry(String country) {
		Country = country;
	}
	public String getSex() {
		return Sex;
	}
	public void setSex(String sex) {
		Sex = sex;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getIDdeadline() {
		return IDdeadline;
	}
	public void setIDdeadline(String iDdeadline) {
		IDdeadline = iDdeadline;
	}
	public String getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}
	public String getIssueAt() {
		return issueAt;
	}
	public void setIssueAt(String issueAt) {
		this.issueAt = issueAt;
	}
}
