package com.jike.shanglv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.IdType;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Enums.SingleOrDouble;
import com.jike.shanglv.Models.InterDemandPassenger;
import com.jike.shanglv.Models.InterDemandStr;
import com.jike.shanglv.Models.Passenger;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;

public class ActivityInternationalRequisitionForm extends Activity {

	protected static final String ALLPASSENGERSLIST = "ALL_PASSENGERS_LIST";
	protected static final String SELECTEDPASSENGERSLIST = "SELECTED_PASSENGERS_LIST";
	protected static final int ADD_PASSENGERS_FORRESULET_CODE = 23;
	protected static final int COMMIT_DEMAND_MSG_CODE = 0;
	protected static final int NEW_DEMAND_SUCCEED_CODE = 1;

	private TextView startCity_tv, arriveCity_tv, startDate_tv, arriveDate_tv,
			add_passager_tv, baoxian_price_and_count_tv;
	private EditText budget_et, remark_et, contact_person_phone_et;
	private RelativeLayout add_passager_rl, baoxian_rl;
	private LinearLayout backDate_ll;
	private ImageButton lianxiren_icon_imgbtn, back_imgbtn, home_imgbtn,
			baoxian_check_imgbtn;
	private ListView passenger_listview;
	private View passenger_head_divid_line, backDate_topLine;
	private Button commit_button;
	private Context context;
	private SharedPreferences sp;
	private ArrayList<Passenger> passengerList;// 选择的乘机人列表
	private ArrayList<Passenger> allPassengerList;// 当前所有乘机人的列表（服务端和用户新增的）
	private String startcity_code = "", arrivecity_code = "", startcity = "",
			arrivecity = "", startoff_date = "", startdate = "", enddate = "";// 从搜索页面获取的数据
	private String commitReturnJson = "", needBaoxianString = "1";
	private SingleOrDouble wayType;
	private CustomProgressDialog progressdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_international_requisition_form);
			initView();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		passengerList = new ArrayList<Passenger>();
		allPassengerList = new ArrayList<Passenger>();
		passenger_listview = (ListView) findViewById(R.id.passenger_listview);
		add_passager_tv = (TextView) findViewById(R.id.add_passager_tv);
		passenger_head_divid_line = findViewById(R.id.passenger_head_divid_line);
		backDate_topLine = findViewById(R.id.backDate_topLine);

		startCity_tv = (TextView) findViewById(R.id.startCity_tv);
		arriveCity_tv = (TextView) findViewById(R.id.arriveCity_tv);
		startDate_tv = (TextView) findViewById(R.id.startDate_tv);
		arriveDate_tv = (TextView) findViewById(R.id.arriveDate_tv);
		budget_et = (EditText) findViewById(R.id.budget_et);
		remark_et = (EditText) findViewById(R.id.remark_et);
		contact_person_phone_et = (EditText) findViewById(R.id.contact_person_phone_et);
		add_passager_rl = (RelativeLayout) findViewById(R.id.add_passager_rl);
		add_passager_rl.setOnClickListener(btnClickListner);
		baoxian_price_and_count_tv = (TextView) findViewById(R.id.baoxian_price_and_count_tv);
		baoxian_rl = (RelativeLayout) findViewById(R.id.baoxian_rl);
		backDate_ll = (LinearLayout) findViewById(R.id.backDate_ll);
		baoxian_check_imgbtn = (ImageButton) findViewById(R.id.baoxian_check_imgbtn);
		baoxian_check_imgbtn.setSelected(true);
		baoxian_rl.setOnClickListener(btnClickListner);

		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		lianxiren_icon_imgbtn = (ImageButton) findViewById(R.id.lianxiren_icon_imgbtn);
		lianxiren_icon_imgbtn.setOnClickListener(btnClickListner);
		back_imgbtn.setOnClickListener(btnClickListner);
		home_imgbtn.setOnClickListener(btnClickListner);
		commit_button = (Button) findViewById(R.id.commit_button);
		commit_button.setOnClickListener(btnClickListner);

		if (sp.getString(SPkeys.gjjpContactPhone.getString(), "").equals(""))
			contact_person_phone_et.setText(CommonFunc.getPhoneNumber(context));
		else
			contact_person_phone_et.setText(sp.getString(
					SPkeys.gjjpContactPhone.getString(), ""));

		getIntentData();
	}

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
			startCity_tv.setText(startcity);
			arriveCity_tv.setText(arrivecity);
			if (wayType == SingleOrDouble.singleWay) {
				if (bundle.containsKey("startoff_date"))
					startoff_date = bundle.getString("startoff_date");
				backDate_ll.setVisibility(View.GONE);
				backDate_topLine.setVisibility(View.GONE);
				startDate_tv.setText(startoff_date);
			} else if (wayType == SingleOrDouble.doubleWayGo
					|| wayType == SingleOrDouble.doubleWayBack) {
				if (bundle.containsKey("startdate"))
					startdate = bundle.getString("startdate");
				if (bundle.containsKey("enddate"))
					enddate = bundle.getString("enddate");
				startDate_tv.setText(startdate);
				arriveDate_tv.setText(enddate);
			}
		}
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
				case R.id.lianxiren_icon_imgbtn:
					startActivityForResult(
							new Intent(
									context,
									com.jike.shanglv.SeclectCity.ContactListActivity.class),
							13);
					break;
				case R.id.add_passager_rl:
					Intent intent = new Intent(context,
							ActivityInlandAirlineticketSelectPassengers.class);
					intent.putExtra(
							ActivityInlandAirlineticketSelectPassengers.SYSTYPE,
							"1");
					intent.putExtra(
							ActivityInlandAirlineticketSelectPassengers.TITLE_NAME,
							"选择乘机人");
					intent.putExtra(ALLPASSENGERSLIST,
							JSONHelper.toJSON(allPassengerList));
					intent.putExtra(SELECTEDPASSENGERSLIST,
							JSONHelper.toJSON(passengerList));
					startActivityForResult(intent,
							ADD_PASSENGERS_FORRESULET_CODE);
					break;
				case R.id.baoxian_rl:
					if (!baoxian_check_imgbtn.isSelected()) {
						baoxian_check_imgbtn.setSelected(true);
						needBaoxianString = "1";
						baoxian_price_and_count_tv.setTextColor(getResources()
								.getColor(R.color.price_yellow));
					} else if (baoxian_check_imgbtn.isSelected()) {
						baoxian_check_imgbtn.setSelected(false);
						needBaoxianString = "0";
						baoxian_price_and_count_tv.setTextColor(getResources()
								.getColor(R.color.danhuise));
					}
					break;
				case R.id.commit_button:
					if (passengerList.size() == 0) {
						// new AlertDialog.Builder(context).setTitle("乘机人不能为空")
						// .setMessage("请添加乘机人！")
						// .setPositiveButton("确定", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("请添加乘机人");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					if (budget_et.getText().toString().trim().length() == 0) {
						// new AlertDialog.Builder(context).setTitle("预算金额不能为空")
						// .setMessage("请输入预算金额！")
						// .setPositiveButton("确定", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("请输入预算金额");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					if (!CommonFunc.isMobileNO(contact_person_phone_et
							.getText().toString().trim())) {
						// new
						// AlertDialog.Builder(context).setTitle("手机号码格式不正确")
						// .setMessage("请输入合法的手机号码！")
						// .setPositiveButton("确定", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("请输入合法的手机号码");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					} else {
						sp.edit()
								.putString(
										SPkeys.gnjpContactPhone.getString(),
										contact_person_phone_et.getText()
												.toString()).commit();
					}
					try {
						if (getStr().equals(""))
							break;
					} catch (Exception e) {
						e.printStackTrace();
					}
					commitOrder();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			JSONTokener jsonParser;
			switch (msg.what) {
			case COMMIT_DEMAND_MSG_CODE:
				jsonParser = new JSONTokener(commitReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					// {"c":"0000","d":"BX1408261127020127"}
					if (state.equals("0000")) {
						String orderID = jsonObject.getString("d");
						Intent intent = new Intent(context,
								ActivityInternationalRequisitionSuccess.class);
						intent.putExtra(
								ActivityInternationalRequisitionSuccess.RECEIPT_ORDER_ID,
								orderID);
						startActivityForResult(intent, NEW_DEMAND_SUCCEED_CODE);
					} else {
						// Toast.makeText(context, "发生未知异常，提交订单失败！", 0).show();
						String message = jsonObject.getJSONObject("d")
								.getString("msg");
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle(message);
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
					}
					progressdialog.dismiss();
				} catch (Exception e) {
					progressdialog.dismiss();
					e.printStackTrace();
				}
				break;
			}
		}
	};

	private void commitOrder() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// url?action=intdemand&str=str参数&sitekey=defage
					// &sign=1232432&userkey=2bfc0c48923cf89de19f6113c127ce81

					MyApp ma = new MyApp(context);
					String str = getStr();
					// String param = "action=intdemand&sitekey=&userkey=" +
					// ma.getHm().get(PackageKeys.USERKEY.getString()).toString()
					// + "&sign="
					// +
					// CommonFunc.MD5(ma.getHm().get(PackageKeys.USERKEY.getString()).toString()
					// + "intdemand" + str)+"&str=" + str;
					// commitReturnJson =
					// HttpUtils.getJsonContent(ma.getServeUrl(),param);
					String orgin = ma.getHm()
							.get(PackageKeys.ORGIN.getString()).toString();
					String param = "?action=createDemandOrder&sitekey="
							+ MyApp.sitekey
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "createDemandOrder" + str) + "&orgin="
							+ orgin;
					// try {
					// str=URLEncoder.encode(str, "utf-8");
					// } catch (UnsupportedEncodingException e) {
					// e.printStackTrace();
					// }
					commitReturnJson = HttpUtils.myPost(ma.getServeUrl()
							+ param, "&str=" + str);
					// String
					// str="{\"userid\":\"3450\",\"amount\":\"760\",\"origin\":\"2\",\"orderremark\":\"Android\\u5ba2\\u6237\\u7aef\",\"siteid\":\"65\",\"flights\":[{\"scode\":\"SHA\",\"scname\":\"\\u4e0a\\u6d77\\u8679\\u6865\",\"ecode\":\"CTU\",\"ecname\":\"\\u6210\\u90fd\",\"et\":\"T2\",\"st\":\"T2\",\"sdate\":\"2014-11-28\",\"stime\":\"06:50:00\",\"edate\":\"2014-11-28\",\"etime\":\"10:45:00\",\"cabin\":\"Q\",\"cabinname\":\"\\u7ecf\\u6d4e\\u8231\",\"crrier\":\"HO\",\"carrname\":\"\\u5409\\u7965\\u822a\\u7a7a\",\"flightno\":\"HO1269\",\"runtime\":\"3\\u5c0f\\u65f655\\u5206\\u949f\",\"discount\":\"40\",\"plane\":\"320\",\"oil\":\"70\",\"tax\":\"50\",\"type\":\"1\",\"rate\":\"6.50\",\"rebate\":\"6.50\",\"isspe\":\"true\",\"fare\":\"640\",\"stafare\":\"1610\",\"policyid\":\"ffffffff-fffe-5366-1180-201408271248\",\"remark\":\"\",\"supplier\":\"Piaomeng\",\"wt\":\"08:00-22:00\",\"rt\":\"08:00-22:00\",\"rateinfo\":\"6.50,0.0,0,0,0\",\"userrebate\":\"6.50\",\"policytype\":\"1\"}],\"passenger\":[{\"pname\":\"\\u6d4b\\u8bd5\",\"ptype\":\"1\",\"idtype\":\"0\",\"idno\":\"340825198601291014\",\"mobile\":\"1380000000\",\"isunum\":\"0\",\"addto\":\"0\"}],\"content\":{\"name\":\"ceshi\",\"mobile\":\"1380000000\",\"email\":\"ceshi@none.com\",\"tel\":\"\"}}";
					// commitReturnJson =
					// HttpUtils.myPost("http://gatewayceshi.51jp.cn/gateway?action=forder&userkey=c15fe8484c5e324b4c2febb97e38671c&sign=61055e6061b44647beb8ad77674f59c8",
					// "&str=" + str);
					Message msg = new Message();
					// Log.v("param", param);
					msg.what = COMMIT_DEMAND_MSG_CODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在提交需求单，请稍候...");
		progressdialog.setCancelable(true);
		progressdialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		progressdialog.show();
	}

	private String getStr() {
		String str = "";
		InterDemandStr interDemandStr = new InterDemandStr();
		interDemandStr.setUid(sp.getString(SPkeys.userid.getString(), ""));
		interDemandStr.setSid(sp.getString(SPkeys.siteid.getString(), "65"));
		interDemandStr.setsCity(startcity);
		interDemandStr.setsCode(startcity_code);
		if (wayType == SingleOrDouble.singleWay) {
			interDemandStr.setsDate(startoff_date);
			interDemandStr.seteDate("");
		} else if (wayType == SingleOrDouble.doubleWayGo
				|| wayType == SingleOrDouble.doubleWayBack) {
			interDemandStr.setsDate(startdate);
			interDemandStr.seteDate(enddate);
		}
		interDemandStr.setsTime("");
		interDemandStr.seteTime("");
		interDemandStr.seteCity(arrivecity);
		interDemandStr.seteCode(arrivecity_code);
		interDemandStr
				.setfType(wayType == SingleOrDouble.singleWay ? "0" : "1");
		interDemandStr.setYusuan(budget_et.getText().toString().trim());
		interDemandStr.setContactor(sp.getString(SPkeys.username.getString(),
				""));
		interDemandStr.setMobile(contact_person_phone_et.getText().toString()
				.trim());
		interDemandStr.setEmail("");
		interDemandStr.setRemark(remark_et.getText().toString().trim());
		// str=JSONHelper.toJSON(interDemandStr);//TODO 直接转化成json
		String psgStr = getPsgInfo();
		if (psgStr.equals("")) {
			return "";
		}
		// String失败，只有部分属性，暂先使用以下拼接方式
		str = "{\"uid\":\"" + interDemandStr.getUid() + "\",\"sid\":\""
				+ interDemandStr.getSid() + "\",\"sCity\":\""
				+ interDemandStr.getsCity() + "\",\"sCode\":\""
				+ interDemandStr.getsCode() + "\",\"sDate\":\""
				+ interDemandStr.getsDate() + "\",\"sTime\":\""
				+ interDemandStr.getsTime() + "\",\"eCity\":\""
				+ interDemandStr.geteCity() + "\",\"eCode\":\""
				+ interDemandStr.geteCode() + "\",\"eDate\":\""
				+ interDemandStr.geteDate() + "\",\"eTime\":\""
				+ interDemandStr.geteTime() + "\",\"fType\":\""
				+ interDemandStr.getfType() + "\",\"yusuan\":\""
				+ interDemandStr.getYusuan() + "\",\"contactor\":\""
				+ interDemandStr.getContactor() + "\",\"mobile\":\""
				+ interDemandStr.getMobile() + "\",\"email\":\""
				+ interDemandStr.getEmail() + "\",\"remark\":\""
				+ interDemandStr.getRemark() + "\",\"psgInfo\":" + getPsgInfo()
				+ "}";
		return str;
	}

	private String getPsgInfo() {// URLEncoder.encode( , "utf-8");
		String str = "";
		ArrayList<InterDemandPassenger> cpList = new ArrayList<InterDemandPassenger>();
		for (int i = 0; i < passengerList.size(); i++) {
			Passenger passenger = passengerList.get(i);
			InterDemandPassenger interDemandPassenger = new InterDemandPassenger();
			String[] name = passenger.getPassengerName().split("/");
			if (name.length == 2) {
				interDemandPassenger.setSurname(name[0]);
				interDemandPassenger.setGivenname(name[1]);
			}
			interDemandPassenger.setCardNo(passenger.getIdentificationNum());
			interDemandPassenger
					.setSex((passenger.getGender() != null && passenger
							.getGender().equals("男")) ? "1" : "2");
			interDemandPassenger.setCardType(String
					.valueOf(IdType.IdTypeReverse.get(passenger
							.getIdentificationType())));
			interDemandPassenger.setCusBirth(passenger.getBirthDay());
			interDemandPassenger.setNumberValiddate(passenger.getIDdeadline());
			if (passenger.getIDdeadline() == null
					|| passenger.getIDdeadline().contains("null")) {
				final CustomerAlertDialog cad = new CustomerAlertDialog(
						context, true);
				cad.setTitle("用户信息非法，请编辑用户" + passenger.getPassengerName()
						+ "的证件有效期，再尝试提交需求单");
				cad.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						cad.dismiss();
					}
				});
				return "";
			}
			try {// URLEncoder.encode(passenger.getNation(), "utf-8")
				interDemandPassenger.setCountry(passenger.getNation());
				interDemandPassenger.setQianfadi(passenger.getIssueAt());
			} catch (Exception e) {
				e.printStackTrace();
			}
			interDemandPassenger.setInsurance(needBaoxianString);
			interDemandPassenger
					.setSavePsg(passenger.getAddto().equals("1") ? "true"
							: "false");
			cpList.add(interDemandPassenger);
		}
		str = JSONHelper.toJSON(cpList);
		str = str.replace("\"null\"", "null").replace("null", "\"\"");
		return str;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			switch (resultCode) {
			case ActivityInlandAirlineticketSelectPassengers.SELECTED_FINISH:
				Bundle b = null;
				if (data != null) {
					b = data.getExtras();
				} else
					break;
				String passengerListString = "",
				allPassengerListString = "";
				if (b != null && b.containsKey(SELECTEDPASSENGERSLIST)) {
					passengerListString = b.getString(SELECTEDPASSENGERSLIST);
				}
				if (b != null && b.containsKey(ALLPASSENGERSLIST)) {
					allPassengerListString = b.getString(ALLPASSENGERSLIST);
				} else
					break;
				try {
					passengerList.clear();
					passengerList = (ArrayList<Passenger>) JSONHelper
							.parseCollection(passengerListString, List.class,
									Passenger.class);
					allPassengerList = (ArrayList<Passenger>) JSONHelper
							.parseCollection(allPassengerListString,
									List.class, Passenger.class);
					passengerList = removeDuplictePassengers(passengerList);
					if (passengerList.size() > 0) {
						add_passager_tv.setText(getResources().getString(
								R.string.modify_passenger));
						passenger_head_divid_line.setVisibility(View.VISIBLE);
					} else if (passengerList.size() == 0) {
						add_passager_tv.setText(getResources().getString(
								R.string.add_passenger));
						passenger_head_divid_line.setVisibility(View.GONE);
					}
					for (int i = 0; i < passengerList.size(); i++) {
						if (passengerList.get(i).getIDdeadline() == null) {
							passengerList.remove(i);
							final CustomerAlertDialog cad = new CustomerAlertDialog(
									context, true);
							cad.setTitle("乘客"
									+ passengerList.get(i).getPassengerName()
									+ "的证件有效期非法，请重新修改");
							cad.setPositiveButton("知道了", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									cad.dismiss();
								}
							});
						}
					}
					ListAdapter adapter = new PassengerListAdapter(context,
							passengerList);
					passenger_listview.setAdapter(adapter);
					ActivityInlandAirlineticketBooking
							.setListViewHeightBasedOnChildren(passenger_listview);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				break;
			default:
				break;
			}

			if (requestCode == 13) {
				if (data == null)
					return;
				Bundle b = data.getExtras();
				if (b != null && b.containsKey("pickedPhoneNum")) {
					String myNum = b.getString("pickedPhoneNum");
					if (myNum.startsWith("17951")) {
						myNum = myNum.substring(5);
					} else if (myNum.startsWith("+86")) {
						myNum = myNum.substring(3);
					}
					contact_person_phone_et.setText(myNum);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				View divid_line = convertView.findViewById(R.id.divid_line);
				TextView passengerName_tv = (TextView) convertView
						.findViewById(R.id.passengerName_tv);
				TextView identificationType_tv = (TextView) convertView
						.findViewById(R.id.identificationType_tv);
				TextView identificationNum_tv = (TextView) convertView
						.findViewById(R.id.identificationNum_tv);
				TextView passengerType_tv = (TextView) convertView
						.findViewById(R.id.passengerType_tv);

				passengerName_tv.setText(str.get(position).getPassengerName());
				identificationType_tv.setText(str.get(position)
						.getIdentificationType());
				identificationNum_tv.setText(str.get(position)
						.getIdentificationNum());
				passengerType_tv.setText("("
						+ str.get(position).getPassengerType() + ")");
				RelativeLayout passenger_rl = (RelativeLayout) convertView
						.findViewById(R.id.passenger_rl);
				passenger_rl.setTag(position + "");
				if (position == passengerList.size() - 1) {
					divid_line.setVisibility(View.GONE);
				}

				ImageButton delete_imgbtn = (ImageButton) convertView
						.findViewById(R.id.delete_imgbtn);
				delete_imgbtn.setTag(position + "");// 给Item中的button设置tag，根据tag判断用户点击了第几行
				delete_imgbtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int index = Integer.parseInt(v.getTag().toString());
						passengerList.remove(index);
						notifyDataSetChanged();
						ActivityInlandAirlineticketBooking
								.setListViewHeightBasedOnChildren(passenger_listview);
						if (passengerList.size() == 0) {
							add_passager_tv.setText(getResources().getString(
									R.string.add_passenger));
							passenger_head_divid_line.setVisibility(View.GONE);
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	// 去除重复的乘机人
	public ArrayList<Passenger> removeDuplictePassengers(
			ArrayList<Passenger> userList) {
		Set<Passenger> s = new TreeSet<Passenger>(new Comparator<Passenger>() {

			@Override
			public int compare(Passenger o1, Passenger o2) {
				if (o1.getPassengerName() != null
						&& o2.getPassengerName() != null) {
					return o1.getPassengerName().compareTo(
							o2.getPassengerName());
				}
				return 0;
			}
		});
		s.addAll(userList);
		return new ArrayList<Passenger>(s);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("passengerList", JSONHelper.toJSON(passengerList));
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		try {
			super.onRestoreInstanceState(savedInstanceState);
			if (savedInstanceState != null) {
				String pl = savedInstanceState.getString("passengerList");
				try {
					passengerList.clear();
					passengerList = (ArrayList<Passenger>) JSONHelper
							.parseCollection(pl, List.class, Passenger.class);
					PassengerListAdapter adapter = new PassengerListAdapter(
							context, passengerList);
					passenger_listview.setAdapter(adapter);
					ActivityInlandAirlineticketBooking
							.setListViewHeightBasedOnChildren(passenger_listview);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
