package com.jike.shanglv;

import org.json.JSONObject;
import org.json.JSONTokener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.NetAndJson.HttpUtils;


public class ActivityHotelOrderDetail extends Activity {

	protected static final int ORDERDETAIL_MSG_CODE = 5;
	protected static final String ORDERRECEIPT = "ORDERRECEIPT";
	private Context context;
	private ImageButton back_imgbtn, home_imgbtn;
	private TextView order_state_tv, order_no_tv, order_date_tv,
			order_totalmoney_tv, contact_person_phone_tv, pay_type_tv,
			guarantee_state_tv, hotelName_tv, roomType_tv, roomCount_tv,
			inDate_tv, outDate_tv, roomNights_tv, latetime_tv, hotel_adress_tv,
			passengers_tv;
	private Button pay_now_btn;
	private ImageView frame_ani_iv;
	private RelativeLayout loading_ll;
	private ScrollView scrollview;
	private SharedPreferences sp;
	private String orderID = "", amount = "";// amount为订单金额
	private String orderDetailReturnJson;
	private JSONObject orderDetailObject;// 返回的订单详情对象

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_hotel_orderdetail);
			try {
				initView();
				if (getOrderReceipt()) {
					startQueryOrderDetail();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		frame_ani_iv.setBackgroundResource(R.anim.frame_rotate_ani);
		AnimationDrawable anim = (AnimationDrawable) frame_ani_iv
				.getBackground();
		anim.setOneShot(false);
		anim.start();
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);

		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		back_imgbtn.setOnClickListener(btnClickListner);
		home_imgbtn.setOnClickListener(btnClickListner);
		frame_ani_iv = (ImageView) findViewById(R.id.frame_ani_iv);
		loading_ll = (RelativeLayout) findViewById(R.id.loading_ll);
		scrollview = (ScrollView) findViewById(R.id.scrollview);
		pay_now_btn = (Button) findViewById(R.id.pay_now_btn);
		pay_now_btn.setOnClickListener(btnClickListner);
		pay_type_tv = (TextView) findViewById(R.id.pay_type_tv);
		guarantee_state_tv = (TextView) findViewById(R.id.guarantee_state_tv);
		hotelName_tv = (TextView) findViewById(R.id.hotelName_tv);
		roomType_tv = (TextView) findViewById(R.id.roomType_tv);
		roomCount_tv = (TextView) findViewById(R.id.roomCount_tv);
		inDate_tv = (TextView) findViewById(R.id.inDate_tv);
		outDate_tv = (TextView) findViewById(R.id.outDate_tv);
		roomNights_tv = (TextView) findViewById(R.id.roomNights_tv);
		latetime_tv = (TextView) findViewById(R.id.latetime_tv);
		hotel_adress_tv = (TextView) findViewById(R.id.hotel_adress_tv);
		passengers_tv = (TextView) findViewById(R.id.passengers_tv);

		order_state_tv = (TextView) findViewById(R.id.order_state_tv);
		order_no_tv = (TextView) findViewById(R.id.order_no_tv);
		order_date_tv = (TextView) findViewById(R.id.order_date_tv);
		order_totalmoney_tv = (TextView) findViewById(R.id.order_totalmoney_tv);
		contact_person_phone_tv = (TextView) findViewById(R.id.contact_person_phone_tv);
	}

	View.OnClickListener btnClickListner = new View.OnClickListener() {
		@SuppressLint("ResourceAsColor")
		@Override
		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.back_imgbtn:
					finish();
					break;
				case R.id.home_imgbtn:
					startActivity(new Intent(context, MainActivity.class));
					break;
				case R.id.pay_now_btn:
					String userid = sp.getString(SPkeys.userid.getString(), "");
					int paysystype = 3;
					String siteid = sp.getString(SPkeys.siteid.getString(),
							"65");
					String sign = CommonFunc.MD5(orderID + amount + userid
							+ paysystype + siteid);
					MyApp ma = new MyApp(context);
					String url = String.format(ma.getPayServeUrl(), orderID,
							amount, userid, paysystype, siteid, sign);
					Intent intent = new Intent(context, Activity_Web_Pay.class);
					intent.putExtra(Activity_Web_Pay.URL, url);
					intent.putExtra(Activity_Web_Pay.TITLE, "酒店订单支付");
					startActivity(intent);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Boolean getOrderReceipt() {
		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra(ORDERRECEIPT)) {
				orderID = intent.getStringExtra(ORDERRECEIPT);
				// pnr=or.getPnr();//为了保持该页面的一致性（有可能来自列表也可能是订单提交页面），不能从上页中获取pnr
				return true;
			}
			return false;
		}
		return false;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			JSONTokener jsonParser;
			switch (msg.what) {
			case ORDERDETAIL_MSG_CODE:
				loading_ll.setVisibility(View.GONE);
				scrollview.setVisibility(View.VISIBLE);
				jsonParser = new JSONTokener(orderDetailReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						orderDetailObject = jsonObject.getJSONObject("d");
						assignment();// 获取数据后对页面上的内容进行赋值
					} else {
						Toast.makeText(context, "网络异常！", 0).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}

		private void assignment() {
			try {
				amount = orderDetailObject.getString("orderAmount");
				String stateString = orderDetailObject.getString("orderStatus");
				String payType = orderDetailObject.getString("payType");
				pay_type_tv.setText(payType.equals("1") ? "预付" : "酒店现付");
				order_state_tv.setText(stateString);
				if (stateString.equals("新订单") && payType.equals("1"))
					((RelativeLayout) findViewById(R.id.bottom_rl))
							.setVisibility(View.VISIBLE);
				else
					((RelativeLayout) findViewById(R.id.bottom_rl))
							.setVisibility(View.GONE);
				order_no_tv.setText(orderDetailObject.getString("orderID"));
				order_date_tv.setText(orderDetailObject.getString("orderDate"));
				order_totalmoney_tv.setText("￥"
						+ orderDetailObject.getString("orderAmount"));
				contact_person_phone_tv.setText(orderDetailObject
						.getString("contactMobile"));
				String isCreditcard = orderDetailObject
						.getString("isCreditcard");
				guarantee_state_tv.setText(isCreditcard.equals("1") ? "信用卡担保交易"
						: "无需担保");
				hotelName_tv.setText(orderDetailObject.getString("hotelName"));
				roomType_tv.setText(orderDetailObject.getString("roomName"));
				roomCount_tv.setText(orderDetailObject.getString("roomCount")
						+ "间");
				inDate_tv.setText("入住时间    "
						+ orderDetailObject.getString("inDate"));
				outDate_tv.setText("离店时间    "
						+ orderDetailObject.getString("outDate"));
				roomNights_tv.setText(orderDetailObject.getString("roomNights")
						+ "晚");
				latetime_tv.setText("最晚到店时间     "
						+ orderDetailObject.getString("latetime"));
				hotel_adress_tv.setText(orderDetailObject
						.getString("orderDate"));
				passengers_tv
						.setText(orderDetailObject.getString("passengers"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void startQueryOrderDetail() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String str = "{\"orderID\":\"" + orderID
							+ "\",\"siteid\":\""
							+ sp.getString(SPkeys.siteid.getString(), "65")
							+ "\"}";
					String param = "action=hotelorderdetail&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "hotelorderdetail" + str);
					orderDetailReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = ORDERDETAIL_MSG_CODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
