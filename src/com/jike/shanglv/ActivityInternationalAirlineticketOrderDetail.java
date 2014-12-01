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
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Models.InternationalFlightInfo;
import com.jike.shanglv.Models.Passenger;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;


public class ActivityInternationalAirlineticketOrderDetail extends Activity {

	protected static final int ORDERDETAIL_MSG_CODE = 6;
	protected static final String ORDERRECEIPT = "ORDERRECEIPT";

	private Context context;
	private ImageButton back_imgbtn, home_imgbtn;
	private TextView order_state_tv, order_no_tv, order_date_tv,
			order_totalmoney_tv, contact_person_phone_tv;
	private ListView passenger_listview, flightInfo_listview;
	private ImageView frame_ani_iv;
	private RelativeLayout loading_ll;
	private ScrollView scrollview;
	private Button pay_now_btn;
	private SharedPreferences sp;
	private ArrayList<Passenger> passengerList;// 乘机人列表
	private ArrayList<InternationalFlightInfo> flightInfoList;
	private String orderID = "", amount = "";// amount为订单金额
	private String orderDetailReturnJson;
	private JSONObject orderDetailObject;// 返回的订单详情对象

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_international_airlineticket_orderdetail);
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
		flightInfoList = new ArrayList<InternationalFlightInfo>();

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
		contact_person_phone_tv = (TextView) findViewById(R.id.contact_person_phone_tv);

		passenger_listview = (ListView) findViewById(R.id.passenger_listview);
		flightInfo_listview = (ListView) findViewById(R.id.flightInfo_listview);
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
				amount = orderDetailObject.getJSONObject("order").getString(
						"orderPrice");
				JSONArray passengersArray = orderDetailObject
						.getJSONArray("passenger");
				for (int i = 0; i < passengersArray.length(); i++) {
					Passenger p = new Passenger();
					p.setPassengerName(passengersArray.getJSONObject(i)
							.getString("CustomName"));
					p.setIdentificationNum(passengersArray.getJSONObject(i)
							.getString("CusCardNo"));
					p.setTicketNumber(passengersArray.getJSONObject(i)
							.getString("TicketNo"));
					passengerList.add(p);
				}
				if (passengerList.size() > 0) {
					PassengerListAdapter adapter = new PassengerListAdapter(
							context, passengerList);
					passenger_listview.setAdapter(adapter);
					ActivityInlandAirlineticketBooking
							.setListViewHeightBasedOnChildren(passenger_listview);
				}

				JSONArray flightInfoArray = orderDetailObject
						.getJSONArray("flight");
				for (int i = 0; i < flightInfoArray.length(); i++) {
					InternationalFlightInfo p = new InternationalFlightInfo();
					p = JSONHelper.parseObject(
							flightInfoArray.getJSONObject(i),
							InternationalFlightInfo.class);
					flightInfoList.add(p);
				}
				if (flightInfoList.size() > 0) {
					FlightInfoListAdapter adapter = new FlightInfoListAdapter(
							context, flightInfoList);
					flightInfo_listview.setAdapter(adapter);
					ActivityInlandAirlineticketBooking
							.setListViewHeightBasedOnChildren(flightInfo_listview);
				}

				String stateString = orderDetailObject.getJSONObject("order")
						.getString("OrderState");
				order_state_tv.setText(stateString);
				if (stateString.equals("已受理"))
					((RelativeLayout) findViewById(R.id.bottom_rl))
							.setVisibility(View.VISIBLE);
				else
					((RelativeLayout) findViewById(R.id.bottom_rl))
							.setVisibility(View.GONE);
				order_no_tv.setText(orderDetailObject.getJSONObject("order")
						.getString("OrderId"));
				order_date_tv.setText(orderDetailObject.getJSONObject("order")
						.getString("OrderDate"));
				order_totalmoney_tv.setText("￥" + amount);
				contact_person_phone_tv.setText(orderDetailObject
						.getJSONObject("order").getString("ContactorMobile"));
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
					String param = "action=intflightorderdetail&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "intflightorderdetail" + str);
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
			try {
				if (convertView == null) {
					convertView = inflater
							.inflate(
									R.layout.item_international_airlineticket_passenger_list,
									null);
				}
				TextView passengerName_tv = (TextView) convertView
						.findViewById(R.id.passengerName_tv);
				TextView identificationType_tv = (TextView) convertView
						.findViewById(R.id.identificationType_tv);
				TextView identificationNum_tv = (TextView) convertView
						.findViewById(R.id.identificationNum_tv);
				TextView ticketNo_tv = (TextView) convertView
						.findViewById(R.id.ticketNo_tv);

				passengerName_tv.setText(str.get(position).getPassengerName());
				identificationNum_tv.setText("");
				String ticketNoString = str.get(position).getTicketNumber();
				if (!ticketNoString.equals(""))
					ticketNo_tv.setText("票号：" + ticketNoString);
				else
					ticketNo_tv.setVisibility(View.GONE);
				identificationType_tv.setText(str.get(position)
						.getIdentificationNum());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	private class FlightInfoListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<InternationalFlightInfo> str;

		public FlightInfoListAdapter(Context context,
				List<InternationalFlightInfo> list1) {
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
			try {
				if (convertView == null) {
					convertView = inflater.inflate(
							R.layout.item_international_orderdetail_flightinfo,
							null);
				}
				TextView offdate_tv = (TextView) convertView
						.findViewById(R.id.offdate_tv);
				TextView startcity_tv = (TextView) convertView
						.findViewById(R.id.startcity_tv);
				TextView arrivecity_tv = (TextView) convertView
						.findViewById(R.id.arrivecity_tv);
				TextView carrinerName_tv = (TextView) convertView
						.findViewById(R.id.carrinerName_tv);
				TextView flightNo_tv = (TextView) convertView
						.findViewById(R.id.flightNo_tv);
				TextView cabinName_tv = (TextView) convertView
						.findViewById(R.id.cabinName_tv);
				TextView offTime_tv = (TextView) convertView
						.findViewById(R.id.offTime_tv);
				TextView startPortAndT_tv = (TextView) convertView
						.findViewById(R.id.startPortAndT_tv);
				TextView arriveTime_tv = (TextView) convertView
						.findViewById(R.id.arriveTime_tv);
				TextView arrivePortAndT_tv = (TextView) convertView
						.findViewById(R.id.arrivePortAndT_tv);
				TextView runtime_tv = (TextView) convertView
						.findViewById(R.id.runtime_tv);

				try {
					offdate_tv.setText(DateUtil.getDate(str.get(position)
							.getStartTime()));
					offTime_tv.setText(DateUtil.getTime(str.get(position)
							.getStartTime()));
					arriveTime_tv.setText(DateUtil.getTime(str.get(position)
							.getEndTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				startcity_tv.setText(str.get(position).getStartPortName());
				arrivecity_tv.setText(str.get(position).getEndPortName());
				carrinerName_tv.setText(str.get(position).getCarrierName());
				flightNo_tv.setText(str.get(position).getFlightNo());
				cabinName_tv.setText(str.get(position).getCode());
				startPortAndT_tv.setText(str.get(position).getStartPortName());
				arrivePortAndT_tv.setText(str.get(position).getEndPortName());
				runtime_tv.setText(str.get(position).getSeTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

}
