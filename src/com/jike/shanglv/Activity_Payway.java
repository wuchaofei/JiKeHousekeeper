package com.jike.shanglv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Enums.SPkeys;

public class Activity_Payway extends Activity {

	public static final String CHONGZHI_AMOUNT = "CHONGZHI_AMOUNT";
	private Context context;
	private SharedPreferences sp;
	private String amount = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payway);

		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		findViewById(R.id.wx_pay_rl).setOnClickListener(myClickListener);
		findViewById(R.id.online_pay_rl).setOnClickListener(myClickListener);
		if (!sp.getString(SPkeys.opensupperpay.getString(), "false").equals("true")) {
			findViewById(R.id.sc_pay_rl).setVisibility(View.GONE);
			findViewById(R.id.bottom_line).setVisibility(View.GONE);
		}
		findViewById(R.id.sc_pay_rl).setOnClickListener(myClickListener);
		findViewById(R.id.back_imgbtn).setOnClickListener(myClickListener);
		try {
			amount = getIntent().getExtras().getString(CHONGZHI_AMOUNT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OnClickListener myClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			try {
				switch (arg0.getId()) {
				case R.id.wx_pay_rl:

					break;
				case R.id.online_pay_rl:
					String userid = sp.getString(SPkeys.userid.getString(), "");
					int paysystype = 15;
					String siteid = sp.getString(SPkeys.siteid.getString(), "");
					String sign = CommonFunc.MD5(amount + userid + paysystype
							+ siteid);
					MyApp ma = new MyApp(context);
					// <string
					// name="test_pay_server_url">http://gatewayceshi.51jp.cn/PayMent/BeginPay.aspx?orderID=%1$s&amp;amount=%2$s&amp;userid=%3$s&amp;paysystype=%4$s&amp;siteid=%5$s&amp;sign=%6$s</string>
					String url = String.format(ma.getPayServeUrl(), "", amount,
							userid, paysystype, siteid, sign);
					Intent intent = new Intent(context, Activity_Web_Pay.class);
					intent.putExtra(Activity_Web_Pay.URL, url);
					intent.putExtra(Activity_Web_Pay.TITLE, "’Àªß≥‰÷µ÷ß∏∂");
					startActivity(intent);
					break;
				case R.id.sc_pay_rl:                    
					Intent intent5 = new Intent(context,com.jike.shanglv.pos.jike.mpos.newversion.MopsWelcomeActivity.class);
					sp.edit().putString(SPkeys.chongZhiJinE.getString(), amount).commit();
					//					intent5.setClass(getApplication(),
//							com.jike.shanglv.pos.jike.mpos.newversion.MopsWelcomeActivity.class);
					startActivity(intent5);
					break;
				case R.id.back_imgbtn:
					finish();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
