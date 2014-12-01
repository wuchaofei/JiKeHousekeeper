package com.jike.shanglv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.jike.shanglv.Common.ClearEditText;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.NetAndJson.HttpUtils;

public class ActivityZhanghuchongzhi extends Activity {

	private ImageButton back_imgbtn, home_imgbtn;
	private TextView dangqianyue_tv, chongzhijilu_tv;
	private com.jike.shanglv.Common.ClearEditText chongzhijine_et;
	private Button chongzhi_button;
	private Context context;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_zhanghuchongzhi);
			initView();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		back_imgbtn.setOnClickListener(clickListener);
		home_imgbtn.setOnClickListener(clickListener);
		chongzhi_button = (Button) findViewById(R.id.chongzhi_button);
		chongzhi_button.setOnClickListener(clickListener);
		dangqianyue_tv = (TextView) findViewById(R.id.dangqianyue_tv);
		chongzhijilu_tv = (TextView) findViewById(R.id.chongzhijilu_tv);
		chongzhijine_et = (ClearEditText) findViewById(R.id.chongzhijine_et);
		chongzhijilu_tv.setOnClickListener(clickListener);
		dangqianyue_tv.setText("￥"
				+ sp.getString(SPkeys.amount.getString(), "0"));
		if (!sp.getBoolean(SPkeys.loginState.getString(), false)) {
			startActivity(new Intent(context, Activity_Login.class));
		}
	}

	View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.back_imgbtn:
					finish();
					break;
				case R.id.home_imgbtn:
					startActivity(new Intent(context, MainActivity.class));
					break;
				case R.id.chongzhijilu_tv:

					break;
				case R.id.chongzhi_button:
					if (HttpUtils.showNetCannotUse(context)) {
						break;
					}
					if (chongzhijine_et.getText().toString().trim().length() == 0) {
						// new AlertDialog.Builder(context).setTitle("请输入充值金额")
						// .setPositiveButton("确认", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("请输入充值金额");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					if (!CommonFunc.isNumber(chongzhijine_et.getText()
							.toString().trim())) {
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("请输入正确的充值金额");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					
					if (Integer.valueOf((new MyApp(context)).getHm().get(
							PackageKeys.ORGIN.getString()).toString())==0||Integer.valueOf((new MyApp(context)).getHm().get(
									PackageKeys.ORGIN.getString()).toString())==1) {
						Intent intent = new Intent(context,
								Activity_Payway.class);
						intent.putExtra(Activity_Payway.CHONGZHI_AMOUNT, chongzhijine_et.getText().toString().trim());
						startActivity(intent);
					} else {
						String userid = sp.getString(SPkeys.userid.getString(),
								"");
						int paysystype = 15;
						String siteid = sp.getString(SPkeys.siteid.getString(),
								"");
						String sign = CommonFunc.MD5(chongzhijine_et.getText()
								.toString().trim()
								+ userid + paysystype + siteid);
						MyApp ma = new MyApp(context);
						String url = String.format(ma.getPayServeUrl(), "",
								chongzhijine_et.getText().toString().trim(),
								userid, paysystype, siteid, sign);
						Intent intent = new Intent(context,
								Activity_Web_Pay.class);
						intent.putExtra(Activity_Web_Pay.URL, url);
						intent.putExtra(Activity_Web_Pay.TITLE, "账户充值支付");
						startActivity(intent);
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
}

