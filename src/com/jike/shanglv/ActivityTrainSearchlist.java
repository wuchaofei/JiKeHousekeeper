package com.jike.shanglv;

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
import android.util.Log;
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

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Models.Seat;
import com.jike.shanglv.Models.TrainListItem;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;

public class ActivityTrainSearchlist extends Activity {

	private Context context;
	private ImageButton back_imgbtn;
	private TextView title_tv, total_train_count_tv, sort_type_tv,
			sort_time_tv;
	private LinearLayout bytraintype_LL, bytime_ll;
	private ImageView sort_type_iv, sort_time_iv;
	private ListView listview;
	private String startcity_code = "", arrivecity_code = "", startcity = "",
			arrivecity = "", startoff_date = "";// 从搜索页面获取的数据
	private SharedPreferences sp;
	private CustomProgressDialog progressdialog;
	private String trainsReturnJson;// 返回的查询列表json

	private ListAdapter adapter;
	private ArrayList<TrainListItem> train_List;
	private Boolean byTimeAsc = false, byTypeAsc = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_train_searchlist);
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
		train_List = new ArrayList<TrainListItem>();
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		listview = (ListView) findViewById(R.id.listview);
		title_tv = (TextView) findViewById(R.id.title_tv);
		back_imgbtn.setOnClickListener(btnClickListner);
		total_train_count_tv = (TextView) findViewById(R.id.total_train_count_tv);
		bytraintype_LL = (LinearLayout) findViewById(R.id.bytraintype_LL);
		bytime_ll = (LinearLayout) findViewById(R.id.bytime_ll);
		sort_type_tv = (TextView) findViewById(R.id.sort_type_tv);
		sort_time_tv = (TextView) findViewById(R.id.sort_time_tv);
		sort_type_iv = (ImageView) findViewById(R.id.sort_type_iv);
		sort_time_iv = (ImageView) findViewById(R.id.sort_time_iv);
		bytraintype_LL.setOnClickListener(btnClickListner);
		bytime_ll.setOnClickListener(btnClickListner);
		getIntentData();
		title_tv.setText(startcity + "-" + arrivecity);
	}

	// 获取Intent数据,并给到页面和做搜索数据使用
	private void getIntentData() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("startcity_code"))
				startcity_code = bundle.getString("startcity_code");// 城市三字码
			if (bundle.containsKey("arrivecity_code"))
				arrivecity_code = bundle.getString("arrivecity_code");
			if (bundle.containsKey("startcity"))
				startcity = bundle.getString("startcity");// 城市名字
			if (bundle.containsKey("arrivecity"))
				arrivecity = bundle.getString("arrivecity");
			if (bundle.containsKey("startoff_date"))
				startoff_date = bundle.getString("startoff_date");
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(trainsReturnJson);
				try {
					if (trainsReturnJson.length() == 0) {
						// new
						// AlertDialog.Builder(context).setTitle("未查到该车段的列车信息")
						// .setPositiveButton("确认", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("未查到该车段的列车信息");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						progressdialog.dismiss();
						break;
					}
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						JSONArray jasonlist = jsonObject.getJSONArray("d");
						createList(jasonlist);
						total_train_count_tv.setText("共" + train_List.size()
								+ "趟");
						adapter = new ListAdapter(context, train_List);
						listview.setAdapter(adapter);
						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								TrainListItem ti = train_List.get(position);
								Intent intents = new Intent(context,
										ActivityTrainBooking.class);
								String tiString = JSONHelper.toJSON(ti);
								String seatListString = JSONHelper.toJSON(ti
										.getSeatList());
								intents.putExtra("TrainListItemString",
										tiString);
								intents.putExtra("SeatListString",
										seatListString);
								intents.putExtra("startcity", startcity);
								intents.putExtra("startcity_code", startcity_code);
								intents.putExtra("arrivecity_code", arrivecity_code);
								intents.putExtra("arrivecity", arrivecity);
								intents.putExtra("startdate", startoff_date);
								startActivity(intents);
							}
						});

					} else {
						String message = "";
						try {
							message = jsonObject.getJSONObject("d").getString(
									"msg");
						} catch (Exception ex) {
							message = jsonObject.getString("msg");
						}
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
		train_List.clear();
		for (int i = 0; i < flist_list.length(); i++) {
			try {
				TrainListItem ti = JSONHelper.parseObject(
						flist_list.getJSONObject(i), TrainListItem.class);
				JSONArray seatListArray = flist_list.getJSONObject(i)
						.getJSONArray("SeatList");
				ArrayList<Seat> SeatList = new ArrayList<Seat>();
				for (int j = 0; j < seatListArray.length(); j++) {
					Seat seat = new Seat();
					seat.setPrice(seatListArray.getJSONObject(j).getString(
							"price"));
					seat.setShengyu(seatListArray.getJSONObject(j).getString(
							"shengyu"));
					seat.setType(seatListArray.getJSONObject(j).getString(
							"type"));
					SeatList.add(seat);
				}
				ti.setSeatList(SeatList);
				ti.setSeat_Type(SeatList.get(0).getType());
				ti.setRemain_Count(SeatList.get(0).getShengyu());
				ti.setPrice(SeatList.get(0).getPrice());
				train_List.add(ti);
			} catch (Exception e) {
			}
		}
	}

	private void startQuery() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// url?action=trainlist&str={"s":"beijing","e":"shanghai","t":"2014-04-30"}&sign=1232432&userkey=2bfc0c48923cf89de19f6113c127ce81&sitekey=defage
					MyApp ma = new MyApp(context);
					// String siteid=sp.getString(SPkeys.siteid.getString(),
					// "65");
					String str = "{\"s\":\"" + startcity_code + "\",\"e\":\""
							+ arrivecity_code + "\",\"t\":\"" + startoff_date
							+ "\"}";
					String param = "action=trainlistv2&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "trainlistv2" + str) + "&sitekey="
							+ MyApp.sitekey;
					trainsReturnJson = HttpUtils.getJsonContent(
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

	Comparator<TrainListItem> comparator_time_asc = new Comparator<TrainListItem>() {
		@Override
		public int compare(TrainListItem s1, TrainListItem s2) {
			return s2.getGoTime().compareTo(s1.getGoTime());
		}
	};
	Comparator<TrainListItem> comparator_time_desc = new Comparator<TrainListItem>() {
		@Override
		public int compare(TrainListItem s1, TrainListItem s2) {
			return s1.getGoTime().compareTo(s2.getGoTime());
		}
	};
	Comparator<TrainListItem> comparator_type_asc = new Comparator<TrainListItem>() {
		@Override
		public int compare(TrainListItem s1, TrainListItem s2) {
			return s1.getTrainID().compareTo(s2.getTrainID());
		}
	};
	Comparator<TrainListItem> comparator_type_desc = new Comparator<TrainListItem>() {
		@Override
		public int compare(TrainListItem s1, TrainListItem s2) {
			return s2.getTrainID().compareTo(s1.getTrainID());
		}
	};
	View.OnClickListener btnClickListner = new View.OnClickListener() {
		@SuppressLint("ResourceAsColor")
		@Override
		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.date_yesterday_ll:
					startQuery();

					break;
				case R.id.date_tomorrow_ll:
					startQuery();
					break;
				// bytime_ll
				case R.id.bytraintype_LL:// sort_type_tv,sort_time_tv
					sort_type_tv.setSelected(true);
					sort_time_tv.setSelected(false);
					sort_type_iv.setSelected(true);
					sort_time_iv.setSelected(false);
					byTypeAsc = !byTypeAsc;
					if (byTypeAsc) {
						sort_type_iv.setBackground(getResources().getDrawable(
								R.drawable.sort_arrow_up));
						Collections.sort(train_List, comparator_type_desc);
						adapter.notifyDataSetChanged();
					} else {
						sort_type_iv.setBackground(getResources().getDrawable(
								R.drawable.sort_arrow_down));
						Collections.sort(train_List, comparator_type_asc);
						adapter.notifyDataSetChanged();
					}
					break;
				case R.id.bytime_ll:// sort_type_tv,sort_time_tv
					sort_type_tv.setSelected(false);
					sort_time_tv.setSelected(true);
					sort_type_iv.setSelected(false);
					sort_time_iv.setSelected(true);
					byTimeAsc = !byTimeAsc;
					if (byTimeAsc) {
						sort_time_iv.setBackground(getResources().getDrawable(
								R.drawable.sort_arrow_up));
						Collections.sort(train_List, comparator_time_desc);
						adapter.notifyDataSetChanged();
					} else {
						sort_time_iv.setBackground(getResources().getDrawable(
								R.drawable.sort_arrow_down));
						Collections.sort(train_List, comparator_time_asc);
						adapter.notifyDataSetChanged();
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
		private List<TrainListItem> str;

		public ListAdapter(Context context, List<TrainListItem> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
		}

		public void updateBitmap(List<TrainListItem> list1) {
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
							R.layout.item_train_searchlist, null);
				}
				TextView train_num_tv = (TextView) convertView
						.findViewById(R.id.train_num_tv);
				TextView train_type_tv = (TextView) convertView
						.findViewById(R.id.train_type_tv);
				TextView start_time_tv = (TextView) convertView
						.findViewById(R.id.start_time_tv);
				TextView arrive_time_tv = (TextView) convertView
						.findViewById(R.id.arrive_time_tv);
				TextView used_time_tv = (TextView) convertView
						.findViewById(R.id.used_time_tv);
				TextView start_station_tv = (TextView) convertView
						.findViewById(R.id.start_station_tv);
				TextView end_station_tv = (TextView) convertView
						.findViewById(R.id.end_station_tv);
				TextView seat_grad_tv = (TextView) convertView
						.findViewById(R.id.seat_grad_tv);
				TextView price_tv = (TextView) convertView
						.findViewById(R.id.price_tv);
				TextView remain_count_tv = (TextView) convertView
						.findViewById(R.id.remain_count_tv);
				ImageView start_station_icon_iv = (ImageView) convertView
						.findViewById(R.id.start_station_icon_iv);
				ImageView end_station_icon_iv = (ImageView) convertView
						.findViewById(R.id.end_station_icon_iv);
				ImageView no_ticket_iv = (ImageView) convertView
						.findViewById(R.id.no_ticket_iv);

				if (!Boolean.valueOf(str.get(position).getYuDing()
						.toLowerCase())) {// 不可预订，显示无票
					no_ticket_iv.setVisibility(View.VISIBLE);
				} else {
					no_ticket_iv.setVisibility(View.GONE);
				}
				train_num_tv.setText(str.get(position).getTrainID());
				train_type_tv.setText(str.get(position).getTrainType());
				start_time_tv.setText(str.get(position).getGoTime());
				arrive_time_tv.setText(str.get(position).getETime());
				used_time_tv.setText("历时： " + str.get(position).getRunTime());
				start_station_tv.setText(str.get(position).getStationS());
				end_station_tv.setText(str.get(position).getStationE());
				seat_grad_tv.setText(str.get(position).getSeat_Type());
				price_tv.setText("￥" + str.get(position).getPrice());
				if (str.get(position).getRemain_Count().equals("40")) {
					remain_count_tv.setText("票源充足 ");
				} else {
					remain_count_tv.setText("余票 "
							+ str.get(position).getRemain_Count() + "张");
				}
				String SFType = str.get(position).getSFType();
				if (SFType.length() == 3) {
					String SType = SFType.substring(0, 1);
					String FType = SFType.substring(2, 3);
					if (SType.equals("始")) {
						start_station_icon_iv.setBackground(getResources()
								.getDrawable(R.drawable.trains_start));
					} else if (SType.equals("过")) {
						start_station_icon_iv.setBackground(getResources()
								.getDrawable(R.drawable.train_over));
					}

					if (FType.equals("终")) {
						end_station_icon_iv.setBackground(getResources()
								.getDrawable(R.drawable.train_final));
					} else if (FType.equals("过")) {
						end_station_icon_iv.setBackground(getResources()
								.getDrawable(R.drawable.train_over));
					}
				}
				// if
				// (!Boolean.valueOf(str.get(position).getYuDing().toLowerCase()))
				// {
				// train_num_tv.setTextColor(getResources().getColor(R.color.gray));
				// train_type_tv.setTextColor(getResources().getColor(R.color.gray));
				// start_time_tv.setTextColor(getResources().getColor(R.color.gray));
				// arrive_time_tv.setTextColor(getResources().getColor(R.color.gray));
				// used_time_tv.setTextColor(getResources().getColor(R.color.gray));
				// start_station_tv.setTextColor(getResources().getColor(R.color.gray));
				// end_station_tv.setTextColor(getResources().getColor(R.color.gray));
				// seat_grad_tv.setTextColor(getResources().getColor(R.color.gray));
				// price_tv.setTextColor(getResources().getColor(R.color.gray));
				// remain_count_tv.setTextColor(getResources().getColor(R.color.gray));
				// if (SFType.length() == 3) {
				// String SType = SFType.substring(0, 1);
				// String FType = SFType.substring(2, 3);
				// if (SType.equals("始")) {
				// start_station_icon_iv.setBackground(getResources()
				// .getDrawable(R.drawable.trains_startgray));
				// } else if (SType.equals("过")) {
				// start_station_icon_iv.setBackground(getResources()
				// .getDrawable(R.drawable.train_overgray));
				// }
				//
				// if (FType.equals("终")) {
				// end_station_icon_iv.setBackground(getResources()
				// .getDrawable(R.drawable.train_finalgray));
				// } else if (FType.equals("过")) {
				// end_station_icon_iv.setBackground(getResources()
				// .getDrawable(R.drawable.train_overgray));
				// }
				// }
				// convertView.setEnabled(false);
				// convertView.setOnClickListener(null);
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}
}
