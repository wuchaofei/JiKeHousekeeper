package com.jike.shanglv.Models;


import org.json.JSONObject;

public class CabList {
	private String Cabin, PolicyID, VerifyInfo, AirLineCode, UserRate,
			Discount, Sale, FlightNo, Rate, RateInfo, CabinName, Fare,
			VTWorteTime, Flag, FareEx, IsGeneralMinFare, ExtFee, IsSpePolicy,
			YouHui, IsSpe, PriceProvider, WorkTime, PolicyRemark, GeneralCabin,
			Supplier, SaleEx, FlagEn, generalFare, ExtFee2, TicketCount;

	private JSONObject json;

	public CabList(JSONObject json) {
		try {
			this.Cabin = json.getString("Cabin");
			this.PolicyID= json.getString("PolicyID");
			this.VerifyInfo= json.getString("VerifyInfo");
			this.AirLineCode= json.getString("AirLineCode");
			this.UserRate= json.getString("UserRate");
			this.Discount= json.getString("Discount");
			this.Sale= json.getString("Sale");
			this.FlightNo= json.getString("FlightNo");
			this.Rate= json.getString("Rate");
			this.RateInfo= json.getString("RateInfo");
			this.CabinName= json.getString("CabinName");
			this.Fare= json.getString("Fare");
			this.VTWorteTime= json.getString("VTWorteTime");
			this.Flag= json.getString("Flag");
			this.FareEx= json.getString("FareEx");
			this.IsGeneralMinFare= json.getString("IsGeneralMinFare");
			this.ExtFee= json.getString("ExtFee");
			this.IsSpePolicy= json.getString("IsSpePolicy");
			this.YouHui= json.getString("YouHui");
			this.IsSpe= json.getString("IsSpe");
			this.PriceProvider= json.getString("PriceProvider");
			this.WorkTime= json.getString("WorkTime");
			this.PolicyRemark= json.getString("PolicyRemark");
			this.GeneralCabin= json.getString("GeneralCabin");
			this.Supplier= json.getString("Supplier");
			this.SaleEx= json.getString("SaleEx");
			this.FlagEn= json.getString("FlagEn");
			this.generalFare= json.getString("generalFare");
			this.ExtFee2= json.getString("ExtFee2");
			this.TicketCount= json.getString("TicketCount");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONObject getJson() {
		return json;
	}

	public String getCabin() {
		return Cabin;
	}

	public String getPolicyID() {
		return PolicyID;
	}

	public String getVerifyInfo() {
		return VerifyInfo;
	}

	public String getAirLineCode() {
		return AirLineCode;
	}

	public String getUserRate() {
		return UserRate;
	}

	public String getDiscount() {
		return Discount;
	}

	public String getSale() {
		return Sale;
	}

	public String getFlightNo() {
		return FlightNo;
	}

	public String getRate() {
		return Rate;
	}

	public String getRateInfo() {
		return RateInfo;
	}

	public String getCabinName() {
		return CabinName;
	}

	public String getFare() {
		return Fare;
	}

	public String getVTWorteTime() {
		return VTWorteTime;
	}

	public String getFlag() {
		return Flag;
	}

	public String getFareEx() {
		return FareEx;
	}

	public String getIsGeneralMinFare() {
		return IsGeneralMinFare;
	}

	public String getExtFee() {
		return ExtFee;
	}

	public String getIsSpePolicy() {
		return IsSpePolicy;
	}

	public String getYouHui() {
		return YouHui;
	}

	public String getIsSpe() {
		return IsSpe;
	}

	public String getPriceProvider() {
		return PriceProvider;
	}

	public String getWorkTime() {
		return WorkTime;
	}

	public String getPolicyRemark() {
		return PolicyRemark;
	}

	public String getGeneralCabin() {
		return GeneralCabin;
	}

	public String getSupplier() {
		return Supplier;
	}

	public String getSaleEx() {
		return SaleEx;
	}

	public String getFlagEn() {
		return FlagEn;
	}

	public String getGeneralFare() {
		return generalFare;
	}

	public String getExtFee2() {
		return ExtFee2;
	}

	public String getTicketCount() {
		return TicketCount;
	}
}
