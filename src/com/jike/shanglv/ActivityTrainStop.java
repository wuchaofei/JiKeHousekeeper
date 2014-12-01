package com.jike.shanglv;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.NetAndJson.HttpUtils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityTrainStop extends Activity {
	private String startcity_code = "BOP", arrivecity_code = "AOH",
			startoff_date = "2014-11-20", trainNo = "G101",
			seatFeature = "一等座";
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_train_stop);
		init();
		startThread();
	}

	/**
	 * 初始化
	 */
	private void init() {
		listView = (ListView) findViewById(R.id.listView1);
	}

	private void startThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				MyApp ma = new MyApp(ActivityTrainStop.this);
				String str = "{\"fromStation\":\"" + startcity_code
						+ "\",\"toStation\":\"" + arrivecity_code
						+ "\",\"departDate\":\"" + startoff_date
						+ "\",\"trainNo\":\"" + trainNo
						+ "\",\"seatFeature\":\"" + seatFeature + "\"}";
				String string = HttpUtils.getJsonContent(
						ma.getServeUrl(),
						"action=gettrainlinebytrainno&userkey="
								+ ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
								+ "&sign="
								+ CommonFunc.MD5(ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
										+ "gettrainlinebytrainno"
										+ str
										+ MyApp.sitekey) + "&str=" + str);
				Log.i("---------------->", string);

			}
		}).start();
	}

	/*
	 * listview适配
	 */
	class MyListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = arg1;
			Holder holder = null;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.stop_item, arg2,
						false);
				holder = new Holder(view);
				view.setTag(holder);
			} else {
				holder = (Holder) view.getTag();
			}
			TextView tv_number = holder.getTv_number();
			TextView tv_name = holder.getTv_name();
			TextView tv_startTime = holder.getTv_startTime();
			TextView tv_endTime = holder.getTv_endTime();
			TextView tv_stopTime = holder.getTv_stopTime();

			return view;
		}

	}

	/**
	 * 优化类
	 */
	class Holder {
		View view;
		TextView tv_number;
		TextView tv_name;
		TextView tv_startTime;
		TextView tv_endTime;
		TextView tv_stopTime;

		public Holder(View view) {
			this.view = view;
		}

		public TextView getTv_number() {
			if (tv_number == null) {
				tv_number = (TextView) view.findViewById(R.id.tv_number);
			}
			return tv_number;
		}

		public TextView getTv_name() {
			if (tv_name == null) {
				tv_name = (TextView) view.findViewById(R.id.tv_name);
			}
			return tv_name;
		}

		public TextView getTv_startTime() {
			if (tv_startTime == null) {
				tv_startTime = (TextView) findViewById(R.id.tv_startTime);
			}
			return tv_startTime;
		}

		public TextView getTv_endTime() {
			if (tv_endTime == null) {
				tv_endTime = (TextView) findViewById(R.id.tv_endTime);
			}
			return tv_endTime;
		}

		public TextView getTv_stopTime() {
			if (tv_stopTime == null) {
				tv_stopTime = (TextView) view.findViewById(R.id.tv_stopTime);
			}
			return tv_stopTime;
		}

	}
}
