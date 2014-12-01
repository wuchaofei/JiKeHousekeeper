package com.jike.shanglv;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jike.shanglv.Common.ClearEditText;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.Common.SelectProvinceCityAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Models.CustomerUser;
import com.jike.shanglv.Models.DealerLevel;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;


public class ActivityClientManageAddoredit extends Activity {

	public static final String EDIT_OR_ADD = "EDIT_OR_ADD";// 0新增，1编辑
	public static final String CUSTOMERINFO_OF_EDIT = "CUSTOMERINFO_OF_EDIT";// 编辑用户或分销商的信息
	protected static final int ADD_CUSTOMER_MSG_CODE = 0;
	protected static final int MODIFY_CUSTOMER_MSG_CODE = 1;
	protected static final int DEALERLEVELMSGCODE = 2;
	private Context context;
	private TextView title_tv, province_city_tv, cancel_tv, finish_tv,
			startValidDay_tv, endValidDay_tv, default_grad_tv;
	private LinearLayout dealer_extra_info_ll, password_ll, setgrad_ll;
	private com.jike.shanglv.Common.ClearEditText password_et,
			comfirmPassword_et, contactPerson_et, contactPhone_et,
			companyName_et;
	private EditText username_et;
	private String addAction = "", modifyAction = "", startValidDay = "",
			endValidDay = "", addReturnJson = "", displayName = "",
			dealerlevallistReturnJson = "", levellistActionName = "";
	private int add_edit = 0;
	private SharedPreferences sp;
	private CustomProgressDialog progressdialog;
	private CustomerUser editCustomerUser;
	private ArrayList<DealerLevel> customerlever_List;
	MyBroadcastReceiver receiverCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_clientmanage_addoredit_client);
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		// 注册广播接收器
		receiverCity = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter("com.province_city.rocky");
		registerReceiver(receiverCity, filter);
		context = this;
		customerlever_List = new ArrayList<DealerLevel>();
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		province_city_tv = (TextView) findViewById(R.id.province_city_tv);
		province_city_tv.setOnClickListener(onClickListener);
		cancel_tv = (TextView) findViewById(R.id.cancel_tv);
		province_city_tv.setOnClickListener(onClickListener);
		finish_tv = (TextView) findViewById(R.id.finish_tv);
		cancel_tv.setOnClickListener(onClickListener);
		finish_tv.setOnClickListener(onClickListener);
		province_city_tv.setOnClickListener(onClickListener);
		username_et = (EditText) findViewById(R.id.username_et);
		password_et = (ClearEditText) findViewById(R.id.password_et);
		comfirmPassword_et = (ClearEditText) findViewById(R.id.comfirmPassword_et);
		contactPerson_et = (ClearEditText) findViewById(R.id.contactPerson_et);
		contactPhone_et = (ClearEditText) findViewById(R.id.contactPhone_et);
		password_ll = (LinearLayout) findViewById(R.id.password_ll);
		dealer_extra_info_ll = (LinearLayout) findViewById(R.id.dealer_extra_info_ll);
		setgrad_ll = (LinearLayout) findViewById(R.id.setgrad_ll);
		setgrad_ll.setOnClickListener(onClickListener);
		companyName_et = (ClearEditText) findViewById(R.id.companyName_et);
		startValidDay_tv = (TextView) findViewById(R.id.startValidDay_tv);
		endValidDay_tv = (TextView) findViewById(R.id.endValidDay_tv);
		startValidDay_tv.setOnClickListener(onClickListener);
		endValidDay_tv.setOnClickListener(onClickListener);
		title_tv = (TextView) findViewById(R.id.title_tv);
		default_grad_tv = (TextView) findViewById(R.id.default_grad_tv);
		startValidDay = DateUtil.GetTodayDate();
		startValidDay_tv.setText(DateUtil.GetTodayDate());

		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		if (bundle != null) {
			displayName = bundle
					.containsKey(ActivityClientManageSetGrad.DISPLAY_TYPENAME_STRING) ? bundle
					.getString(ActivityClientManageSetGrad.DISPLAY_TYPENAME_STRING)
					: "";
			add_edit = bundle.containsKey(EDIT_OR_ADD) ? bundle
					.getInt(EDIT_OR_ADD) : 0;
			if (displayName
					.equals(ActivityClientManageSetGrad.CUSTOMER_DISPLAYNAME)) {
				levellistActionName = "customerlevallist";
			} else if (displayName
					.equals(ActivityClientManageSetGrad.DEALER_DISPLAYNAME)) {
				levellistActionName = "dealerlevallist";
			}
			title_tv.setText("添加" + displayName);
			startQueryGrad();
			if (add_edit == 1) {// 编辑
				title_tv.setText("编辑" + displayName);
				if (displayName
						.equals(ActivityClientManageSetGrad.CUSTOMER_DISPLAYNAME)) {
					modifyAction = "modifycustomer";
				} else if (displayName
						.equals(ActivityClientManageSetGrad.DEALER_DISPLAYNAME)) {
					modifyAction = "modifydealer";
				}
				password_ll.setVisibility(View.GONE);
				String cuString = bundle.containsKey(CUSTOMERINFO_OF_EDIT) ? bundle
						.getString(CUSTOMERINFO_OF_EDIT) : "";
				try {
					editCustomerUser = JSONHelper.parseObject(cuString,
							CustomerUser.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				default_grad_tv.setText(editCustomerUser.getDealerLevel());
				levelId = editCustomerUser.getLevelID();
				// username_et.setText(!editCustomerUser.getUserName().equals("null")?editCustomerUser.getUserName():"");
				username_et.setText(editCustomerUser.getUserName());
				username_et.setEnabled(false);
				// contactPerson_et.setText(!editCustomerUser.getRealName().equals("null")?editCustomerUser.getRealName():"");
				contactPerson_et.setText(editCustomerUser.getRealName());
				contactPerson_et.setEnabled(false);
				// contactPhone_et.setText(!editCustomerUser.getPhone().equals("null")?editCustomerUser.getPhone():"");
				contactPhone_et.setText(editCustomerUser.getPhone());
				contactPhone_et.setEnabled(false);
				province_city_tv.setText(editCustomerUser.getCityName() + "-"
						+ editCustomerUser.getProvinceName());
				startValidDay_tv.setText(editCustomerUser.getStartDate());
				endValidDay_tv.setText(editCustomerUser.getEndDate());
				startValidDay = editCustomerUser.getStartDate();
				endValidDay = editCustomerUser.getEndDate();
				companyName_et.setText(editCustomerUser.getCompanyName());
			}
		}
		if (displayName
				.equals(ActivityClientManageSetGrad.CUSTOMER_DISPLAYNAME)) {
			addAction = "addcustomeruser";
			dealer_extra_info_ll.setVisibility(View.GONE);
		} else if (displayName
				.equals(ActivityClientManageSetGrad.DEALER_DISPLAYNAME)) {
			addAction = "adddealeruser";
			dealer_extra_info_ll.setVisibility(View.VISIBLE);
		}
	}

	public void onDestroy() {
		unregisterReceiver(receiverCity);
		super.onDestroy();
	}

	View.OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Date date = null;
				switch (arg0.getId()) {
				case R.id.setgrad_ll:
					iniPopupWindow(0, initLevelData());
					pwMyPopWindow.showAtLocation(setgrad_ll, Gravity.BOTTOM, 0,
							0);
					break;
				case R.id.province_city_tv:
					SelectProvinceCityAlertDialog sad = new SelectProvinceCityAlertDialog(
							ActivityClientManageAddoredit.this);
					break;
				case R.id.cancel_tv:
					finish();
					break;
				case R.id.finish_tv:
					if (add_edit == 0 && validInput())// 新增
						startAdd();
					else if (add_edit == 1 && validInput())// 修改
						startModify();
					break;
				case R.id.startValidDay_tv:
					Calendar c1 = Calendar.getInstance();
					try {
						if (!startValidDay.isEmpty()) {
							date = sdf.parse(startValidDay);
							c1.setTime(date);
						}
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}
					new DatePickerDialog(context,
							new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									startValidDay_tv.setText(year + "/"
											+ (monthOfYear + 1) + "/"
											+ dayOfMonth);
									startValidDay = year + "/"
											+ (monthOfYear + 1) + "/"
											+ dayOfMonth;
								}
							}, c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
							c1.get(Calendar.DAY_OF_MONTH)).show();
					break;
				case R.id.endValidDay_tv:
					Calendar c11 = Calendar.getInstance();
					try {
						if (!startValidDay.isEmpty()) {
							date = sdf.parse(endValidDay);
							c11.setTime(date);
						}
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}
					new DatePickerDialog(context,
							new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									endValidDay_tv.setText(year + "/"
											+ (monthOfYear + 1) + "/"
											+ dayOfMonth);
									endValidDay = year + "/"
											+ (monthOfYear + 1) + "/"
											+ dayOfMonth;
								}
							}, c11.get(Calendar.YEAR), c11.get(Calendar.MONTH),
							c11.get(Calendar.DAY_OF_MONTH)).show();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String content = intent.getStringExtra("msgContent");
				province_city_tv.setText(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void startQueryGrad() {
		if (HttpUtils.showNetCannotUse(context)) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String str = "{\"userID\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\"}";
					String param = "action="
							+ levellistActionName
							+ "&str="
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
									+ levellistActionName + str);
					dealerlevallistReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = DEALERLEVELMSGCODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void startAdd() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String provinceCityString = province_city_tv.getText()
							.toString().trim();
					String city = "", province = "";
					if (provinceCityString.length() > 0
							&& provinceCityString.contains("-")) {
						city = provinceCityString.substring(0,
								provinceCityString.indexOf("-"));
						province = provinceCityString
								.substring(provinceCityString.indexOf("-") + 1);
					}
					String str = "";
					if (addAction.equals("addcustomeruser"))
						str = "{\"userID\":\""
								+ sp.getString(SPkeys.userid.getString(), "")
								+ "\",\"userName\":\""
								+ username_et.getText().toString().trim()
								+ "\",\"userPass\":\""
								+ password_et.getText().toString().trim()
								+ "\",\"contactName\":\""
								+ contactPerson_et.getText().toString().trim()
								+ "\",\"contactPhone\":\""
								+ contactPhone_et.getText().toString().trim()
								+ "\",\"dealerLevel\":\"" + levelId
								+ "\",\"province\":\"" + province
								+ "\",\"city\":\"" + city + "\"}";
					else if (addAction.equals("adddealeruser")) {
						str = "{\"userID\":\""
								+ sp.getString(SPkeys.userid.getString(), "")
								+ "\",\"userName\":\""
								+ username_et.getText().toString().trim()
								+ "\",\"userPass\":\""
								+ password_et.getText().toString().trim()
								+ "\",\"contactName\":\""
								+ contactPerson_et.getText().toString().trim()
								+ "\",\"contactPhone\":\""
								+ contactPhone_et.getText().toString().trim()
								+ "\",\"dealerLevel\":\"" + levelId
								+ "\",\"startDate\":\"" + startValidDay
								+ "\",\"endDate\":\"" + endValidDay
								+ "\",\"companyName\":\""
								+ companyName_et.getText().toString().trim()
								+ "\",\"province\":\"" + province
								+ "\",\"city\":\"" + city + "\"}";
					}
					String param = "action="
							+ addAction
							+ "&sitekey="
							+ MyApp.sitekey
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ addAction + str);
					try {
						str = URLEncoder.encode(str, "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					// addReturnJson = HttpUtils.myPost(ma.getServeUrl() +
					// param,
					// "&str=" + str);
					addReturnJson = HttpUtils.getJsonContent(ma.getServeUrl(),
							param + "&str=" + str);
					Message msg = new Message();
					msg.what = ADD_CUSTOMER_MSG_CODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在提交新" + displayName + "注册信息，请稍候...");
		progressdialog.setCancelable(true);
		progressdialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		progressdialog.show();
	}

	private void startModify() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String provinceCityString = province_city_tv.getText()
							.toString().trim();
					String city = "", province = "";
					if (provinceCityString.length() > 0
							&& provinceCityString.contains("-")) {
						city = provinceCityString.substring(0,
								provinceCityString.indexOf("-"));
						province = provinceCityString
								.substring(provinceCityString.indexOf("-") + 1);
					}
					String str = "";
					if (addAction.equals("addcustomeruser"))
						str = "{\"userID\":\""
								+ sp.getString(SPkeys.userid.getString(), "")
								+ "\",\"userName\":\""
								+ username_et.getText().toString().trim()
								+ "\",\"dealerLevel\":\"" + levelId
								+ "\",\"provinceName\":\"" + province
								+ "\",\"cityName\":\"" + city + "\"}";
					else if (addAction.equals("adddealeruser")) {
						str = "{\"userID\":\""
								+ sp.getString(SPkeys.userid.getString(), "")
								+ "\",\"userName\":\""
								+ username_et.getText().toString().trim()
								+ "\",\"dealerLevel\":\"" + levelId
								+ "\",\"startDate\":\"" + startValidDay
								+ "\",\"endDate\":\"" + endValidDay
								+ "\",\"companyName\":\""
								+ companyName_et.getText().toString().trim()
								+ "\",\"provinceName\":\"" + province
								+ "\",\"cityName\":\"" + city + "\"}";
					}
					String param = "action="
							+ modifyAction
							+ "&sitekey="
							+ MyApp.sitekey
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ modifyAction + str);
					try {
						str = URLEncoder.encode(str, "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					addReturnJson = HttpUtils.getJsonContent(ma.getServeUrl(),
							param + "&str=" + str);
					Message msg = new Message();
					msg.what = MODIFY_CUSTOMER_MSG_CODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在提交" + displayName + "修改信息，请稍候...");
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
			JSONTokener jsonParser;
			Boolean isModify = false;
			switch (msg.what) {
			case DEALERLEVELMSGCODE:
				jsonParser = new JSONTokener(dealerlevallistReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						JSONArray cArray = jsonObject.getJSONArray("d");
						customerlever_List.clear();
						for (int i = 0; i < cArray.length(); i++) {
							DealerLevel cUser = JSONHelper.parseObject(
									cArray.getJSONObject(i), DealerLevel.class);
							customerlever_List.add(cUser);
						}
					} else {
						String message = jsonObject.getJSONObject("d")
								.getString("msg");
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
			case MODIFY_CUSTOMER_MSG_CODE:
				isModify = true;
			case ADD_CUSTOMER_MSG_CODE:
				jsonParser = new JSONTokener(addReturnJson);
				try {
					progressdialog.dismiss();
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					String message = "";
					if (state.equals("0000")) {
						message = isModify == true ? "修改成功" : "添加成功";
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle(message);
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
								finish();
							}
						});
					} else {
						message = jsonObject.getJSONObject("d")
								.getString("msg");
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
					progressdialog.dismiss();
					e.printStackTrace();
					Toast.makeText(context, "发生未知异常,操作失败", 0).show();
				}
				break;
			}
		}
	};

	private Boolean validInput() {
		final CustomerAlertDialog cad = new CustomerAlertDialog(context, true);
		// , contactPhone_et, ;
		if (!CommonFunc
				.isValidUserName(username_et.getText().toString().trim())) {
			cad.setTitle("请输入用户名(由字母、数字或下划线组成长度为6-12位)");
			cad.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if (add_edit == 0
				&& !CommonFunc.isValidPassword(password_et.getText().toString()
						.trim())) {
			cad.setTitle("为保证密码的安全性，请输入6-20位的数字或字母的组合！");
			cad.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if (contactPerson_et.getText().toString().trim().isEmpty()) {
			cad.setTitle("请输入联系人姓名");
			cad.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if (add_edit == 0
				&& comfirmPassword_et.getText().toString().trim().isEmpty()) {
			cad.setTitle("请再次输入密码");
			cad.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if (!CommonFunc.isMobileNO(contactPhone_et.getText().toString().trim())) {
			cad.setTitle("请输入合法的手机号码");
			cad.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if (province_city_tv.getText().toString().trim().isEmpty()) {
			cad.setTitle("请选择用户所在城市");
			cad.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if (add_edit == 0
				&& !password_et.getText().toString().trim()
						.equals(comfirmPassword_et.getText().toString().trim())) {
			cad.setTitle("两次输入密码 不一致");
			cad.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if (addAction.equals("adddealeruser")
				&& companyName_et.getText().toString().trim().isEmpty()) {
			cad.setTitle("请输入客户公司名");
			cad.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if (addAction.equals("adddealeruser") && startValidDay.equals("")) {
			cad.setTitle("选择用户有效期开始日期");
			cad.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if (addAction.equals("adddealeruser") && endValidDay.equals("")) {
			cad.setTitle("选择用户有效期结束日期");
			cad.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		cad.dismiss();
		return true;
	}

	private PopupWindow pwMyPopWindow;// popupwindow
	private ListView lvPopupList;
	private int currentID = 0;
	private String levelId = "";

	private void iniPopupWindow(final int xjOrJg,
			final List<Map<String, Object>> list1) {
		final LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.popupwindow_list_select, null);
		lvPopupList = (ListView) layout.findViewById(R.id.lv_popup_list);
		pwMyPopWindow = new PopupWindow(layout);
		pwMyPopWindow.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件

		MyListAdapter adapter = new MyListAdapter(context, list1);
		adapter.setCurrentID(currentID);
		lvPopupList.setAdapter(adapter);
		lvPopupList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				default_grad_tv
						.setText(list1.get(position).get("title") != null ? list1
								.get(position).get("title").toString()
								: "");
				currentID = position;
				levelId = customerlever_List.get(currentID).getLevalID();
				pwMyPopWindow.dismiss();
			}
		});
		// 控制popupwindow的宽度和高度自适应
		lvPopupList.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		pwMyPopWindow.setWidth(LayoutParams.FILL_PARENT);// lvPopupList.getMeasuredWidth()
		pwMyPopWindow.setHeight(LayoutParams.FILL_PARENT);// ((lvPopupList.getMeasuredHeight())*
															// list1.size());
		pwMyPopWindow.setAnimationStyle(R.style.AnimBottomPopup);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 控制popupwindow点击屏幕其他地方消失
		pwMyPopWindow.setBackgroundDrawable(dw);// (new BitmapDrawable());//
												// 设置背景图片，不能在布局中设置，要通过代码来设置
		pwMyPopWindow.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功，如上

		// 对弹出的全屏选择框添加OnTouchListener监听判断获取触屏位置，如果在listview外面则销毁弹出框
		layout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					View layout = inflater.inflate(
							R.layout.popupwindow_list_select, null);
					int height = lvPopupList.getTop();
					int y = (int) event.getY();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (y < height) {
							pwMyPopWindow.dismiss();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});
	}

	private ArrayList<Map<String, Object>> initLevelData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < customerlever_List.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", customerlever_List.get(i).getLevalName());
			list.add(map);
		}
		return list;
	}

	private class MyListAdapter extends BaseAdapter {
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		private LayoutInflater inflater;
		List<Map<String, Object>> list;
		Context c;
		int currentID = 0;

		public MyListAdapter(Context context, List<Map<String, Object>> list2) {
			inflater = LayoutInflater.from(context);
			this.c = context;
			this.list = list2;
		}

		public void setList(ArrayList<Map<String, Object>> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				Holder myHolder;
				if (convertView == null) {
					myHolder = new Holder();
					convertView = inflater.inflate(
							R.layout.item_train_baoxian_list_single, null);
					myHolder.title = (TextView) convertView
							.findViewById(R.id.title);
					myHolder.iv = (ImageView) convertView
							.findViewById(R.id.img);
					convertView.setTag(myHolder);
				} else {
					myHolder = (Holder) convertView.getTag();
				}
				if (position == this.currentID)
					myHolder.iv.setBackgroundDrawable(c.getResources()
							.getDrawable(R.drawable.radio_clk));
				else
					myHolder.iv.setBackgroundDrawable(c.getResources()
							.getDrawable(R.drawable.radio));
				myHolder.title
						.setText(list.get(position).get("title") != null ? list
								.get(position).get("title").toString() : "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}

		class Holder {
			ImageView iv;
			TextView title;
		}

		public void setCurrentID(int currentID) {
			this.currentID = currentID;
		}
	}
}
