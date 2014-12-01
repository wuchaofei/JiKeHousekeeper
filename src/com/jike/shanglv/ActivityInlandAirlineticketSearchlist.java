package com.jike.shanglv;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jike.shanglv.Common.AirportConvert;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Enums.SingleOrDouble;
import com.jike.shanglv.Models.InlandAirlineInfo;
import com.jike.shanglv.NetAndJson.HttpUtils;


public class ActivityInlandAirlineticketSearchlist extends Activity {

	private Context context;
	private ImageButton back_imgbtn, home_imgbtn;
	private ImageView left_arrow_iv, right_arrow_iv, sort_arrow_time_iv,
			sort_arrow_price_iv;
	private TextView sort_time_tv, sort_price_tv, title_tv, date_current_tv;
	private LinearLayout date_yesterday_ll, date_tomorrow_ll, bytime_LL,
			byprice_ll;
	private ListView listview;
	private String goFlight = "", goFlightSelectedIndex = "";// 往返机票去程航班信息
	private String currentdate = "",// 当前日期
			startcity_code = "", arrivecity_code = "",
			startcity = "",
			arrivecity = "", startoff_date = "", startdate = "", enddate = "";// 从搜索页面获取的数据
	private SingleOrDouble wayType;
	private SharedPreferences sp;
	private CustomProgressDialog progressdialog;
	private Boolean byPriceAsc = false, byTimeAsc = true;// 默认按时间升序排
	private String flistReturnJson;// 返回的查询列表json
	private JSONArray flist;// 查询到的航班列表
	private JSONArray flights;// 航空公司集合信息

