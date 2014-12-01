package com.jike.shanglv;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jike.shanglv.Common.ClearEditText;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.NetAndJson.HttpUtils;


public class ActivityHotel extends Activity {

	private final int ruzhudate = 0, lidiandate = 1, ruzhucity = 2;
	private ImageButton back_imgbtn, home_imgbtn;
	private TextView city_tv, ruzhu_date_tv, lidian_date_tv, xingji_tv,
			jiage_tv;
	private com.jike.shanglv.Common.ClearEditText keywords_et;
	private LinearLayout city_ll, my_position_ll, ruzhu_date_ll,
			lidian_date_ll, xingji_ll, jiage_ll;
	private Button search_button;
	private Context context;
	InputMethodManager imm;
	private SharedPreferences sp;
	private Boolean isNearby = false;
	private double latitude, longtitude;
	private String myaddress = "";
	private int errorCode;// 定位结果
	private LocationClient mLocationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_hotel);
			initView();
			myNear();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void myNear() {
		mLocationClient.start();
		mLocationClient.requestLocation();
		city_tv.setText("我附近的酒店");
		isNearby = true;
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		mLocationClient = new LocationClient(this.getApplicationContext());
		mLocationClient.registerLocationListener(new MyLocationListener());
		InitLocation();

		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		city_tv = (TextView) findViewById(R.id.city_tv);
		ruzhu_date_tv = (TextView) findViewById(R.id.ruzhu_date_tv);
		lidian_date_tv = (TextView) findViewById(R.id.lidian_date_tv);
		xingji_tv = (TextView) findViewById(R.id.xingji_tv);
		jiage_tv = (TextView) findViewById(R.id.jiage_tv);
		keywords_et = (ClearEditText) findViewById(R.id.keywords_et);
		my_position_ll = (LinearLayout) findViewById(R.id.my_position_ll);
		ruzhu_date_ll = (LinearLayout) findViewById(R.id.ruzhu_date_ll);
		lidian_date_ll = (LinearLayout) findViewById(R.id.lidian_date_ll);
		xingji_ll = (LinearLayout) findViewById(R.id.xingji_ll);
		jiage_ll = (LinearLayout) findViewById(R.id.jiage_ll);
		city_ll = (LinearLayout) findViewById(R.id.city_ll);
		city_ll.setOnClickListener(clickListener);
		back_imgbtn.setOnClickListener(clickListener);
		home_imgbtn.setOnClickListener(clickListener);
		my_position_ll.setOnClickListener(clickListener);
		ruzhu_date_ll.setOnClickListener(clickListener);
		lidian_date_ll.setOnClickListener(clickListener);
		xingji_ll.setOnClickListener(clickListener);
		jiage_ll.setOnClickListener(clickListener);

		ruzhu_date_tv.setText(DateUtil.GetDateAfterToday(1));
		lidian_date_tv.setText(DateUtil.GetDateAfterToday(2));

		search_button = (Button) findViewById(R.id.chongzhi_button);
		search_button.setOnClickListener(clickListener);
	}

	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		// option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式:高精度
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000;
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为1000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {// Receive Location
			try {
				latitude = location.getLatitude();
				longtitude = location.getLongitude();
				myaddress = location.getAddrStr();
				errorCode = location.getLocType();
				// ((TextView)findViewById(R.id.test_tv)).setText(myaddress);
				if (errorCode == 63 || errorCode == 63 || errorCode == 67
						|| (errorCode > 500 && errorCode < 701)) {
					// Toast.makeText(context, "网络异常，自动定位失败", 0).show();
					city_tv.setText("上海");
					isNearby = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	View.OnClickListener clickListener = new View.OnClickListener() {
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
				case R.id.my_position_ll:
					myNear();
					break;
				case R.id.city_ll:
					Intent cityIntent = new Intent();
					cityIntent
							.setClass(
									context,
									com.jike.shanglv.SeclectCity.HotelCityActivity.class);
					startActivityForResult(cityIntent, ruzhucity);
					break;
				case R.id.ruzhu_date_ll:
					Intent dateIntent = new Intent();
					dateIntent.setClass(context,
							com.jike.shanglv.ShipCalendar.MainActivity.class);
					dateIntent.putExtra(
							com.jike.shanglv.ShipCalendar.MainActivity.TITLE,
							"入住日期");
					startActivityForResult(dateIntent, ruzhudate);
					break;
				case R.id.lidian_date_ll:
					Intent dateIntent1 = new Intent();
					dateIntent1.setClass(context,
							com.jike.shanglv.ShipCalendar.MainActivity.class);
					dateIntent1.putExtra(
							com.jike.shanglv.ShipCalendar.MainActivity.TITLE,
							"离店日期");
					startActivityForResult(dateIntent1, lidiandate);
					break;
				case R.id.xingji_ll:
					imm.hideSoftInputFromWindow(((Activity) context)
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					// popupWindow_xingji.showAtLocation(buxian_xingji_btn,Gravity.BOTTOM,
					// 0, 0);

					iniPopupWindow(0, initXingjiData());
					pwMyPopWindow.showAtLocation(search_button, Gravity.BOTTOM,
							0, 0);
					break;
				case R.id.jiage_ll:
					imm.hideSoftInputFromWindow(((Activity) context)
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					// popupWindow_jiage.showAtLocation(buxian_price_btn,Gravity.BOTTOM,
					// 0, 0);
					iniPopupWindow(1, initJiageData());
					pwMyPopWindow.showAtLocation(search_button, Gravity.BOTTOM,
							0, 0);
					break;
				case R.id.chongzhi_button:
					if (!sp.getBoolean(SPkeys.loginState.getString(), false)) {
						startActivity(new Intent(context, Activity_Login.class));
						break;
					}
					if (DateUtil.compareDateIsBefore(ruzhu_date_tv.getText()
							.toString(), lidian_date_tv.getText().toString())) {
						// new
						// AlertDialog.Builder(context).setTitle("入住日期不能大于离店日期")
						// .setPositiveButton("知道了", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("入住日期不能大于离店日期");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					if (HttpUtils.showNetCannotUse(context)) {
						break;
					}
					if (city_tv.getText().toString().equals("我附近的酒店")) {
						isNearby = true;
					}
					if (city_tv.getText().toString().equals("我附近的酒店")
							&& (myaddress == null || myaddress.equals(""))) {// 定位失败
						city_tv.setText("上海");
						isNearby = false;
						Toast.makeText(context, "定位失败，请选择城市进行查询", 0).show();
						break;
					}
					Intent intents = new Intent(context,
							ActivityHotelSearchlist.class);
					intents.putExtra("nearby", isNearby);
					intents.putExtra("latitude", latitude);
					intents.putExtra("longtitude", longtitude);
					intents.putExtra("myaddress", myaddress);
					intents.putExtra("city", city_tv.getText());
					intents.putExtra("ruzhu_date", ruzhu_date_tv.getText()
							.toString());
					intents.putExtra("lidian_date", lidian_date_tv.getText()
							.toString());
					intents.putExtra("starlevel", xingji_tv.getText()
							.toString());
					intents.putExtra("price", jiage_tv.getText().toString());
					intents.putExtra("keywords", keywords_et.getText()
							.toString());
					startActivity(intents);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/*
	 * 选择城市或日期后结果回显到界面
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (data == null)
				return;
			Bundle b = data.getExtras();
			String myDate = DateUtil.GetTodayDate();// 获取从com.jike.jikepart.ShipCalendar.MainActivity中回传的值
			switch (requestCode) {
			case ruzhudate:
				if (b != null && b.containsKey("pickedDate")) {
					myDate = b.getString("pickedDate");
					ruzhu_date_tv.setText(myDate);
					if (DateUtil.compareDateIsBefore(myDate, lidian_date_tv
							.getText().toString().trim())) {
						try {
							lidian_date_tv.setText(DateUtil
									.getSpecifiedDayAfter(myDate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			case lidiandate:
				if (b != null && b.containsKey("pickedDate")) {
					myDate = b.getString("pickedDate");
					lidian_date_tv.setText(myDate);
					if (DateUtil.compareDateIsBefore(ruzhu_date_tv.getText()
							.toString().trim(), myDate)) {
						try {
							if (DateUtil.IsMoreThanToday(myDate)) {
								ruzhu_date_tv.setText(DateUtil
										.getSpecifiedDayBefore(myDate));
							} else {
								ruzhu_date_tv.setText(DateUtil.GetTodayDate());
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			case ruzhucity:
				if (b != null && b.containsKey("pickedCity")) {
					String myCity = b.getString("pickedCity");
					city_tv.setText(myCity);
					isNearby = false;
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private PopupWindow pwMyPopWindow;// popupwindow
	private ListView lvPopupList;
	private int currentID_XJ = 0;
	private int currentID_JG = 0;

	/*
	 * xjOrJg 0:星级；1：价格
	 */
	private void iniPopupWindow(final int xjOrJg,
			final List<Map<String, Object>> list1) {
		final LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.popupwindow_list_select, null);
		lvPopupList = (ListView) layout.findViewById(R.id.lv_popup_list);
		pwMyPopWindow = new PopupWindow(layout);
		pwMyPopWindow.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件

		MyListAdapter adapter = new MyListAdapter(context, list1);
		adapter.setCurrentID(xjOrJg == 0 ? currentID_XJ : currentID_JG);
		lvPopupList.setAdapter(adapter);
		lvPopupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					if (xjOrJg == 0) {// 0:星级
						xingji_tv.setText(list1.get(position).get("title")
								.toString());
						currentID_XJ = position;
						pwMyPopWindow.dismiss();
					} else if (xjOrJg == 1) {// 1：价格
						jiage_tv.setText(list1.get(position).get("title")
								.toString());
						currentID_JG = position;
						pwMyPopWindow.dismiss();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// 控制popupwindow的宽度和高度自适应
		lvPopupList.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		pwMyPopWindow.setWidth(LayoutParams.FILL_PARENT);// lvPopupList.getMeasuredWidth()
		pwMyPopWindow.setHeight(LayoutParams.FILL_PARENT);// ((lvPopupList.getMeasuredHeight())*
															// list1.size());
		pwMyPopWindow.setAnimationStyle(R.style.AnimBottomPopup);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 控制popupwindow点击屏幕其他地方消失
		pwMyPopWindow.setBackgroundDrawable(dw);// (new BitmapDrawable());//
												// 设置背景图片，不能在布局中设置，要通过代码来设置
		pwMyPopWindow.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功，如上

		// 对弹出的全屏选择框添加OnTouchListener监听判断获取触屏位置，如果在listview外面则销毁弹出框
		layout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					View layout = inflater.inflate(
							R.layout.popupwindow_list_select, null);
					int height = lvPopupList.getTop();
					int y = (int) event.getY();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (y < height) {
							pwMyPopWindow.dismiss();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});
	}

	private ArrayList<Map<String, Object>> initXingjiData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "不限");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "五星级/豪华");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "四星级/高档");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "三星级/舒适");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "二星级及以下");
		list.add(map);
		return list;
	}

	private ArrayList<Map<String, Object>> initJiageData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "不限");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥150以下");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥150-￥300");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥301-￥450");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥451-￥600");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥601-￥1000");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥1000以上");
		list.add(map);
		return list;
	}

	private class MyListAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		private LayoutInflater inflater;
		List<Map<String, Object>> list;
		Context c;
		int currentID = 0;

		public MyListAdapter(Context context, List<Map<String, Object>> list2) {
			inflater = LayoutInflater.from(context);
			this.c = context;
			this.list = list2;
		}

		public void setList(ArrayList<Map<String, Object>> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				Holder myHolder;
				if (convertView == null) {
					myHolder = new Holder();
					convertView = inflater.inflate(
							R.layout.item_train_baoxian_list_single, null);
					myHolder.title = (TextView) convertView
							.findViewById(R.id.title);
					myHolder.iv = (ImageView) convertView
							.findViewById(R.id.img);
					convertView.setTag(myHolder);
				} else {
					myHolder = (Holder) convertView.getTag();
				}
				if (position == this.currentID)
					myHolder.iv.setBackgroundDrawable(c.getResources()
							.getDrawable(R.drawable.radio_clk));
				else
					myHolder.iv.setBackgroundDrawable(c.getResources()
							.getDrawable(R.drawable.radio));
				myHolder.title.setText(list.get(position).get("title")
						.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}

		class Holder {
			ImageView iv;
			TextView title;
		}

		public void setCurrentID(int currentID) {
			this.currentID = currentID;
		}
	}
}
