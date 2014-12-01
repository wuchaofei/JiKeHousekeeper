package com.jike.shanglv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;

import org.json.JSONObject;
import org.json.JSONTokener;
import android.annotation.SuppressLint;
import android.app.Activity;
//import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
//import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.jike.shanglv.Common.ClearEditText;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.LazyList.ImageLoader;
import com.jike.shanglv.Models.Passenger;
import com.jike.shanglv.Models.Seat;
import com.jike.shanglv.Models.TrainListItem;
import com.jike.shanglv.Models.TrainOrderPassenger;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;


public class ActivityTrainBooking extends Activity {

	protected static final String ALLPASSENGERSLIST = "ALL_PASSENGERS_LIST";
	protected static final String SELECTEDPASSENGERSLIST = "SELECTED_PASSENGERS_LIST";
	protected static final int ADD_PASSENGERS_FORRESULET_CODE = 0;
	protected static final int VALIDCODE_MSG_CODE = 1;
	protected static final int COMMIT_ORDER_MSG_CODE = 2;
	protected static final int NEW_ORDER_DETAIL_CODE = 3;
	protected static final int CONTANCT_REQUEST_CODE = 4;
	protected static final int BAOXIAN_REQUEST_CODE = 5;

	private RelativeLayout add_passager_rl, baoxian_rl;
	private ImageButton lianxiren_icon_imgbtn, back_imgbtn, home_imgbtn;
	private TextView djsx_tv, train_num_tv, train_type_tv, runtime_tv,
			start_station_tv, start_time_tv, start_date_tv, end_station_tv,
			end_time_tv, end_date_tv, add_passager_tv, seat_grad_tv,
			ticket_price_tv, remain_count_tv, modify_seat_tv,
			baoxian_price_and_count_tv, order_totalmoney_tv;
	private ClearEditText contact_person_phone_et, yanzhengma_input_et;
	private View passenger_head_divid_line;
	private ListView passenger_listview, xibie_listview;
	private ImageView start_station_icon_iv, end_station_icon_iv,
			yanzhengma_iv;
	private Button order_now_btn;
	private Context context;
	private SharedPreferences sp;
	private ImageLoader imageLoader;

	private TrainListItem ti = new TrainListItem();// 从列表传过来的车票信息
	private String startdate, commitReturnJson, seat_Type;
	private float ticket_price, baoxian_unitPrice = 10, totalPrice;// 保费：0、5、10
	private int selectedSeatIndex = -1, remainTicketCount = 1;// 当前选择席别及剩余票数
	private Bitmap validCodeBitmap;
	private ArrayList<Passenger> passengerList;// 选择的乘机人列表
	private ArrayList<Passenger> allPassengerList;// 当前所有乘机人的列表（服务端和用户新增的）
	private CustomProgressDialog progressdialog;
	MyListAdapter adapter_xibie;

