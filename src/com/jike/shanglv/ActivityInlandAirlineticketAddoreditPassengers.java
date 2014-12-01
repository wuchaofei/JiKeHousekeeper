package com.jike.shanglv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jike.shanglv.Common.ClearEditText;
import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Common.IDCard;
import com.jike.shanglv.Common.IdType;
import com.jike.shanglv.Models.Passenger;
import com.jike.shanglv.NetAndJson.JSONHelper;


public class ActivityInlandAirlineticketAddoreditPassengers extends Activity {

	protected static final int EDIT_PASSENGER_CODE = 2;
	protected static final String TOKEN_NAME = "PASSENGER";

	private TextView cancel_tv, finish_tv, passengerType_tv,
			identificationType_tv;
	private ClearEditText passengerName_et, identificationNum_et, phoneNum_et;
	// private Button shenfenzheng_btn, huzhao_btn, gangaotongxingzheng_btn,
	// taobaozheng_btn, qita_btn, chengren_btn, ertongpiao_btn,
	// yingerpiao_btn;
	private PopupWindow popupWindow_idtype, popupWindow_ptype;
	private View popupWindowView_idtype, popupWindowView_ptype;
	private Context context;
	InputMethodManager imm;
	private Passenger editPassenger;
	private ArrayList<Passenger> passengerList;// 所有联系人的列表

	// 选择乘机人页面点击编辑后传过来的乘机人信息
	private String passengerString = "";// 所有联系人信息反序列化后传过来
	private int index = 0;// 当前编辑联系人在所有联系人列表中的序号
	private String systype = "0";// "systype":"0国内 1国际 2火车票"
	private Boolean saveAsContact = true;// 保存为常用联系人

