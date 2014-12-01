//用户登录
package com.jike.shanglv;

import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;
import com.jike.shanglv.NetAndJson.UserInfo;
import com.jike.shanglv.Update.UpdateManager;

public class Activity_Login extends Activity {

	private ImageView back_imgbtn;
	private EditText uername_input_et, password_input_et;
	private ImageView autologin_checkbox_iv;
	private Button login_btn;
	private RelativeLayout autologin_rl;
	private TextView registernew_tv, forgetpassword_tv;
	private Context context;

	private Boolean auto = true;
	private Drawable checkedDrawable, uncheckedDrawable;
	private SharedPreferences sp;
	private String loginReturnJson;// 登录验证后返回的结果数据
	private CustomProgressDialog progressdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_login);
			init();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		try {
			context = this;
			sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
			auto = sp.getBoolean(SPkeys.autoLogin.getString(), true);

			checkedDrawable = context.getResources().getDrawable(
					R.drawable.fuxuankuang_yes);
			uncheckedDrawable = context.getResources().getDrawable(
					R.drawable.fuxuankuang_no);

			uername_input_et = (EditText) findViewById(R.id.uername_input_et);
			uername_input_et.setFocusable(false);
			uername_input_et.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					try {
						uername_input_et.setFocusableInTouchMode(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}
			});

			password_input_et = (EditText) findViewById(R.id.password_input_et);
			password_input_et.setFocusable(false);
			password_input_et.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					password_input_et.setFocusableInTouchMode(true);
					return false;
				}
			});

			if (auto) {
				uername_input_et.setText(sp.getString(
						SPkeys.lastUsername.getString(), ""));
				password_input_et.setText(sp.getString(
						SPkeys.lastPassword.getString(), ""));

			}

			autologin_checkbox_iv = (ImageView) findViewById(R.id.autologin_checkbox_iv);
			login_btn = (Button) findViewById(R.id.login_btn);
			back_imgbtn = (ImageView) findViewById(R.id.back_imgbtn);
			autologin_rl = (RelativeLayout) findViewById(R.id.autologin_rl);
			registernew_tv = (TextView) findViewById(R.id.registernew_tv);
			forgetpassword_tv = (TextView) findViewById(R.id.forgetpassword_tv);

			login_btn.setOnClickListener(myListener);
			back_imgbtn.setOnClickListener(myListener);
			autologin_rl.setOnClickListener(myListener);
			registernew_tv.setOnClickListener(myListener);
			forgetpassword_tv.setOnClickListener(myListener);
