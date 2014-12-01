package com.jike.shanglv;

import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
//import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jike.shanglv.Common.ClearEditText;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.NetAndJson.HttpUtils;


public class ActivityHuafeichongzhi extends Activity {

	protected static final int PHONEPRO = 0;
	protected static final int COMMITMSG = 1;
	private String phoneproReturnJson, commitReturnJson, prodid, orderId;
	private ImageButton back_imgbtn, home_imgbtn;
	private TextView chongzhijine_tv, thephonenum_tv, guishudi_tv, paymoney_tv;
	private com.jike.shanglv.Common.ClearEditText phonenum_et,
			confirm_phonenum_et;
	private Button chongzhi_button;
	private ImageView contact_person_phone_iv, frame_ani_iv;
	private RelativeLayout choose_mianzhi_rl;
	private Context context;
	private SharedPreferences sp;
	private CustomProgressDialog progressdialog;
	String[] mianzhi_list = new String[] { "10元", "20元", "30元", "50元", "100元" };
	private LinearLayout loading_ll, hedui_ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_huafeichongzhi);
			initView();
			chongzhi_button.setEnabled(false);
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startLoadingAni() {
		loading_ll.setVisibility(View.VISIBLE);
		hedui_ll.setVisibility(View.GONE);
		frame_ani_iv.setBackgroundResource(R.anim.frame_rotate_ani_small);
		AnimationDrawable anim = (AnimationDrawable) frame_ani_iv
				.getBackground();
		anim.setOneShot(false);
		anim.start();
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);

		loading_ll = (LinearLayout) findViewById(R.id.loading_ll);
		hedui_ll = (LinearLayout) findViewById(R.id.hedui_ll);
		frame_ani_iv = (ImageView) findViewById(R.id.frame_ani_iv);
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		back_imgbtn.setOnClickListener(clickListener);
		home_imgbtn.setOnClickListener(clickListener);
		chongzhijine_tv = (TextView) findViewById(R.id.chongzhijine_tv);
		thephonenum_tv = (TextView) findViewById(R.id.thephonenum_tv);
		guishudi_tv = (TextView) findViewById(R.id.guishudi_tv);
		paymoney_tv = (TextView) findViewById(R.id.paymoney_tv);
		contact_person_phone_iv = (ImageView) findViewById(R.id.contact_person_phone_iv);
		phonenum_et = (ClearEditText) findViewById(R.id.phonenum_et);
		confirm_phonenum_et = (ClearEditText) findViewById(R.id.confirm_phonenum_et);
		chongzhi_button = (Button) findViewById(R.id.chongzhi_button);
		choose_mianzhi_rl = (RelativeLayout) findViewById(R.id.choose_mianzhi_rl);
		choose_mianzhi_rl.setOnClickListener(clickListener);
		contact_person_phone_iv.setOnClickListener(clickListener);
		chongzhi_button.setOnClickListener(clickListener);
		confirm_phonenum_et.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				String mobile = confirm_phonenum_et.getText().toString();
				if (mobile.length() == 11) {
					if (!phonenum_et
							.getText()
							.toString()
							.trim()
							.equals(confirm_phonenum_et.getText().toString()
									.trim())) {
						// new AlertDialog.Builder(context)
						// .setTitle("两次号码输入不一致，请重新输入")
						// .setPositiveButton("确定", new OnClickListener() {
						// @Override
						// public void onClick(DialogInterface arg0,
						// int arg1) {
						// confirm_phonenum_et.setText("");
						// }
						// }).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("两次号码输入不一致，请重新输入");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
					}
					startQueryPhonepro();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		phonenum_et.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				String mobile = phonenum_et.getText().toString();
				if (mobile.length() != 11) {
					thephonenum_tv.setText("");
					guishudi_tv.setText("");
					paymoney_tv.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
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
				case R.id.contact_person_phone_iv:
					startActivityForResult(
							new Intent(
									context,
									com.jike.shanglv.SeclectCity.ContactListActivity.class),
							13);
					thephonenum_tv.setText("");
					guishudi_tv.setText("");
					paymoney_tv.setText("");
					break;
				case R.id.choose_mianzhi_rl:
					int selectedIndex = 0;
					String currentString = chongzhijine_tv.getText().toString()
							.trim();
					for (int i = 0; i < mianzhi_list.length; i++) {
						if (mianzhi_list[i].equals(currentString))
							selectedIndex = i;
					}
					new AlertDialog.Builder(context)
							.setTitle("请选择面值")
							.setSingleChoiceItems(mianzhi_list, selectedIndex,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											thephonenum_tv.setText("");
											guishudi_tv.setText("");
											paymoney_tv.setText("");
											chongzhijine_tv
													.setText(mianzhi_list[which]);
											startQueryPhonepro();
											dialog.dismiss();
											chongzhi_button.setEnabled(false);
										}
									}).setNegativeButton("取消", null).show();
					break;
				case R.id.chongzhi_button:
					if (!sp.getBoolean(SPkeys.loginState.getString(), false)) {
						startActivity(new Intent(context, Activity_Login.class));
						break;
					}
					if (!CommonFunc.isMobileNO(phonenum_et.getText().toString()
							.trim())) {
						// new
						// AlertDialog.Builder(context).setTitle("手机号码格式不正确")
						// .setMessage("请输入合法的手机号码！")
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
						break;
					}
					if (!phonenum_et
							.getText()
							.toString()
							.trim()
							.equals(confirm_phonenum_et.getText().toString()
									.trim())) {
						// new AlertDialog.Builder(context)
						// .setTitle("两次号码输入不一致，请重新输入")
						// .setPositiveButton("确定", new OnClickListener() {
						// @Override
						// public void onClick(DialogInterface arg0,
						// int arg1) {
						// confirm_phonenum_et.setText("");
						// }
						// }).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("两次号码输入不一致，请重新输入");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
								confirm_phonenum_et.setText("");
							}
						});
					}
					startCommitOrder();
				default:

					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == 13) {
				if (data == null)
					return;
				Bundle b = data.getExtras();
				if (b != null && b.containsKey("pickedPhoneNum")) {
					String myNum = b.getString("pickedPhoneNum");
					if (myNum.startsWith("17951")) {
						myNum = myNum.substring(5);
					} else if (myNum.startsWith("+86")) {
						myNum = myNum.substring(3);
					}
					if (!CommonFunc.isMobileNO(myNum)) {
						phonenum_et.setText("");
						confirm_phonenum_et.setText("");
						hedui_ll.setVisibility(View.GONE);
						final CustomerAlertDialog cad2 = new CustomerAlertDialog(
								context, true);
						cad2.setTitle("手机号码格式不正确");
						cad2.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad2.dismiss();
							}
						});
						return;
					}
					phonenum_et.setText(myNum);
					confirm_phonenum_et.setText(myNum);

					thephonenum_tv.setText("");
					guishudi_tv.setText("");
					paymoney_tv.setText("");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startQueryPhonepro() {
		startLoadingAni();
		if (HttpUtils.showNetCannotUse(context)) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// url?action=phonepro&sign=1232432&userkey=2bfc0c48923cf89de19f6113c127ce81&sitekey=defage
					// &str={"phone":"","value":"","userid":"","siteid":""}
					MyApp ma = new MyApp(context);
					String siteid = sp.getString(SPkeys.siteid.getString(),
							"65");
					String str = "";
					try {
						str = "{\"phone\":\""
								+ phonenum_et.getText().toString().trim()
								+ "\",\"value\":\""
								+ chongzhijine_tv
										.getText()
										.toString()
										.trim()
										.substring(
												0,
												chongzhijine_tv.getText()
														.toString().trim()
														.length() - 1)
								+ "\",\"userid\":\""
								+ sp.getString(SPkeys.userid.getString(), "")
								+ "\",\"siteid\":\"" + siteid + "\"}";
					} catch (Exception e) {
						e.printStackTrace();
					}
					String param = "action=phoneprov2&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sitekey="
							+ MyApp.sitekey
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "phoneprov2" + str);
					phoneproReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = PHONEPRO;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void startCommitOrder() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// url?action=phoneorder&sign=1232432&userkey=2bfc0c48923cf89de19f6113c127ce81&sitekey=defage
					// &str={"phone":"","amount":"","pid":"","value":"","sid":"","uid":""}
					MyApp ma = new MyApp(context);
					String siteid = sp.getString(SPkeys.siteid.getString(),
							"65");
					String czAmount = "";
					czAmount = paymoney_tv.getText().toString().trim();
					if (czAmount.length() > 0) {
						czAmount = czAmount.substring(1);
					}
					String str = "{\"phone\":\""
							+ phonenum_et.getText().toString().trim()
							+ "\",\"amount\":\""
							+ czAmount
							+ "\",\"pid\":\""
							+ prodid
							+ "\",\"value\":\""
							+ chongzhijine_tv
									.getText()
									.toString()
									.trim()
									.substring(
											0,
											chongzhijine_tv.getText()
													.toString().trim().length() - 1)
							+ "\",\"uid\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"sid\":\"" + siteid + "\"}";
					String orgin=ma.getHm().get(PackageKeys.ORGIN.getString())
							.toString();
					String param = "action=phoneorder&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sitekey="
							+ MyApp.sitekey
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "phoneorder" + str)+"&orgin="+orgin;
					commitReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = COMMITMSG;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在提交话费充值订单，请稍候...");
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
			case PHONEPRO:
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(phoneproReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					if (state.equals("0000")) {
						jsonObject = jsonObject.getJSONObject("d");
						thephonenum_tv.setText(phonenum_et.getText().toString()
								.trim());
						guishudi_tv.setText(jsonObject.getString("province")
								+ jsonObject.getString("provider"));
						paymoney_tv
								.setText("￥" + jsonObject.getString("price"));
						prodid = jsonObject.getString("prodid");
						chongzhi_button.setEnabled(true);
						loading_ll.setVisibility(View.GONE);
						hedui_ll.setVisibility(View.VISIBLE);
						chongzhi_button.setBackground(getResources()
								.getDrawable(R.drawable.btn_3));
						chongzhi_button.setEnabled(true);
					} else {
						// String message = jsonObject.getString("msg");
						// new AlertDialog.Builder(context).setTitle("验证价格失败")
						// .setMessage(message)
						// .setPositiveButton("确认", null).show();
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("验证价格失败");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						chongzhi_button.setBackgroundColor(getResources()
								.getColor(R.color.gray));
						chongzhi_button.setEnabled(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case COMMITMSG:
				jsonParser = new JSONTokener(commitReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					if (state.equals("0000")) {
						jsonObject = jsonObject.getJSONObject("d");
						orderId = jsonObject.getString("msg");

						String userid = sp.getString(SPkeys.userid.getString(),
								"");
						int paysystype = 14;
						String siteid = sp.getString(SPkeys.siteid.getString(),
								"65");
						String sign = CommonFunc.MD5(orderId
								+ paymoney_tv.getText().toString().trim()
										.substring(1) + userid + paysystype
								+ siteid);
						MyApp ma = new MyApp(context);
						// <string
						// name="test_pay_server_url">http://gatewayceshi.51jp.cn/PayMent/BeginPay.aspx?orderID=%1$s&amp;amount=%2$s&amp;userid=%3$s&amp;paysystype=%4$s&amp;siteid=%5$s&amp;sign=%6$s</string>
						String url = String.format(ma.getPayServeUrl(),
								orderId, paymoney_tv.getText().toString()
										.trim().substring(1), userid,
								paysystype, siteid, sign);
						Intent intent = new Intent(context,
								Activity_Web_Pay.class);
						intent.putExtra(Activity_Web_Pay.URL, url);
						intent.putExtra(Activity_Web_Pay.TITLE, "话费充值支付");
						startActivity(intent);
					} else {
						String message = jsonObject.getString("msg");
						// new AlertDialog.Builder(context).setTitle("订单提交失败")
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
						chongzhi_button.setBackgroundColor(getResources()
								.getColor(R.color.gray));
						chongzhi_button.setEnabled(false);
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
