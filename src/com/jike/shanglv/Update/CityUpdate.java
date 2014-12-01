package com.jike.shanglv.Update;


import org.json.JSONObject;
import org.json.JSONTokener;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jike.shanglv.MyApp;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.NetAndJson.HttpUtils;

public class CityUpdate {
	private final static int HOTEL_MSG_CODE=0;
	private Context context;
	private String returnHotelCity="";
	
	public CityUpdate(Context context){
		this.context=context;
	}
	
	public Boolean UpdateHotelCity(){
		Boolean isSuccess=false;
		startHotelCity();
		return isSuccess;
	}

	private void startHotelCity(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				MyApp ma = new MyApp(context);
				String str = "{\"key\":\"}";
				String param = "action=hct&sitekey=&userkey="
						+ ma.getHm()
								.get(PackageKeys.USERKEY.getString())
								.toString()
						+ "&str="
						+ str
						+ "&sign="
						+ CommonFunc.MD5(ma.getHm()
								.get(PackageKeys.USERKEY.getString())
								.toString()
								+ "hct" + str);
				returnHotelCity = HttpUtils.getJsonContent(
						ma.getServeUrl(), param);
				Message msg = new Message();
				msg.what = HOTEL_MSG_CODE;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HOTEL_MSG_CODE://¾Æµê

				JSONTokener jsonParser;
				jsonParser = new JSONTokener(returnHotelCity);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						
					} else {
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};

}
