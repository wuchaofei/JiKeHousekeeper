//找回登录密码
package com.jike.shanglv;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.jike.shanglv.Common.ClearEditText;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.NetAndJson.HttpUtils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Activity_RetrievePassword extends Activity {
	private static final int GET_USERID_CODE = 0;
	private static final int GET_YANZHENGMA_CODE = 1;
	private static final int VERIFY_YANZHENGMA_CODE = 2;

	private EditText phone_input_et, username_input_et;
	private ClearEditText yanzhengma_cet;
	private TextView get_yanzhengma_tv;
	private Button retrieve_btn;
	private ImageButton back_imgbtn;
	private SharedPreferences sp;
	private Context context;
	private String useridReturnJson = "", yanzhengmaReturnJson = "",
			verifyReturnJson = "", userId = "", siteId = "";
	private CustomProgressDialog progressdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_retrieve_password);
			sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
			context = this;
			((MyApplication) getApplication()).addActivity(this);

			retrieve_btn = (Button) findViewById(R.id.retrieve_btn);
			phone_input_et = (EditText) findViewById(R.id.phone_input_et);
			username_input_et = (EditText) findViewById(R.id.username_input_et);
			yanzhengma_cet = (ClearEditText) findViewById(R.id.yanzhengma_cet);
			back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
			back_imgbtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			retrieve_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						if (yanzhengma_cet.getText().toString().trim().length() == 0) {
							// new
							// AlertDialog.Builder(context).setTitle("请输入验证码")
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
						} else {
							startVerify();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			retrieve_btn.setEnabled(false);
			retrieve_btn.setBackground(getResources().getDrawable(
					R.drawable.btn_3_d));
			get_yanzhengma_tv = (TextView) findViewById(R.id.get_yanzhengma_tv);
			get_yanzhengma_tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						if (!CommonFunc.isMobileNO(phone_input_et.getText()
								.toString().trim())) {
							// new
							// AlertDialog.Builder(Activity_RetrievePassword.this)
							// .setTitle("手机号码格式不正确").setMessage("请输入合法的手机号码！")
							// .setPositiveButton("确定", null).show();
							final CustomerAlertDialog cad = new CustomerAlertDialog(
									context, true);
							cad.setTitle("手机号码格式不正确");
							cad.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									cad.dismiss();
								}
							});
							return;
						}
						if (username_input_et.getText().toString().trim()
								.length() == 0) {
							// new
							// AlertDialog.Builder(context).setTitle("用户名不能为空")
							// .setMessage("请输入用户名！")
							// .setPositiveButton("确定", null).show();
							final CustomerAlertDialog cad = new CustomerAlertDialog(
									context, true);
							cad.setTitle("用户名不能为空");
							cad.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									cad.dismiss();
								}
							});
						}
						startGetUserId();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startGetYanzhengma() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(getApplicationContext());
					String str = "{\"userID\":\"" + userId + "\",\"siteID\":\""
							+ siteId + "\",\"phone\":\""
							+ phone_input_et.getText().toString().trim()
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
					msg.what = GET_YANZHENGMA_CODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void startGetUserId() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(getApplicationContext());
					String str = "{\"userName\":\""
							+ username_input_et.getText().toString().trim()
							+ "\"}";
					String param = "action=checkuser&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "checkuser" + str) + "&sitekey="
							+ MyApp.sitekey;
					useridReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = GET_USERID_CODE;
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
					String str = "{\"userID\":\"" + userId + "\",\"siteID\":\""
							+ siteId + "\",\"cdk\":\""
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
					msg.what = VERIFY_YANZHENGMA_CODE;
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
			case GET_USERID_CODE:
				// {"c":"0000","d":{"username":"rocky18","userid":"3400","phone":"18616846244","siteid":"65"}}
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(useridReturnJson);
				if (useridReturnJson.length() == 0) {// 未获取到用户id，提示发生错误
					// new AlertDialog.Builder(context).setTitle("获取用户信息失败")
					// .setPositiveButton("确认", null).show();
					final CustomerAlertDialog cad = new CustomerAlertDialog(
							context, true);
					cad.setTitle("获取用户信息失败");
					cad.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							cad.dismiss();
						}
					});
					break;
				}
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					jsonObject = jsonObject.getJSONObject("d");
					if (state.equals("0000")) {
						userId = jsonObject.getString("userid");
						siteId = jsonObject.getString("siteid");
						startGetYanzhengma();
					} else {
						String emsg = jsonObject.getString("msg");
						// new AlertDialog.Builder(context).setTitle(emsg)
						// .setPositiveButton("确认", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle(emsg);
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
			case GET_YANZHENGMA_CODE:
				jsonParser = new JSONTokener(yanzhengmaReturnJson);
				try {
					if (yanzhengmaReturnJson.length() == 0) {
						// new AlertDialog.Builder(context).setTitle("验证码发送失败")
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
					JSONObject jsonObject1 = (JSONObject) jsonParser
							.nextValue();
					String state1 = jsonObject1.getString("c");
					String message = jsonObject1.getJSONObject("d").getString(
							"msg");
					if (state1.equals("0000")) {
						retrieve_btn.setEnabled(true);
						retrieve_btn.setBackground(getResources().getDrawable(
								R.drawable.btn_3));
						// new AlertDialog.Builder(context).setTitle(message)
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
						// new AlertDialog.Builder(context).setTitle(message)
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
			case VERIFY_YANZHENGMA_CODE:// 校验验证码的正确与否
				jsonParser = new JSONTokener(verifyReturnJson);
				try {
					progressdialog.dismiss();
					if (verifyReturnJson.length() == 0) {
						// new AlertDialog.Builder(context).setTitle("验证码校验出错")
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
					JSONObject jsonObject2 = (JSONObject) jsonParser
							.nextValue();
					String state2 = jsonObject2.getString("c");
					String message1 = jsonObject2.getJSONObject("d").getString(
							"msg");
					if (state2.equals("0000")) {
						Intent intent = new Intent(context,
								ActivityResetZfPsw.class);
						intent.putExtra(ActivityResetZfPsw.ISRESETLOGINPSW,
								true);
						startActivity(intent);
					} else {
						// new AlertDialog.Builder(context).setTitle("验证码不正确")
						// .setMessage(message1)
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
