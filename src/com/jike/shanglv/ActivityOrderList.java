//订单列表
package com.jike.shanglv;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.Common.RefreshListView;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Enums.SingleOrDouble;
import com.jike.shanglv.Models.OrderList_AirlineTicket;
import com.jike.shanglv.Models.OrderList_Hotel;
import com.jike.shanglv.Models.OrderList_Phone;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;


public class ActivityOrderList extends Activity implements
		RefreshListView.IOnRefreshListener, RefreshListView.IOnLoadMoreListener {
	// action
	public static final String HOTEL_ORDERLIST = "hotelorderlist";// 酒店订单列表
	public static final String TRAIN_ORDERLIST = "trainorderlist";// 火车票订单列表
	public static final String PHONE_ORDERLIST = "phoneorderlist";// 手机充值订单列表
	public static final String FLIGHT_ORDERLIST = "flightorderlist";// 国内机票订单信息
	public static final String DEMAND_ORDERLIST = "demandorderlist";// 需求单订单列表
	public static final String INTFLIGHT_ORDERLIST = "intflightorderlistv2";// 国际机票列表

	public static final String ORDERID_TOKENNAME = "QUERY_WITH_ORDERID";
	public static final String ACTION_TOKENNAME = "ACTION_TOKENNAME";
	public static final String TITLE_TOKENNAME = "TITLE_TOKENNAME";

	private TextView singleline_tv, doubleline_tv, title;
	private ImageView scrollbar_iv;
	private ImageButton back_imgbtn, home_imgbtn;
	private com.jike.shanglv.Common.RefreshListView listview;
	private Context context;
	private float screenWidth;// 手机屏幕宽度
	private int bmpW;// 动画图片宽度
	private int offset = 0;// 动画图片偏移量
	private SingleOrDouble wayType = SingleOrDouble.singleWay; // 单程:一个月内 or
																// 往返：一个月前
	private SharedPreferences sp;
	private CustomProgressDialog progressdialog;
	private String actionName = "", orderlistReturnJson = "", startDate = "",
			endDate = "", orderID = "";
	private int pageSize = 20, pageIndex = 1, count = 0;// 页大小、当前页、返回的数据总条数
	private Adapter adapter;
	private ArrayList<OrderList_AirlineTicket> order_List_airlineticket;
	private ArrayList<OrderList_Hotel> order_List_hotel;
	private ArrayList<OrderList_Phone> order_List_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orderlist);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		order_List_airlineticket = new ArrayList<OrderList_AirlineTicket>();
		order_List_hotel = new ArrayList<OrderList_Hotel>();
		order_List_phone = new ArrayList<OrderList_Phone>();
		context = this;
		((MyApplication) getApplication()).addActivity(this);
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels; // 获取分辨率宽度

		scrollbar_iv = (ImageView) findViewById(R.id.scrollbar_iv);
		bmpW = BitmapFactory
				.decodeResource(getResources(), R.drawable.typeline).getWidth();// 获取图片宽度
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		offset = (int) ((screenWidth / 2 - bmpW) / 2);// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(0, 0);
		scrollbar_iv.setImageMatrix(matrix);// 设置动画初始位置

		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		title = (TextView) findViewById(R.id.title);
		listview = (com.jike.shanglv.Common.RefreshListView) findViewById(R.id.listview);
		listview.setOnRefreshListener(this);
		listview.setOnLoadMoreListener(this);

		singleline_tv = (TextView) findViewById(R.id.singleline_tv);
		doubleline_tv = (TextView) findViewById(R.id.doubleline_tv);
		singleline_tv.setOnClickListener(btnClickListner);
		doubleline_tv.setOnClickListener(btnClickListner);
		back_imgbtn.setOnClickListener(btnClickListner);
		home_imgbtn.setOnClickListener(btnClickListner);
		// 默认查一月内的数据
		startDate = DateUtil.GetDateAfterToday(-30);
		endDate = DateUtil.GetDateAfterToday(1);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(ACTION_TOKENNAME)) {
				actionName = bundle.getString(ACTION_TOKENNAME);
			}
			if (bundle.containsKey(ORDERID_TOKENNAME)) {
				orderID = bundle.getString(ORDERID_TOKENNAME);
			}
			if (bundle.containsKey(TITLE_TOKENNAME)) {
				title.setText(bundle.getString(TITLE_TOKENNAME));
			}
		}
		startQuery();
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// startQuery();
	// }

	View.OnClickListener btnClickListner = new View.OnClickListener() {
		@SuppressLint("ResourceAsColor")
		@Override
		public void onClick(View v) {
			try {
				Intent dateIntent = new Intent();
				dateIntent.setClass(context,
						com.jike.shanglv.ShipCalendar.MainActivity.class);
				Intent cityIntent = new Intent();
				cityIntent
						.setClass(
								context,
								com.jike.shanglv.SeclectCity.AirportCityActivity.class);
				int one = (int) ((screenWidth / 2) + 50);

				switch (v.getId()) {
				case R.id.singleline_tv:// 一月内
					// 页卡切换时原来的数据清空
					if (actionName.equals(FLIGHT_ORDERLIST)
							|| actionName.equals(DEMAND_ORDERLIST)
							|| actionName.equals(TRAIN_ORDERLIST)
							|| actionName.equals(INTFLIGHT_ORDERLIST)) {
						order_List_airlineticket.clear();
						if (order_List_airlineticket != null)
							((AirlineTicketListAdapter) adapter)
									.refreshData(order_List_airlineticket);
					} else if (actionName.equals(HOTEL_ORDERLIST)) {
						order_List_hotel.clear();
						if (order_List_hotel != null)
							((HotelListAdapter) adapter)
									.refreshData(order_List_hotel);
					} else if (actionName.equals(PHONE_ORDERLIST)) {
						order_List_phone.clear();
						if (order_List_phone != null)
							((PhoneListAdapter) adapter)
									.refreshData(order_List_phone);
					}

					wayType = SingleOrDouble.singleWay;
					singleline_tv.setTextColor(context.getResources().getColor(
							R.color.blue_title_color));
					doubleline_tv.setTextColor(context.getResources().getColor(
							R.color.black_txt_color));
					Animation animation = new TranslateAnimation(one, 0, 0, 0);
					animation.setFillAfter(true);// True:图片停在动画结束位置
					animation.setDuration(200);
					scrollbar_iv.startAnimation(animation);

					startDate = DateUtil.GetDateAfterToday(-30);
					endDate = DateUtil.GetDateAfterToday(1);
					startQuery();
					break;
				case R.id.doubleline_tv:// 一月前
					// 页卡切换时原来的数据清空
					if (actionName.equals(FLIGHT_ORDERLIST)
							|| actionName.equals(DEMAND_ORDERLIST)
							|| actionName.equals(TRAIN_ORDERLIST)
							|| actionName.equals(INTFLIGHT_ORDERLIST)) {
						order_List_airlineticket.clear();
						if (order_List_airlineticket != null)
							((AirlineTicketListAdapter) adapter)
									.refreshData(order_List_airlineticket);
					} else if (actionName.equals(HOTEL_ORDERLIST)) {
						order_List_hotel.clear();
						if (order_List_hotel != null)
							((HotelListAdapter) adapter)
									.refreshData(order_List_hotel);
					} else if (actionName.equals(PHONE_ORDERLIST)) {
						order_List_phone.clear();
						if (order_List_phone != null)
							((PhoneListAdapter) adapter)
									.refreshData(order_List_phone);
					}

					wayType = SingleOrDouble.doubleWayGo;
					singleline_tv.setTextColor(context.getResources().getColor(
							R.color.black_txt_color));
					doubleline_tv.setTextColor(context.getResources().getColor(
							R.color.blue_title_color));
					animation = new TranslateAnimation(offset, one, 0, 0);
					animation.setFillAfter(true);// True:图片停在动画结束位置
					animation.setDuration(200);
					scrollbar_iv.startAnimation(animation);
					startDate = "2000-01-01";
					endDate = DateUtil.GetDateAfterToday(-30);
					startQuery();
					break;
				case R.id.back_imgbtn:// 返回
					finish();
					break;
				case R.id.home_imgbtn:// 主页
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

	private void startQuery() {
		if (HttpUtils.showNetCannotUse(context)) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// type=1:查航班号 type=2:查航段， air 航空公司
					// action=flist&str={'s':'sha','e':hfe,'sd':'2014-01-28','userid':'649','siteid':'65'}
					MyApp ma = new MyApp(context);
					String str = "{\"orderID\":\"" + orderID + "\",\"tm1\":\""
							+ startDate + "\",\"tm2\":\"" + endDate
							+ "\",\"userID\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"pageSize\":\"" + pageSize
							+ "\",\"pageIndex\":\"" + pageIndex + "\"}";
					String signString = CommonFunc.MD5(ma.getHm()
							.get(PackageKeys.USERKEY.getString()).toString()
							+ actionName + str);
					try {// 解决获取数据时的400错误
						str = URLEncoder.encode(str, "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					String param = "action="
							+ actionName
							+ "&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString() + "&sitekey=" + MyApp.sitekey
							+ "&sign=" + signString;

					orderlistReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					// String param = "?action=" + actionName
					// + "&userkey=" +
					// ma.getHm().get(PackageKeys.USERKEY.getString()).toString()
					// +
					// "&sitekey="
					// + MyApp.sitekey + "&sign="
					// +
					// CommonFunc.MD5(ma.getHm().get(PackageKeys.USERKEY.getString()).toString()
					// + actionName + str);
					// orderlistReturnJson = HttpUtils.myPost(ma.getServeUrl() +
					// param,
					// "&str=" + str);
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在查询订单，请稍候...");
		progressdialog.setCancelable(true);
		progressdialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		progressdialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		startQuery();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				progressdialog.dismiss();
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(orderlistReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						count = Integer.parseInt(jsonObject
								.getString("recordcount"));
						JSONArray jsonArray = jsonObject.getJSONArray("d");
						if (jsonArray.length() == 0
								&& (order_List_airlineticket.size() == 0
										&& order_List_hotel.size() == 0 && order_List_phone
										.size() == 0)) {
							// new AlertDialog.Builder(context)
							// .setTitle("未查询到相关数据")
							// .setPositiveButton("确认", null).show();
							final CustomerAlertDialog cad = new CustomerAlertDialog(
									context, true);
							cad.setTitle("未查询到相关数据");
							cad.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									cad.dismiss();
								}
							});
							// 不能这么干，因为动态加载更多时，也会清除数据
							// if (actionName.equals(FLIGHT_ORDERLIST))
							// {//国内机票或需求单
							// order_List_airlineticket.clear();
							// }else if (actionName.equals(DEMAND_ORDERLIST))
							// {//需求单
							// order_List_airlineticket.clear();
							// }else if (actionName.equals(HOTEL_ORDERLIST)) {
							// order_List_hotel.clear();
							// }else if (actionName.equals(TRAIN_ORDERLIST))
							// {//火车票
							// order_List_airlineticket.clear();
							// }else if (actionName.equals(PHONE_ORDERLIST)) {
							// order_List_phone.clear();
							// }
						}
						createList(jsonArray);
						if (actionName.equals(FLIGHT_ORDERLIST)
								|| actionName.equals(DEMAND_ORDERLIST)
								|| actionName.equals(TRAIN_ORDERLIST)
								|| actionName.equals(INTFLIGHT_ORDERLIST)) {
							adapter = new AirlineTicketListAdapter(context,
									order_List_airlineticket);
						} else if (actionName.equals(HOTEL_ORDERLIST)) {
							adapter = new HotelListAdapter(context,
									order_List_hotel);
						} else if (actionName.equals(PHONE_ORDERLIST)) {
							adapter = new PhoneListAdapter(context,
									order_List_phone);
						}
						if (order_List_airlineticket.size() == Integer
								.valueOf(count)
								|| order_List_hotel.size() == Integer
										.valueOf(count)
								|| order_List_phone.size() == Integer
										.valueOf(count)) {
							listview.removeFootView();// 如果数据就几条一次就加载完了，移除查看更多
							listview.setOnRefreshListener(null);
							listview.setOnLoadMoreListener(null);// 禁用刷新功能
						}
						listview.setAdapter((ListAdapter) adapter);
						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								if (actionName.equals(FLIGHT_ORDERLIST)) {// 国内机票
									OrderList_AirlineTicket order = order_List_airlineticket
											.get(position - 1);
									Intent intent = new Intent(
											context,
											ActivityInlandAirlineticketOrderDetail.class);
									intent.putExtra(
											ActivityInlandAirlineticketOrderDetail.ORDERRECEIPT,
											order.getOrderID());
									// startActivityForResule(intent);
									startActivityForResult(intent, 1);
								} else if (actionName
										.equals(INTFLIGHT_ORDERLIST)) {// 国际机票
									OrderList_AirlineTicket order = order_List_airlineticket
											.get(position - 1);
									Intent intent = new Intent(
											context,
											ActivityInternationalAirlineticketOrderDetail.class);
									intent.putExtra(
											ActivityInternationalAirlineticketOrderDetail.ORDERRECEIPT,
											order.getOrderID());
									// startActivity(intent);
									startActivityForResult(intent, 1);
								} else if (actionName.equals(DEMAND_ORDERLIST)) {// 需求单
									OrderList_AirlineTicket order = order_List_airlineticket
											.get(position - 1);
								} else if (actionName.equals(HOTEL_ORDERLIST)) {// 酒店订单
									OrderList_Hotel order = order_List_hotel
											.get(position - 1);
									Intent intent = new Intent(context,
											ActivityHotelOrderDetail.class);
									intent.putExtra(
											ActivityHotelOrderDetail.ORDERRECEIPT,
											order.getOrderID());
									// startActivity(intent);
									startActivityForResult(intent, 1);
								} else if (actionName.equals(TRAIN_ORDERLIST)) {// 火车票
									OrderList_AirlineTicket order = order_List_airlineticket
											.get(position - 1);
									Intent intent = new Intent(context,
											ActivityTrainOrderDetail.class);
									intent.putExtra(
											ActivityTrainOrderDetail.ORDERRECEIPT,
											order.getOrderID());
									// startActivity(intent);
									startActivityForResult(intent, 1);
								} else if (actionName.equals(PHONE_ORDERLIST)) {// 话费充值
									OrderList_Phone order = order_List_phone
											.get(position - 1);
									String orderId = order.getOrderid();
									String money = order.getAmount();
									String userid = sp.getString(
											SPkeys.userid.getString(), "");
									if (!order.getStatus().equals("未付款"))
										return;
									int paysystype = 14;
									String siteid = sp.getString(
											SPkeys.siteid.getString(), "65");
									String sign = CommonFunc.MD5(orderId
											+ money + userid + paysystype
											+ siteid);
									MyApp ma = new MyApp(context);
									// <string
									// name="test_pay_server_url">http://gatewayceshi.51jp.cn/PayMent/BeginPay.aspx?orderID=%1$s&amp;amount=%2$s&amp;userid=%3$s&amp;paysystype=%4$s&amp;siteid=%5$s&amp;sign=%6$s</string>
									String url = String.format(
											ma.getPayServeUrl(), orderId,
											money, userid, paysystype, siteid,
											sign);
									Intent intent = new Intent(context,
											Activity_Web_Pay.class);
									intent.putExtra(Activity_Web_Pay.URL, url);
									intent.putExtra(Activity_Web_Pay.TITLE,
											"话费充值支付");
									// startActivity(intent);
									startActivityForResult(intent, 1);
								}
							}
						});
					} else {
						String message = jsonObject.getJSONObject("d")
								.getString("msg");
						// new AlertDialog.Builder(context).setTitle("查询失败")
						// .setMessage(message)
						// .setPositiveButton("确认", null).show();
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
				} catch (Exception e) {
					e.printStackTrace();
				}
				progressdialog.dismiss();
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
		if (actionName.equals(FLIGHT_ORDERLIST)) {// 国内机票
			for (int i = 0; i < flist_list.length(); i++) {
				try {
					OrderList_AirlineTicket inland = new OrderList_AirlineTicket(
							flist_list.getJSONObject(i), 1);
					order_List_airlineticket.add(inland);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (actionName.equals(INTFLIGHT_ORDERLIST)) {// 国际
			for (int i = 0; i < flist_list.length(); i++) {
				try {
					OrderList_AirlineTicket inland = new OrderList_AirlineTicket(
							flist_list.getJSONObject(i), 4);
					order_List_airlineticket.add(inland);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (actionName.equals(DEMAND_ORDERLIST)) {// 需求单
			for (int i = 0; i < flist_list.length(); i++) {
				try {
					OrderList_AirlineTicket inland = new OrderList_AirlineTicket(
							flist_list.getJSONObject(i), 2);
					order_List_airlineticket.add(inland);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (actionName.equals(HOTEL_ORDERLIST)) {
			for (int i = 0; i < flist_list.length(); i++) {
				try {
					OrderList_Hotel hotel = JSONHelper.parseObject(
							flist_list.getJSONObject(i), OrderList_Hotel.class);
					order_List_hotel.add(hotel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (actionName.equals(TRAIN_ORDERLIST)) {// 火车票
			for (int i = 0; i < flist_list.length(); i++) {
				try {
					OrderList_AirlineTicket train = new OrderList_AirlineTicket(
							flist_list.getJSONObject(i), 3);
					order_List_airlineticket.add(train);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (actionName.equals(PHONE_ORDERLIST)) {
			for (int i = 0; i < flist_list.length(); i++) {
				try {
					OrderList_Phone phone = JSONHelper.parseObject(
							flist_list.getJSONObject(i), OrderList_Phone.class);
					order_List_phone.add(phone);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		removeDuplicteOrders();
	}

	Comparator<OrderList_AirlineTicket> comparator_airline = new Comparator<OrderList_AirlineTicket>() {
		@SuppressWarnings("deprecation")
		@Override
		public int compare(OrderList_AirlineTicket s1,
				OrderList_AirlineTicket s2) {
			return DateUtil.isDateBefore(s1.getOrderTime(), s2.getOrderTime()) ? 1
					: -1;
			// return (int) (date1.getTime()-date2.getTime());
		}
	};
	Comparator<OrderList_Hotel> comparator_hotel = new Comparator<OrderList_Hotel>() {
		@Override
		public int compare(OrderList_Hotel s1, OrderList_Hotel s2) {
			return DateUtil.isDateBefore(s1.getOrderDate(), s2.getOrderDate()) ? 1
					: -1;
		}
	};
	Comparator<OrderList_Phone> comparator_phone = new Comparator<OrderList_Phone>() {
		@Override
		public int compare(OrderList_Phone s1, OrderList_Phone s2) {
			return DateUtil.isDateBefore(s1.getAddtime(), s2.getAddtime()) ? 1
					: -1;
		}
	};

	@SuppressLint("ResourceAsColor")
	private class AirlineTicketListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<OrderList_AirlineTicket> str;

		public AirlineTicketListAdapter(Context context,
				List<OrderList_AirlineTicket> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
			Collections.sort(str, comparator_airline);
		}

		public void refreshData(List<OrderList_AirlineTicket> data) {
			this.str = data;
			notifyDataSetChanged();
		}

		public void updateBitmap(List<OrderList_AirlineTicket> list1) {
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

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				if (convertView == null) {
					convertView = inflater.inflate(
							R.layout.item_orderlist_airlineticket, null);
				}
				TextView orderId_tv = (TextView) convertView
						.findViewById(R.id.orderId_tv);
				TextView startCity_tv = (TextView) convertView
						.findViewById(R.id.startCity_tv);
				TextView endCity_tv = (TextView) convertView
						.findViewById(R.id.endCity_tv);
				TextView price_tv = (TextView) convertView
						.findViewById(R.id.price_tv);
				TextView startoff_date_tv = (TextView) convertView
						.findViewById(R.id.startoff_date_tv);
				TextView state_tv = (TextView) convertView
						.findViewById(R.id.state_tv);

				orderId_tv.setText(str.get(position).getOrderID());
				try {
					startoff_date_tv.setText(DateUtil.getDate(str.get(position)
							.getStartOffDate()));
				} catch (Exception e) {
					e.printStackTrace();
					startoff_date_tv.setText(str.get(position)
							.getStartOffDate());// 国内机票直接返回2014-09-18的格式
				}
				startCity_tv.setText(str.get(position).getStartCity());
				endCity_tv.setText(str.get(position).getEndCity());
				price_tv.setText("￥" + str.get(position).getAmount());
				state_tv.setText(str.get(position).getOrderStatus());

				String red = "需补款  暂不能出票 不能出票  改签  不能出票(退款中) 不能出票(已退款) 不能取消 充值失败 未付款", green = "确认提交 后续支付 新订单  出票成功  退款成功 已受理  已入住  已确认 待入住 待出票 草稿单 充值成功 部分成功", blue = "出票中 退款中 取消中 已离店 待处理 待处理 已撤单 充值中", gray = "已取消 退票  已退款";
				if (red.contains(str.get(position).getOrderStatus())) {
					state_tv.setTextColor(getResources().getColor(R.color.red));
				} else if (green.contains(str.get(position).getOrderStatus())) {
					state_tv.setTextColor(getResources()
							.getColor(R.color.green));
				} else if (blue.contains(str.get(position).getOrderStatus())) {
					state_tv.setTextColor(getResources().getColor(
							R.color.state_blue));
				} else if (gray.contains(str.get(position).getOrderStatus())) {
					state_tv.setTextColor(getResources().getColor(R.color.gray));
				} else {
					state_tv.setTextColor(getResources().getColor(
							R.color.state_blue));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	private class HotelListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<OrderList_Hotel> str;

		public HotelListAdapter(Context context, List<OrderList_Hotel> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
			Collections.sort(str, comparator_hotel);
		}

		public void refreshData(List<OrderList_Hotel> data) {
			this.str = data;
			notifyDataSetChanged();
		}

		public void updateBitmap(List<OrderList_Hotel> list1) {
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

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				if (convertView == null) {
					convertView = inflater.inflate(
							R.layout.item_orderlist_hotel, null);
				}
				TextView orderId_tv = (TextView) convertView
						.findViewById(R.id.orderId_tv);
				TextView hotel_name_tv = (TextView) convertView
						.findViewById(R.id.hotel_name_tv);
				TextView bed_tv = (TextView) convertView
						.findViewById(R.id.bed_tv);
				TextView price_tv = (TextView) convertView
						.findViewById(R.id.price_tv);
				TextView ruzhu_date_tv = (TextView) convertView
						.findViewById(R.id.ruzhu_date_tv);
				TextView state_tv = (TextView) convertView
						.findViewById(R.id.state_tv);

				orderId_tv.setText(str.get(position).getOrderID());
				try {
					ruzhu_date_tv.setText(DateUtil.getDate(str.get(position)
							.getInDate()));
				} catch (Exception e) {
					ruzhu_date_tv.setText(str.get(position).getInDate());
				}
				hotel_name_tv.setText(str.get(position).getHotelName());
				bed_tv.setText(str.get(position).getRoomName());
				price_tv.setText("￥" + str.get(position).getOrderAmount());
				state_tv.setText(str.get(position).getOrderStatus());

				String red = "需补款  暂不能出票 不能出票  改签  不能出票(退款中) 不能出票(已退款) 不能取消 充值失败 未付款", green = "确认提交 后续支付 新订单  出票成功  退款成功 已受理  已入住  已确认 待入住 待出票 草稿单 充值成功 部分成功", blue = "出票中 退款中 取消中 已离店 待处理 待处理 已撤单 充值中", gray = "已取消 退票  已退款";
				if (red.contains(str.get(position).getOrderStatus())) {
					state_tv.setTextColor(getResources().getColor(R.color.red));
				} else if (green.contains(str.get(position).getOrderStatus())) {
					state_tv.setTextColor(getResources()
							.getColor(R.color.green));
				} else if (blue.contains(str.get(position).getOrderStatus())) {
					state_tv.setTextColor(getResources().getColor(
							R.color.state_blue));
				} else if (gray.contains(str.get(position).getOrderStatus())) {
					state_tv.setTextColor(getResources().getColor(R.color.gray));
				} else {
					state_tv.setTextColor(getResources().getColor(
							R.color.state_blue));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	private class PhoneListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<OrderList_Phone> str;

		public PhoneListAdapter(Context context, List<OrderList_Phone> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
			Collections.sort(str, comparator_phone);
		}

		public void refreshData(List<OrderList_Phone> data) {
			this.str = data;
			notifyDataSetChanged();
		}

		public void updateBitmap(List<OrderList_Phone> list1) {
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

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				if (convertView == null) {
					convertView = inflater.inflate(
							R.layout.item_orderlist_phone, null);
				}
				TextView phone_num_tv = (TextView) convertView
						.findViewById(R.id.phone_num_tv);
				TextView price_tv = (TextView) convertView
						.findViewById(R.id.price_tv);
				TextView chongzhi_time_tv = (TextView) convertView
						.findViewById(R.id.chongzhi_time_tv);
				TextView state_tv = (TextView) convertView
						.findViewById(R.id.state_tv);

				phone_num_tv.setText(str.get(position).getPhone());
				chongzhi_time_tv.setText(str.get(position).getAddtime());
				price_tv.setText("￥" + str.get(position).getAmount());
				state_tv.setText(str.get(position).getStatus());

				String red = "需补款  暂不能出票 不能出票  改签  不能出票(退款中) 不能出票(已退款) 不能取消 充值失败 未付款", green = "确认提交 后续支付 新订单  出票成功  退款成功 已受理  已入住  已确认 待入住 待出票 草稿单 充值成功 部分成功", blue = "出票中 退款中 取消中 已离店 待处理 待处理 已撤单 充值中", gray = "已取消 退票  已退款";
				if (red.contains(str.get(position).getStatus())) {
					state_tv.setTextColor(getResources().getColor(R.color.red));
				} else if (green.contains(str.get(position).getStatus())) {
					state_tv.setTextColor(getResources()
							.getColor(R.color.green));
				} else if (blue.contains(str.get(position).getStatus())) {
					state_tv.setTextColor(getResources().getColor(
							R.color.state_blue));
				} else if (gray.contains(str.get(position).getStatus())) {
					state_tv.setTextColor(getResources().getColor(R.color.gray));
				} else {
					state_tv.setTextColor(getResources().getColor(
							R.color.state_blue));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	@Override
	public void OnLoadMore() {
		LoadMoreDataAsynTask mLoadMoreAsynTask = new LoadMoreDataAsynTask();
		mLoadMoreAsynTask.execute();
	}

	@Override
	public void OnRefresh() {
		RefreshDataAsynTask mRefreshAsynTask = new RefreshDataAsynTask();
		mRefreshAsynTask.execute();
	}

	class RefreshDataAsynTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(3000);
				pageIndex++;
				startQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (actionName.equals(FLIGHT_ORDERLIST)
					|| actionName.equals(DEMAND_ORDERLIST)
					|| actionName.equals(TRAIN_ORDERLIST)) {
				if (order_List_airlineticket != null)
					((AirlineTicketListAdapter) adapter)
							.refreshData(order_List_airlineticket);
			} else if (actionName.equals(HOTEL_ORDERLIST)) {
				if (order_List_hotel != null)
					((HotelListAdapter) adapter).refreshData(order_List_hotel);
			} else if (actionName.equals(PHONE_ORDERLIST)) {
				if (order_List_phone != null)
					((PhoneListAdapter) adapter).refreshData(order_List_phone);
			}
			listview.onRefreshComplete();
		}
	}

	class LoadMoreDataAsynTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(3000);
				pageIndex++;
				startQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				if (actionName.equals(FLIGHT_ORDERLIST)
						|| actionName.equals(DEMAND_ORDERLIST)
						|| actionName.equals(TRAIN_ORDERLIST)) {
					((AirlineTicketListAdapter) adapter)
							.refreshData(order_List_airlineticket);
					if (order_List_airlineticket.size() == Integer
							.valueOf(count)) {
						listview.onLoadMoreComplete(true);
					} else {
						listview.onLoadMoreComplete(false);
					}
				} else if (actionName.equals(HOTEL_ORDERLIST)) {
					((HotelListAdapter) adapter).refreshData(order_List_hotel);
					if (order_List_hotel.size() == Integer.valueOf(count)) {
						listview.onLoadMoreComplete(true);
					} else {
						listview.onLoadMoreComplete(false);
					}
				} else if (actionName.equals(PHONE_ORDERLIST)) {
					((PhoneListAdapter) adapter).refreshData(order_List_phone);
					if (order_List_phone.size() == Integer.valueOf(count)) {
						listview.onLoadMoreComplete(true);
					} else {
						listview.onLoadMoreComplete(false);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 去除重复的订单
	private void removeDuplicteOrders() {
		Set<OrderList_AirlineTicket> s = new TreeSet<OrderList_AirlineTicket>(
				new Comparator<OrderList_AirlineTicket>() {
					@Override
					public int compare(OrderList_AirlineTicket o1,
							OrderList_AirlineTicket o2) {
						return o1.getOrderID().compareTo(o2.getOrderID());
					}
				});
		s.addAll(order_List_airlineticket);
		order_List_airlineticket = new ArrayList<OrderList_AirlineTicket>(s);

		Set<OrderList_Hotel> s1 = new TreeSet<OrderList_Hotel>(
				new Comparator<OrderList_Hotel>() {
					@Override
					public int compare(OrderList_Hotel o1, OrderList_Hotel o2) {
						return o1.getOrderID().compareTo(o2.getOrderID());
					}
				});
		s1.addAll(order_List_hotel);
		order_List_hotel = new ArrayList<OrderList_Hotel>(s1);

		Set<OrderList_Phone> s2 = new TreeSet<OrderList_Phone>(
				new Comparator<OrderList_Phone>() {
					@Override
					public int compare(OrderList_Phone o1, OrderList_Phone o2) {
						return o1.getOrderid().compareTo(o2.getOrderid());
					}
				});
		s2.addAll(order_List_phone);
		order_List_phone = new ArrayList<OrderList_Phone>(s2);
	}
}