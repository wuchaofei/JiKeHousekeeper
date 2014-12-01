package com.jike.shanglv;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
//import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Enums.SingleOrDouble;
import com.jike.shanglv.Models.CabList;
import com.jike.shanglv.Models.InlandAirlineInfo;

public class ActivityInlandAirlineticketSelectCabin extends Activity {

	protected static final String TOKEN_NAME = "THE_FLIGHT_USER_SELECTED";// 工作（用户选择）
																			// 航班
	protected static final String TOKEN_NAME1 = "THE_FLIGHT_USER_SELECTED_DOUBLEWAY_GO";
	protected static final String TOKEN_NAME2 = "THE_FLIGHT_USER_SELECTED_DOUBLEWAY_BACK";
	private Context context;
	private ImageButton back_imgbtn, home_imgbtn;
	private TextView title_tv, startoff_date_tv, dayOfTheWeek_tv,
			PlaneTypeAndModel_tv, startoff_time_tv, arrive_time_tv,
			start_port_tv, arrive_port_tv, runtime_tv, oil_tax_tv;
	private ListView listview;
	private SharedPreferences sp;
	private CustomProgressDialog progressdialog;
	private JSONObject jsonObject;
	private int index;

	private ListAdapter adapter;
	private ArrayList<CabList> Cablist_List;

