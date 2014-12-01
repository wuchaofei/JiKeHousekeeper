package com.jike.shanglv;

//测试F:\android\android-sdk\.android\debug.keystore的SHA1： 2D:31:8D:0F:56:E4:DD:92:9D:90:A3:97:2B:22:FA:09:59:1D:6D:98
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.RefreshListView;
import com.jike.shanglv.Common.StarLevel;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.LazyList.ImageLoader;
import com.jike.shanglv.Models.Hotel;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ActivityHotelSearchlist extends Activity implements
		RefreshListView.IOnRefreshListener, RefreshListView.IOnLoadMoreListener {

	protected static final int FILTER_REQUEST_CODE = 0;
	private Context context;
	private ImageButton back_imgbtn;
	private ImageView sort_arrow_price_iv, sort_arrow_pingfen_iv,
			sort_arrow_starlevel_iv;
	private TextView list_map_tv, title_tv, sort_price_tv, shaixuan_tv,
			sort_pingfen_tv, sort_starlevel_tv;
	private LinearLayout byprice_LL, shaixuan_LL, pingfen_LL, bystarlevel_ll;
	private RefreshListView listview;
	private SharedPreferences sp;
	private CustomProgressDialog progressdialog;
	private Boolean byPriceAsc = false, byPingfenAsc = false, bystar = false;
	private String nearbyReturnJson, hotelsReturnJson = "", city = "",
			ruzhu_date = "", lidian_date = "", starlevel = "", price = "",
			keywords = "";// 返回的查询列表json
	private ListAdapter adapter;
	private ArrayList<Hotel> reqdata_List;
	private JSONArray listArray;

	int pgsize = 20, pgindex = 1, reqdata_List_size = 0;// 页大小、待请求的页,数据列表长度
	String minprice = "", maxprice = "", star = "",// 价格区间，星级54321
			totalput = "", totalpg = "";

	DisplayImageOptions options; // 配置图片加载及显示选项
	private double latitude, longtitude;
	private String myaddress;
	private Boolean isNearby = false;

	MapView mMapView = null;
	BaiduMap mBaidumap = null;
	Hotel maphotel;// 酒店地图中，标注的酒店信息，使用全局变量以便事件处理

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			SDKInitializer.initialize(getApplicationContext());
			setContentView(R.layout.activity_hotel_searchlist);
			initView();
			startQuery();
		} catch (Exception e) {
			// TODO: handle exception
		}
		((MyApplication) getApplication()).addActivity(this);
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);

		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaidumap = mMapView.getMap();

		reqdata_List = new ArrayList<Hotel>();
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		list_map_tv = (TextView) findViewById(R.id.list_map_tv);
		listview = (RefreshListView) findViewById(R.id.listview);
		listview.setOnRefreshListener(this);
		listview.setOnLoadMoreListener(this);

		sort_arrow_price_iv = (ImageView) findViewById(R.id.sort_arrow_price_iv);
		sort_arrow_pingfen_iv = (ImageView) findViewById(R.id.sort_arrow_pingfen_iv);
		sort_arrow_starlevel_iv = (ImageView) findViewById(R.id.sort_arrow_starlevel_iv);

		title_tv = (TextView) findViewById(R.id.title_tv);
		sort_price_tv = (TextView) findViewById(R.id.sort_price_tv);
		shaixuan_tv = (TextView) findViewById(R.id.shaixuan_tv);
		sort_pingfen_tv = (TextView) findViewById(R.id.sort_pingfen_tv);
		sort_starlevel_tv = (TextView) findViewById(R.id.sort_starlevel_tv);
		byprice_LL = (LinearLayout) findViewById(R.id.byprice_LL);
		back_imgbtn.setOnClickListener(btnClickListner);
		list_map_tv.setOnClickListener(btnClickListner);
		byprice_LL.setOnClickListener(btnClickListner);
		shaixuan_LL = (LinearLayout) findViewById(R.id.shaixuan_LL);
		pingfen_LL = (LinearLayout) findViewById(R.id.pingfen_LL);
		bystarlevel_ll = (LinearLayout) findViewById(R.id.bystarlevel_ll);
		shaixuan_LL.setOnClickListener(btnClickListner);
		pingfen_LL.setOnClickListener(btnClickListner);
		bystarlevel_ll.setOnClickListener(btnClickListner);

		getIntentData();
	}

	// 获取Intent数据,并给到页面和做搜索数据使用
	private void getIntentData() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("nearby"))
				isNearby = bundle.getBoolean("nearby");
			if (bundle.containsKey("latitude"))
				latitude = bundle.getDouble("latitude");
			if (bundle.containsKey("longtitude"))
				longtitude = bundle.getDouble("longtitude");
			if (bundle.containsKey("myaddress"))
				myaddress = bundle.getString("myaddress");
			if (bundle.containsKey("city"))
				city = bundle.getString("city");
			if (bundle.containsKey("ruzhu_date"))
				ruzhu_date = bundle.getString("ruzhu_date");// 入住日期
			if (bundle.containsKey("lidian_date"))
				lidian_date = bundle.getString("lidian_date");
			if (bundle.containsKey("starlevel"))
				starlevel = bundle.getString("starlevel");// 星级
			if (bundle.containsKey("price"))
				price = bundle.getString("price");
			if (bundle.containsKey("keywords"))
				keywords = bundle.getString("keywords");
			star = StarLevel.StarlevelReverse.get(starlevel);
			if (star == null || star.equals("null"))
				star = "";
			if (price.equals("不限") || price == null || price.equals("null")) {
				minprice = "";
				maxprice = "";
			} else if (price.equals("￥150以下")) {
				minprice = "";
				maxprice = "150";
			} else if (price.equals("￥150-￥300")) {
				minprice = "150";
				maxprice = "300";
			} else if (price.equals("￥301-￥450")) {
				minprice = "301";
				maxprice = "450";
			} else if (price.equals("￥451-￥600")) {
				minprice = "451";
				maxprice = "600";
			} else if (price.equals("￥601-￥1000")) {
				minprice = "601";
				maxprice = "1000";
			} else if (price.equals("￥1000以上")) {
				minprice = "1000";
				maxprice = "";
			}
		}
		TextView my_address_tv = ((TextView) findViewById(R.id.my_address_tv));
		if (isNearby) {
			my_address_tv.setVisibility(View.VISIBLE);
			my_address_tv.setText("您的位置   " + myaddress);
		} else {
			my_address_tv.setVisibility(View.GONE);
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:// 酒店列表
				JSONTokener jsonParser;
				if (hotelsReturnJson.equals("")) {
					// new AlertDialog.Builder(context).setTitle("未查询到数据")
					// .setPositiveButton("确定", null).show();
					final CustomerAlertDialog cad = new CustomerAlertDialog(
							context, true);
					cad.setTitle("未查询到数据");
					cad.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							cad.dismiss();
						}
					});
					progressdialog.dismiss();
					break;
				}
				jsonParser = new JSONTokener(hotelsReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						if (jsonObject.getJSONObject("d").length() == 0) {
							// new
							// AlertDialog.Builder(context).setTitle("未查询到数据")
							// .setPositiveButton("确定", null).show();
							final CustomerAlertDialog cad = new CustomerAlertDialog(
									context, true);
							cad.setTitle("未查询到数据");
							cad.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									cad.dismiss();
								}
							});
							progressdialog.dismiss();
							break;
						}
						jsonObject = jsonObject.getJSONObject("d");
						totalput = jsonObject.getString("totalput");
						title_tv.setText("共" + totalput + "家");
						totalpg = jsonObject.getString("totalpg");
						listArray = jsonObject.getJSONArray("reqdata");
						reqdata_List_size = reqdata_List.size();
						createList(listArray);
						// reqdata_List=filterData(reqdata_List);
						adapter = new ListAdapter(context, reqdata_List);
						listview.setAdapter(adapter);
						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								Hotel ht = reqdata_List.get(position - 1);
								Intent intents = new Intent(context,
										ActivityHotelDetail.class);
								intents.putExtra("hotelId", ht.getID());
								intents.putExtra("ruzhu_date", ruzhu_date);
								intents.putExtra("lidian_date", lidian_date);
								startActivity(intents);
							}
						});
					} else {
						String message = jsonObject.getString("msg");
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
				progressdialog.dismiss();
				mapSign();
				// filterData();
				break;
			case 2:// 附近的酒店列表
				if (nearbyReturnJson.equals("")) {
					// new AlertDialog.Builder(context).setTitle("未查询到数据")
					// .setPositiveButton("确定", null).show();
					final CustomerAlertDialog cad = new CustomerAlertDialog(
							context, true);
					cad.setTitle("查询失败");
					cad.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							cad.dismiss();
						}
					});
					progressdialog.dismiss();
					break;
				}
				jsonParser = new JSONTokener(nearbyReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						if (jsonObject.getJSONObject("d").length() == 0) {
							// new
							// AlertDialog.Builder(context).setTitle("未查询到数据")
							// .setPositiveButton("确定", null).show();
							final CustomerAlertDialog cad = new CustomerAlertDialog(
									context, true);
							cad.setTitle("未查询到数据");
							cad.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									cad.dismiss();
								}
							});
							progressdialog.dismiss();
							break;
						}
						jsonObject = jsonObject.getJSONObject("d");
						totalput = jsonObject.getString("totalput");
						title_tv.setText("共" + totalput + "家");
						totalpg = jsonObject.getString("totalpg");
						listArray = jsonObject.getJSONArray("reqdata");
						reqdata_List_size = reqdata_List.size();
						createList(listArray);
						// reqdata_List=filterData(reqdata_List);
						adapter = new ListAdapter(context, reqdata_List);
						listview.setAdapter(adapter);
						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								Hotel ht = reqdata_List.get(position - 1);
								Intent intents = new Intent(context,
										ActivityHotelDetail.class);
								intents.putExtra("hotelId", ht.getID());
								intents.putExtra("ruzhu_date", ruzhu_date);
								intents.putExtra("lidian_date", lidian_date);
								startActivity(intents);
							}
						});
					} else {
						String message = jsonObject.getString("msg");
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
				mapSign();
				// filterData();// 附件的酒店返回的数据未按照搜索条件返回，返回后过滤一遍，达到按搜索条件查询的效果
				break;
			}
		}
	};

	/**
	 * 构建list对象
	 * @param reqdata
	 */
	private void createList(JSONArray reqdata) {
		for (int i = 0; i < reqdata.length(); i++) {
			try {
				Hotel hotel = JSONHelper.parseObject(reqdata.getJSONObject(i),
						Hotel.class);
				hotel.setHaoping(getScore(hotel.getHaoping()));// 将haoping字段替换成评分，以便排序、显示
				reqdata_List.add(hotel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getScore(String haoping) {// 156$23$1
		Float score = 0f;
		// String[] s=haoping.split("$");
		try {
			String hp = haoping.substring(0, haoping.indexOf("$"));
			String zp = haoping.substring(haoping.indexOf("$") + 1,
					haoping.lastIndexOf("$"));
			String cp = haoping.substring(haoping.lastIndexOf("$") + 1);
			if (Float.valueOf(hp) + Float.valueOf(zp) + Float.valueOf(cp) == 0f) {
				score = -1f;
			} else
				score = 10
						* Float.valueOf(hp)
						/ (Float.valueOf(hp) + Float.valueOf(zp) + Float
								.valueOf(cp));
		} catch (Exception e) {
			e.printStackTrace();
		}
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.#");
		return df.format(score);
	}

	Comparator<Hotel> comparator_pingfen_asc = new Comparator<Hotel>() {
		@Override
		public int compare(Hotel s1, Hotel s2) {
			if (!s1.getHaoping().equals(s2.getHaoping())) {
				return Float.valueOf(s1.getHaoping()) < Float.valueOf((s2
						.getHaoping())) ? 1 : -1;
			} else
				return 0;
		}
	};
	Comparator<Hotel> comparator_pingfen_desc = new Comparator<Hotel>() {
		@Override
		public int compare(Hotel s1, Hotel s2) {
			if (!s1.getHaoping().equals(s2.getHaoping())) {
				return Float.valueOf(s1.getHaoping()) > Float.valueOf((s2
						.getHaoping())) ? 1 : -1;
			} else
				return 0;
		}
	};
	Comparator<Hotel> comparator_price_asc = new Comparator<Hotel>() {
		@Override
		public int compare(Hotel s1, Hotel s2) {
			if (!s1.getPrice().equals(s2.getPrice())) {
				return Integer.valueOf(s2.getPrice())
						- Integer.valueOf((s1.getPrice()));
			} else
				return 0;
		}
	};
	Comparator<Hotel> comparator_price_desc = new Comparator<Hotel>() {
		@Override
		public int compare(Hotel s1, Hotel s2) {
			if (!s1.getPrice().equals(s2.getPrice())) {
				return Integer.valueOf(s1.getPrice())
						- Integer.valueOf((s2.getPrice()));
			} else
				return 0;
		}
	};
	Comparator<Hotel> comparator_starlevel_asc = new Comparator<Hotel>() {
		@Override
		public int compare(Hotel s1, Hotel s2) {
			if (!s1.getStar().equals(s2.getStar())) {
				return Integer.valueOf(s2.getStar())
						- Integer.valueOf((s1.getStar()));
			} else
				return 0;
		}
	};
	Comparator<Hotel> comparator_starlevel_desc = new Comparator<Hotel>() {
		@Override
		public int compare(Hotel s1, Hotel s2) {
			if (!s1.getStar().equals(s2.getStar())) {
				return Integer.valueOf(s1.getStar())
						- Integer.valueOf((s2.getStar()));
			} else
				return 0;
		}
	};

	private void startQuery() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// action=hlist&sign=c12b01b80e4b2674229dd71a48b5af36&userkey=2bfc0c48923cf89de19f6113c127ce81&sitekey=defage
					// &str={'city':'上海','pgsize':'10','pgindex':'1','hn':'','key':'','yufu':'','esdid':'','minprice':'','maxprice':'','lsid':'','areid':'','star':'','fw':''}
					String strEm = "";
					MyApp ma = new MyApp(context);
					Message msg = new Message();
					String str1 = "";
					// URLEncoder.encode(city, "utf-8")
					str1 = "{\"city\":\"" + city + "\",\"pgsize\":\"" + pgsize
							+ "\",\"pgindex\":\"" + pgindex + "\",\"hn\":\""
							+ keywords + "\",\"key\":\"" + strEm
							+ "\",\"yufu\":\"" + strEm + "\",\"esdid\":\""
							+ strEm + "\",\"minprice\":\"" + minprice
							+ "\",\"maxprice\":\"" + maxprice
							+ "\",\"lsid\":\"" + strEm + "\",\"areid\":\""
							+ strEm + "\",\"star\":\"" + star
							+ "\",\"fw\":\"\"}";
					String param1 = "";
					try {
						param1 = "action=hlist&str="
								+ URLEncoder.encode(str1, "utf-8")
								+ "&userkey="
								+ ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
								+ "&sitekey="
								+ MyApp.sitekey
								+ "&sign="
								+ CommonFunc.MD5(ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
										+ "hlist" + str1);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String str2 = "";
					str2 = "{\"lng\":\"" + longtitude + "\",\"pagesize\":\""
							+ pgsize + "\",\"pg\":\"" + pgindex
							+ "\",\"lat\":\"" + latitude + "\"}";
					String param2 = "action=nearby&str="
							+ str2
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sitekey="
							+ MyApp.sitekey
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "nearby" + str2);
					if (!isNearby) {
						hotelsReturnJson = HttpUtils.getJsonContent(
								ma.getServeUrl(), param1);
						msg.what = 1;
					} else {
						nearbyReturnJson = HttpUtils.getJsonContent(
								ma.getServeUrl(), param2);
						msg.what = 2;
					}
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在查询酒店列表，请稍候...");
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
				case R.id.byprice_LL:
					sort_price_tv.setSelected(true);
					sort_arrow_price_iv.setSelected(true);
					sort_pingfen_tv.setSelected(false);
					sort_arrow_pingfen_iv.setSelected(false);
					sort_starlevel_tv.setSelected(false);
					sort_arrow_starlevel_iv.setSelected(false);
					byPriceAsc = !byPriceAsc;
					if (byPriceAsc) {
						sort_arrow_price_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_up));
						Collections.sort(reqdata_List, comparator_price_desc);
						adapter.notifyDataSetChanged();
					} else {
						sort_arrow_price_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_down));
						Collections.sort(reqdata_List, comparator_price_asc);
						adapter.notifyDataSetChanged();
					}
					break;
				case R.id.pingfen_LL:
					sort_price_tv.setSelected(false);
					sort_arrow_price_iv.setSelected(false);
					sort_pingfen_tv.setSelected(true);
					sort_arrow_pingfen_iv.setSelected(true);
					sort_starlevel_tv.setSelected(false);
					sort_arrow_starlevel_iv.setSelected(false);
					byPingfenAsc = !byPingfenAsc;
					if (byPingfenAsc) {
						sort_arrow_pingfen_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_up));
						Collections.sort(reqdata_List, comparator_pingfen_desc);
						adapter.notifyDataSetChanged();
					} else {
						sort_arrow_pingfen_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_down));
						Collections.sort(reqdata_List, comparator_pingfen_asc);
						adapter.notifyDataSetChanged();
					}
					break;
				case R.id.bystarlevel_ll:
					sort_price_tv.setSelected(false);
					sort_arrow_price_iv.setSelected(false);
					sort_pingfen_tv.setSelected(false);
					sort_arrow_pingfen_iv.setSelected(false);
					sort_starlevel_tv.setSelected(true);
					sort_arrow_starlevel_iv.setSelected(true);
					bystar = !bystar;
					if (bystar) {
						sort_arrow_starlevel_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_up));
						Collections.sort(reqdata_List,
								comparator_starlevel_desc);
						adapter.notifyDataSetChanged();
					} else {
						sort_arrow_starlevel_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_down));
						Collections
								.sort(reqdata_List, comparator_starlevel_asc);
						adapter.notifyDataSetChanged();
					}
					break;
				case R.id.back_imgbtn:
					if (list_map_tv.getText().toString().equals("列表")) {
						mMapView.setVisibility(View.GONE);
						list_map_tv.setText("地图");
					} else if (list_map_tv.getText().toString().equals("地图")) {
						finish();
					}
					break;
				case R.id.list_map_tv:
					if (list_map_tv.getText().toString().equals("地图")) {
						mMapView.setVisibility(View.VISIBLE);
						list_map_tv.setText("列表");
					} else if (list_map_tv.getText().toString().equals("列表")) {
						mMapView.setVisibility(View.GONE);
						list_map_tv.setText("地图");
					}
					break;
				case R.id.shaixuan_LL:
					startActivityForResult(new Intent(context,
							ActivityHotelFilter.class), FILTER_REQUEST_CODE);
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
			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == FILTER_REQUEST_CODE) {// 筛选结果返回
				if (data == null) {
					return;
				}
				Bundle b = data.getExtras();
				if (b == null || !b.containsKey("filterdDate")) {
					return;
				} else {
					b = b.getBundle("filterdDate");
				}
				if (b != null && b.containsKey("minprice")) {
					minprice = b.getString("minprice");
				}
				if (b != null && b.containsKey("maxprice")) {
					maxprice = b.getString("maxprice");
				}
				if (b != null && b.containsKey("star")) {
					star = b.getString("star");
				}
				if (b != null && b.containsKey("keywords")) {
					keywords = b.getString("keywords");
				}
				// adapter.updateListView(filterData(reqdata_List));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 按条件筛选数据
	 */
	private ArrayList<Hotel> filterData(ArrayList<Hotel> reqdata_List) {
		ArrayList<Hotel> filterDateList2 = new ArrayList<Hotel>();
		try {
			ArrayList<Hotel> filterDateList = new ArrayList<Hotel>();
			if (keywords.isEmpty()) {
				filterDateList = reqdata_List;
			} else {
				filterDateList.clear();
				for (int i = 0; i < reqdata_List.size(); i++) {
					Hotel hotel = reqdata_List.get(i);
					if (hotel.getAddress() != null
							&& hotel.getAddress().indexOf(keywords) != -1
							|| hotel.getCBD() != null
							&& hotel.getCBD().indexOf(keywords) != -1
							|| hotel.getName() != null
							&& hotel.getName().indexOf(keywords) != -1
							|| hotel.getService() != null
							&& hotel.getService().indexOf(keywords) != -1) {
						filterDateList.add(hotel);
					}
				}
			}
			ArrayList<Hotel> filterDateList1 = new ArrayList<Hotel>();
			if (filterDateList.size() > 0) {
				float min = Float.valueOf(minprice.isEmpty() ? "0" : minprice);
				float max = Float.valueOf(maxprice.isEmpty() ? "999999"
						: maxprice);
				for (int i = 0; i < filterDateList.size(); i++) {
					Hotel hotel = filterDateList.get(i);
					if ((hotel.getPrice() != null || !hotel.getPrice()
							.isEmpty())
							&& Float.valueOf(hotel.getPrice()) > min
							&& Float.valueOf(hotel.getPrice()) < max) {
						filterDateList1.add(hotel);
					}
				}
			}
			if (filterDateList1.size() > 0 && !star.isEmpty()) {
				for (int i = 0; i < filterDateList1.size(); i++) {
					Hotel hotel = filterDateList1.get(i);
					if (hotel.getStar().equals(star)) {
						filterDateList2.add(hotel);
					}
				}
			} else {
				filterDateList2 = filterDateList1;
			}
			// adapter.updateListView(filterDateList2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filterDateList2;
	}

	private void mapSign() {
		try {
			// 设定中心点坐标
			LatLng cenpt = new LatLng(latitude, longtitude);
			// 定义地图状态
			MapStatus mMapStatus = new MapStatus.Builder().target(cenpt)
					.zoom(16).build();
			// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
			MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
					.newMapStatus(mMapStatus);
			// 改变地图状态
			mBaidumap.setMapStatus(mMapStatusUpdate);

			// 定义Maker坐标点 标注当前位置
			LatLng point = new LatLng(latitude, longtitude);
			BitmapDescriptor bitmap = BitmapDescriptorFactory
					.fromResource(R.drawable.map_location_icon); // 构建Marker图标
			OverlayOptions option = new MarkerOptions().title("我的位置")
					.position(point).icon(bitmap); // 构建MarkerOption，用于在地图上添加Marker
			mBaidumap.addOverlay(option);// 在地图上添加Marker，并显示

			LatLng latLng = null;
			OverlayOptions overlayOptions = null;
			Marker marker = null;
			BitmapDescriptor mIconMaker = BitmapDescriptorFactory
					.fromResource(R.drawable.hotel_map_bg);
			for (int i = 0; i < reqdata_List.size(); i++) {
				maphotel = reqdata_List.get(i);
				// 位置reqdata_List.size()
				latLng = new LatLng(Double.parseDouble(maphotel.getY()),
						Double.parseDouble(maphotel.getX()));
				View view = getMapView(maphotel.getName(), maphotel.getPrice());
				mIconMaker = BitmapDescriptorFactory.fromView(view);
				// 图标
				overlayOptions = new MarkerOptions().position(latLng)
						.icon(mIconMaker).zIndex(5);
				marker = (Marker) (mBaidumap.addOverlay(overlayOptions));
				Bundle bundle = new Bundle();
				bundle.putSerializable("hotelId", maphotel.getID());
				marker.setExtraInfo(bundle);

				// 对Marker的点击
				mBaidumap.setOnMarkerClickListener(new OnMarkerClickListener() {
					@Override
					public boolean onMarkerClick(final Marker marker) {
						// 获得marker中的数据
						String info = (String) marker.getExtraInfo().get(
								"hotelId");
						Intent intents = new Intent(context,
								ActivityHotelDetail.class);
						intents.putExtra("hotelId", info);
						intents.putExtra("ruzhu_date", ruzhu_date);
						intents.putExtra("lidian_date", lidian_date);
						startActivity(intents);
						return true;
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 自定义地图上的标注
	 */
	public View getMapView(String hotelName, String hotelPrice) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View convertView = inflater.inflate(R.layout.hotel_map_icon, null);
		TextView hotel_name_tv = (TextView) convertView
				.findViewById(R.id.hotel_name_tv);
		TextView hotelPrice_tv = (TextView) convertView
				.findViewById(R.id.price_tv);
		hotel_name_tv.setText(hotelName);
		hotelPrice_tv.setText("￥" + hotelPrice);
		return convertView;
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<Hotel> str;
		public ImageLoader imageLoader;

		public ListAdapter(Context context, List<Hotel> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
			imageLoader = new ImageLoader(context.getApplicationContext());
		}

		@Override
		public int getCount() {
			return str.size();
		}

		public void updateListView(List<Hotel> list) {
			this.str = list;
			notifyDataSetChanged();
		}

		public void refreshData(List<Hotel> data) {
			this.str = data;
			notifyDataSetChanged();
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
							R.layout.item_hotel_searchlist, null);
				}
				TextView hotel_name_tv = (TextView) convertView
						.findViewById(R.id.hotel_name_tv);
				TextView score_tv = (TextView) convertView
						.findViewById(R.id.score_tv);
				TextView starlevel_tv = (TextView) convertView
						.findViewById(R.id.starlevel_tv);
				TextView area_tv = (TextView) convertView
						.findViewById(R.id.area_tv);
				TextView price_tv = (TextView) convertView
						.findViewById(R.id.price_tv);
				TextView juli_tv = (TextView) convertView
						.findViewById(R.id.juli_tv);
				ImageView hotel_pic_iv = (ImageView) convertView
						.findViewById(R.id.hotel_pic_iv);

				String juli = str.get(position).getJuli();
				if (juli != null && juli.length() > 0) {
					juli_tv.setText(getJuli(juli));
					area_tv.setText(str.get(position).getCBD());
				} else {
					juli_tv.setVisibility(View.GONE);
					area_tv.setText(str.get(position).getCBD()
							.replace("区域", "").replace(":", "")
							.replace("：", "").replace(" ", ""));
				}
				hotel_name_tv.setText(str.get(position).getName());
				score_tv.setText(Float.valueOf(str.get(position).getHaoping()) == -1f ? "暂无"
						: (str.get(position).getHaoping() + "分"));
				starlevel_tv.setText(StarLevel.Starlevel.get(str.get(position)
						.getStar()));
				price_tv.setText("￥" + str.get(position).getPrice());
				// imageLoader.DisplayImage(data.get(position), hotel_pic_iv);
				imageLoader.DisplayImage(str.get(position).getPicture(),
						hotel_pic_iv);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}

		private String getJuli(String juli) {
			String juliString = "";
			int distance = Integer.valueOf(juli);
			if (distance < 1000) {
				juliString = juli + "米";
			} else {
				float f = distance / 1000f;
				DecimalFormat df = new DecimalFormat("#.#");
				f = Float.parseFloat(df.format(f));
				juliString = f + "公里";
			}
			return juliString;
		}
	}

	@Override
	public void OnLoadMore() {
		try {
			LoadMoreDataAsynTask mLoadMoreAsynTask = new LoadMoreDataAsynTask();
			mLoadMoreAsynTask.execute();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void OnRefresh() {
		try {
			RefreshDataAsynTask mRefreshAsynTask = new RefreshDataAsynTask();
			mRefreshAsynTask.execute();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	class RefreshDataAsynTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(3000);
				pgindex++;
				startQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				adapter.refreshData(reqdata_List);
				listview.onRefreshComplete();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	class LoadMoreDataAsynTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(6000);
				pgindex++;
				startQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.refreshData(reqdata_List);
			if (reqdata_List.size() == Integer.valueOf(totalput)) {
				listview.onLoadMoreComplete(true);
			} else {
				listview.onLoadMoreComplete(false);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
}