	private LinearLayout lay_stop;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_train_booking);
			initView();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		imageLoader = new ImageLoader(context.getApplicationContext());
		passengerList = new ArrayList<Passenger>();
		allPassengerList = new ArrayList<Passenger>();

		lay_stop=(LinearLayout) findViewById(R.id.lay_stop);
		
		xibie_listview = (ListView) findViewById(R.id.xibie_listview);
		add_passager_rl = (RelativeLayout) findViewById(R.id.add_passager_rl);
		baoxian_rl = (RelativeLayout) findViewById(R.id.baoxian_rl);
		lianxiren_icon_imgbtn = (ImageButton) findViewById(R.id.lianxiren_icon_imgbtn);
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		train_num_tv = (TextView) findViewById(R.id.train_num_tv);
		train_type_tv = (TextView) findViewById(R.id.train_type_tv);
		runtime_tv = (TextView) findViewById(R.id.runtime_tv);
		start_station_tv = (TextView) findViewById(R.id.start_station_tv);
		start_time_tv = (TextView) findViewById(R.id.start_time_tv);
		start_date_tv = (TextView) findViewById(R.id.start_date_tv);
		end_station_tv = (TextView) findViewById(R.id.end_station_tv);
		end_time_tv = (TextView) findViewById(R.id.end_time_tv);
		end_date_tv = (TextView) findViewById(R.id.end_date_tv);
		seat_grad_tv = (TextView) findViewById(R.id.seat_grad_tv);
		ticket_price_tv = (TextView) findViewById(R.id.ticket_price_tv);
		remain_count_tv = (TextView) findViewById(R.id.remain_count_tv);
		modify_seat_tv = (TextView) findViewById(R.id.modify_seat_tv);
		add_passager_tv = (TextView) findViewById(R.id.add_passager_tv);
		djsx_tv = (TextView) findViewById(R.id.djsx_tv);
		baoxian_price_and_count_tv = (TextView) findViewById(R.id.baoxian_price_and_count_tv);
		order_totalmoney_tv = (TextView) findViewById(R.id.order_totalmoney_tv);
		contact_person_phone_et = (ClearEditText) findViewById(R.id.contact_person_phone_et);
		yanzhengma_input_et = (ClearEditText) findViewById(R.id.yanzhengma_input_et);
		passenger_head_divid_line = findViewById(R.id.passenger_head_divid_line);
		passenger_listview = (ListView) findViewById(R.id.passenger_listview);
		start_station_icon_iv = (ImageView) findViewById(R.id.start_station_icon_iv);
		end_station_icon_iv = (ImageView) findViewById(R.id.end_station_icon_iv);
		yanzhengma_iv = (ImageView) findViewById(R.id.yanzhengma_iv);
		order_now_btn = (Button) findViewById(R.id.order_now_btn);
		back_imgbtn.setOnClickListener(btnClickListner);
		home_imgbtn.setOnClickListener(btnClickListner);
		modify_seat_tv.setOnClickListener(btnClickListner);
		yanzhengma_iv.setOnClickListener(btnClickListner);
		((TextView) findViewById(R.id.djsx_tv))
				.setOnClickListener(btnClickListner);
		add_passager_rl.setOnClickListener(btnClickListner);
		order_now_btn.setOnClickListener(btnClickListner);
		baoxian_price_and_count_tv.setOnClickListener(btnClickListner);
		baoxian_rl.setOnClickListener(btnClickListner);
		lianxiren_icon_imgbtn.setOnClickListener(btnClickListner);

		getIntentTrainInfo();
		train_num_tv.setText(ti.getTrainID());
		train_type_tv.setText(ti.getTrainType());
		runtime_tv.setText("历时:" + ti.getRunTime());
		start_station_tv.setText(ti.getStationS());
		end_station_tv.setText(ti.getStationE());
		seat_grad_tv.setText(ti.getSeat_Type());
		ticket_price_tv.setText("￥" + ticket_price);
		remain_count_tv.setText("余票" + ti.getRemain_Count() + "张");
		start_time_tv.setText(ti.getGoTime());
		end_time_tv.setText(ti.getETime());
		adapter_xibie = new MyListAdapter(context, ti.getSeatList());
		adapter_xibie.setCurrentID(selectedSeatIndex);
		remainTicketCount = Integer.valueOf(ti.getRemain_Count());
		// adapter_xibie.notifyDataSetChanged();
		xibie_listview.setAdapter(adapter_xibie);
		ActivityInlandAirlineticketBooking
				.setListViewHeightBasedOnChildren(xibie_listview);
		xibie_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position != selectedSeatIndex) {
					adapter_xibie.setCurrentID(position);
					adapter_xibie.notifyDataSetChanged();
				}
				selectedSeatIndex = position;
				// adapter_xibie.notifyDataSetChanged();
				ticket_price = Float.valueOf(ti.getSeatList().get(position)
						.getPrice());
				remainTicketCount = Integer.valueOf(ti.getSeatList()
						.get(position).getShengyu());
				caculateMoney();
			}
		});
		if (selectedSeatIndex == -1) {
			order_now_btn.setEnabled(false);
			order_now_btn.setBackground(getResources().getDrawable(
					R.drawable.booking_immediately_button_d));
		}

		String SFType = ti.getSFType();
		if (SFType.length() == 3) {
			String SType = SFType.substring(0, 1);
			String FType = SFType.substring(2, 3);
			if (SType.equals("始")) {
				start_station_icon_iv.setBackground(getResources().getDrawable(
						R.drawable.trains_start));
			} else if (SType.equals("过")) {
				start_station_icon_iv.setBackground(getResources().getDrawable(
						R.drawable.train_over));
			}
			if (FType.equals("终")) {
				end_station_icon_iv.setBackground(getResources().getDrawable(
						R.drawable.train_final));
			} else if (FType.equals("过")) {
				end_station_icon_iv.setBackground(getResources().getDrawable(
						R.drawable.train_over));
			}
		}
		getValidCodePic();// 获取验证码信息
		// 对于常用联系人，直接返回上次订票时的联系人手机号，若不存在则返回本机手机号码
		if (sp.getString(SPkeys.trainContactPhone.getString(), "").equals(""))
			contact_person_phone_et.setText(CommonFunc.getPhoneNumber(context));
		else
			contact_person_phone_et.setText(sp.getString(
					SPkeys.trainContactPhone.getString(), ""));
		caculateMoney();
		
		/*lay_stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, ActivityTrainStop.class));
				
			}
		});*/
	}

	private void caculateMoney() {
		int personCount = 0;
		if (passengerList == null) {
			personCount = 0;
		} else {
			personCount = passengerList.size();
		}
		// baoxian_price_and_count_tv.setText("￥" + baoxian_unitPrice + "x"
		// + personCount + "份");
		totalPrice = personCount * (ticket_price + baoxian_unitPrice);

		order_totalmoney_tv.setText("￥" + String.valueOf(totalPrice));
	}

	private void getIntentTrainInfo() {
		Bundle bundle = this.getIntent().getExtras();
		try {
			if (bundle != null) {
				if (bundle.containsKey("TrainListItemString")) {
					JSONObject jsonObject = new JSONObject(
							bundle.getString("TrainListItemString"));
					ti = JSONHelper
							.parseObject(jsonObject, TrainListItem.class);
				}
				if (bundle.containsKey("SeatListString")) {
					JSONArray array = new JSONArray(
							bundle.getString("SeatListString"));
					ArrayList<Seat> seats = new ArrayList<Seat>();
					Boolean hasSelectedASeatBoolean = false;
					for (int i = 0; i < array.length(); i++) {
						Seat seat = new Seat();
						seat.setPrice(array.getJSONObject(i).getString("price"));
						seat.setShengyu(array.getJSONObject(i).getString(
								"shengyu"));
						seat.setType(array.getJSONObject(i).getString("type"));
						seats.add(seat);
						if (!hasSelectedASeatBoolean
								&& !seat.getShengyu().equals("0")) {
							selectedSeatIndex = i;
							ticket_price = Float.valueOf(array.getJSONObject(i)
									.getString("price"));
							hasSelectedASeatBoolean = true;
						}
					}
					ti.setSeatList(seats);
				}
				if (bundle.containsKey("startdate")) {
					Calendar c = Calendar.getInstance();
					startdate = bundle.getString("startdate");
					start_date_tv.setText(startdate);
					String sd = startdate + " " + ti.getGoTime() + ":00";
					Date date = new SimpleDateFormat("yy-MM-dd HH:mm:ss")
							.parse(sd);
					c.setTime(date);
					Time run_time = new Time(ti.getRunTime());
					c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY)
							+ Time.HOUR);
					c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + Time.MINUTE);
					String arriveDay = new SimpleDateFormat("yyyy-MM-dd")
							.format(c.getTime());
					end_date_tv.setText(arriveDay);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getValidCodePic() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String url = ma.getValidcodeServeUrl();
					validCodeBitmap = HttpUtils.getHttpBitmap(url);
					Message msg = new Message();
					msg.what = VALIDCODE_MSG_CODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void commitOrder() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String siteid = sp.getString(SPkeys.siteid.getString(),
							"65");
					String str = "{\"uid\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"Amount\": \""
							+ totalPrice
							+ "\",\"sid\":\""
							+ siteid
							+ "\",\"vcode\":\""
							+ yanzhengma_input_et.getText().toString().trim()
							+ "\",\"TrainNo\":\""
							+ ti.getTrainID()
							+ "\",\"SCity\":\""
							+ ti.getStationS()
							+ "\",\"ECity\":\""
							+ ti.getStationE()
							+ "\",\"ETime\":\""
							+ ti.getETime()
							+ "\",\"STime\":\""
							+ ti.getGoTime()
							+ "\",\"SDate\":\""
							+ startdate
							+ "\",\"Mobile\":\""
							+ contact_person_phone_et.getText().toString()
									.trim()
							+ "\",\"Email\":\"123@163.com\",\"Name\":\""
							+ sp.getString(SPkeys.username.getString(), "")
							+ "\",\"TicketCount\":\"" + passengerList.size()
							+ "\",\"PsgInfo\":" + getPassengers() + "}";
					str = str.replace("null", "");
					String orgin=ma.getHm().get(PackageKeys.ORGIN.getString())
							.toString();
					String param = "?action=trainorderv2&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sitekey="
							+ MyApp.sitekey
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "trainorderv2" + str)+"&orgin="+orgin;
					// try {
					// str = URLEncoder.encode(str, "utf-8");
					// } catch (UnsupportedEncodingException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					commitReturnJson = HttpUtils.myPost(ma.getServeUrl()
							+ param, "&str=" + str);
					Message msg = new Message();
					msg.what = COMMIT_ORDER_MSG_CODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在提交订单，请稍候...");
		progressdialog.setCancelable(true);
		progressdialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		progressdialog.show();
	}

	private String getPassengers() {
		String psgsString = "";
		ArrayList<TrainOrderPassenger> trainOrderPassengerList = new ArrayList<TrainOrderPassenger>();
		for (int i = 0; i < passengerList.size(); i++) {
			TrainOrderPassenger trainOrderPassenger = new TrainOrderPassenger();
			trainOrderPassenger.setCardNo(passengerList.get(i)
					.getIdentificationNum());
			trainOrderPassenger.setCardType(passengerList.get(i)
					.getIdentificationType());
			trainOrderPassenger.setIncAmount(String.valueOf(baoxian_unitPrice));
			trainOrderPassenger
					.setPhone(passengerList.get(i).getMobie() == null ? ""
							: passengerList.get(i).getMobie());
			trainOrderPassenger.setSaleprice(ticket_price + "");
			trainOrderPassenger.setSeatType(seat_Type);
			try {// URLEncoder.encode(passengerList.get(i).getPassengerName(),
					// "utf-8")
				trainOrderPassenger.setPsgName(passengerList.get(i)
						.getPassengerName());
				trainOrderPassenger.setTicketType(passengerList.get(i)
						.getPassengerType());
			} catch (Exception e) {
				e.printStackTrace();
			}
			trainOrderPassengerList.add(trainOrderPassenger);
		}
		psgsString = JSONHelper.toJSON(trainOrderPassengerList);
		return psgsString;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VALIDCODE_MSG_CODE:
				djsx_tv.setVisibility(View.VISIBLE);
				yanzhengma_iv.setImageBitmap(validCodeBitmap);
				break;
			case COMMIT_ORDER_MSG_CODE:
				if (commitReturnJson.length() == 0) {
					getValidCodePic();// 提交失败后需刷新验证码
					yanzhengma_input_et.setText("");
					progressdialog.dismiss();
					// new AlertDialog.Builder(context).setTitle("发生异常，订单提交失败！")
					// .setPositiveButton("确认", null).show();
					final CustomerAlertDialog cad = new CustomerAlertDialog(
							context, true);
					cad.setTitle("发生异常，订单提交失败");
					cad.setPositiveButton("知道了", new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							cad.dismiss();
						}
					});
					break;
				}
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(commitReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						String orderID = jsonObject.getJSONObject("d")
								.getString("msg");
						Intent intent = new Intent(context,
								ActivityTrainOrderDetail.class);
						intent.putExtra(ActivityTrainOrderDetail.ORDERRECEIPT,
								orderID);
						startActivityForResult(intent, NEW_ORDER_DETAIL_CODE);

					} else {
						// new AlertDialog.Builder(context)
						// .setTitle(
						// jsonObject.getJSONObject("d")
						// .getString("msg"))
						// .setPositiveButton("确定", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle(jsonObject.getJSONObject("d").getString(
								"msg"));
						cad.setPositiveButton("知道了", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						getValidCodePic();// 提交失败后需刷新验证码
						yanzhengma_input_et.setText("");
					}
					// else {
					// Toast.makeText(context, "发生异常，提交订单失败！", 0).show();
					// getValidCodePic();//提交失败后需刷新验证码
					// yanzhengma_input_et.setText("");
					// }
					progressdialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
					getValidCodePic();// 提交失败后需刷新验证码
					yanzhengma_input_et.setText("");
					progressdialog.dismiss();
				}
				break;
			}
		}
	};

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
							CONTANCT_REQUEST_CODE);
					break;
				case R.id.modify_seat_tv:
					finish();
					break;
				case R.id.djsx_tv:
				case R.id.yanzhengma_iv:
					getValidCodePic();
					break;
				case R.id.add_passager_rl:
					Intent intent = new Intent(context,
							ActivityInlandAirlineticketSelectPassengers.class);
					intent.putExtra(
							ActivityInlandAirlineticketSelectPassengers.SYSTYPE,
							"2");
					intent.putExtra(
							ActivityInlandAirlineticketSelectPassengers.TITLE_NAME,
							"选择旅客");
					intent.putExtra(ALLPASSENGERSLIST,
							JSONHelper.toJSON(allPassengerList));
					intent.putExtra(SELECTEDPASSENGERSLIST,
							JSONHelper.toJSON(passengerList));
					startActivityForResult(intent,
							ADD_PASSENGERS_FORRESULET_CODE);
					break;
				case R.id.baoxian_price_and_count_tv:
				case R.id.baoxian_check_imgbtn:
				case R.id.baoxian_rl:
					Intent intent_bx = new Intent(context,
							ActivityTrainBaoxian.class);
					intent_bx.putExtra(ActivityTrainBaoxian.BAOXIAN_BUNDSTRING,
							baoxian_price_and_count_tv.getText().toString()
									.trim());
					startActivityForResult(intent_bx, BAOXIAN_REQUEST_CODE);
					break;
				case R.id.order_now_btn:
					if (selectedSeatIndex == -1) {
						order_now_btn.setEnabled(false);
						break;
					}
					if (passengerList.size() == 0) {
						// new AlertDialog.Builder(context).setTitle("乘客不能为空")
						// .setMessage("请添加乘客信息！")
						// .setPositiveButton("确定", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("请添加乘客信息");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					} else if (remainTicketCount < passengerList.size()) {
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("当前仅剩余" + remainTicketCount + "张票，无法满足"
								+ passengerList.size() + "个人的预订需求");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}

					if (yanzhengma_input_et.getText().toString().length() != 4) {
						// new AlertDialog.Builder(context).setTitle("请输入验证码")
						// .setPositiveButton("确定", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("请输入验证码");
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
						cad.setTitle("手机号码格式不正确");
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
										SPkeys.trainContactPhone.getString(),
										contact_person_phone_et.getText()
												.toString()).commit();
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
					passengerList = ActivityInlandAirlineticketBooking
							.removeDuplictePassengers(passengerList);
					caculateMoney();
					if (passengerList.size() > 0) {
						add_passager_tv.setText("修改乘客");
						passenger_head_divid_line.setVisibility(View.VISIBLE);
					} else if (passengerList.size() == 0) {
						add_passager_tv.setText("新增乘客");
						passenger_head_divid_line.setVisibility(View.GONE);
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
			if (requestCode == CONTANCT_REQUEST_CODE) {// 联系人
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
			} else if (requestCode == BAOXIAN_REQUEST_CODE) {
				if (data == null)
					return;
				Bundle b = data.getExtras();
				if (b != null
						&& b.containsKey(ActivityTrainBaoxian.BAOXIAN_BUNDSTRING)) {
					String baoxian = b
							.getString(ActivityTrainBaoxian.BAOXIAN_BUNDSTRING);
					baoxian_price_and_count_tv.setText(baoxian);
					if (baoxian.equals(ActivityTrainBaoxian.No_Baoxian)) {
						baoxian_unitPrice = 0;
					} else if (baoxian
							.equals(ActivityTrainBaoxian.Baoxian_Five)) {
						baoxian_unitPrice = 5;
					} else if (baoxian.equals(ActivityTrainBaoxian.Baoxian_Ten)) {
						baoxian_unitPrice = 10;
					}
				}
				caculateMoney();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 旅客列表
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
						caculateMoney();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	Boolean hasSelected = false;// 是否有选中（模拟单选的效果）

	// 席别选择
	public class MyListAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		private LayoutInflater inflater;
		ArrayList<Seat> seats;
		Context c;
		int currentID = 0;

		public MyListAdapter(Context context, ArrayList<Seat> seats) {
			inflater = LayoutInflater.from(context);
			this.c = context;
			this.seats = seats;
		}

		public void setList(ArrayList<Seat> seats) {
			this.seats = seats;
		}

		@Override
		public int getCount() {
			return seats.size();
		}

		@Override
		public Object getItem(int position) {
			return seats.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder myHolder;
			try {
				if (convertView == null) {
					myHolder = new Holder();
					convertView = inflater.inflate(
							R.layout.item_train_xibie_list_single, null);
					myHolder.seat_grad_tv = (TextView) convertView
							.findViewById(R.id.seat_grad_tv);
					myHolder.ticket_price_tv = (TextView) convertView
							.findViewById(R.id.ticket_price_tv);
					myHolder.remain_count_tv = (TextView) convertView
							.findViewById(R.id.remain_count_tv);
					myHolder.iv = (ImageView) convertView
							.findViewById(R.id.img);
					convertView.setTag(myHolder);
				} else {
					myHolder = (Holder) convertView.getTag();
				}
				// myHolder.iv.setBackgroundResource((Integer)
				// list.get(position).get("img"));
				if (position == this.currentID
						&& !seats.get(position).getShengyu().equals("0")) {
					myHolder.iv.setBackgroundDrawable(c.getResources()
							.getDrawable(R.drawable.checkmark_icon_selected));
					hasSelected = true;
					selectedSeatIndex = position;
					// 票价格和剩余票数 随所选座位变化
					ticket_price = Float.valueOf(seats.get(selectedSeatIndex)
							.getPrice());
					remainTicketCount = Integer.valueOf(seats.get(
							selectedSeatIndex).getShengyu());
					seat_Type = seats.get(selectedSeatIndex).getType();
				} else if (!hasSelected
						&& !seats.get(position).getShengyu().equals("0")) {
					myHolder.iv.setBackgroundDrawable(c.getResources()
							.getDrawable(R.drawable.checkmark_icon_selected));
					hasSelected = true;
					selectedSeatIndex = position;
					ticket_price = Float.valueOf(seats.get(selectedSeatIndex)
							.getPrice());
					remainTicketCount = Integer.valueOf(seats.get(
							selectedSeatIndex).getShengyu());
					seat_Type = seats.get(selectedSeatIndex).getType();
				} else {
					myHolder.iv.setBackgroundDrawable(c.getResources()
							.getDrawable(R.drawable.checkmark_icon_unselected));
				}
				myHolder.seat_grad_tv.setText(seats.get(position).getType());
				myHolder.ticket_price_tv.setText("￥"
						+ seats.get(position).getPrice());

				if (seats.get(position).getShengyu().equals("40")) {
					myHolder.remain_count_tv.setText("票源充足 ");
				} else {
					myHolder.remain_count_tv.setText("余票"
							+ seats.get(position).getShengyu() + "张");
				}
				if (ti.getSeat_Type().equals(seats.get(position).getType())) {// 默认选中席别与前页列表中展现的相同
					selectedSeatIndex = currentID;
				}
				if (seats.get(position).getShengyu().equals("0")) {// 余票为0，不可预订
					myHolder.seat_grad_tv.setTextColor(getResources().getColor(
							R.color.gray));
					myHolder.ticket_price_tv.setTextColor(getResources()
							.getColor(R.color.gray));
					myHolder.remain_count_tv.setTextColor(getResources()
							.getColor(R.color.gray));
					myHolder.iv.setBackgroundDrawable(c.getResources()
							.getDrawable(R.drawable.radio));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}

		class Holder {
			ImageView iv;
			TextView seat_grad_tv, ticket_price_tv, remain_count_tv;
		}

		public void setCurrentID(int currentID) {
			this.currentID = currentID;
		}

	}

}