	private SingleOrDouble wayType = SingleOrDouble.singleWay;
	private String goFlight = "", goFlightSelectedIndex = "";// 往返机票去程航班信息
	private String startcity_code = "", arrivecity_code = "", startcity = "",
			arrivecity = "", startoff_date = "", startdate = "", enddate = "";// 从搜索页面传递过来的数据

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_inland_airlineticket_selectcabin);
			try {
				initView();
			} catch (Exception e) {
				e.printStackTrace();
			}
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		Cablist_List = new ArrayList<CabList>();
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		listview = (ListView) findViewById(R.id.listview);

		title_tv = (TextView) findViewById(R.id.title_tv);
		startoff_date_tv = (TextView) findViewById(R.id.startoff_date_tv);
		dayOfTheWeek_tv = (TextView) findViewById(R.id.dayOfTheWeek_tv);
		PlaneTypeAndModel_tv = (TextView) findViewById(R.id.PlaneTypeAndModel_tv);
		startoff_time_tv = (TextView) findViewById(R.id.startoff_time_tv);
		arrive_time_tv = (TextView) findViewById(R.id.arrive_time_tv);
		start_port_tv = (TextView) findViewById(R.id.start_port_tv);
		arrive_port_tv = (TextView) findViewById(R.id.arrive_port_tv);
		runtime_tv = (TextView) findViewById(R.id.runtime_tv);
		oil_tax_tv = (TextView) findViewById(R.id.oil_tax_tv);

		back_imgbtn.setOnClickListener(btnClickListner);
		home_imgbtn.setOnClickListener(btnClickListner);

		getIntentData();

		InlandAirlineInfo ia = new InlandAirlineInfo(jsonObject);
		title_tv.setText(ia.getCarrinerName() + ia.getFlightNo());
		try {
			startoff_date_tv.setText(DateUtil.getDate(ia.getOffTime()));
			dayOfTheWeek_tv.setText(DateUtil.getDayOfWeek(ia.getOffTime()));
			startoff_time_tv.setText(DateUtil.getTime(ia.getOffTime()));
			arrive_time_tv.setText(DateUtil.getTime(ia.getArriveTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		PlaneTypeAndModel_tv.setText(ia.getPlaneType() + "("
				+ ia.getPlaneModel() + ")");
		start_port_tv.setText(ia.getStartPortName() + ia.getStartT());
		arrive_port_tv.setText(ia.getEndPortName() + ia.getEndT());
		runtime_tv.setText(ia.getRunTime());
		oil_tax_tv
				.setText("燃油+机建=￥"
						+ (Integer.parseInt(ia.getOil()) + Integer.parseInt(ia
								.getTax())));

		JSONArray flist = null;
		try {
			flist = jsonObject.getJSONArray("CabList");
			createList(flist);
		} catch (Exception e) {
			e.printStackTrace();
		}

		adapter = new ListAdapter(context, Cablist_List);
		listview.setAdapter(adapter);
	}

	private void getIntentData() {
		Bundle bundle = this.getIntent().getExtras();
		// 搜索列表选择的航班信息
		try {
			jsonObject = new JSONObject(bundle.get(TOKEN_NAME).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 用户搜索条件数据
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
			} else if (wayType == SingleOrDouble.doubleWayGo
					|| wayType == SingleOrDouble.doubleWayBack) {
				if (bundle.containsKey("startdate"))
					startdate = bundle.getString("startdate");
				if (bundle.containsKey("enddate"))
					enddate = bundle.getString("enddate");
			}
		}
	}

	/**
	 * 构建list对象
	 * 
	 * @param flist_list
	 */
	private void createList(JSONArray flist_list) {
		Cablist_List.clear();
		for (int i = 0; i < flist_list.length(); i++) {
			try {
				CabList ia = new CabList(flist_list.getJSONObject(i));
				Cablist_List.add(ia);
			} catch (Exception e) {
			}
		}
	}

	View.OnClickListener btnClickListner = new View.OnClickListener() {
		@SuppressLint("ResourceAsColor")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_imgbtn:
				finish();
				break;
			case R.id.home_imgbtn:
				startActivity(new Intent(context, MainActivity.class));
				break;
			default:
				break;
			}
		}
	};

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<CabList> str;

		public ListAdapter(Context context, List<CabList> list1) {
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
							R.layout.item_inland_airlineticket_cabinlist, null);
				}
				TextView tv_price = (TextView) convertView
						.findViewById(R.id.tv_price);
				TextView CabinName_tv = (TextView) convertView
						.findViewById(R.id.CabinName_tv);
				TextView discount_tv = (TextView) convertView
						.findViewById(R.id.discount_tv);
				TextView fanMoney_tv = (TextView) convertView
						.findViewById(R.id.fanMoney_tv);
				TextView price_tv = (TextView) convertView
						.findViewById(R.id.price_tv);
				TextView ticketCount_tv = (TextView) convertView
						.findViewById(R.id.ticketCount_tv);
				RelativeLayout fanMoney_rl = (RelativeLayout) convertView
						.findViewById(R.id.fanMoney_rl);
				ImageButton booking_imgbtn = (ImageButton) convertView
						.findViewById(R.id.booking_imgbtn);
				RelativeLayout tuiGaiQian_rl = (RelativeLayout) convertView
						.findViewById(R.id.tuiGaiQian_rl);

				// if (MyApp.platform==Platform.B2C) {
				// fanMoney_rl.setVisibility(View.GONE);
				// }
				// else if (MyApp.platform==Platform.B2B)
				// {
				// fanMoney_rl.setVisibility(View.VISIBLE);
				// }
				tv_price.setText(" ￥" + str.get(position).getFareEx());
				CabinName_tv.setText(str.get(position).getCabinName()
						+ str.get(position).getCabin());
				discount_tv.setText(discountDeal(str.get(position)
						.getDiscount().trim()));
				fanMoney_tv.setText(" ￥" + str.get(position).getYouHui());
				price_tv.setText(" ￥" + str.get(position).getSale());
				ticketCount_tv.setText(tiecketCountDeal(str.get(position)
						.getTicketCount()));

				booking_imgbtn.setTag(position + "");// 给Item中的button设置tag，根据tag判断用户点击了第几行
				booking_imgbtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						index = Integer.parseInt(v.getTag().toString());
						Intent intentSend = new Intent(context,
								ActivityInlandAirlineticketBooking.class);
						if (wayType == SingleOrDouble.singleWay
								|| wayType == SingleOrDouble.doubleWayGo) {
							intentSend
									.putExtra(
											ActivityInlandAirlineticketBooking.ORDERWAYTYPE,
											SingleOrDouble.singleWay);
							intentSend
									.putExtra(
											ActivityInlandAirlineticketSelectCabin.TOKEN_NAME1,
											jsonObject.toString());
							intentSend
									.putExtra(
											ActivityInlandAirlineticketBooking.SELECTED_CABIN_INDEX1,
											index);

							if (wayType == SingleOrDouble.doubleWayGo) {// 如果是往返机票，且目前选择的是去程，则跳到航班搜索页面，选择返程机票
								// new
								// android.app.AlertDialog.Builder(context).setTitle("请选择返程机票")
								// .setMessage("   已选择去程航班，点击“确定”选择返程航班！")
								// .setPositiveButton("确定",
								// new OnClickListener() {
								// @Override
								// public void onClick(DialogInterface dialog,
								// int
								// which) {
								// Intent intents = new Intent(
								// context,
								// ActivityInlandAirlineticketSearchlist.class);
								// intents.putExtra("wayType",
								// SingleOrDouble.doubleWayBack);
								// intents.putExtra("startcity", startcity);
								// intents.putExtra("arrivecity", arrivecity);
								// intents.putExtra("startcity_code",
								// startcity_code);
								// intents.putExtra("arrivecity_code",
								// arrivecity_code);
								// intents.putExtra("startdate", startdate);
								// intents.putExtra("enddate", enddate);
								// intents.putExtra(ActivityInlandAirlineticketBooking.SELECTED_CABIN_INDEX1,
								// String.valueOf(index));
								// intents.putExtra(ActivityInlandAirlineticketSelectCabin.TOKEN_NAME1,
								// jsonObject.toString());
								// startActivity(intents);
								// }
								// })
								// .setNeutralButton("稍等一会", null)
								// .show();
								final CustomerAlertDialog cad = new CustomerAlertDialog(
										context, false);
								cad.setTitle("已选择去程航班，点击“确定”选择返程航班");
								cad.setPositiveButton("确定",
										new OnClickListener() {
											@Override
											public void onClick(View arg0) {
												Intent intents = new Intent(
														context,
														ActivityInlandAirlineticketSearchlist.class);
												intents.putExtra(
														"wayType",
														SingleOrDouble.doubleWayBack);
												intents.putExtra("startcity",
														startcity);
												intents.putExtra("arrivecity",
														arrivecity);
												intents.putExtra(
														"startcity_code",
														startcity_code);
												intents.putExtra(
														"arrivecity_code",
														arrivecity_code);
												intents.putExtra("startdate",
														startdate);
												intents.putExtra("enddate",
														enddate);
												intents.putExtra(
														ActivityInlandAirlineticketBooking.SELECTED_CABIN_INDEX1,
														String.valueOf(index));
												intents.putExtra(
														ActivityInlandAirlineticketSelectCabin.TOKEN_NAME1,
														jsonObject.toString());
												startActivity(intents);
												cad.dismiss();
											}
										});
								cad.setNegativeButton1("稍等一会",
										new OnClickListener() {
											@Override
											public void onClick(View arg0) {
												cad.dismiss();
											}
										});
							} else
								startActivity(intentSend);
						} else if (wayType == SingleOrDouble.doubleWayBack) {
							intentSend
									.putExtra(
											ActivityInlandAirlineticketBooking.ORDERWAYTYPE,
											SingleOrDouble.doubleWayBack);
							// 从搜索列表传过来的航班信息，不管是单程还是往返（go/back），都使用TOKEN_NAME，但下到订单页面时，有可能有两个航班信息，使用TOKEN_NAME2标注往返返程数据
							intentSend
									.putExtra(
											ActivityInlandAirlineticketSelectCabin.TOKEN_NAME2,
											jsonObject.toString());
							intentSend
									.putExtra(
											ActivityInlandAirlineticketBooking.SELECTED_CABIN_INDEX2,
											index);
							intentSend
									.putExtra(
											ActivityInlandAirlineticketSelectCabin.TOKEN_NAME1,
											goFlight);
							intentSend
									.putExtra(
											ActivityInlandAirlineticketBooking.SELECTED_CABIN_INDEX1,
											goFlightSelectedIndex);
							startActivity(intentSend);
						}
					}
				});

				tuiGaiQian_rl.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// Toast.makeText(context,"退改签", 0).show();
					}
				});
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
				tiecketCount = "9张以上";
			} else if (Integer.parseInt(str) == 1) {
				tiecketCount = "仅剩1张";
			} else {
				tiecketCount = str + "张";
			}
			return tiecketCount;
		}
	}
}
