package com.jike.shanglv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;

import com.jike.shanglv.Enums.SPkeys;


public class ActivityTrainBaoxian extends Activity {

	public static final String BAOXIAN_BUNDSTRING = "BAOXIAN_BUNDSTRING_CURRENT";
	public static final String No_Baoxian = "不购买保险";
	public static final String Baoxian_Five = "￥5";
	public static final String Baoxian_Ten = "￥10";

	public static final String TITLE_SELECT = "购买保险";
	public static final String TITLE_SHUOMING = "保险说明";
	private Context context;
	private ImageButton back_imgbtn;
	private TextView baoxian_shuoming_tv, title_tv, shuoming_content_tv;
	private ListView baoxian_listview;
	private LinearLayout baoxian_shuoming_ll;
	private SharedPreferences sp;

	private MyListAdapter adapter;
	private String curentBaoxian = "";
	int currentID = -1;
	List<Map<String, Object>> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_train_baoxian);
			initView();
			((MyApplication) getApplication()).addActivity(this);

			adapter = new MyListAdapter(context, list);
			adapter.setCurrentID(currentID);
			baoxian_listview.setAdapter(adapter);
			baoxian_listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (position != currentID) {
						adapter.setCurrentID(position);
						adapter.notifyDataSetChanged();
					}
					currentID = position;
					switch (currentID) {
					case 0:
						curentBaoxian = No_Baoxian;
						break;
					case 1:
						curentBaoxian = Baoxian_Five;
						break;
					case 2:
						curentBaoxian = Baoxian_Ten;
						break;
					default:
						break;
					}
					setResult(
							0,
							getIntent().putExtra(BAOXIAN_BUNDSTRING,
									curentBaoxian));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		title_tv = (TextView) findViewById(R.id.title_tv);
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		baoxian_shuoming_ll = (LinearLayout) findViewById(R.id.baoxian_shuoming_ll);
		baoxian_listview = (ListView) findViewById(R.id.baoxian_listview);
		baoxian_shuoming_tv = (TextView) findViewById(R.id.baoxian_shuoming_tv);
		shuoming_content_tv = (TextView) findViewById(R.id.shuoming_content_tv);
		back_imgbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (title_tv.getText().toString().equals(TITLE_SHUOMING)) {
					title_tv.setText(TITLE_SELECT);
					baoxian_listview.setVisibility(View.VISIBLE);
					baoxian_shuoming_ll.setVisibility(View.VISIBLE);
					shuoming_content_tv.setVisibility(View.GONE);
				} else
					finish();
			}
		});
		baoxian_shuoming_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				title_tv.setText(TITLE_SHUOMING);
				baoxian_listview.setVisibility(View.GONE);
				baoxian_shuoming_ll.setVisibility(View.GONE);
				shuoming_content_tv.setVisibility(View.VISIBLE);
			}
		});
		getIntentData();
		initData();
	}

	public void initData() {
		list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", No_Baoxian);
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", Baoxian_Five);
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", Baoxian_Ten);
		list.add(map);
	}

	// 获取Intent数据,并给到页面和做搜索数据使用
	private void getIntentData() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(BAOXIAN_BUNDSTRING))
				;
			curentBaoxian = bundle.getString(BAOXIAN_BUNDSTRING);
		}
		if (curentBaoxian.equals(No_Baoxian)) {
			currentID = 0;
		} else if (curentBaoxian.equals(Baoxian_Five)) {
			currentID = 1;
		} else if (curentBaoxian.equals(Baoxian_Ten)) {
			currentID = 2;
		}
	}

	public class MyListAdapter extends BaseAdapter {

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
				// myHolder.iv.setBackgroundResource((Integer)
				// list.get(position).get("img"));
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
