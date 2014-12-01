//重置登录/支付密码
package com.jike.shanglv;

import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
//import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.jike.shanglv.Common.ClearEditText;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.NetAndJson.HttpUtils;


public class ActivityResetZfPsw extends Activity {
	public static final String ISRESETLOGINPSW = "ISRESETLOGINPSW";
	private Context context;
	private ImageButton back_iv;
	private Button ok_button;
	private SharedPreferences sp;
	private CustomProgressDialog progressdialog;
	private com.jike.shanglv.Common.ClearEditText newpsw_cet, confirmpsw_cet;
	private String changePswReturnJson = "";
	private Boolean isResetLoginPsw = true;// 重置登录密码，false位重置支付密码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_reset_zfpsw);
			((MyApplication) getApplication()).addActivity(this);

			sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
			context = this;
			back_iv = (ImageButton) findViewById(R.id.back_imgbtn);
			back_iv.setOnClickListener(btnClickListner);
			ok_button = (Button) findViewById(R.id.ok_button);
			ok_button.setOnClickListener(btnClickListner);
			newpsw_cet = (ClearEditText) findViewById(R.id.newpsw_cet);
			confirmpsw_cet = (ClearEditText) findViewById(R.id.confirmpsw_cet);
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				if (bundle.containsKey(ISRESETLOGINPSW)) {
					isResetLoginPsw = bundle.getBoolean(ISRESETLOGINPSW);
				}
			}
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
					startActivity(new Intent(context, ActivityMyAccout.class));
					break;
				case R.id.ok_button:
					if (!newpsw_cet.getText().toString()
							.equals(confirmpsw_cet.getText().toString())) {
						// new
						// AlertDialog.Builder(context).setTitle("两次输入的新密码不一致")
						// .setPositiveButton("确定", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("两次输入的新密码不一致");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					if (!CommonFunc.isValidPassword(newpsw_cet.getText()
							.toString().trim())) {
						// new AlertDialog.Builder(context).setTitle("密码格式不正确")
						// .setMessage("为保证密码的安全性，请输入6-20位的数字或字母的组合！")
						// .setPositiveButton("确定", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("为保证密码的安全性，请输入6-20位的数字或字母的组合");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					startReset();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void startReset() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// uid:用户ID sid:网站ID oldpass:老登录密码 newpass:新登录密码
					MyApp ma = new MyApp(getApplicationContext());
					String str = "{\"userID\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"siteID\":\""
							+ sp.getString(SPkeys.siteid.getString(), "")
							+ "\",\"newpass\":\""
							+ newpsw_cet.getText().toString() + "\"}";
					String actionNameString = "";
					if (isResetLoginPsw)
						actionNameString = "restloginpass";
					else
						actionNameString = "restpaypass";
					String param = "action="
							+ actionNameString
							+ "&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ actionNameString + str) + "&sitekey="
							+ MyApp.sitekey;
					changePswReturnJson = HttpUtils.getJsonContent(
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
		progressdialog.setMessage("正在提交重置密码请求，请稍候...");
		progressdialog.setCancelable(true);
		progressdialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		progressdialog.show();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(changePswReturnJson);
				try {
					if (changePswReturnJson.length() == 0) {
						// new AlertDialog.Builder(context)
						// .setTitle("重置密码失败！")
						// .setPositiveButton("确认", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("重置密码失败");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						progressdialog.dismiss();
						break;
					}
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					jsonObject = jsonObject.getJSONObject("d");
					String message = jsonObject.getString("msg");
					if (state.equals("0000")) {
						// new AlertDialog.Builder(context).setTitle("密码重置成功")
						// .setMessage(message)
						// .setPositiveButton("确认", new OnClickListener() {
						// @Override
						// public void onClick(DialogInterface arg0,
						// int arg1) {
						// finish();
						// }
						// }).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("重置密码成功");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
								finish();
							}
						});
					} else {
						// new AlertDialog.Builder(context).setTitle("重置密码失败")
						// .setMessage(message)
						// .setPositiveButton("确认", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle(message);
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
}
