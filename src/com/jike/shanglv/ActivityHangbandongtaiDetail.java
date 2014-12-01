//航班动态详情
package com.jike.shanglv;

import com.jike.shanglv.Models.Hangbandongtai;
import com.jike.shanglv.NetAndJson.JSONHelper;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ActivityHangbandongtaiDetail extends Activity {

	protected static final String FLIGHTINFO = "FLIGHT_DETAIL_INFO";
	private TextView flight_tv, state_tv, startcity_tv, endcity_tv, realfly_tv,
			realarrive_tv, planfly_tv, planreach_tv;
	Hangbandongtai hbd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_hangbandongtai_detail);
			((MyApplication) getApplication()).addActivity(this);

			flight_tv = (TextView) findViewById(R.id.flight_tv);
			state_tv = (TextView) findViewById(R.id.state_tv);
			startcity_tv = (TextView) findViewById(R.id.startcity_tv);
			endcity_tv = (TextView) findViewById(R.id.endcity_tv);
			realfly_tv = (TextView) findViewById(R.id.realfly_tv);
			realarrive_tv = (TextView) findViewById(R.id.realarrive_tv);
			planfly_tv = (TextView) findViewById(R.id.planfly_tv);
			planreach_tv = (TextView) findViewById(R.id.planreach_tv);

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
							startActivity(new Intent(
									ActivityHangbandongtaiDetail.this,
									MainActivity.class));
						}
					});

			String flightInfoString = getIntent().getExtras().getString(
					FLIGHTINFO);
			hbd = new Hangbandongtai();
			try {
				hbd = JSONHelper.parseObject(flightInfoString,
						Hangbandongtai.class);
				flight_tv.setText("航班：" + hbd.getFlightno());
				state_tv.setText(hbd.getState());
				startcity_tv.setText(hbd.getScity());
				endcity_tv.setText(hbd.getEcity());
				realfly_tv.setText((hbd.getRealfly() == null ? "" : hbd
						.getRealfly()).replace("null", ""));
				realarrive_tv.setText((hbd.getRealreach() == null ? "" : hbd
						.getRealreach()).replace("null", ""));
				planfly_tv.setText(hbd.getPlanfly());
				planreach_tv.setText(hbd.getPlanreach());

				String red = "取消  延误", green = "待起飞 起飞  正常", blue = "到达";
				if (red.contains(hbd.getState())) {
					state_tv.setTextColor(getResources().getColor(R.color.red));
				}
				if (green.contains(hbd.getState())) {
					state_tv.setTextColor(getResources()
							.getColor(R.color.green));
				}
				if (blue.contains(hbd.getState())) {
					state_tv.setTextColor(getResources().getColor(
							R.color.state_blue));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
