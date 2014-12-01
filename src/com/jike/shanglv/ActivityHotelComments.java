package com.jike.shanglv;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jike.shanglv.Models.HotelComment;
import com.jike.shanglv.NetAndJson.JSONHelper;


public class ActivityHotelComments extends Activity {

	private ImageButton back_imgbtn, home_imgbtn;
	private HotelComment hComment;
	private TextView pingfen_tv, pingfencount_tv;
	private ListView listview;
	private RelativeLayout orderNow_rl;
	private ArrayList<HotelComment> hCommentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_hotel_comments);
			initView();
			assign();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		hCommentList = new ArrayList<HotelComment>();
		pingfen_tv = (TextView) findViewById(R.id.pingfen_tv);
		pingfencount_tv = (TextView) findViewById(R.id.pingfencount_tv);
		listview = (ListView) findViewById(R.id.listview);
		orderNow_rl = (RelativeLayout) findViewById(R.id.orderNow_rl);
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		back_imgbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		home_imgbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ActivityHotelComments.this,
						MainActivity.class));
			}
		});
		orderNow_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void assign() {
		hComment = new HotelComment();
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("commentObjectString"))
				try {
					JSONObject object = new JSONObject(
							bundle.getString("commentObjectString"));
					JSONArray jArray = object.getJSONArray("comment");
					for (int i = 0; i < jArray.length(); i++) {
						HotelComment hComment = JSONHelper.parseObject(
								jArray.getJSONObject(i), HotelComment.class);
						hCommentList.add(hComment);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			if (bundle.containsKey("pingfen")) {
				pingfen_tv.setText("ÆÀ·Ö " + bundle.getString("pingfen"));
			}
			if (bundle.containsKey("pingfencount")) {
				pingfencount_tv.setText(bundle.getString("pingfencount"));
			}
		}
		ListAdapter adapter = new ListAdapter(ActivityHotelComments.this,
				hCommentList);
		listview.setAdapter(adapter);
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<HotelComment> str;

		public ListAdapter(Context context, List<HotelComment> list1) {
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
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.item_hotel_comment_list, null);
			}
			TextView user_tv = (TextView) convertView
					.findViewById(R.id.user_tv);
			TextView time_tv = (TextView) convertView
					.findViewById(R.id.time_tv);
			TextView haoping_tv = (TextView) convertView
					.findViewById(R.id.haoping_tv);
			TextView content_tv = (TextView) convertView
					.findViewById(R.id.content_tv);

			user_tv.setText(str.get(position).getUsername());
			time_tv.setText(str.get(position).getTime());
			haoping_tv.setText(str.get(position).getHaoping());
			content_tv.setText(str.get(position).getContent());
			return convertView;
		}
	}

}
