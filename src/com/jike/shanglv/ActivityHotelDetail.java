//酒店详情
package com.jike.shanglv;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.Common.StarLevel;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.LazyList.ImageLoader;
import com.jike.shanglv.Models.HotelComment;
import com.jike.shanglv.Models.HotelDetail;
import com.jike.shanglv.Models.HotelRoom;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;

public class ActivityHotelDetail extends Activity {

	protected static final String FLIGHTINFO = "FLIGHT_DETAIL_INFO";
	protected static final int ROOM_MSG = 1;
	protected static final int DETAIL_MSG = 2;
	protected static final int COMMENTS_MSG = 3;
	protected static final int REQ_BOOKING_FOR_DISSMISS_POP = 4;

	private TextView hotel_name_tv, score_tv, dianping_cout_tv,
			pinglun_yulan_tv, hotel_adress_tv, hotel_js_tv, ruzhu_date_tv,
			lidian_date_tv, no_rooms_status;
	private RelativeLayout adress_map_ll, hotel_js_rl, pinglun_rl;
	private LinearLayout loading_ll;
	private ImageView hotel_pic_iv, frame_ani_iv;
	private ListView listview;
	private Context context;
	private String hotelsDetailReturnJson = "", roomsReturnJson = "",
			commentsReturnJson = "", hotelId = "", ruzhu_date = "",
			lidian_date = "", hotelName = "", starLevel = "";
	private RoomListAdapter adapter;
	private CustomProgressDialog progressdialog;
	public ImageLoader imageLoader;
	private SharedPreferences sp;

	private HotelDetail hotelDetail;
	private JSONArray roomArray;
	private ArrayList<HotelRoom> hotelRoomsList;
	private String selectRoomIndexString = "0";
	private JSONObject commentObject;

