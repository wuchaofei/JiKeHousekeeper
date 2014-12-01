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


public class ActivityTrainOrderDetail extends Activity {

	protected static final int ORDERDETAIL_MSG_CODE = 4;
	protected static final String ORDERRECEIPT = "ORDERRECEIPT";
	protected static final int COMFIRMORDER_MSG_CODE = 0;

	private Context context;
	private ImageButton back_imgbtn, home_imgbtn;
	private TextView order_state_tv, order_no_tv, order_date_tv,
			order_totalmoney_tv, train_num_tv, train_type_tv, seat_type_tv,
			start_station_tv, end_station_tv, start_time_tv, end_time_tv,
			startoffdate_tv, contact_person_phone_tv, baoxian_tv;
	private ImageView start_station_icon_iv, end_station_icon_iv, frame_ani_iv;
	private ListView passenger_listview;
	private Button pay_now_btn;
	private RelativeLayout loading_ll;
	private ScrollView scrollview;
	private SharedPreferences sp;
	private ArrayList<Passenger> passengerList;// 乘客列表
	private String orderID = "", amount = "";// amount为订单金额
	private String orderDetailReturnJson, comfirmOrderReturnJson;
	private JSONObject orderDetailObject;// 返回的订单详情对象

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_orderdetail);
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
		frame_ani_iv = (ImageView) findViewById(R.id.frame_ani_iv);
		loading_ll = (RelativeLayout) findViewById(R.id.loading_ll);
		scrollview = (ScrollView) findViewById(R.id.scrollview);

		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		back_imgbtn.setOnClickListener(btnClickListner);
		home_imgbtn.setOnClickListener(btnClickListner);
		pay_now_btn = (Button) findViewById(R.id.pay_now_btn);
		pay_now_btn.setOnClickListener(btnClickListner);
		start_station_icon_iv = (ImageView) findViewById(R.id.start_station_icon_iv);
		end_station_icon_iv = (ImageView) findViewById(R.id.end_station_icon_iv);
		train_num_tv = (TextView) findViewById(R.id.train_num_tv);
		train_type_tv = (TextView) findViewById(R.id.train_type_tv);
		seat_type_tv = (TextView) findViewById(R.id.seat_type_tv);
		start_station_tv = (TextView) findViewById(R.id.start_station_tv);
		end_station_tv = (TextView) findViewById(R.id.end_station_tv);
		start_time_tv = (TextView) findViewById(R.id.start_time_tv);
		end_time_tv = (TextView) findViewById(R.id.end_time_tv);
		startoffdate_tv = (TextView) findViewById(R.id.startoffdate_tv);
		order_state_tv = (TextView) findViewById(R.id.order_state_tv);
		order_no_tv = (TextView) findViewById(R.id.order_no_tv);
		order_date_tv = (TextView) findViewById(R.id.order_date_tv);
		order_totalmoney_tv = (TextView) findViewById(R.id.order_totalmoney_tv);
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
					final CustomerAlertDialog cad = new CustomerAlertDialog(
							context, false);
					cad.setTitle("是否确认购买火车票？确认后，系统将自动扣款，用于支付本次订单。");
					cad.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							comfirmOrder();
							cad.dismiss();
						}
					});
					cad.setNegativeButton1("取消", new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							cad.dismiss();
						}
					});

					// String userid=sp.getString(SPkeys.userid.getString(),
					// "");
					// int paysystype=14;
					// String siteid=sp.getString(SPkeys.siteid.getString(),
					// "65");
					// String sign=CommonFunc. MD5(orderID + amount + userid +
					// paysystype + siteid);
					// MyApp ma = new MyApp(context);
					// String url=String.format(ma.getPayServeUrl(),orderID,
					// amount,userid,paysystype,siteid,sign);
					// Intent intent=new Intent(context,Activity_Web_Pay.class);
					// intent.putExtra(Activity_Web_Pay.URL, url);
					// intent.putExtra(Activity_Web_Pay.TITLE, "火车票订单支付");
					// startActivity(intent);
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
				jsonParser = new JSONTokener(orderDetailReturnJson);
				try {
					loading_ll.setVisibility(View.GONE);
					scrollview.setVisibility(View.VISIBLE);
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						orderDetailObject = jsonObject.getJSONObject("d");
						assignment();// 获取数据后对页面上的内容进行赋值
					} else {
						Toast.makeText(context, "发生异常，获取订单信息失败！", 0).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case COMFIRMORDER_MSG_CODE:
				jsonParser = new JSONTokener(comfirmOrderReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000") || state.equals("1111")) {
						String mesString = jsonObject.getJSONObject("d")
								.getString("msg");
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle(mesString);
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(context,
										ActivityOrderList.class);
								intent.putExtra(
										ActivityOrderList.ACTION_TOKENNAME,
										ActivityOrderList.TRAIN_ORDERLIST);
								intent.putExtra(
										ActivityOrderList.TITLE_TOKENNAME,
										"火车票订单");
								startActivity(intent);
								cad.dismiss();
							}
						});
					} else {
						Toast.makeText(context, "发生异常", 0).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}

		private void assignment() {
			try {
				amount = orderDetailObject.getString("Amount");
				JSONArray passengersArray = orderDetailObject
						.getJSONArray("PsgInfo");
				for (int i = 0; i < passengersArray.length(); i++) {
					Passenger p = new Passenger();
					p.setPassengerName(passengersArray.getJSONObject(i)
							.getString("PsgName"));
					p.setIdentificationNum(passengersArray.getJSONObject(i)
							.getString("CardNo"));
					p.setPassengerType(passengersArray.getJSONObject(i)
							.getString("SeatType"));// 将用户座位席别存在乘客类型中
					p.setGangao(passengersArray.getJSONObject(i).getString(
							"SeatNo"));// 将用户座位存在港澳通行证号码中
					p.setHuzhao(passengersArray.getJSONObject(i).getString(
							"IncAmount"));// 将用户保险存在护照号码中
					passengerList.add(p);
				}
				if (passengerList.size() > 0) {
					PassengerListAdapter adapter = new PassengerListAdapter(
							context, passengerList);
					passenger_listview.setAdapter(adapter);
					ActivityInlandAirlineticketBooking
							.setListViewHeightBasedOnChildren(passenger_listview);
				}
				String stateString = orderDetailObject.getString("Status");
				order_state_tv.setText(stateString);
				if (stateString.equals("新订单"))
					((RelativeLayout) findViewById(R.id.bottom_rl))
							.setVisibility(View.VISIBLE);
				else
					((RelativeLayout) findViewById(R.id.bottom_rl))
							.setVisibility(View.GONE);
				order_no_tv.setText(orderDetailObject.getString("OrderID"));
				order_date_tv.setText(orderDetailObject.getString("OrderTime"));
				order_totalmoney_tv.setText("￥"
						+ orderDetailObject.getString("Amount"));
				contact_person_phone_tv.setText(orderDetailObject
						.getString("Mobile"));

				train_num_tv.setText(orderDetailObject.getString("TrainNo"));
				// 返回的数据没有车次类型，此处显示车票张数
				train_type_tv.setText(orderDetailObject
						.getString("TicketCount") + "张");
				// seat_type_tv.setText(passengerList.get(0).getPassengerType());
				seat_type_tv.setVisibility(View.GONE);
				start_station_tv.setText(orderDetailObject.getString("SCity"));
				end_station_tv.setText(orderDetailObject.getString("ECity"));
				start_time_tv.setText(orderDetailObject.getString("STime"));
				end_time_tv.setText(orderDetailObject.getString("ETime"));
				try {
					startoffdate_tv.setText(DateUtil.getDate(orderDetailObject
							.getString("SDate")));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void comfirmOrder() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String str = "{\"orderid\":\"" + orderID
							+ "\",\"userid\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\"}";
					String param = "action=trainOrderConfirmV2&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "trainOrderConfirmV2" + str);
					comfirmOrderReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = COMFIRMORDER_MSG_CODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

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
					String param = "action=trainorderdetail&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "trainorderdetail" + str);
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
					convertView = inflater.inflate(
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
				passengerType_tv.setText("("
						+ str.get(position).getIdentificationNum() + ")");
				identificationNum_tv.setText("座位号："
						+ str.get(position).getGangao().replace("null", "未知"));
				identificationType_tv.setText(str.get(position)
						.getPassengerType());

				ImageButton delete_imgbtn = (ImageButton) convertView
						.findViewById(R.id.delete_imgbtn);
				delete_imgbtn.setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}
}
