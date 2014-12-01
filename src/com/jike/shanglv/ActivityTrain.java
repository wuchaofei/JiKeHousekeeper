//火车票搜索查询主界面
package com.jike.shanglv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.NetAndJson.HttpUtils;

public class ActivityTrain extends Activity {

	private TextView startcity_tv, endcity_tv, startcity_code_tv,
			endcity_code_tv, startoff_date_tv;
	private RelativeLayout date_choose_rl;
	private LinearLayout startcity_choose_ll, endcity_choose_ll;
	private ImageView swith_city_iv;
	private ImageButton back_imgbtn, home_imgbtn;
	private Button search_button;
	private Context context;
	private SharedPreferences sp;
	private final int startoff_date = 2, startcity = 3, arrivecity = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_train);
			initView();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		search_button = (Button) findViewById(R.id.search_button);

		startcity_tv = (TextView) findViewById(R.id.startcity_tv);
		endcity_tv = (TextView) findViewById(R.id.endcity_tv);
		startcity_code_tv = (TextView) findViewById(R.id.startcity_code_tv);
		endcity_code_tv = (TextView) findViewById(R.id.endcity_code_tv);
		startoff_date_tv = (TextView) findViewById(R.id.startoff_date_tv);
		startoff_date_tv.setText(DateUtil.GetDateAfterToday(1));

		startcity_choose_ll = (LinearLayout) findViewById(R.id.startcity_choose_ll);
		endcity_choose_ll = (LinearLayout) findViewById(R.id.endcity_choose_ll);
		swith_city_iv = (ImageView) findViewById(R.id.swith_city_iv);
		date_choose_rl = (RelativeLayout) findViewById(R.id.date_choose_rl);
		date_choose_rl.setOnClickListener(btnClickListner);
		swith_city_iv.setOnClickListener(btnClickListner);
		startcity_choose_ll.setOnClickListener(btnClickListner);
		endcity_choose_ll.setOnClickListener(btnClickListner);
		startoff_date_tv.setOnClickListener(btnClickListner);
		startcity_tv.setOnClickListener(btnClickListner);
		endcity_tv.setOnClickListener(btnClickListner);
		back_imgbtn.setOnClickListener(btnClickListner);
		home_imgbtn.setOnClickListener(btnClickListner);
		search_button.setOnClickListener(btnClickListner);
	}

	View.OnClickListener btnClickListner = new View.OnClickListener() {
		@SuppressLint("ResourceAsColor")
		@Override
		public void onClick(View v) {
			try {
				Intent dateIntent = new Intent();
				dateIntent.setClass(context,
						com.jike.shanglv.ShipCalendar.MainActivity.class);
				Intent cityIntent = new Intent();
				cityIntent.setClass(context,
						com.jike.shanglv.SeclectCity.TrainCityActivity.class);

				switch (v.getId()) {
				case R.id.startcity_choose_ll:
				case R.id.startcity_tv:// 出发城市
					startActivityForResult(cityIntent, startcity);
					break;
				case R.id.endcity_choose_ll:
				case R.id.endcity_tv:// 到达城市
					startActivityForResult(cityIntent, arrivecity);
					break;
				case R.id.date_choose_rl:
				case R.id.startoff_date_tv:// 单程 出发日期
					dateIntent.putExtra(
							com.jike.shanglv.ShipCalendar.MainActivity.TITLE,
							"出发日期");
					startActivityForResult(dateIntent, startoff_date);
					break;
				case R.id.back_imgbtn:// 返回
					finish();
					break;
				case R.id.home_imgbtn:// 主页
					finish();
					break;
				case R.id.swith_city_iv:
					String tempCity = "",
					tempCityCode = "";
					tempCity = startcity_tv.getText().toString().trim();
					startcity_tv
							.setText(endcity_tv.getText().toString().trim());
					endcity_tv.setText(tempCity);

					tempCityCode = startcity_code_tv.getText().toString()
							.trim();
					startcity_code_tv.setText(endcity_code_tv.getText()
							.toString().trim());
					endcity_code_tv.setText(tempCityCode);
					break;
				case R.id.search_button:// 搜索
					if (!sp.getBoolean(SPkeys.loginState.getString(), false)) {
						startActivity(new Intent(context, Activity_Login.class));
						break;
					}
					if (startcity_tv.getText().toString().trim()
							.equals(endcity_tv.getText().toString().trim())) {
						// new
						// AlertDialog.Builder(context).setTitle("出发和到达不能为同一个城市").setPositiveButton("知道了",
						// null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("出发和到达不能为同一个城市");
						cad.setPositiveButton("知道了", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					if (HttpUtils.showNetCannotUse(context)) {
						return;
					}
					Intent intents = new Intent(context,
							ActivityTrainSearchlist.class);
					intents.putExtra("startoff_date", startoff_date_tv
							.getText().toString());
					intents.putExtra("startcity", startcity_tv.getText()
							.toString());
					intents.putExtra("arrivecity", endcity_tv.getText()
							.toString());
					intents.putExtra("startcity_code", startcity_code_tv
							.getText().toString());
					intents.putExtra("arrivecity_code", endcity_code_tv
							.getText().toString());
					startActivity(intents);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/*
	 * 选择城市或日期后结果回显到界面
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		Bundle b = data.getExtras();
		String myDate = DateUtil.GetTodayDate();// 获取从com.jike.jikepart.ShipCalendar.MainActivity中回传的值
		String myCity = "上海";
		switch (requestCode) {
		case startoff_date:
			if (b != null && b.containsKey("pickedDate")) {
				myDate = b.getString("pickedDate");
				startoff_date_tv.setText(myDate);
			}
			break;
		case startcity:
			if (b != null && b.containsKey("pickedCity")) {
				myCity = b.getString("pickedCity");
				startcity_tv.setText(myCity.substring(0, myCity.indexOf('#')));
				startcity_code_tv
						.setText(myCity.substring(myCity.indexOf('#') + 1));
			}
			break;
		case arrivecity:
			if (b != null && b.containsKey("pickedCity")) {
				myCity = b.getString("pickedCity");
				endcity_tv.setText(myCity.substring(0, myCity.indexOf('#')));
				endcity_code_tv
						.setText(myCity.substring(myCity.indexOf('#') + 1));
			}
			break;
		default:
			break;
		}
	}

	/*
	 * 保存选择结果状态
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {// 保存界面状态，记录点击TextView前是单程还是往返
		super.onSaveInstanceState(outState);
		outState.putSerializable("startoff_date_tv", startoff_date_tv.getText()
				.toString());
		outState.putSerializable("startcity_tv", startcity_tv.getText()
				.toString());
		outState.putSerializable("endcity_tv", endcity_tv.getText().toString());
		outState.putSerializable("startcity_code_tv", startcity_code_tv
				.getText().toString());
		outState.putSerializable("endcity_code_tv", endcity_code_tv.getText()
				.toString());
	}

	/*
	 * 恢复选择的结果状态
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedState) {// 根据之前保存的状态，恢复页面状态
		super.onRestoreInstanceState(savedState);
		startoff_date_tv.setText(savedState.getSerializable("startoff_date_tv")
				.toString());
		endcity_tv.setText(savedState.getSerializable("endcity_tv").toString());
		startcity_tv.setText(savedState.getSerializable("startcity_tv")
				.toString());
		endcity_code_tv.setText(savedState.getSerializable("endcity_code_tv")
				.toString());
		startcity_code_tv.setText(savedState.getSerializable(
				"startcity_code_tv").toString());
	}
}