	private PopupWindow popupWindow_order;
	private View popupWindowView_order;
	InputMethodManager imm;
	private ImageView room_pic_iv;
	private Button close_btn;
	private TextView room_type_tv, floor_tv, area_tv, breakfast_tv, bedtype_tv,
			total_price_tv, commit_order_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_hotel_detail);
			initView();
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

	@SuppressWarnings("static-access")
	private void initView() {
		context = this;
		imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
		imageLoader = new ImageLoader(context.getApplicationContext());
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		hotelRoomsList = new ArrayList<HotelRoom>();

		hotel_name_tv = (TextView) findViewById(R.id.hotel_name_tv);
		score_tv = (TextView) findViewById(R.id.score_tv);
		dianping_cout_tv = (TextView) findViewById(R.id.dianping_cout_tv);
		pinglun_yulan_tv = (TextView) findViewById(R.id.pinglun_yulan_tv);
		hotel_adress_tv = (TextView) findViewById(R.id.hotel_adress_tv);
		hotel_js_tv = (TextView) findViewById(R.id.hotel_js_tv);
		ruzhu_date_tv = (TextView) findViewById(R.id.ruzhu_date_tv);
		lidian_date_tv = (TextView) findViewById(R.id.lidian_date_tv);
		hotel_pic_iv = (ImageView) findViewById(R.id.hotel_pic_iv);
		frame_ani_iv = (ImageView) findViewById(R.id.frame_ani_iv);
		no_rooms_status = (TextView) findViewById(R.id.no_rooms_status);

		adress_map_ll = (RelativeLayout) findViewById(R.id.adress_map_ll);
		hotel_js_rl = (RelativeLayout) findViewById(R.id.hotel_js_rl);
		pinglun_rl = (RelativeLayout) findViewById(R.id.pinglun_rl);
		loading_ll = (LinearLayout) findViewById(R.id.loading_ll);
		listview = (ListView) findViewById(R.id.listview);

		getIntentData();

		hotel_js_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(context,
							ActivityHotelIntroduce.class);
					intent.putExtra("hotelDetail",
							JSONHelper.toJSON(hotelDetail));
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		pinglun_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(context,
							ActivityHotelComments.class);
					intent.putExtra("commentObjectString",
							commentObject.toString());
					intent.putExtra("pingfen", score_tv.getText().toString());
					intent.putExtra("pingfencount", dianping_cout_tv.getText()
							.toString());
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		findViewById(R.id.back_imgbtn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		findViewById(R.id.home_imgbtn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(ActivityHotelDetail.this,
								MainActivity.class));
					}
				});

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		popupWindowView_order = inflater.inflate(
				R.layout.popupwindow_hotel_ordering, null);
		popupWindow_order = new PopupWindow(popupWindowView_order,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		popupWindow_order.setBackgroundDrawable(new BitmapDrawable());
		// 设置PopupWindow的弹出和消失效果
		popupWindow_order.setAnimationStyle(R.style.popupAnimation);
		room_type_tv = (TextView) popupWindowView_order
				.findViewById(R.id.room_type_tv);
		floor_tv = (TextView) popupWindowView_order.findViewById(R.id.floor_tv);
		area_tv = (TextView) popupWindowView_order.findViewById(R.id.area_tv);
		breakfast_tv = (TextView) popupWindowView_order
				.findViewById(R.id.breakfast_tv);
		bedtype_tv = (TextView) popupWindowView_order
				.findViewById(R.id.bedtype_tv);
		total_price_tv = (TextView) popupWindowView_order
				.findViewById(R.id.total_price_tv);
		commit_order_tv = (TextView) popupWindowView_order
				.findViewById(R.id.commit_order_tv);
		room_pic_iv = (ImageView) popupWindowView_order
				.findViewById(R.id.room_pic_iv);
		close_btn = (Button) popupWindowView_order.findViewById(R.id.close_btn);
		close_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow_order.dismiss();
			}
		});
		adress_map_ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(context,
							ActivityHotelLocation.class);
					intent.putExtra("hotel", JSONHelper.toJSON(hotelDetail));
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		commit_order_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ActivityHotelBooking.class);
				intent.putExtra("hotelId", hotelId);
				intent.putExtra("hotelName", hotelName);
				intent.putExtra("starLevel", starLevel);
				intent.putExtra("ruzhu_date", ruzhu_date);
				intent.putExtra("lidian_date", lidian_date);
				HotelRoom selectHotelRoom = hotelRoomsList.get(Integer
						.valueOf(selectRoomIndexString));
				String roomString = JSONHelper.toJSON(selectHotelRoom);
				intent.putExtra("roomInfo", roomString);
				startActivityForResult(intent, REQ_BOOKING_FOR_DISSMISS_POP);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == REQ_BOOKING_FOR_DISSMISS_POP) {
				popupWindow_order.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取Intent数据,并给到页面和做搜索数据使用
	private void getIntentData() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("hotelId"))
				hotelId = bundle.getString("hotelId");
			if (bundle.containsKey("ruzhu_date"))
				ruzhu_date = bundle.getString("ruzhu_date");// 入住日期
			if (bundle.containsKey("lidian_date"))
				lidian_date = bundle.getString("lidian_date");
		}
		try {
			ruzhu_date_tv.setText("入住时间："
					+ DateUtil.getMonthDayDate(ruzhu_date));
			lidian_date_tv.setText("离店时间："
					+ DateUtil.getMonthDayDate(lidian_date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		startQueryHotelDetail();
		startQueryRooms();
		startQueryComments();
	}

	private void startQueryHotelDetail() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// url?action=hotelinfo&sign=7745955d2500de4d473e7badbe5c904d&userkey=2bfc0c48923cf89de19f6113c127ce81&str={'hid':'10153'}&sitekey=defage
					MyApp ma = new MyApp(context);
					String str = "";
					try {
						str = "{\"hid\":\"" + hotelId + "\"}";
					} catch (Exception e) {
						e.printStackTrace();
					}
					String param = "action=hotelinfo&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sitekey="
							+ MyApp.sitekey
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "hotelinfo" + str);
					hotelsDetailReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = DETAIL_MSG;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在查询酒店详情，请稍候...");
		progressdialog.setCancelable(true);
		progressdialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		progressdialog.show();
	}

	private void startQueryRooms() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// action=rooms&sign=7745955d2500de4d473e7badbe5c904d&userkey=2bfc0c48923cf89de19f6113c127ce81&sitekey=defage
					// &str={'hid':'10011','tm1':'2014-05-12','tm2':'2014-05-15','uid':'3208','sid':'65'}
					MyApp ma = new MyApp(context);
					String str = "";
					try {
						str = "{\"hid\":\"" + hotelId + "\",\"tm1\":\""
								+ ruzhu_date + "\",\"tm2\":\"" + lidian_date
								+ "\",\"uid\":\""
								+ sp.getString(SPkeys.userid.getString(), "")
								+ "\",\"sid\":\"65\"}";
					} catch (Exception e) {
						e.printStackTrace();
					}
					String param = "action=rooms&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sitekey="
							+ MyApp.sitekey
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "rooms" + str);
					roomsReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = ROOM_MSG;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void startQueryComments() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// url?action=comments&sign=7745955d2500de4d473e7badbe5c904d&userkey=2bfc0c48923cf89de19f6113c127ce81&sitekey=defage
					// &str={'hid':'10153',"pg":"1","hp":"1"}
					MyApp ma = new MyApp(context);
					String str = "";
					try {
						str = "{\"hid\":\"" + hotelId + "\",\"pg\":\"" + 1
								+ "\",\"hp\":\"" + 1 + "\"}";
					} catch (Exception e) {
						e.printStackTrace();
					}
					String param = "action=comments&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sitekey="
							+ MyApp.sitekey
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "comments" + str);
					commentsReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = COMMENTS_MSG;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DETAIL_MSG:
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(hotelsDetailReturnJson);
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
						hotelDetail = JSONHelper.parseObject(jsonObject,
								HotelDetail.class);
						starLevel = StarLevel.Starlevel.get(hotelDetail
								.getStar());
						hotelName = hotelDetail.getHotelname();
						hotel_name_tv.setText(hotelName);
						score_tv.setText(ActivityHotelSearchlist
								.getScore(hotelDetail.getHaoping()) + "分");
						dianping_cout_tv.setText(getPinglunCount(hotelDetail
								.getHaoping()) + "人评论");
						hotel_adress_tv.setText(hotelDetail.getAddress());
						hotel_js_tv.setText(hotelDetail.getKaiye() + "开业");
						imageLoader.DisplayImage(hotelDetail.getPicture(),
								hotel_pic_iv);
					} else {
						String message = jsonObject.getString("msg");
						// new AlertDialog.Builder(context).setTitle("查询酒店详情失败")
						// .setMessage(message)
						// .setPositiveButton("确认", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("查询酒店详情失败");
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
			case ROOM_MSG:
				jsonParser = new JSONTokener(roomsReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					// {"c":"0000","d":[{"hotelid":"11290","tm1":"2014-09-03","tm2":"2014-09-04","rooms":[]}]}
					if (state.equals("0000")) {
						if (jsonObject.get("d") == null) {
							// new AlertDialog.Builder(context)
							// .setTitle("查询房型信息失败")
							// .setPositiveButton("确定", null).show();
							final CustomerAlertDialog cad = new CustomerAlertDialog(
									context, true);
							cad.setTitle("查询房型信息失败");
							cad.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									cad.dismiss();
								}
							});
							progressdialog.dismiss();
							break;
						} else if (jsonObject.getJSONArray("d")
								.getJSONObject(0).getJSONArray("rooms")
								.length() == 0) {
							no_rooms_status.setVisibility(View.VISIBLE);
						}
						roomArray = jsonObject.getJSONArray("d")
								.getJSONObject(0).getJSONArray("rooms");
						for (int i = 0; i < roomArray.length(); i++) {
							HotelRoom hr = new HotelRoom(
									roomArray.getJSONObject(i));
							ArrayList<HotelRoom> hrlist = new ArrayList<HotelRoom>();
							hrlist = hr.HotelRoomList(roomArray
									.getJSONObject(i));
							hotelRoomsList.addAll(hrlist);
						}
						adapter = new RoomListAdapter(context, hotelRoomsList);
						listview.setAdapter(adapter);
						listview.setItemsCanFocus(false);
						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								selectRoomIndexString = position + "";
								int index = position;
								imm.hideSoftInputFromWindow(
										((Activity) context).getCurrentFocus()
												.getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);
								popupWindow_order.showAtLocation(room_type_tv,
										Gravity.BOTTOM, 0, 0);

								if (hotelRoomsList.get(index).getImg() != null
										&& hotelRoomsList.get(index).getImg()
												.size() > 0) {
									imageLoader.DisplayImage(hotelRoomsList
											.get(index).getImg().get(0)
											.getImgurl(), room_pic_iv);
								}
								room_type_tv.setText(hotelRoomsList.get(index)
										.getTitle());
								floor_tv.setText("楼层    "
										+ hotelRoomsList.get(index).getFloor());
								area_tv.setText("面积    "
										+ hotelRoomsList.get(index).getArea());
								breakfast_tv.setText("早餐    "
										+ hotelRoomsList.get(index)
												.getPlanname());
								bedtype_tv.setText("床型    "
										+ hotelRoomsList.get(index).getBed());
								total_price_tv.setText(hotelRoomsList
										.get(index).getTotalprice()
										+ hotelRoomsList.get(index)
												.getPriceCode());
							}
						});

						ActivityInlandAirlineticketBooking
								.setListViewHeightBasedOnChildren(listview);
						loading_ll.setVisibility(View.GONE);

					} else {
						String message = jsonObject.getString("msg");
						// new AlertDialog.Builder(context).setTitle("查询酒店详情失败")
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
				break;
			case COMMENTS_MSG:
				jsonParser = new JSONTokener(commentsReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						if (jsonObject.get("d") == null) {
							// new AlertDialog.Builder(context)
							// .setTitle("查询用户评论信息失败")
							// .setPositiveButton("确定", null).show();
							final CustomerAlertDialog cad = new CustomerAlertDialog(
									context, true);
							cad.setTitle("查询用户评论信息失败");
							cad.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									cad.dismiss();
								}
							});
							progressdialog.dismiss();
							break;
						}
						commentObject = jsonObject.getJSONObject("d");// 传到评论页面的值
						JSONObject commentObject0 = jsonObject
								.getJSONObject("d").getJSONArray("comment")
								.getJSONObject(0);
						HotelComment hComment = JSONHelper.parseObject(
								commentObject0, HotelComment.class);
						pinglun_yulan_tv.setText(hComment.getContent());
					} else {
						String message = jsonObject.getString("msg");
						// new AlertDialog.Builder(context).setTitle("查询用户评论失败")
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
				break;
			}
		}
	};

	public static int getPinglunCount(String haoping) {// 156$23$1
		int score = 0;
		// String[] s=haoping.split("$");
		try {
			String hp = haoping.substring(0, haoping.indexOf("$"));
			String zp = haoping.substring(haoping.indexOf("$") + 1,
					haoping.lastIndexOf("$"));
			String cp = haoping.substring(haoping.lastIndexOf("$") + 1);
			score = Integer.valueOf(hp) + Integer.valueOf(zp)
					+ Integer.valueOf(cp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return score;
	}

	private class RoomListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<HotelRoom> str;

		public RoomListAdapter(Context context, List<HotelRoom> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
		}

		@Override
		public int getCount() {
			return str.size();
		}

		public void refreshData(List<HotelRoom> data) {
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
							R.layout.item_hotel_roomlist, null);
				}
				TextView tv_roomtype = (TextView) convertView
						.findViewById(R.id.tv_roomtype);
				TextView room_title_tv = (TextView) convertView
						.findViewById(R.id.room_title_tv);
				TextView room_area_tv = (TextView) convertView
						.findViewById(R.id.room_area_tv);
				TextView remark_tv = (TextView) convertView
						.findViewById(R.id.remark_tv);
				TextView AvailableAmount_tv = (TextView) convertView
						.findViewById(R.id.AvailableAmount_tv);
				TextView price_tv = (TextView) convertView
						.findViewById(R.id.price_tv);
				TextView yufujia_tv = (TextView) convertView
						.findViewById(R.id.yufujia_tv);
				TextView fanMoney_tv = (TextView) convertView
						.findViewById(R.id.fanMoney_tv);
				ImageButton booking_imgbtn = (ImageButton) convertView
						.findViewById(R.id.booking_imgbtn);
				RelativeLayout fanMoney_rl = (RelativeLayout) convertView
						.findViewById(R.id.fanMoney_rl);

				room_title_tv.setText(str.get(position).getTitle());
				if (str.get(position).getArea().length() > 0) {
					room_area_tv.setText(str.get(position).getArea() + "平米");
				}
				remark_tv.setText(str.get(position).getPlanname());
				AvailableAmount_tv.setText(str.get(position)
						.getAvailableAmount());
				price_tv.setText("￥" + str.get(position).getTotalprice());
				if (str.get(position).getRoomtype().equals("0")) {// 0现付，1预付
					fanMoney_rl.setVisibility(View.VISIBLE);
					yufujia_tv.setVisibility(View.INVISIBLE);
					tv_roomtype.setText("现付");
					fanMoney_tv.setText("￥" + str.get(position).getJiangjin());
				} else if (str.get(position).getRoomtype().equals("1")) {// 0现付，1预付
					fanMoney_rl.setVisibility(View.INVISIBLE);
					yufujia_tv.setVisibility(View.VISIBLE);
					tv_roomtype.setText("预付");
				}

				if (str.get(position).getAvailableAmount().trim().equals("订完")) {
					booking_imgbtn.setEnabled(false);
					booking_imgbtn.setBackground(getResources().getDrawable(
							R.drawable.hotel_fullhouse_button));
				}
				booking_imgbtn.setTag(position + "");// 给Item中的button设置tag，根据tag判断用户点击了第几行
				booking_imgbtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							int index = Integer.valueOf(v.getTag().toString()
									.trim());
							selectRoomIndexString = v.getTag().toString()
									.trim();
							imm.hideSoftInputFromWindow(((Activity) context)
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
							popupWindow_order.showAtLocation(room_type_tv,
									Gravity.BOTTOM, 0, 0);

							if (hotelRoomsList.get(index).getImg() != null
									&& hotelRoomsList.get(index).getImg()
											.size() > 0) {
								imageLoader.DisplayImage(
										hotelRoomsList.get(index).getImg()
												.get(0).getImgurl(),
										room_pic_iv);
							}
							room_type_tv.setText(hotelRoomsList.get(index)
									.getTitle());
							floor_tv.setText("楼层    "
									+ hotelRoomsList.get(index).getFloor());
							area_tv.setText("面积    "
									+ hotelRoomsList.get(index).getArea());
							breakfast_tv.setText("早餐    "
									+ hotelRoomsList.get(index).getPlanname());
							bedtype_tv.setText("床型    "
									+ hotelRoomsList.get(index).getBed());
							total_price_tv.setText(str.get(index)
									.getTotalprice()
									+ hotelRoomsList.get(index).getPriceCode());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}
}