	private ListAdapter adapter;
	private ArrayList<InlandAirlineInfo> InlandAirline_List;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inland_airlineticket_searchlist);
		try {
			initView();
			((MyApplication) getApplication()).addActivity(this);
			startQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		InlandAirline_List = new ArrayList<InlandAirlineInfo>();
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		listview = (ListView) findViewById(R.id.listview);

		left_arrow_iv = (ImageView) findViewById(R.id.left_arrow_iv);
		right_arrow_iv = (ImageView) findViewById(R.id.right_arrow_iv);
		sort_arrow_time_iv = (ImageView) findViewById(R.id.sort_arrow_time_iv);
		sort_arrow_price_iv = (ImageView) findViewById(R.id.sort_arrow_price_iv);

		sort_time_tv = (TextView) findViewById(R.id.sort_time_tv);
		sort_price_tv = (TextView) findViewById(R.id.sort_price_tv);
		title_tv = (TextView) findViewById(R.id.title_tv);
		date_current_tv = (TextView) findViewById(R.id.date_current_tv);

		date_yesterday_ll = (LinearLayout) findViewById(R.id.date_yesterday_ll);
		date_tomorrow_ll = (LinearLayout) findViewById(R.id.date_tomorrow_ll);
		bytime_LL = (LinearLayout) findViewById(R.id.bytime_LL);
		byprice_ll = (LinearLayout) findViewById(R.id.byprice_ll);

		date_yesterday_ll.setOnClickListener(btnClickListner);
		date_tomorrow_ll.setOnClickListener(btnClickListner);
		bytime_LL.setOnClickListener(btnClickListner);
		byprice_ll.setOnClickListener(btnClickListner);
		back_imgbtn.setOnClickListener(btnClickListner);
		home_imgbtn.setOnClickListener(btnClickListner);

		getIntentData();

		title_tv.setText(startcity + "-" + arrivecity);
		if (!DateUtil.IsMoreThanToday(currentdate)) {
			left_arrow_iv.setBackground(getResources().getDrawable(
					R.drawable.solid_arrow_left_disable));
		} else {
			left_arrow_iv.setBackground(getResources().getDrawable(
					R.drawable.solid_arrow_left));
		}
	}

	// 获取Intent数据,并给到页面和做搜索数据使用
	private void getIntentData() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("wayType"))
				wayType = (SingleOrDouble) bundle.get("wayType");
			if (bundle.containsKey("startcity_code"))
				startcity_code = bundle.getString("startcity_code");// 城市三字码
			if (bundle.containsKey("arrivecity_code"))
				arrivecity_code = bundle.getString("arrivecity_code");
			if (bundle.containsKey("startcity"))
				startcity = bundle.getString("startcity");// 城市名字
			if (bundle.containsKey("arrivecity"))
				arrivecity = bundle.getString("arrivecity");
			if (bundle
					.containsKey(ActivityInlandAirlineticketBooking.SELECTED_CABIN_INDEX1))
				goFlightSelectedIndex = bundle
						.getString(ActivityInlandAirlineticketBooking.SELECTED_CABIN_INDEX1);
			if (bundle
					.containsKey(ActivityInlandAirlineticketSelectCabin.TOKEN_NAME1))
				goFlight = bundle
						.getString(ActivityInlandAirlineticketSelectCabin.TOKEN_NAME1);

			if (wayType == SingleOrDouble.singleWay) {
				if (bundle.containsKey("startoff_date"))
					startoff_date = bundle.getString("startoff_date");
				currentdate = startoff_date;
				date_current_tv.setText(currentdate);
			} else if (wayType == SingleOrDouble.doubleWayGo
					|| wayType == SingleOrDouble.doubleWayBack) {
				if (bundle.containsKey("startdate"))
					startdate = bundle.getString("startdate");
				if (bundle.containsKey("enddate"))
					enddate = bundle.getString("enddate");
				if (wayType == SingleOrDouble.doubleWayGo)
					currentdate = startdate;
				else if (wayType == SingleOrDouble.doubleWayBack) {
					currentdate = enddate;
					String cityString = "", codeString = "";// 返程机票，交换出发、到达城市
					cityString = startcity;
					startcity = arrivecity;
					arrivecity = cityString;

					codeString = startcity_code;
					startcity_code = arrivecity_code;
					arrivecity_code = codeString;
				}
				date_current_tv.setText(currentdate);// 往返机票订单，先显示出发日期
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				progressdialog.dismiss();
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(flistReturnJson);
				if (flistReturnJson.length() == 0) {
					final CustomerAlertDialog cad = new CustomerAlertDialog(
							context, true);
					cad.setTitle("查询失败");
					cad.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							cad.dismiss();
						}
					});
				}
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						String num = jsonObject.getString("num");
						if (num.equals("0")) {
							final CustomerAlertDialog cad = new CustomerAlertDialog(
									context, true);
							cad.setTitle("未查到该航段的航班信息");
							cad.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									cad.dismiss();
								}
							});
							return;
						}
						flist = jsonObject.getJSONArray("d");
						String db = jsonObject.getString("db");
						flights = jsonObject.getJSONArray("al");
						createList(flist);
						adapter = new ListAdapter(context, InlandAirline_List);
						listview.setAdapter(adapter);
						try {// 获取列表中数据的日期，以免发生显示错位
							date_current_tv.setText(DateUtil
									.getDate(InlandAirline_List.get(0)
											.getOffTime()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								InlandAirlineInfo ql = InlandAirline_List
										.get(position);
								Intent intents = new Intent(
										context,
										ActivityInlandAirlineticketSelectCabin.class);
								intents.putExtra(
										ActivityInlandAirlineticketSelectCabin.TOKEN_NAME,
										ql.getJson().toString());
								intents.putExtra("wayType", wayType);
								intents.putExtra("startcity", startcity);
								intents.putExtra("arrivecity", arrivecity);
								intents.putExtra("startcity_code",
										startcity_code);
								intents.putExtra("arrivecity_code",
										arrivecity_code);
								intents.putExtra(
										ActivityInlandAirlineticketBooking.SELECTED_CABIN_INDEX1,
										goFlightSelectedIndex);// 历史 去程 舱位
								intents.putExtra(
										ActivityInlandAirlineticketSelectCabin.TOKEN_NAME1,
										goFlight);// 历史 去程 航班
								if (wayType == SingleOrDouble.singleWay)
									intents.putExtra("startoff_date",
											startoff_date);
								else if (wayType == SingleOrDouble.doubleWayGo) {
									intents.putExtra("startdate", startdate);
									intents.putExtra("enddate", enddate);
								}
								startActivity(intents);
							}
						});

					} else {
						// String message = jsonObject.getString("msg");
						// new AlertDialog.Builder(context).setTitle("查询失败")
						// .setMessage(message)
						// .setPositiveButton("确认", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("查询失败");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};

	/**
	 * 构建list对象
	 * 
	 * @param flist_list
	 */
	private void createList(JSONArray flist_list) {
		InlandAirline_List.clear();
		for (int i = 0; i < flist_list.length(); i++) {
			try {
				InlandAirlineInfo ia = new InlandAirlineInfo(
						flist_list.getJSONObject(i));
				InlandAirline_List.add(ia);
			} catch (Exception e) {
			}
		}
	}

	Comparator<InlandAirlineInfo> comparator1 = new Comparator<InlandAirlineInfo>() {
		@Override
		public int compare(InlandAirlineInfo s1, InlandAirlineInfo s2) {
			if (s1.getMinFare() != s2.getMinFare()) {
				return Integer.parseInt(s1.getMinFare())
						- Integer.parseInt(s2.getMinFare());
			} else
				return 0;
		}
	};
	Comparator<InlandAirlineInfo> comparator2 = new Comparator<InlandAirlineInfo>() {
		@Override
		public int compare(InlandAirlineInfo s1, InlandAirlineInfo s2) {
			if (s1.getMinFare() != s2.getMinFare()) {
				return Integer.parseInt(s2.getMinFare())
						- Integer.parseInt(s1.getMinFare());
			} else
				return 0;
		}
	};

	Comparator<InlandAirlineInfo> comparator3 = new Comparator<InlandAirlineInfo>() {
		@Override
		public int compare(InlandAirlineInfo s1, InlandAirlineInfo s2) {
			if (!s1.getOffTime().equals(s2.getOffTime())) {
				return s1.getOffTime().compareTo(s2.getOffTime());
			} else
				return 0;
		}
	};
	Comparator<InlandAirlineInfo> comparator4 = new Comparator<InlandAirlineInfo>() {
		@Override
		public int compare(InlandAirlineInfo s1, InlandAirlineInfo s2) {
			if (!s1.getOffTime().equals(s2.getOffTime())) {
				return s2.getOffTime().compareTo(s1.getOffTime());
			} else
				return 0;
		}
	};

	private void startQuery() {
		if (HttpUtils.showNetCannotUse(context)) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// action=flist&str={'s':'sha','e':hfe,'sd':'2014-01-28','userid':'649','siteid':'65'}
					MyApp ma = new MyApp(context);
					String siteid = sp.getString(SPkeys.siteid.getString(),
							"65");
					String str = "{\"s\":\"" + startcity_code + "\",\"e\":\""
							+ arrivecity_code + "\",\"sd\":\"" + currentdate
							+ "\",\"userid\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"siteid\":\"" + siteid + "\"}";
					String param = "action=flist&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "flist" + str);
					flistReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在查询，请稍候...");
		progressdialog.setCancelable(true);
		progressdialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		progressdialog.show();
	}

	View.OnClickListener btnClickListner = new View.OnClickListener() {
		@SuppressLint("ResourceAsColor")
		@Override
		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.date_yesterday_ll:

					if (!DateUtil.IsMoreThanToday(currentdate)) {
						left_arrow_iv.setBackground(getResources().getDrawable(
								R.drawable.solid_arrow_left_disable));
						date_yesterday_ll.setEnabled(false);
						break;
					}
					try {
						currentdate = DateUtil
								.getSpecifiedDayBefore(currentdate);
						// date_current_tv.setText(currentdate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if (!DateUtil.IsMoreThanToday(currentdate)) {
						left_arrow_iv.setBackground(getResources().getDrawable(
								R.drawable.solid_arrow_left_disable));
						date_yesterday_ll.setEnabled(true);
					}
					startQuery();
					break;
				case R.id.date_tomorrow_ll:
					try {
						currentdate = DateUtil
								.getSpecifiedDayAfter(currentdate);
						// date_current_tv.setText(currentdate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if (DateUtil.IsMoreThanToday(currentdate))
						left_arrow_iv.setBackground(getResources().getDrawable(
								R.drawable.solid_arrow_left));
					startQuery();
					break;
				case R.id.bytime_LL:
					sort_time_tv.setSelected(true);
					sort_arrow_time_iv.setSelected(true);
					sort_price_tv.setSelected(false);
					sort_arrow_price_iv.setSelected(false);
					byTimeAsc = !byTimeAsc;
					if (byTimeAsc) {
						sort_arrow_time_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_up));
						Collections.sort(InlandAirline_List, comparator3);
						adapter = new ListAdapter(context, InlandAirline_List);
						listview.setAdapter(adapter);
					} else {
						sort_arrow_time_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_down));
						Collections.sort(InlandAirline_List, comparator4);
						adapter = new ListAdapter(context, InlandAirline_List);
						listview.setAdapter(adapter);
					}
					break;
				case R.id.byprice_ll:
					sort_price_tv.setSelected(true);
					sort_arrow_price_iv.setSelected(true);
					sort_time_tv.setSelected(false);
					sort_arrow_time_iv.setSelected(false);
					byPriceAsc = !byPriceAsc;
					if (byPriceAsc) {
						sort_arrow_price_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_up));
						Collections.sort(InlandAirline_List, comparator1);
						adapter = new ListAdapter(context, InlandAirline_List);
						listview.setAdapter(adapter);
					} else {
						sort_arrow_price_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_down));
						Collections.sort(InlandAirline_List, comparator2);
						adapter = new ListAdapter(context, InlandAirline_List);
						listview.setAdapter(adapter);
					}
					break;
				case R.id.back_imgbtn:
					finish();
					break;
				case R.id.home_imgbtn:
					startActivity(new Intent(context, MainActivity.class));
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<InlandAirlineInfo> str;

		public ListAdapter(Context context, List<InlandAirlineInfo> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
		}

		public void updateBitmap(List<InlandAirlineInfo> list1) {
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
									R.layout.item_inland_airlineticket_searchlist,
									null);
				}
				TextView startTime_tv = (TextView) convertView
						.findViewById(R.id.startTime_tv);
				TextView endTime_tv = (TextView) convertView
						.findViewById(R.id.endTime_tv);
				TextView startCity_tv = (TextView) convertView
						.findViewById(R.id.startCity_tv);
				TextView endCity_tv = (TextView) convertView
						.findViewById(R.id.endCity_tv);
				TextView discount_tv = (TextView) convertView
						.findViewById(R.id.discount_tv);
				TextView price_tv = (TextView) convertView
						.findViewById(R.id.price_tv);
				TextView FlightName_tv = (TextView) convertView
						.findViewById(R.id.FlightName_tv);
				TextView FlightNo_tv = (TextView) convertView
						.findViewById(R.id.FlightNo_tv);
				TextView PlaneTypeAndModel = (TextView) convertView
						.findViewById(R.id.PlaneTypeAndModel);
				TextView CabinName_tv = (TextView) convertView
						.findViewById(R.id.CabinName_tv);
				TextView ticketCount_tv = (TextView) convertView
						.findViewById(R.id.ticketCount_tv);
				TextView fanMoney_tv = (TextView) convertView
						.findViewById(R.id.fanMoney_tv);

				LinearLayout fanMoney_ll = (LinearLayout) convertView
						.findViewById(R.id.fanMoney_ll);
				// if (MyApp.platform==Platform.B2C) {
				// fanMoney_ll.setVisibility(View.GONE);
				// }
				// else if (MyApp.platform==Platform.B2B)
				// {
				// fanMoney_ll.setVisibility(View.VISIBLE);
				// }

				try {
					startTime_tv.setText(DateUtil.getTime(str.get(position)
							.getOffTime()));
					endTime_tv.setText(DateUtil.getTime(str.get(position)
							.getArriveTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String startPort = str.get(position).getStartPortName();
				String endPort = str.get(position).getEndPortName();
				if (AirportConvert.AIRPORT.containsKey(startPort)) {
					startCity_tv.setText(AirportConvert.AIRPORT.get(startPort)
							+ str.get(position).getStartT());
				} else
					startCity_tv.setText(startPort
							+ str.get(position).getStartT());
				if (AirportConvert.AIRPORT.containsKey(endPort)) {
					endCity_tv.setText(AirportConvert.AIRPORT.get(endPort)
							+ str.get(position).getEndT());
				} else
					endCity_tv.setText(endPort + str.get(position).getEndT());

				if (startCity_tv.getText().toString().length()
						+ endCity_tv.getText().toString().length() > 10) {
					startCity_tv.setText(" " + str.get(position).getStartT());
					endCity_tv.setText(" " + str.get(position).getEndT());
				}
				discount_tv.setText(discountDeal(str.get(position)
						.getMinDiscount().trim()));
				price_tv.setText(" ￥" + str.get(position).getMinFare());
				FlightName_tv.setText(str.get(position).getCarrinerName());
				FlightNo_tv.setText(str.get(position).getFlightNo());
				PlaneTypeAndModel.setText(str.get(position).getPlaneType()
						+ str.get(position).getPlaneModel());
				ticketCount_tv.setText(tiecketCountDeal(str.get(position)
						.getMinTicketCount()) + "张");
				CabinName_tv.setText(str.get(position).getCabinName());
				fanMoney_tv.setText(str.get(position).getYouHui());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}

		private String discountDeal(String str) {
			String discountString = "";
			if (str.equals("100")) {
				discountString = "全价";
			}
			if (str.length() == 2) {
				discountString = str.substring(0, 1) + "." + str.substring(1)
						+ "折";
			}
			return discountString;
		}

		private String tiecketCountDeal(String str) {
			String tiecketCount = "";
			if (Integer.parseInt(str) > 9) {
				tiecketCount = ">9";
			} else if (Integer.parseInt(str) == 1) {
				tiecketCount = "仅剩1";
			} else {
				tiecketCount = str;
			}
			return tiecketCount;
		}
	}
}
