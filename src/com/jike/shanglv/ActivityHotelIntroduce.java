package com.jike.shanglv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jike.shanglv.Models.HotelDetail;
import com.jike.shanglv.NetAndJson.JSONHelper;


public class ActivityHotelIntroduce extends Activity {

	private ImageButton back_imgbtn, home_imgbtn;
	private TextView hotel_introduce_tv, hotel_sheshi_tv, hotel_traffic_tv,
			hotel_address_tv;
	private HotelDetail hotelDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_hotel_introduce);
			initView();
			assign();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		hotel_introduce_tv = (TextView) findViewById(R.id.hotel_introduce_tv);
		hotel_sheshi_tv = (TextView) findViewById(R.id.hotel_sheshi_tv);
		hotel_traffic_tv = (TextView) findViewById(R.id.hotel_traffic_tv);
		hotel_address_tv = (TextView) findViewById(R.id.hotel_address_tv);

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
				startActivity(new Intent(ActivityHotelIntroduce.this,
						MainActivity.class));
			}
		});
	}

	private void assign() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("hotelDetail"))
				try {
					hotelDetail = JSONHelper.parseObject(
							bundle.getString("hotelDetail"), HotelDetail.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		hotel_introduce_tv.setText(hotelDetail.getContent());
		hotel_sheshi_tv.setText(hotelDetail.getService());
		hotel_traffic_tv.setText(hotelDetail.getTraffic());
		hotel_address_tv.setText(hotelDetail.getAddress());
	}
}
