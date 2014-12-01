package com.jike.shanglv;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Models.Passenger;
import com.jike.shanglv.NetAndJson.HttpUtils;


public class ActivityInlandAirlineticketOrderDetail extends Activity {

	protected static final int ORDERDETAIL_MSG_CODE = 3;
	protected static final String ORDERRECEIPT = "ORDERRECEIPT";

	private Context context;
	private ImageButton back_imgbtn, home_imgbtn;
	private TextView order_state_tv, order_no_tv, order_date_tv,
			order_totalmoney_tv, offdate_tv, startcity_tv, arrivecity_tv,
			carrinerName_tv, flightNo_tv, cabinName_tv, planTypeAndModel_tv,
			offTime_tv, startPortAndT_tv, arriveTime_tv, arrivePortAndT_tv,
			jipiaojia_tv, fanMoney_tv, jijian_price_tv, ranyou_price_tv,
			contact_person_phone_tv, baoxian_tv;
	private ListView passenger_listview;
	private ImageView frame_ani_iv;
	private RelativeLayout loading_ll;
	private ScrollView scrollview;
	private Button pay_now_btn;
	private SharedPreferences sp;
	private ArrayList<Passenger> passengerList;// 乘机人列表
	private String orderID = "", pnr = "", amount = "", PayLimit, stateString;// amount为订单金额
	private String orderDetailReturnJson;
	private JSONObject orderDetailObject;// 返回的订单详情对象

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inland_airlineticket_orderdetail);
		try {
			initView();
			if (getOrderReceipt()) {
				startQueryOrderDetail();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		((MyApplication) getApplication()).addActivity(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		try {
			super.onWindowFocusChanged(hasFocus);
			frame_ani_iv.setBackgroundResource(R.anim.frame_rotate_ani);
			AnimationDrawable anim = (AnimationDrawable) frame_ani_iv
					.getBackground();
			anim.setOneShot(false);
			anim.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		passengerList = new ArrayList<Passenger>();

		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		back_imgbtn.setOnClickListener(btnClickListner);
		home_imgbtn.setOnClickListener(btnClickListner);
		pay_now_btn = (Button) findViewById(R.id.pay_now_btn);
		pay_now_btn.setOnClickListener(btnClickListner);
		frame_ani_iv = (ImageView) findViewById(R.id.frame_ani_iv);
		loading_ll = (RelativeLayout) findViewById(R.id.loading_ll);
		scrollview = (ScrollView) findViewById(R.id.scrollview);

		order_state_tv = (TextView) findViewById(R.id.order_state_tv);
		order_no_tv = (TextView) findViewById(R.id.order_no_tv);
		order_date_tv = (TextView) findViewById(R.id.order_date_tv);
		order_totalmoney_tv = (TextView) findViewById(R.id.order_totalmoney_tv);
		offdate_tv = (TextView) findViewById(R.id.offdate_tv);
		startcity_tv = (TextView) findViewById(R.id.startcity_tv);
		arrivecity_tv = (TextView) findViewById(R.id.arrivecity_tv);
		carrinerName_tv = (TextView) findViewById(R.id.carrinerName_tv);
		jipiaojia_tv = (TextView) findViewById(R.id.jipiaojia_tv);
		jijian_price_tv = (TextView) findViewById(R.id.jijian_price_tv);
		ranyou_price_tv = (TextView) findViewById(R.id.ranyou_price_tv);
		fanMoney_tv = (TextView) findViewById(R.id.fanMoney_tv);
		flightNo_tv = (TextView) findViewById(R.id.flightNo_tv);
		cabinName_tv = (TextView) findViewById(R.id.cabinName_tv);
		planTypeAndModel_tv = (TextView) findViewById(R.id.planTypeAndModel_tv);
		offTime_tv = (TextView) findViewById(R.id.offTime_tv);
		startPortAndT_tv = (TextView) findViewById(R.id.startPortAndT_tv);
		arriveTime_tv = (TextView) findViewById(R.id.arriveTime_tv);
		arrivePortAndT_tv = (TextView) findViewById(R.id.arrivePortAndT_tv);
		contact_person_phone_tv = (TextView) findViewById(R.id.contact_person_phone_tv);
		// baoxian_tv=(TextView) findViewById(R.id.baoxian_tv);

		passenger_listview = (ListView) findViewById(R.id.passenger_listview);
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
					int paysystype = 1;
					String siteid = sp.getString(SPkeys.siteid.getString(), "");
					String sign = CommonFunc.MD5(orderID + amount + userid
							+ paysystype + siteid);
					MyApp ma = new MyApp(context);
					String url = String.format(ma.getPayServeUrl(), orderID,
							amount, userid, paysystype, siteid, sign);
					Intent intent = new Intent(context, Activity_Web_Pay.class);
					intent.putExtra(Activity_Web_Pay.URL, url);
					intent.putExtra(Activity_Web_Pay.TITLE, "机票订单支付");
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
						if (true) { // (MyApp.RELEASE) {// 测试版可以支付，只有正式发布才做此判断
							if (PayLimit != null
									&& !PayLimit.isEmpty()
									&& DateUtil.isOverThirtyMinite(PayLimit)
									&& pnr.trim().equals("")
									&& (stateString.equals("新订单") || stateString
											.equals("已受理"))) {
								pay_now_btn
										.setBackgroundDrawable(getResources()
												.getDrawable(R.drawable.btn_3_d));
								pay_now_btn.setEnabled(false);
								final CustomerAlertDialog cad = new CustomerAlertDialog(
										context, true);
								cad.setTitle("已超过支付时限，无法支付支付！");
								cad.setPositiveButton("确定",
										new OnClickListener() {
											@Override
											public void onClick(View arg0) {
												cad.dismiss();
											}
										});
							} else if (pnr.trim().equals("")
									&& (stateString.equals("新订单") || stateString
											.equals("已受理"))) {
								pay_now_btn
										.setBackgroundDrawable(getResources()
												.getDrawable(R.drawable.btn_3_d));
								pay_now_btn.setEnabled(false);
								// new AlertDialog.Builder(context)
								// .setTitle("定位失败")
								// .setMessage("定位失败，暂不能支付！")
								// .setPositiveButton("确定", null).show();
								final CustomerAlertDialog cad = new CustomerAlertDialog(
										context, true);
								cad.setTitle("定位失败，暂不能支付！");
								cad.setPositiveButton("确定",
										new OnClickListener() {
											@Override
											public void onClick(View arg0) {
												cad.dismiss();
											}
										});
								((RelativeLayout) findViewById(R.id.bottom_rl))
										.setVisibility(View.GONE);
							} else if (stateString.equals("新订单")
									|| stateString.equals("已受理")) {
								pay_now_btn
										.setBackgroundDrawable(getResources()
												.getDrawable(R.drawable.btn_3));
								pay_now_btn.setEnabled(true);
								((RelativeLayout) findViewById(R.id.bottom_rl))
										.setVisibility(View.VISIBLE);
							} else {
								((RelativeLayout) findViewById(R.id.bottom_rl))
										.setVisibility(View.GONE);
							}

						}
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
				amount = orderDetailObject.getJSONObject("orders").getString(
						"Amount");
				pnr = orderDetailObject.getJSONObject("orders")
						.getString("PNR");
				PayLimit = orderDetailObject.getJSONObject("orders").getString(
						"PayLimit");
				JSONArray passengersArray = orderDetailObject
						.getJSONArray("psginfo");
				for (int i = 0; i < passengersArray.length(); i++) {
					Passenger p = new Passenger();
					p.setPassengerName(passengersArray.getJSONObject(i)
							.getString("PsgName"));
					p.setIdentificationNum(passengersArray.getJSONObject(i)
							.getString("CardNo"));
					p.setTicketNumber(passengersArray.getJSONObject(i)
							.getString("TicketNumber"));
					passengerList.add(p);
				}
				if (passengerList.size() > 0) {
					PassengerListAdapter adapter = new PassengerListAdapter(
							context, passengerList);
					passenger_listview.setAdapter(adapter);
					ActivityInlandAirlineticketBooking
							.setListViewHeightBasedOnChildren(passenger_listview);
				}
				stateString = orderDetailObject.getJSONObject("orders")
						.getString("Status");
				order_state_tv.setText(stateString);

				order_no_tv.setText(orderDetailObject.getJSONObject("orders")
						.getString("OrderID"));
				order_date_tv.setText(orderDetailObject.getJSONObject("orders")
						.getString("Time"));
				order_totalmoney_tv.setText("￥"
						+ orderDetailObject.getJSONObject("orders").getString(
								"Amount"));
				try {
					offdate_tv.setText(DateUtil.getDate(orderDetailObject
							.getJSONArray("flights").getJSONObject(0)
							.getString("BeginDate")));
					offTime_tv.setText(DateUtil.getTime(orderDetailObject
							.getJSONArray("flights").getJSONObject(0)
							.getString("BeginDate")));
					if (orderDetailObject.getJSONArray("flights")
							.getJSONObject(0).getString("ArrvTime").trim()
							.length() < 8) {
						arriveTime_tv.setText((orderDetailObject.getJSONArray(
								"flights").getJSONObject(0)
								.getString("ArrvTime")));
					} else {
						arriveTime_tv
								.setText(DateUtil
										.getTime(orderDetailObject
												.getJSONArray("flights")
												.getJSONObject(0)
												.getString("ArrvTime")));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				startcity_tv.setText(orderDetailObject.getJSONArray("flights")
						.getJSONObject(0).getString("SCityName"));
				arrivecity_tv.setText(orderDetailObject.getJSONArray("flights")
						.getJSONObject(0).getString("ECityName"));
				carrinerName_tv.setText(orderDetailObject
						.getJSONArray("flights").getJSONObject(0)
						.getString("CarrName"));
				jipiaojia_tv.setText("￥"
						+ orderDetailObject.getJSONArray("flights")
								.getJSONObject(0).getString("Fare")
								.replace(".00", ""));
				jijian_price_tv.setText("￥"
						+ orderDetailObject.getJSONArray("flights")
								.getJSONObject(0).getString("Tax")
								.replace(".00", ""));
				ranyou_price_tv.setText("￥"
						+ orderDetailObject.getJSONArray("flights")
								.getJSONObject(0).getString("Oil")
								.replace(".00", ""));
				// fanMoney_tv.setText(orderDetailObject.getJSONArray("flights").getJSONObject(0).getString(""));
				flightNo_tv.setText(orderDetailObject.getJSONArray("flights")
						.getJSONObject(0).getString("FlightNo"));
				cabinName_tv.setText(orderDetailObject.getJSONArray("flights")
						.getJSONObject(0).getString("Cabin")
						+ "舱");
				// planTypeAndModel_tv.setText(orderDetailObject.getJSONArray("flights").getJSONObject(0).getString(""));
				startPortAndT_tv.setText(orderDetailObject
						.getJSONArray("flights").getJSONObject(0)
						.getString("SCityName"));
				arrivePortAndT_tv.setText(orderDetailObject
						.getJSONArray("flights").getJSONObject(0)
						.getString("ECityName"));
				contact_person_phone_tv.setText(orderDetailObject
						.getJSONObject("orders").getString("Mobile"));
				// baoxian_tv.setText(orderDetailObject.getJSONObject("orders").getString(""));
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
					String param = "action=flightorderdetail&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "flightorderdetail" + str);
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

	private class PassengerListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<Passenger> str;

		public PassengerListAdapter(Context context, List<Passenger> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
		}

		@Override
		public int getCount() {
			return str.size();
		}

		@Override
		public Object getItem(int position) {
			return str.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater
						.inflate(
								R.layout.item_inland_airlineticket_passenger_list,
								null);
			}
			TextView passengerName_tv = (TextView) convertView
					.findViewById(R.id.passengerName_tv);
			TextView identificationType_tv = (TextView) convertView
					.findViewById(R.id.identificationType_tv);
			TextView identificationNum_tv = (TextView) convertView
					.findViewById(R.id.identificationNum_tv);
			TextView passengerType_tv = (TextView) convertView
					.findViewById(R.id.passengerType_tv);

			passengerName_tv.setText(str.get(position).getPassengerName());
			identificationNum_tv.setText("");
			passengerType_tv.setText("("
					+ str.get(position).getIdentificationNum() + ")");
			identificationType_tv.setText("票号："
					+ str.get(position).getTicketNumber());

			ImageButton delete_imgbtn = (ImageButton) convertView
					.findViewById(R.id.delete_imgbtn);
			delete_imgbtn.setVisibility(View.GONE);

			return convertView;
		}
	}
}
