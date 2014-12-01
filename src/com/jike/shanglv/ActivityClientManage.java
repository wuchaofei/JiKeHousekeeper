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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.Common.RefreshListView;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Models.CustomerUser;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;
import com.jike.shanglv.SeclectCity.ClearEditText;


public class ActivityClientManage extends Activity implements
		RefreshListView.IOnRefreshListener, RefreshListView.IOnLoadMoreListener {

	private RelativeLayout add_client_rl, client_grad_set_rl;
	private LinearLayout loading_ll, bygrad_LL, bystate_ll;
	private ImageView frame_ani_iv, sort_arrow_grad_iv, sort_arrow_state_iv;
	private TextView query_status_tv, sort_state_tv, sort_grad_tv,
			add_client_tv, client_grade_set_tv, title_tv;
	private com.jike.shanglv.SeclectCity.ClearEditText filter_edit;
	private com.jike.shanglv.Common.RefreshListView listview;
	private Context context;
	private SharedPreferences sp;
	private String customerReturnJson = "", displayName = "", ActionName = "";
	private ArrayList<CustomerUser> customers_List;
	private int index = 1, recordcount = 0;
	private ListAdapter adapter;
	private Boolean byGradAsc = false, byStateAsc = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_client_manage);

			context = this;
			sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
			customers_List = new ArrayList<CustomerUser>();
			listview = (com.jike.shanglv.Common.RefreshListView) findViewById(R.id.listview);
			listview.setOnRefreshListener(this);
			listview.setOnLoadMoreListener(this);
			loading_ll = (LinearLayout) findViewById(R.id.loading_ll);
			frame_ani_iv = (ImageView) findViewById(R.id.frame_ani_iv);
			query_status_tv = (TextView) findViewById(R.id.query_status_tv);
			add_client_rl = (RelativeLayout) findViewById(R.id.add_client_rl);
			client_grad_set_rl = (RelativeLayout) findViewById(R.id.client_grad_set_rl);
			filter_edit = (ClearEditText) findViewById(R.id.filter_edit);
			bygrad_LL = (LinearLayout) findViewById(R.id.bygrad_LL);
			bystate_ll = (LinearLayout) findViewById(R.id.bystate_ll);
			sort_state_tv = (TextView) findViewById(R.id.sort_state_tv);
			sort_grad_tv = (TextView) findViewById(R.id.sort_grad_tv);
			add_client_tv = (TextView) findViewById(R.id.add_client_tv);
			title_tv = (TextView) findViewById(R.id.title_tv);
			client_grade_set_tv = (TextView) findViewById(R.id.client_grade_set_tv);
			sort_arrow_grad_iv = (ImageView) findViewById(R.id.sort_arrow_grad_iv);
			sort_arrow_state_iv = (ImageView) findViewById(R.id.sort_arrow_state_iv);
			((ImageButton) findViewById(R.id.back_imgbtn))
					.setOnClickListener(clickListener);
			((ImageButton) findViewById(R.id.home_imgbtn))
					.setOnClickListener(clickListener);
			client_grad_set_rl.setOnClickListener(clickListener);
			add_client_rl.setOnClickListener(clickListener);
			bygrad_LL.setOnClickListener(clickListener);
			bystate_ll.setOnClickListener(clickListener);
			Bundle bundle = new Bundle();
			bundle = getIntent().getExtras();
			if (bundle != null) {
				displayName = bundle
						.containsKey(ActivityClientManageSetGrad.DISPLAY_TYPENAME_STRING) ? bundle
						.getString(ActivityClientManageSetGrad.DISPLAY_TYPENAME_STRING)
						: "";
				add_client_tv.setText("添加" + displayName);
				client_grade_set_tv.setText(displayName + "级别设置");
				title_tv.setText(displayName + "管理");
			}
			if (displayName
					.equals(ActivityClientManageSetGrad.CUSTOMER_DISPLAYNAME)) {
				ActionName = "customeruserlist";
			} else if (displayName
					.equals(ActivityClientManageSetGrad.DEALER_DISPLAYNAME)) {
				ActionName = "dealeruserlist";
			}
			filter_edit.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					filterData(arg0.toString());
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
				}

				@Override
				public void afterTextChanged(Editable arg0) {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			try {
				switch (arg0.getId()) {
				case R.id.client_grad_set_rl:
					Intent intent = new Intent(context,
							ActivityClientManageSetGrad.class);
					intent.putExtra(
							ActivityClientManageSetGrad.DISPLAY_TYPENAME_STRING,
							displayName);
					startActivity(intent);
					break;
				case R.id.add_client_rl:
					Intent intent1 = new Intent(context,
							ActivityClientManageAddoredit.class);
					intent1.putExtra(
							ActivityClientManageSetGrad.DISPLAY_TYPENAME_STRING,
							displayName);
					intent1.putExtra(ActivityClientManageAddoredit.EDIT_OR_ADD,
							0);
					startActivity(intent1);
					break;
				case R.id.home_imgbtn:
				case R.id.back_imgbtn:
					finish();
					break;
				case R.id.bygrad_LL:
					sort_arrow_grad_iv.setSelected(true);
					sort_grad_tv.setSelected(true);
					sort_state_tv.setSelected(false);
					sort_arrow_state_iv.setSelected(false);
					byGradAsc = !byGradAsc;
					if (byGradAsc) {
						sort_arrow_grad_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_up));
						Collections.sort(customers_List, comparator_grad_desc);
						adapter.notifyDataSetChanged();
					} else {
						sort_arrow_grad_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_down));
						Collections.sort(customers_List, comparator_grad_asc);
						adapter.notifyDataSetChanged();
					}
					break;
				case R.id.bystate_ll:
					sort_arrow_grad_iv.setSelected(false);
					sort_grad_tv.setSelected(false);
					sort_state_tv.setSelected(true);
					sort_arrow_state_iv.setSelected(true);
					byStateAsc = !byStateAsc;
					if (byStateAsc) {
						sort_arrow_state_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_up));
						Collections.sort(customers_List, comparator_state_desc);
						adapter.notifyDataSetChanged();
					} else {
						sort_arrow_state_iv.setBackground(getResources()
								.getDrawable(R.drawable.sort_arrow_down));
						Collections.sort(customers_List, comparator_state_asc);
						adapter.notifyDataSetChanged();
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	Comparator<CustomerUser> comparator_grad_asc = new Comparator<CustomerUser>() {
		@Override
		public int compare(CustomerUser s1, CustomerUser s2) {
			if (s1.getDealerLevel() != null && s2.getDealerLevel() != null
					&& !s1.getDealerLevel().equals(s2.getDealerLevel())) {
				return s1.getDealerLevel().compareTo(s2.getDealerLevel());
			} else
				return 0;
		}
	};

	Comparator<CustomerUser> comparator_grad_desc = new Comparator<CustomerUser>() {
		@Override
		public int compare(CustomerUser s1, CustomerUser s2) {
			if (s1.getDealerLevel() != null && s2.getDealerLevel() != null
					&& !s1.getDealerLevel().equals(s2.getDealerLevel())) {
				return s2.getDealerLevel().compareTo(s1.getDealerLevel());
			} else
				return 0;
		}
	};

	Comparator<CustomerUser> comparator_state_asc = new Comparator<CustomerUser>() {
		@Override
		public int compare(CustomerUser s1, CustomerUser s2) {
			if (s1.getStatus() != null && s2.getStatus() != null
					&& !s1.getStatus().equals(s2.getStatus())) {
				return s1.getStatus().compareTo(s2.getStatus());
			} else
				return 0;
		}
	};

	Comparator<CustomerUser> comparator_state_desc = new Comparator<CustomerUser>() {
		@Override
		public int compare(CustomerUser s1, CustomerUser s2) {
			if (s1.getStatus() != null && s2.getStatus() != null
					&& !s1.getStatus().equals(s2.getStatus())) {
				return s2.getStatus().compareTo(s1.getStatus());
			} else
				return 0;
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		startQueryCustomer();
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
			startQueryCustomer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startQueryCustomer() {
		if (HttpUtils.showNetCannotUse(context)) {
			loading_ll.setVisibility(View.GONE);
			return;
		}
		// url?action=customeruserlist&sign=1232432&userkey=2bfc0c48923cf89de19f6113c127ce81
		// &str={"pageSize":"","pageIndex":"","userID":"","userName":"","tm1":"","tm2":"","status":""}
		// &sitekey=defage
		// str={"pageSize":"每页大小","pageIndex":"当前页","userID":"用户ID","userName":"用户名查询","tm1":"添加日期开始","tm2":"添加日期结束","status":"状态(0正常1禁用2待审核)"}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String str = "{\"pageSize\":\"" + 20 + "\",\"userID\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"pageIndex\":\"" + index
							+ "\",\"userName\":\"" + "" + "\",\"status\":\""
							+ "" + "\",\"tm1\":\"" + "" + "\",\"tm2\":\"" + ""
							+ "\"}";
					String param = "action="
							+ ActionName
							+ "&str="
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
									+ ActionName + str);
					customerReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = 1;
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
			case 1:
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(customerReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					recordcount = jsonObject.getInt("recordcount");

					if (state.equals("0000")) {
						JSONArray cArray = jsonObject.getJSONArray("d");
						// if(cArray.length()>0)customers_List.clear();
						for (int i = 0; i < cArray.length(); i++) {
							CustomerUser cUser = JSONHelper
									.parseObject(cArray.getJSONObject(i),
											CustomerUser.class);
							customers_List.add(cUser);
						}
						customers_List = removeDuplicteCustomerUsers(customers_List);
						adapter = new ListAdapter(context, customers_List);
						if (customers_List.size() == Integer
								.valueOf(recordcount)) {
							listview.removeFootView();// 如果数据就几条一次就加载完了，移除查看更多
							listview.setOnRefreshListener(null);
							listview.setOnLoadMoreListener(null);// 禁用刷新功能
						}
						listview.setAdapter(adapter);
						if (customers_List.size() == 0) {
							query_status_tv.setText("未查询到客户信息");
							listview.setVisibility(View.GONE);
							frame_ani_iv.setVisibility(View.INVISIBLE);
							break;
						}
						loading_ll.setVisibility(View.GONE);
						listview.setVisibility(View.VISIBLE);
						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								CustomerUser cu = customers_List
										.get(position - 1);
								Intent intent1 = new Intent(context,
										ActivityClientManageAddoredit.class);
								intent1.putExtra(
										ActivityClientManageSetGrad.DISPLAY_TYPENAME_STRING,
										displayName);
								intent1.putExtra(
										ActivityClientManageAddoredit.EDIT_OR_ADD,
										1);
								intent1.putExtra(
										ActivityClientManageAddoredit.CUSTOMERINFO_OF_EDIT,
										JSONHelper.toJSON(cu));
								startActivity(intent1);
							}
						});
					} else {
						query_status_tv.setText("查询客户信息失败");
						frame_ani_iv.setVisibility(View.INVISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};

	// 去除重复
	public static ArrayList<CustomerUser> removeDuplicteCustomerUsers(
			ArrayList<CustomerUser> userList) {
		Set<CustomerUser> s = new TreeSet<CustomerUser>(
				new Comparator<CustomerUser>() {

					@Override
					public int compare(CustomerUser o1, CustomerUser o2) {
						return o1.getUserName().compareTo(o2.getUserName());
					}
				});
		s.addAll(userList);
		return new ArrayList<CustomerUser>(s);
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<CustomerUser> str;

		public ListAdapter(Context context, List<CustomerUser> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
			// Collections.sort(str, comparator_time_asc);// 对数据按照计划起飞时间排序
		}

		public void refreshData(List<CustomerUser> data) {
			this.str = data;
			notifyDataSetChanged();
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
							R.layout.item_customeruser_list, null);
				}
				TextView username_tv = (TextView) convertView
						.findViewById(R.id.username_tv);
				TextView usergrad_tv = (TextView) convertView
						.findViewById(R.id.usergrad_tv);
				TextView state_tv = (TextView) convertView
						.findViewById(R.id.state_tv);

				username_tv.setText(str.get(position).getUserName());
				usergrad_tv.setText(str.get(position).getDealerLevel());
				state_tv.setText(str.get(position).getStatus());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<CustomerUser> filterDateList = new ArrayList<CustomerUser>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = customers_List;
		} else {
			filterDateList.clear();
			for (CustomerUser cu : customers_List) {
				String name = cu.getUserName();
				if (name.indexOf(filterStr.toString()) != -1) {
					filterDateList.add(cu);
				}

				if (name.indexOf(filterStr.toString()) != -1) {
					filterDateList.add(cu);
				}
				if (name.indexOf(filterStr.toString()) != -1) {
					filterDateList.add(cu);
				}
				if (name.indexOf(filterStr.toString()) != -1) {
					filterDateList.add(cu);
				}
				if (name.indexOf(filterStr.toString()) != -1) {
					filterDateList.add(cu);
				}
				if (name.indexOf(filterStr.toString()) != -1) {
					filterDateList.add(cu);
				}
			}
		}
		filterDateList = DateUtil.removeDuplicateWithOrder(filterDateList);
		if (adapter != null)
			adapter.refreshData(filterDateList);
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
				startQueryCustomer();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.refreshData(customers_List);
			listview.onRefreshComplete();
		}
	}

	class LoadMoreDataAsynTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(3000);
				index++;
				startQueryCustomer();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.refreshData(customers_List);
			if (customers_List.size() == Integer.valueOf(recordcount)) {
				listview.onLoadMoreComplete(true);
			} else {
				listview.onLoadMoreComplete(false);
			}
		}
	}
}