//			if ((new MyApp(context)).getHm().get(
//					PackageKeys.PLATFORM.getString()) == Platform.B2B)
			if(sp.getString(SPkeys.utype.getString(), "0").equals(
					"1")){
				registernew_tv.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:// 获取登录返回的数据

				JSONTokener jsonParser;
				jsonParser = new JSONTokener(loginReturnJson);
				try {
					if (loginReturnJson.length()==0) {
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("登录失败");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
					}
					
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						String content = jsonObject.getString("d");
						sp.edit()
								.putString(SPkeys.UserInfoJson.getString(),
										content).commit();
						sp.edit()
								.putString(SPkeys.lastUsername.getString(),
										uername_input_et.getText().toString())
								.commit();
						sp.edit()
								.putString(SPkeys.lastPassword.getString(),
										password_input_et.getText().toString())
								.commit();
						sp.edit()
								.putBoolean(SPkeys.autoLogin.getString(), auto)
								.commit();

						// 以下代码将用户信息反序列化到SharedPreferences中
						UserInfo user = JSONHelper.parseObject(content,
								UserInfo.class);
						sp.edit()
								.putString(SPkeys.userid.getString(),
										user.getUserid()).commit();
						sp.edit()
								.putString(SPkeys.username.getString(),
										user.getUsername()).commit();
						sp.edit()
								.putString(SPkeys.amount.getString(),
										user.getAmmount()).commit();
						sp.edit()
								.putString(SPkeys.siteid.getString(),
										user.getSiteid()).commit();
						sp.edit()
								.putString(SPkeys.userphone.getString(),
										user.getMobile()).commit();
						sp.edit()
								.putString(SPkeys.useremail.getString(),
										user.getEmail()).commit();
						sp.edit().putString(SPkeys.utype.getString(),
								user.getUsertype()).commit();
						sp.edit().putString(SPkeys.showCustomer.getString(),
								user.getShowCustomer()).commit();
						sp.edit().putString(SPkeys.showDealer.getString(),
								user.getShowDealer()).commit();
						sp.edit().putString(SPkeys.opensupperpay.getString(),
								user.getOpensupperpay()).commit();
						// 其他信息以后用时再增加
						// 登录后将登录状态置为true
						sp.edit()
								.putBoolean(SPkeys.loginState.getString(), true)
								.commit();
						finish();
					} else {
						String message = "";
						sp.edit().remove(SPkeys.UserInfoJson.getString())
								.commit();
						sp.edit().remove(SPkeys.lastUsername.getString())
								.commit();
						sp.edit().remove(SPkeys.lastPassword.getString())
								.commit();
						sp.edit().remove(SPkeys.autoLogin.getString()).commit();
						sp.edit().remove(SPkeys.userid.getString()).commit();
						sp.edit().remove(SPkeys.username.getString()).commit();
						sp.edit().remove(SPkeys.siteid.getString()).commit();
						sp.edit().remove(SPkeys.amount.getString()).commit();
						sp.edit().remove(SPkeys.userphone.getString()).commit();
						sp.edit().remove(SPkeys.useremail.getString()).commit();
						sp.edit().remove(SPkeys.utype.getString()).commit();
						if (state.equals("9999")) {
							MyApp ma = new MyApp(context);
							UpdateManager manager = new UpdateManager(context,
									ma.getHm()
											.get(PackageKeys.UPDATE_NOTE
													.getString()).toString());
							manager.checkForUpdates(false);
						}
						try {
							message = jsonObject.getJSONObject("d").getString(
									"msg");
						} catch (Exception e) {
							message = jsonObject.getString("msg");
						}
						// new AlertDialog.Builder(context).setTitle("登录失败")
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

	OnClickListener myListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.back_imgbtn:
					finish();
					break;
				case R.id.autologin_rl:
					auto = !auto;
					if (auto) {
						autologin_checkbox_iv.setBackground(checkedDrawable);
					} else {
						autologin_checkbox_iv.setBackground(uncheckedDrawable);
					}
					break;
				case R.id.registernew_tv:
					startActivity(new Intent(context, Activity_Register.class));
					break;
				case R.id.forgetpassword_tv:// 找回密码
					startActivity(new Intent(context,
							Activity_RetrievePassword.class));
					break;
				case R.id.login_btn:
					if (uername_input_et.getText().toString().trim().length() == 0) {
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("请输入用户名");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					if (password_input_et.getText().toString().trim().length() == 0) {
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("请输入密码");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						break;
					}
					// 登录验证
					if (HttpUtils.showNetCannotUse(context)) {
						break;
					}

					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								MyApp ma = new MyApp(context);
								String version = "";
								try {
									version = context
											.getPackageManager()
											.getPackageInfo(
													context.getPackageName(), 0).versionName;
								} catch (NameNotFoundException e) {
									e.printStackTrace();
								}
//								int utype = 0;
//								Platform pf = (Platform) ma.getHm().get(
//										PackageKeys.PLATFORM.getString());
//								if (pf == Platform.B2B)
//									utype = 1;
//								else if (pf == Platform.B2C)
//									utype = 2;
								String str = "{\"uname\":\""
										+ uername_input_et.getText().toString()
												.trim()
										+ "\",\"upwd\":\""
										+ password_input_et.getText()
												.toString().trim()
//										+ "\",\"utype\":\"" + utype
										+ "\",\"version\":\"" + version + "\"}";
								String param = "action=userlogin&sitekey=&userkey="
										+ ma.getHm()
												.get(PackageKeys.USERKEY
														.getString())
												.toString()
										+ "&str="
										+ str
										+ "&sign="
										+ CommonFunc.MD5(ma
												.getHm()
												.get(PackageKeys.USERKEY
														.getString())
												.toString()
												+ "userlogin" + str);
								loginReturnJson = HttpUtils.getJsonContent(
										ma.getServeUrl(), param);
								Log.v("loginReturnJson", loginReturnJson);
								Message msg = new Message();
								msg.what = 1;
								handler.sendMessage(msg);
							} catch (Exception exception) {
							}
						}
					}).start();
					progressdialog = CustomProgressDialog.createDialog(context);
					progressdialog.setMessage("正在登录，请稍候...");
					progressdialog.setCancelable(true);
					progressdialog.show();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
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
