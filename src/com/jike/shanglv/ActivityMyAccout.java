package com.jike.shanglv;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.Platform;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;
import com.jike.shanglv.NetAndJson.UserInfo;


public class ActivityMyAccout extends Activity {
	private ImageButton back_iv;
	private Button logout_button;
	private TextView username_tv, zhanghuyue_tv, chongzhi_tv, phone_tv,
			email_tv;
	private SharedPreferences sp;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_myaccount);
			((MyApplication) getApplication()).addActivity(this);

			context = this;
			sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
			back_iv = (ImageButton) findViewById(R.id.back_imgbtn);
			back_iv.setOnClickListener(btnClickListner);
			logout_button = (Button) findViewById(R.id.logout_button);
			logout_button.setOnClickListener(btnClickListner);

			username_tv = (TextView) findViewById(R.id.username_tv);
			zhanghuyue_tv = (TextView) findViewById(R.id.zhanghuyue_tv);
			chongzhi_tv = (TextView) findViewById(R.id.chongzhi_tv);
			phone_tv = (TextView) findViewById(R.id.phone_tv);
			email_tv = (TextView) findViewById(R.id.email_tv);

			username_tv.setText(sp.getString(SPkeys.username.getString(), ""));
			zhanghuyue_tv.setText(sp.getString(SPkeys.amount.getString(), ""));
			phone_tv.setText(sp.getString(SPkeys.userphone.getString(), ""));
			email_tv.setText(sp.getString(SPkeys.useremail.getString(), ""));
			chongzhi_tv.setOnClickListener(btnClickListner);
			((RelativeLayout) findViewById(R.id.changePsw_rl))
					.setOnClickListener(btnClickListner);
			((RelativeLayout) findViewById(R.id.findZfPsw_rl))
					.setOnClickListener(btnClickListner);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	View.OnClickListener btnClickListner = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.back_imgbtn:
					startActivity(new Intent(ActivityMyAccout.this,
							MineActivity.class));
					break;
				case R.id.logout_button:
					// new
					// AlertDialog.Builder(context).setTitle("确认注销").setNegativeButton("取消",
					// null)
					// .setMessage("确认注销当前用户？").setPositiveButton("注销", new
					// OnClickListener() {
					// @Override
					// public void onClick(DialogInterface arg0, int arg1) {
					// sp.edit().putString(SPkeys.userid.getString(),"").commit();
					// sp.edit().putString(SPkeys.username.getString(),"").commit();
					// sp.edit().putBoolean(SPkeys.loginState.getString(),
					// false).commit();
					// finish();
					// }
					// }).show();

					final CustomerAlertDialog cad = new CustomerAlertDialog(
							context, false);
					cad.setTitle("确认注销当前用户？");
					cad.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							sp.edit().putString(SPkeys.userid.getString(), "")
									.commit();
							sp.edit()
									.putString(SPkeys.username.getString(), "")
									.commit();
							sp.edit()
									.putString(SPkeys.lastPassword.getString(),
											"").commit();
							sp.edit()
									.putBoolean(SPkeys.loginState.getString(),
											false).commit();
							sp.edit().remove(SPkeys.showCustomer.toString()).commit();
							sp.edit().remove(SPkeys.showDealer.toString()).commit();
							sp.edit().remove(SPkeys.utype.toString()).commit();
							finish();
							cad.dismiss();
						}
					});
					cad.setNegativeButton1("取消", new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							cad.dismiss();
						}
					});

					break;
				case R.id.chongzhi_tv:
					startActivity(new Intent(ActivityMyAccout.this,
							ActivityZhanghuchongzhi.class));
					break;
				case R.id.changePsw_rl:
					startActivity(new Intent(ActivityMyAccout.this,
							ActivityChangePsw.class));
					break;
				case R.id.findZfPsw_rl:
					startActivity(new Intent(ActivityMyAccout.this,
							ActivityConfirmInfoBeforeFindZfpsw.class));
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
