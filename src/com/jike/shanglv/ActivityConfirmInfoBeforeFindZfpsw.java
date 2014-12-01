package com.jike.shanglv;

import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jike.shanglv.Common.ClearEditText;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.NetAndJson.HttpUtils;


public class ActivityConfirmInfoBeforeFindZfpsw extends Activity {

	private Context context;
	private ImageButton back_iv;
	private Button nextstep_button;
	private SharedPreferences sp;
	private TextView username_tv, phone_tv;
	private com.jike.shanglv.Common.ClearEditText yanzhengma_cet;
	private String yanzhengmaReturnJson = "", verifyReturnJson = "";
	private CustomProgressDialog progressdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_confirminfo_before_findzfpsw);
			((MyApplication) getApplication()).addActivity(this);

			sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
			context = this;
			back_iv = (ImageButton) findViewById(R.id.back_imgbtn);
			back_iv.setOnClickListener(btnClickListner);
			nextstep_button = (Button) findViewById(R.id.nextstep_button);
			nextstep_button.setOnClickListener(btnClickListner);
			username_tv = (TextView) findViewById(R.id.username_tv);
			phone_tv = (TextView) findViewById(R.id.phone_tv);
			username_tv.setText(sp.getString(SPkeys.username.getString(), ""));
			phone_tv.setText(sp.getString(SPkeys.userphone.getString(), ""));
			yanzhengma_cet = (ClearEditText) findViewById(R.id.yanzhengma_cet);
			((TextView) findViewById(R.id.get_yanzhengma_tv))
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
					startActivity(new Intent(context, ActivityMyAccout.class));
					break;
				case R.id.nextstep_button:
					if (yanzhengma_cet.getText().toString().trim().length() == 0) {
						// new AlertDialog.Builder(context).setTitle("请输入验证码")
						// .setPositiveButton("确定", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("请输入验证码");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					} else {
						startVerify();
					}
				case R.id.get_yanzhengma_tv:
					startGetYanzhengma();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void startGetYanzhengma() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(getApplicationContext());
					String str = "{\"userID\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"siteID\":\""
							+ sp.getString(SPkeys.siteid.getString(), "")
							+ "\",\"phone\":\""
							+ sp.getString(SPkeys.userphone.getString(), "")
							+ "\"}";
					String param = "action=restcode&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "restcode" + str) + "&sitekey="
							+ MyApp.sitekey;
					yanzhengmaReturnJson = HttpUtils.getJsonContent(
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

	private void startVerify() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(getApplicationContext());
					String str = "{\"userID\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"siteID\":\""
							+ sp.getString(SPkeys.siteid.getString(), "")
							+ "\",\"cdk\":\""
							+ yanzhengma_cet.getText().toString().trim()
							+ "\"}";
					String param = "action=chenkedcode&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "chenkedcode" + str) + "&sitekey="
							+ MyApp.sitekey;
					verifyReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = 2;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在核对验证码，请稍候...");
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
				jsonParser = new JSONTokener(yanzhengmaReturnJson);
				try {
					if (yanzhengmaReturnJson.length() == 0) {
						// new AlertDialog.Builder(context)
						// .setTitle("验证码发送失败")
						// .setPositiveButton("确认", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("验证码发送失败");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					String message = jsonObject.getString("d");
					if (state.equals("0000")) {
						nextstep_button.setEnabled(true);
						// new AlertDialog.Builder(context).setTitle("验证码发送成功")
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
					} else {
						// new AlertDialog.Builder(context).setTitle("验证码发送失败")
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
				break;
			case 2:// 校验验证码的正确与否
				jsonParser = new JSONTokener(verifyReturnJson);
				try {
					progressdialog.dismiss();
					if (verifyReturnJson.length() == 0) {
						// new AlertDialog.Builder(context)
						// .setTitle("验证码校验出错")
						// .setPositiveButton("确认", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("验证码校验出错");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					String message = jsonObject.getString("d");
					if (state.equals("0000")) {
						Intent intent = new Intent(context,
								ActivityResetZfPsw.class);
						intent.putExtra(ActivityResetZfPsw.ISRESETLOGINPSW,
								false);
						startActivity(intent);
					} else {
						// new AlertDialog.Builder(context).setTitle("验证码不正确")
						// .setMessage(message)
						// .setPositiveButton("确认", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("验证码不正确");
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
				break;
			}
		}
	};
}
