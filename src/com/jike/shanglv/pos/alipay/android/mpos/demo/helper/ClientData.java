package com.jike.shanglv.pos.alipay.android.mpos.demo.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class ClientData {

	public static final String DATA = "client_data";
	private final String MID = "mid";
	private final String AID = "aid";
	private final String MPOS_ID = "mposid";

	private Context context;
	private String mid = "";
	private String aid = "";
	private String mposid = "";

	public void saveMposId(String mid, String aid, String mposId) {
		this.mid = mid;
		this.aid = aid;
		this.mposid = mposId;

		 SharedPreferences sharedPref = context.getSharedPreferences(DATA, 0);
		 sharedPref.edit().putString(MID, mid).commit();
		 sharedPref.edit().putString(AID, aid).commit();
		 sharedPref.edit().putString(MPOS_ID, mposId).commit();
	}

	public String getMid() {
		return mid;
	}

	public String getAid() {
		return aid;
	}

	public String getMposid() {
		return mposid;
	}

	public static ClientData create(Context context) {
		ClientData data = new ClientData();
		data.context = context;
		data.readData();
		return data;
	}
	
	public static ClientData create_new(Context context) {
		ClientData data = new ClientData();
		data.context = context;
		return data;
	}

	private void readData() {
		 SharedPreferences sharedPref = context.getSharedPreferences(DATA, 0);

		 this.mid = sharedPref.getString(MID, null);
		 this.aid = sharedPref.getString(AID, null);
		 this.mposid = sharedPref.getString(MPOS_ID, null);
	}
}