	// 国际机票乘机人编辑
	private TextView IDdeadline_et, nation_et, gender_et, birthDay_et;
	private RelativeLayout IDdeadline_rl, nation_rl, gender_rl, birthDay_rl,
			savecontact_rl;
	private ImageView savecontact_checkbox_iv;
	private String IDdeadline = "", nation = "", gender = "", birthDay = "",
			issueAt = "";
	String genderValue = "男", genderKey = "1";// 性别选择，用户不选择默认时的值
	private EditText issueAt_et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			Intent intent = getIntent();
			if (intent != null) {
				if (intent
						.hasExtra(ActivityInlandAirlineticketSelectPassengers.SYSTYPE)) {
					systype = intent
							.getStringExtra(ActivityInlandAirlineticketSelectPassengers.SYSTYPE);
				}
			}
			if (systype.equals("0") || systype.equals("2")) {// 国内机票或火车票
				setContentView(R.layout.activity_inland_airlineticket_addoredit_passengers);
			} else if (systype.equals("1")) {// 国际需求单
				setContentView(R.layout.activity_international_airlineticket_addoredit_passengers);
				initInternationalView();
			}
			initView();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception ce) {
			ce.printStackTrace();
		}
	}

	private void initView() {
		context = this;
		passengerList = new ArrayList<Passenger>();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		cancel_tv = (TextView) findViewById(R.id.cancel_tv);
		finish_tv = (TextView) findViewById(R.id.finish_tv);
		passengerType_tv = (TextView) findViewById(R.id.passengerType_tv);
		identificationType_tv = (TextView) findViewById(R.id.identificationType_tv);
		passengerName_et = (ClearEditText) findViewById(R.id.passengerName_et);
		identificationNum_et = (ClearEditText) findViewById(R.id.identificationNum_et);
		phoneNum_et = (ClearEditText) findViewById(R.id.phoneNum_et);
		savecontact_rl = (RelativeLayout) findViewById(R.id.savecontact_rl);
		savecontact_checkbox_iv = (ImageView) findViewById(R.id.savecontact_checkbox_iv);

		cancel_tv.setOnClickListener(clickListener);
		finish_tv.setOnClickListener(clickListener);
		identificationType_tv.setOnClickListener(clickListener);
		passengerType_tv.setOnClickListener(clickListener);
		savecontact_rl.setOnClickListener(clickListener);

		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra("passengerList") && intent.hasExtra("index")) {
				passengerString = intent.getStringExtra("passengerList");
				index = intent.getIntExtra("index", 0);
			} else {
				return;
			}
			try {
				passengerList = (ArrayList<Passenger>) JSONHelper
						.parseCollection(passengerString, List.class,
								Passenger.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			editPassenger = passengerList.get(index);
			if (editPassenger.getAddto() != null
					&& !editPassenger.getAddto().equals("null")) {
				saveAsContact = editPassenger.getAddto() == "1" ? true : false;
				if (saveAsContact) {
					savecontact_checkbox_iv.setBackground(context
							.getResources().getDrawable(
									R.drawable.fuxuankuang_yes));
				} else {
					savecontact_checkbox_iv.setBackground(context
							.getResources().getDrawable(
									R.drawable.fuxuankuang_no));
				}
			}
			if (editPassenger.getPassengerType() != null
					&& !editPassenger.getPassengerType().equals("null"))
				passengerType_tv.setText(editPassenger.getPassengerType());
			if (editPassenger.getPassengerName() != null
					&& !editPassenger.getPassengerName().equals("null"))
				passengerName_et.setText(editPassenger.getPassengerName());
			if (editPassenger.getIdentificationNum() != null
					&& !editPassenger.getIdentificationNum().equals("null"))
				identificationNum_et.setText(editPassenger
						.getIdentificationNum());
			if (editPassenger.getIdentificationType() != null
					&& !editPassenger.getIdentificationType().equals("null"))
				identificationType_tv.setText(editPassenger
						.getIdentificationType());
			if (editPassenger.getMobie() != null
					&& !editPassenger.getMobie().equals("null"))
				phoneNum_et.setText(editPassenger.getMobie());
			if (systype.equals("1")) {// 若为国际机票
				// issueAt_et,IDdeadline_et, nation_et, gender_et, birthDay_et
				if (editPassenger.getIssueAt() != null
						&& !editPassenger.getIssueAt().equals("null"))
					issueAt_et.setText(editPassenger.getIssueAt());
				if (editPassenger.getIDdeadline() != null
						&& !editPassenger.getIDdeadline().equals("null"))
					IDdeadline_et.setText(editPassenger.getIDdeadline());
				if (editPassenger.getNation() != null
						&& !editPassenger.getNation().equals("null"))
					nation_et.setText(editPassenger.getNation());
				if (editPassenger.getGender() != null
						&& !editPassenger.getGender().equals("null"))
					gender_et.setText(editPassenger.getGender());
				if (editPassenger.getBirthDay() != null
						&& !editPassenger.getBirthDay().equals("null"))
					birthDay_et.setText(editPassenger.getBirthDay());
			}

			if (passengerName_et.getText().toString().trim().equals("")) {// 新建联系人，默认选中保存为常用联系人
				saveAsContact = true;
				savecontact_checkbox_iv.setBackground(context.getResources()
						.getDrawable(R.drawable.fuxuankuang_yes));
			}
		}
	}

	private void initInternationalView() {
		IDdeadline_et = (TextView) findViewById(R.id.IDdeadline_et);
		nation_et = (TextView) findViewById(R.id.nation_et);
		gender_et = (TextView) findViewById(R.id.gender_et);
		birthDay_et = (TextView) findViewById(R.id.birthDay_et);

		issueAt_et = (EditText) findViewById(R.id.issueAt_et);

		IDdeadline_rl = (RelativeLayout) findViewById(R.id.IDdeadline_rl);
		nation_rl = (RelativeLayout) findViewById(R.id.nation_rl);
		gender_rl = (RelativeLayout) findViewById(R.id.gender_rl);
		birthDay_rl = (RelativeLayout) findViewById(R.id.birthDay_rl);
		IDdeadline_rl.setOnClickListener(clickListener);
		nation_rl.setOnClickListener(clickListener);
		gender_rl.setOnClickListener(clickListener);
		birthDay_rl.setOnClickListener(clickListener);
	}

	/**
	 * 点击空白处隐藏键盘
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				System.out.println("down");
				if (ActivityInlandAirlineticketAddoreditPassengers.this
						.getCurrentFocus() != null) {
					if (ActivityInlandAirlineticketAddoreditPassengers.this
							.getCurrentFocus().getWindowToken() != null) {
						imm.hideSoftInputFromWindow(
								ActivityInlandAirlineticketAddoreditPassengers.this
										.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}
			}
		} catch (Exception ce) {
			ce.printStackTrace();
		}
		return super.onTouchEvent(event);
	}

	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.cancel_tv://
					finish();
					break;
				case R.id.savecontact_rl:
					saveAsContact = !saveAsContact;
					if (saveAsContact) {
						savecontact_checkbox_iv.setBackground(context
								.getResources().getDrawable(
										R.drawable.fuxuankuang_yes));
					} else {
						savecontact_checkbox_iv.setBackground(context
								.getResources().getDrawable(
										R.drawable.fuxuankuang_no));
					}
					break;
				case R.id.finish_tv:
					if (!validInput())
						break;
					Passenger passenger = new Passenger();
					passenger.setAddto(saveAsContact ? "1" : "0");
					passenger.setPassengerName(passengerName_et.getText()
							.toString().trim());
					passenger.setIdentificationNum(identificationNum_et
							.getText().toString().trim());
					passenger.setMobie(phoneNum_et.getText().toString().trim());
					passenger.setPassengerType(passengerType_tv.getText()
							.toString().trim());
					passenger.setIdentificationType(identificationType_tv
							.getText().toString().trim());
					// passenger.setNation(nation);
					// passenger.setGender(gender);
					// passenger.setBirthDay(birthDay);
					// passenger.setIDdeadline(IDdeadline);
					if (systype.equals("1")){
						passenger.setNation(nation_et.getText().toString());
						passenger.setGender(gender_et.getText().toString());
						passenger.setBirthDay(birthDay_et.getText().toString());
						passenger.setIDdeadline(IDdeadline_et.getText().toString());
						issueAt = issueAt_et.getText().toString().trim();
						passenger.setIssueAt(issueAt);
					}
					if (passenger.getPassengerName().equals(null)
							|| passenger.getPassengerName().trim().equals("")) {
						passengerList.remove(index);
					} else {
						passengerList.set(index, passenger);// 用新的编辑后的联系人替换联系人列表中原来的联系人
					}
					String passengerJsonString = JSONHelper
							.toJSON(passengerList);
					setResult(
							EDIT_PASSENGER_CODE,
							getIntent()
									.putExtra(
											ActivityInlandAirlineticketBooking.ALLPASSENGERSLIST,
											passengerJsonString));
					finish();// 返回所有联系人的列表
					break;
				case R.id.identificationType_tv:
					imm.hideSoftInputFromWindow(
							ActivityInlandAirlineticketAddoreditPassengers.this
									.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					// popupWindow_idtype.showAtLocation(shenfenzheng_btn,
					// Gravity.BOTTOM, 0, 0);
					if (systype.equals("1")) {// 若为国际机票
						iniPopupWindow(0,
								initInternationalIdentificationTypeData());// 国际不能使用身份证
					} else {
						iniPopupWindow(0, initInlandIdentificationTypeData());
					}
					pwMyPopWindow.showAtLocation(finish_tv, Gravity.BOTTOM, 0,
							0);
					break;
				case R.id.passengerType_tv:
					imm.hideSoftInputFromWindow(
							ActivityInlandAirlineticketAddoreditPassengers.this
									.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					// popupWindow_ptype.showAtLocation(chengren_btn,
					// Gravity.BOTTOM, 0, 0);

					iniPopupWindow(0, initPassengerTypeData());
					pwMyPopWindow.showAtLocation(finish_tv, Gravity.BOTTOM, 0,
							0);
					break;
				case R.id.IDdeadline_rl:
					Calendar c = Calendar.getInstance();
					new DatePickerDialog(context,
							new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									// TODO Auto-generated method stub
									IDdeadline_et.setText(year + "/"
											+ (monthOfYear + 1) + "/"
											+ dayOfMonth);
									IDdeadline = year + "/" + (monthOfYear + 1)
											+ "/" + dayOfMonth;
								}
							}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
							c.get(Calendar.DAY_OF_MONTH)).show();
					break;
				case R.id.birthDay_rl:
					Calendar c1 = Calendar.getInstance();
					new DatePickerDialog(context,
							new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									// TODO Auto-generated method stub
									birthDay_et.setText(year + "/"
											+ (monthOfYear + 1) + "/"
											+ dayOfMonth);
									birthDay = year + "/" + (monthOfYear + 1)
											+ "/" + dayOfMonth;
								}
							}, c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
							c1.get(Calendar.DAY_OF_MONTH)).show();
					break;
				case R.id.gender_rl:
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setTitle("请选择性别");
					final String[] sex = { "女", "男" };
					// 设置一个单项选择下拉框
					/**
					 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
					 * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
					 * 第三个参数给每一个单选项绑定一个监听器
					 */
					builder.setSingleChoiceItems(sex, 1,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// Toast.makeText(context, "性别为：" +
									// sex[which],
									// Toast.LENGTH_SHORT).show();
									genderValue = (sex[which]);
									genderKey = which + "";
									gender_et.setText(genderValue);
									gender = genderKey;
								}
							});
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									gender_et.setText(genderValue);
									gender = genderKey;
								}
							});
					builder.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
					builder.show();
					break;
				case R.id.nation_rl:
					AlertDialog.Builder builder1 = new AlertDialog.Builder(
							context);
					builder1.setTitle("请选择国籍");
					// 指定下拉列表的显示数据
					final String[] cities = { "中国", "台湾", "香港", "澳门", "新加坡",
							"日本", "韩国", "美国", "加拿大", "法国", "英国", "其他" };
					// 设置一个下拉的列表选择项
					builder1.setItems(cities,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// Toast.makeText(context, "选择的国籍为：" +
									// cities[which],
									// Toast.LENGTH_SHORT).show();
									nation_et.setText(cities[which]);
									nation = cities[which];
								}
							});
					builder1.show();
					break;
				default:
					break;
				}
			} catch (Exception ce) {
				ce.printStackTrace();
			}
		}
	};

	OnClickListener popupClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			identificationType_tv.setText(btn.getText());
			popupWindow_idtype.dismiss();
		}
	};

	OnClickListener popupClickListener_ptype = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			passengerType_tv.setText(btn.getText());
			popupWindow_ptype.dismiss();
		}
	};

	// 输入合法性判断
	private Boolean validInput() {
		if (passengerName_et.getText().toString().trim().length() == 0) {
			// new AlertDialog.Builder(context).setTitle("姓名不能为空")
			// .setMessage("请输入乘客姓名！").setPositiveButton("确定", null)
			// .show();
			final CustomerAlertDialog cad = new CustomerAlertDialog(context,
					true);
			cad.setTitle("请输入乘客姓名");
			cad.setPositiveButton("知道了", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if (!systype.equals("1")
				&& phoneNum_et.getText().toString().trim().length() > 0
				&& (CommonFunc.isMobileNO(phoneNum_et.getText().toString()
						.trim()) == false && CommonFunc.isPhone(phoneNum_et
						.getText().toString().trim()) == false)) {
			// new AlertDialog.Builder(context).setTitle("电话号码格式不正确")
			// .setMessage("请输入合法的手机号码或座机号码！")
			// .setPositiveButton("确定", null).show();
			final CustomerAlertDialog cad = new CustomerAlertDialog(context,
					true);
			cad.setTitle("请输入合法的手机号码或座机号码");
			cad.setPositiveButton("知道了", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if (identificationNum_et.getText().toString().trim().length() == 0) {
			// new AlertDialog.Builder(context).setTitle("证件号码不能为空")
			// .setMessage("请输入证件号码！").setPositiveButton("确定", null)
			// .show();
			final CustomerAlertDialog cad = new CustomerAlertDialog(context,
					true);
			cad.setTitle("请输入证件号码");
			cad.setPositiveButton("知道了", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}
			});
			return false;
		}
		if ((identificationType_tv.getText().toString().trim())
				.equals(IdType.IdType.get(0))) {
			if (!(new IDCard().verify(identificationNum_et.getText().toString()
					.trim()))) {
				// new AlertDialog.Builder(context).setTitle("身份证号不合法")
				// .setMessage("请输入合法的身份证号码！")
				// .setPositiveButton("确定", null).show();
				final CustomerAlertDialog cad = new CustomerAlertDialog(
						context, true);
				cad.setTitle("请输入合法的身份证号码");
				cad.setPositiveButton("知道了", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						cad.dismiss();
					}
				});
				return false;
			}
		}
		if (systype.equals("1")) {
			if (!CommonFunc.isEnglishName(passengerName_et.getText().toString()
					.trim())) {
				// new AlertDialog.Builder(context).setTitle("姓名格式不正确")
				// .setMessage("请输入英文名，姓氏和名字之间以斜杠分割，格式为\"zhang/san\"")
				// .setPositiveButton("确定", null).show();
				final CustomerAlertDialog cad = new CustomerAlertDialog(
						context, true);
				cad.setTitle("请输入英文名，姓氏和名字之间以斜杠分割，例如:\"zhang/san\"");
				cad.setPositiveButton("知道了", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						cad.dismiss();
					}
				});
				return false;
			}
			if (IDdeadline_et.getText().toString().trim().length() == 0) {
				// new AlertDialog.Builder(context).setTitle("请选择证件有效期")
				// .setMessage("请选择证件有效期！").setPositiveButton("确定", null)
				// .show();
				final CustomerAlertDialog cad = new CustomerAlertDialog(
						context, true);
				cad.setTitle("请选择证件有效期");
				cad.setPositiveButton("知道了", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						cad.dismiss();
					}
				});
				return false;
			}
			if (nation_et.getText().toString().trim().length() == 0) {
				// new AlertDialog.Builder(context).setTitle("请选择国籍")
				// .setMessage("请选择国籍！").setPositiveButton("确定", null)
				// .show();
				final CustomerAlertDialog cad = new CustomerAlertDialog(
						context, true);
				cad.setTitle("请选择国籍");
				cad.setPositiveButton("知道了", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						cad.dismiss();
					}
				});
				return false;
			}
			if (issueAt_et.getText().toString().trim().length() == 0) {
				// new AlertDialog.Builder(context).setTitle("请输入证件签发地")
				// .setMessage("请输入证件签发地！").setPositiveButton("确定", null)
				// .show();
				final CustomerAlertDialog cad = new CustomerAlertDialog(
						context, true);
				cad.setTitle("请输入证件签发地");
				cad.setPositiveButton("知道了", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						cad.dismiss();
					}
				});
				return false;
			}
			if (gender_et.getText().toString().trim().length() == 0) {
				// new AlertDialog.Builder(context).setTitle("请选择性别")
				// .setMessage("请选择性别！").setPositiveButton("确定", null)
				// .show();
				final CustomerAlertDialog cad = new CustomerAlertDialog(
						context, true);
				cad.setTitle("请选择性别");
				cad.setPositiveButton("知道了", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						cad.dismiss();
					}
				});
				return false;
			}
			if (birthDay_et.getText().toString().trim().length() == 0) {
				// new AlertDialog.Builder(context).setTitle("请选择出生年月日")
				// .setMessage("请选择出生年月日！").setPositiveButton("确定", null)
				// .show();
				final CustomerAlertDialog cad = new CustomerAlertDialog(
						context, true);
				cad.setTitle("请选择出生年月日");
				cad.setPositiveButton("知道了", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						cad.dismiss();
					}
				});
				return false;
			}
		}
		return true;
	}

	private PopupWindow pwMyPopWindow;// popupwindow
	private ListView lvPopupList;
	private int currentID_ZJ = 0;
	private int currentID_CK = 0;

	/*
	 * xjOrJg 0:证件类型；1：乘客类型
	 */
	private void iniPopupWindow(final int zjOrCk,
			final List<Map<String, Object>> list1) {
		final LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.popupwindow_list_select, null);
		lvPopupList = (ListView) layout.findViewById(R.id.lv_popup_list);
		pwMyPopWindow = new PopupWindow(layout);
		pwMyPopWindow.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件

		MyListAdapter adapter = new MyListAdapter(context, list1);
		adapter.setCurrentID(zjOrCk == 0 ? currentID_ZJ : currentID_CK);
		lvPopupList.setAdapter(adapter);
		lvPopupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (zjOrCk == 0) {// 0:证件类型
					identificationType_tv.setText(list1.get(position)
							.get("title").toString());
					currentID_ZJ = position;
					pwMyPopWindow.dismiss();
				} else if (zjOrCk == 1) {// 1：乘客类型
					passengerType_tv.setText(list1.get(position).get("title")
							.toString());
					currentID_CK = position;
					pwMyPopWindow.dismiss();
				}
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

	private ArrayList<Map<String, Object>> initInlandIdentificationTypeData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "身份证");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "护照");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "港澳通行证");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "台胞证");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "其他");
		list.add(map);
		return list;
	}

	private ArrayList<Map<String, Object>> initInternationalIdentificationTypeData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "护照");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "港澳通行证");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "台胞证");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "其他");
		list.add(map);
		return list;
	}

	private ArrayList<Map<String, Object>> initPassengerTypeData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "成人");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "儿童0");// 以0结尾的数据表示不可选 本次APP客户端不支持儿童和婴儿票
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "婴儿0");
		list.add(map);
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
				String titleString = list.get(position).get("title").toString();
				if (titleString.endsWith("0")) {// 以0结尾的数据表示不可选
					myHolder.title.setText(titleString.substring(0,
							titleString.length() - 1));
					convertView.setOnClickListener(null);
					myHolder.title.setTextColor(getResources().getColor(
							R.color.gray));
				} else {
					myHolder.title.setText(titleString);
					myHolder.title.setTextColor(getResources().getColor(
							R.color.black));
				}
			} catch (Exception ce) {
				ce.printStackTrace();
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
