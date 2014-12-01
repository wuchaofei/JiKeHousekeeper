package com.jike.shanglv;

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
import android.widget.TextView;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.RefreshListView;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Enums.SingleOrDouble;
import com.jike.shanglv.LazyList.ImageLoader;
import com.jike.shanglv.Models.Hangbandongtai;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;


@SuppressLint("ResourceAsColor")
public class ActivityHangbandongtaiSearchlist extends Activity implements
		RefreshListView.IOnRefreshListener, RefreshListView.IOnLoadMoreListener {

	private Context context;
	private ImageButton back_imgbtn, home_imgbtn;
	private com.jike.shanglv.Common.RefreshListView listview;
	private String flightNo = "", startcity_code = "", arrivecity_code = "",
			startcity = "", arrivecity = "", startoff_date = "";// 从搜索页面获取的数据
	private SingleOrDouble wayType;
	private SharedPreferences sp;
	private CustomProgressDialog progressdialog;
	private String flistReturnJson, count;// 返回的查询列表json
	private JSONArray flist;// 查询到的航班列表
	private int index = 0;
	private ListAdapter adapter;
	private ArrayList<Hangbandongtai> dongtai_List;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_hangbandongtai_searchlist);
			initView();
			startQuery();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startQuery() {
		if (wayType == SingleOrDouble.singleWay) {// 按城市查询
			startQueryViaCity();
		} else {// 按航班号直接查询
			startQueryViaNo();
		}
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		dongtai_List = new ArrayList<Hangbandongtai>();
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		listview = (com.jike.shanglv.Common.RefreshListView) findViewById(R.id.listview);
		listview.setOnRefreshListener(this);
		listview.setOnLoadMoreListener(this);

		back_imgbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		home_imgbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, MainActivity.class));
			}
		});
		getIntentData();
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
			if (bundle.containsKey("flightNo"))
				flightNo = bundle.getString("flightNo");
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(flistReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					count = jsonObject.getString("count");

					if (state.equals("0000")) {
						flist = jsonObject.getJSONArray("d");
						createList(flist);
						dongtai_List = removeDuplicteHangbandongtais(dongtai_List);
						adapter = new ListAdapter(context, dongtai_List);
						listview.setAdapter(adapter);
						if (dongtai_List.size() == 0) {
							// new AlertDialog.Builder(context)
							// .setTitle("未查相关的航班信息")
							// .setPositiveButton("确认", null).show();
							final CustomerAlertDialog cad = new CustomerAlertDialog(
									context, true);
							cad.setTitle("未查相关的航班信息");
							cad.setPositiveButton("知道了", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									cad.dismiss();
								}
							});
						}
						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								Hangbandongtai ql = dongtai_List
										.get(position - 1);
								Intent intents = new Intent(context,
										ActivityHangbandongtaiDetail.class);
								intents.putExtra(
										ActivityHangbandongtaiDetail.FLIGHTINFO,
										JSONHelper.toJSON(ql));
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
		for (int i = 0; i < flist_list.length(); i++) {
			try {
				Hangbandongtai hb = JSONHelper.parseObject(
						flist_list.getJSONObject(i), Hangbandongtai.class);
				dongtai_List.add(hb);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void startQueryViaCity() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// type=1:查航班号 type=2:查航段， air 航空公司
					// action=flist&str={'s':'sha','e':hfe,'sd':'2014-01-28','userid':'649','siteid':'65'}
					MyApp ma = new MyApp(context);
					String str = "{\"s\":\"" + startcity_code + "\",\"e\":\""
							+ arrivecity_code + "\",\"index\":\"" + index
							+ "\",\"air\":\"" + "" + "\",\"flightno\":\""
							+ flightNo + "\",\"type\":\"" + 2 + "\"}";
					String param = "action=flighttime&str="
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
									+ "flighttime" + str);
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

	private void startQueryViaNo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// action=flist&str={'s':'sha','e':hfe,'sd':'2014-01-28','userid':'649','siteid':'65'}
					MyApp ma = new MyApp(context);
					String str = "{\"s\":\"" + startcity_code + "\",\"e\":\""
							+ arrivecity_code + "\",\"index\":\"" + index
							+ "\",\"air\":\"" + "" + "\",\"flightno\":\""
							+ flightNo + "\",\"type\":\"" + 1 + "\"}";
					String param = "action=flighttime&str="
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
									+ "flighttime" + str);
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
		listview.removeFootView();// 按航班号查询只有一个航班，移除查看更多
		listview.setOnRefreshListener(null);
		listview.setOnLoadMoreListener(null);// 禁用刷新功能

		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在查询航班动态信息，请稍候...");
		progressdialog.setCancelable(true);
		progressdialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		progressdialog.show();
	}

	Comparator<Hangbandongtai> comparator_time_asc = new Comparator<Hangbandongtai>() {
		@Override
		public int compare(Hangbandongtai s1, Hangbandongtai s2) {
			if (!s1.getPlanfly().equals(s2.getPlanfly())) {
				return s1.getPlanfly().compareTo(s2.getPlanfly());
			} else
				return 0;
		}
	};

	// 去除重复的航班
	public static ArrayList<Hangbandongtai> removeDuplicteHangbandongtais(
			ArrayList<Hangbandongtai> userList) {
		Set<Hangbandongtai> s = new TreeSet<Hangbandongtai>(
				new Comparator<Hangbandongtai>() {
					@Override
					public int compare(Hangbandongtai o1, Hangbandongtai o2) {
						return o1.getFlightno().compareTo(o2.getFlightno());
					}
				});
		s.addAll(userList);
		return new ArrayList<Hangbandongtai>(s);
	}

	@SuppressLint("ResourceAsColor")
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<Hangbandongtai> str;
		public ImageLoader imageLoader;

		public ListAdapter(Context context, List<Hangbandongtai> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
			imageLoader = new ImageLoader(context.getApplicationContext());
			Collections.sort(str, comparator_time_asc);// 对数据按照计划起飞时间排序
		}

		public void refreshData(List<Hangbandongtai> data) {
			this.str = data;
			notifyDataSetChanged();
		}

		public void updateBitmap(List<Hangbandongtai> list1) {
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
							R.layout.item_hangbandongtai_searchlist, null);
				}
				TextView flightNo_tv = (TextView) convertView
						.findViewById(R.id.flightNo_tv);
				TextView flightName_tv = (TextView) convertView
						.findViewById(R.id.flightName_tv);
				TextView startTime_tv = (TextView) convertView
						.findViewById(R.id.startTime_tv);
				TextView endTime_tv = (TextView) convertView
						.findViewById(R.id.endTime_tv);
				TextView startCity_tv = (TextView) convertView
						.findViewById(R.id.startCity_tv);
				TextView endCity_tv = (TextView) convertView
						.findViewById(R.id.endCity_tv);
				TextView state_tv = (TextView) convertView
						.findViewById(R.id.state_tv);
				ImageView flight_company_logo = (ImageView) convertView
						.findViewById(R.id.flight_company_logo);

				String picN = str.get(position).getFlightno().substring(0, 2);
				MyApp ma = new MyApp(context);
				String imgUrl = String.format(ma.getFlightCompanyLogo(), picN);
				imageLoader.DisplayImageWithoutDefault(imgUrl,
						flight_company_logo);
				flightNo_tv.setText(str.get(position).getFlightno());
				flightName_tv.setVisibility(View.GONE);
				startTime_tv.setText(str.get(position).getRealfly());
				endTime_tv.setText(str.get(position).getRealreach());
				startCity_tv.setText(str.get(position).getScity());
				endCity_tv.setText(str.get(position).getEcity());
				state_tv.setText(str.get(position).getState());
				// 如果飞机尚未起飞或者尚未达到，则显示计划起飞到达时间
				if (str.get(position).getRealfly() == null
						|| str.get(position).getRealfly().trim().length() == 0) {
					startTime_tv.setText(str.get(position).getPlanfly());
				}
				if (str.get(position).getRealreach() == null
						|| str.get(position).getRealreach().trim().length() == 0) {
					endTime_tv.setText(str.get(position).getPlanreach());
				}
				String red = "取消 延误", green = "待起飞  起飞 正常", blue = "到达";
				if (red.contains(str.get(position).getState())) {
					state_tv.setTextColor(getResources().getColor(R.color.red));
				}
				if (green.contains(str.get(position).getState())) {
					state_tv.setTextColor(getResources()
							.getColor(R.color.green));
				}
				if (blue.contains(str.get(position).getState())) {
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
				index++;
				startQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.refreshData(dongtai_List);
			listview.onRefreshComplete();
		}
	}

	class LoadMoreDataAsynTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(3000);
				index++;
				startQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.refreshData(dongtai_List);
			if (dongtai_List.size() == Integer.valueOf(count)) {
				listview.onLoadMoreComplete(true);
			} else {
				listview.onLoadMoreComplete(false);
			}
		}
	}
}
