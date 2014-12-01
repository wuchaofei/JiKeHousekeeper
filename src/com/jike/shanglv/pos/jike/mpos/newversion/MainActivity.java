/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.jike.shanglv.pos.jike.mpos.newversion;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import com.alipay.wireless.Validation.ValifyMoney;
import com.alipay.wireless.util.StringUtil;
import com.alipay.wireless.widget.NoneChineseFormater;
import com.alipay.wireless.widget.TabbarItem;
import com.alipay.wireless.widget.TabbarWidget;
import com.alipay.wireless.widget.TabbarWidget.OnItemClickListener;
import com.jike.shanglv.R;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.pos.alipay.android.mpos.demo.helper.ClientData;
import com.jike.shanglv.pos.alipay.android.mpos.demo.helper.MD5Util;
import com.jike.shanglv.pos.alipay.android.mpos.demo.helper.PartnerConfig;
import com.jike.shanglv.pos.alipay.android.mpos.demo.helper.PayModel;
import com.jike.shanglv.pos.alipay.android.mpos.demo.helper.PosError;
import com.jike.shanglv.pos.alipay.android.mpos.demo.pos.MPosConntion;
import com.jike.shanglv.pos.alipay.android.mpos.demo.pos.ParamsItem;
import com.jike.shanglv.pos.alipay.android.mpos.demo.pos.ResultCallback;

/**
 * 
 * @author jianmin.jiang
 * 
 * @version $Id: MainActivity.java, v 0.1 2012-2-17 下午9:00:13 jianmin.jiang Exp
 *          $
 */
public class MainActivity extends BaseActivity {

	public final static String MPOS_ID = "mpos_id";

	private ClientData data;

	private TextView name,error;
	private EditText receiptNumber;
	// private TextView limit;
	private CheckBox reciptInputOrder;
	private EditText reciptOrderNumber;
	private Button submit;
	private TabbarWidget tabbar;
	
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.mpos_receipt_form);
		
		context = this;
		data = ClientData.create(this);
		name = (TextView) findViewById(R.id.company_name);
		name.setText("可用余额："+PartnerConfig.amount);
		
		findViewById(R.id.back_imgbtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		error = (TextView) findViewById(R.id.num_error);
		receiptNumber = (EditText) findViewById(R.id.receipt_number);
		receiptNumber.setText(getSharedPreferences(SPkeys.SPNAME.getString(), 0).getString(SPkeys.chongZhiJinE.getString(), ""));
		// limit = (TextView) findViewById(R.id.recipt_hint);
		reciptInputOrder = (CheckBox) findViewById(R.id.recipt_input_order);
		reciptOrderNumber = (EditText) findViewById(R.id.recipt_order_number);
		submit = (Button) findViewById(R.id.submit);
		tabbar = (TabbarWidget) findViewById(R.id.tab_bar);

		new SubmitClickable(submit, new EditText[] { receiptNumber,
				reciptOrderNumber });
		reciptOrderNumber.addTextChangedListener(new NoneChineseFormater(
				reciptOrderNumber));
		receiptNumber.addTextChangedListener(watcher);
		reciptInputOrder.setOnCheckedChangeListener(checkedChangeListener);
		submit.setOnClickListener(onSubmit);
		tabbar.setOnItemClickListener(itemListener);

		TabItem tab = new TabItem(this);
		tab.setTabName(getResources().getString(R.string.tab_receipt));
		tab.setTabCode(0);
		tab.setSelectedIconResource(R.drawable.checkstand);
		tab.setUnSelectedIconResource(R.drawable.checkstand_unchecked);
		tabbar.addTabbarItem(tab);
		TabItem tab2 = new TabItem(this);
		tab2.setTabName(getResources().getString(R.string.tab_query));
		tab2.setTabCode(1);
		tab2.setSelectedIconResource(R.drawable.record);
		tab2.setUnSelectedIconResource(R.drawable.record_unchecked);
		tabbar.addTabbarItem(tab2);
		
//		Button yue = (Button) findViewById(R.id.yue);
//		yue.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				show("您的当前余额为：￥" + PartnerConfig.amount);
//			}
//		});
//		
//		Button about = (Button) findViewById(R.id.about);
//		about.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				try {
//					PackageManager pm = context.getPackageManager();
//					PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
//
//					Calendar c = Calendar.getInstance();
//					int year = c.get(Calendar.YEAR);
//				
//				String a = getString(R.string.app_name)
//						+ "\n\n"
//						+ "版本号:"
//						+ pi.versionName
//						+ "\n\nCopyright ? " + year + 
//						"\n51jp.cn Inc.\nAll Rights Reserved.\n版权所有 ? " + year + 
//						"\n上海际珂信息科技有限公司";
//				
//				show(a);
//				} catch (Exception e) {
//				}
//			}
//		});
		
//		Button logout = (Button) findViewById(R.id.logout);
//		logout.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				AlertDialog dialog = createCustomAlterDialog();
//				dialog.setMessage(getString(R.string.logout_client));
//				dialog.setButton(getString(R.string.ok),
//						new DialogInterface.OnClickListener() {
//
//							public void onClick(DialogInterface dialog, int which) {
//								SharedPreferences sharedPref = getSharedPreferences("LOGIN", 0);
//								sharedPref.edit().putString("id", "").commit();
//								sharedPref.edit().putString("code", "").commit();
//								
//								Intent intent = new Intent(context, MopsWelcomeActivity.class);
//								startActivity(intent);
//								finish();
//							}
//						});
//				dialog.setButton2(getString(R.string.cancel),
//						new DialogInterface.OnClickListener() {
//
//							public void onClick(DialogInterface dialog, int which) {
//
//							}
//						});
//				dialog.show();
//			}
//		});
		
//		Button exit = (Button) findViewById(R.id.exit);
//		exit.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
////				alterExit();
//			}
//		});

	}
	
	@Override
	protected void onResume() { 
		super.onResume();
	}

	private void goActivateForm() {
		Intent intent = new Intent(this, MopsWelcomeActivity.class);
		startActivity(intent);
		this.finish();
	}

	private void doQuery(StringBuffer order, ResultCallback callback) {
		MPosConntion.create().process(this, order, callback);
	}

	private void doPos(StringBuffer order, ResultCallback callback) {
		MPosConntion.create().process(this, order, callback);
	}

	private boolean valifyData() {
		boolean canSubmit = false;
		String str = receiptNumber.getText().toString();
		if (new ValifyMoney().valify(str)) {
			canSubmit = true;
//			error.setVisibility(View.GONE);
			if (reciptInputOrder.isChecked()) {
				canSubmit = reciptOrderNumber.getText().length() > 0;
				if (!canSubmit) {
					reciptInputOrder.requestFocus();
				}
			}
		} else {
			if (!StringUtil.isEmpty(str)) {
				error.setVisibility(View.VISIBLE);
				error.setText(R.string.num_fmt_error);
				receiptNumber.requestFocus();
			} else {
//				error.setVisibility(View.GONE);
			}
		}
		SubmitClickable.setSubmitStyle(submit, canSubmit);
		return canSubmit;
	}

	private void checkError(ParamsItem item) {
		if ("error_code".equals(item.getKey())) {
			PosError error = PosError.getEnumByCode(item.getValue());
			if (error == null) {
				return;
			}
			switch (error) {
			// 以下错误码需要重新授权
			case IND_ERR_NOT_EXIST_AID:
			case IND_ERR_INVALID_AID:
			case IND_ERR_NOT_EXIST_MID:
			case IND_ERR_NOT_EXIST_SELLER:
			case IND_ERR_NOT_EXIST_PARTNER:
			case IND_ERR_INVALID_PARTNER:
			case IND_ERR_INVALID_MPOS_ID:
			case IND_ERR_INVALID_MID:
			case IND_ERR_INVALID_SIGN:
				ClientData.create(this).saveMposId("", "", "");
				goActivateForm();
				break;
			case IND_ERR_CLIENT_NEED_UPDATE:
				// 更新自己的客户端
				break;
			default:
				break;
			}
		}
	}

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				reciptOrderNumber.setVisibility(View.VISIBLE);
			} else {
				reciptOrderNumber.setVisibility(View.GONE);
			}
			valifyData();
		}
	};

	private TabbarWidget.OnItemClickListener itemListener = new OnItemClickListener() {

		public void onItemClick(int position, TabbarItem tab) {
			String mposId = ClientData.create(getApplicationContext())
					.getMposid();
			if (StringUtil.isEmpty(mposId)) {
				goActivateForm();
				return;
			}
			if (position == 1) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(new ParamsItem("service", "merchant_query_trade")
						.getParams());
				buffer.append("&");
				buffer.append(new ParamsItem("mpos_id", mposId).getParams());
				doQuery(buffer, queryCallback);
			}
//			if (position != 0) {
//				tabbar.setSelectedIndex(0);
//			}
		}
	};

	private TextWatcher watcher = new TextWatcher() {

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		public void afterTextChanged(Editable s) {
			try{
				if(receiptNumber.getText().length() == 0){
					error.setVisibility(View.GONE);
					return;
				}
				Float ss = Float.parseFloat(receiptNumber.getText().toString());
				ss = (float) (ss*(0.994));
				
				BigDecimal bd = new BigDecimal(Float.toString(ss));
				bd = bd.setScale(2, BigDecimal.ROUND_DOWN);
				
				ss = bd.floatValue();
				bd = null;
				
				error.setVisibility(View.VISIBLE);
				error.setText("实际到帐：" + ss + "元");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	String orderid;

	private View.OnClickListener onSubmit = new OnClickListener() {

		public void onClick(View v) {
			
			if (!valifyData()) {
				return;
			}
			
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault());
			Date date = new Date();
			String strKey = format.format(date);
			
			orderid = strKey + data.getMposid().hashCode();
			
			new Thread(new appinfo()).start();
			
		}
	};
	
	private void go(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(new ParamsItem("service", "merchant_pos").getParams());
		buffer.append("&");
		buffer.append(new ParamsItem("mpos_id", data.getMposid())
				.getParams());
		buffer.append("&");

		String amount = receiptNumber.getText().toString();
		int index = amount.lastIndexOf(".");

		String[] money = new String[2];
		if (index > 0) {
			money[0] = amount.substring(0, index);
			money[1] = amount.substring(index + 1);
			if (money[1] == null || "".equals(money[1])) {
				money[1] = "00";
			}
		} else {
			money[0] = amount;
			money[1] = "00";
		}
		if (money[1].length() == 1) {
			money[1] += "0";
		}
		buffer.append(new ParamsItem("amount", money[0] + "." + money[1])
				.getParams());
		

		
		buffer.append("&");
		buffer.append(new ParamsItem("out_trade_no", orderid).getParams());
		if (reciptInputOrder.isChecked()) {
			buffer.append("&");
			buffer.append(new ParamsItem("ind_order_no", reciptOrderNumber
					.getText().toString()).getParams());
		}
		
		buffer.append("&");
		buffer.append(new ParamsItem("notify_url", PartnerConfig.notify_url).getParams());
		buffer.append("&");
		buffer.append(new ParamsItem("royalty", PartnerConfig.royalty).getParams());

		doPos(buffer, posCallback);
	}
	
	String result;
	
	private class appinfo implements Runnable {

		public void run() {
			
			String s = MobileposApplication.HTTPURL[MobileposApplication.Package_Index] + "/api/json.aspx?action=paylog&sitekey=" +
					MobileposApplication.SITEKEY[MobileposApplication.Package_Index] + "&str={\"amount\":\""
					+ receiptNumber.getText().toString()
					+ "\",\"userid\":\"" + PartnerConfig.userid
					+ "\",\"authcode\":\"\",\"orderid\":\""
					+ orderid + "\",\"paymenttype\":\""
					+ "15" + "\",\"neworderid\":\""
					+ orderid + "\"}";
			
			result = MobileposApplication.getWeb(s);
			Message message = new Message();
			h_appinfo.sendMessage(message);
		}
	}
	
	private Handler h_appinfo = new Handler() {

		public void handleMessage(Message msg) {

			if (result != null) {
				
				try{
					JSONObject jobj = new JSONObject(result);

					String status = jobj.getString("c");
					if (status.equals("0000")) {
						go();

					} else {
						String str = jobj.getJSONObject("d").getString("msg");
						show(str);
					}
				}catch(Exception e){
					show("未知错误，请重试");
				}
			} else {
				show("联网失败，请重试");
			}
		}
	};
	
	public void show(String msg) {
		try{
		new AlertDialog.Builder(this)
				.setMessage(msg)
				.setPositiveButton(getString(R.string.ok),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}

						}).show();
		}catch(Exception e){}
	}

	private ResultCallback queryCallback = new ResultCallback() {

		@Override
		protected void onResultBack(String result) {
			tabbar.setSelectedIndex(0);
			ParamsItem[] items = MD5Util.getResult(result);
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					ParamsItem item = items[i];
					if ("is_success".equals(item.getKey())) {
					} else {
						checkError(item);
					}
				}
			}
		}
	};

	private ResultCallback posCallback = new ResultCallback() {

		@Override
		protected void onResultBack(String result) {
			ParamsItem[] items = MD5Util.getResult(result);
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					ParamsItem item = items[i];
					if ("is_success".equals(item.getKey())) {
						if ("true".equalsIgnoreCase(item.getValue())) {
							if (!MD5Util.checkSign(result,PartnerConfig.MD5_KEY)) {
								// 验签失败，不进行成功后续动作
								return;
							}
							reciptOrderNumber.setText("");
							
							new Thread(new Login()).start();
						}
					} else {
						checkError(item);
					}
				}
			}
		}
	};
	
	private class Login implements Runnable {

		public void run() {
			SharedPreferences sharedPref = getSharedPreferences("LOGIN", 0);
			result = MobileposApplication.getWeb(MobileposApplication.HTTPURL[MobileposApplication.Package_Index] + "/api/json.aspx?action=login&sitekey=" +
					MobileposApplication.SITEKEY[MobileposApplication.Package_Index] + "&str={\"uname\":\"" +
					PartnerConfig.id + "\",\"upass\":\"" +
					PartnerConfig.code + "\",\"attr\":\"" +
					MobileposApplication.ATTR[MobileposApplication.Package_Index] + "\"}");
			Message message = new Message();
			h.sendMessage(message);
		}
	}
	private Handler h = new Handler() {

		public void handleMessage(Message msg) {
			if (result != null) {
				try{
					JSONObject jobj = new JSONObject(result);
					String status = jobj.getString("c");
					if (status.equals("0000")) {
						JSONObject d = jobj.getJSONObject("d");

						PartnerConfig.amount = d.getString("amount");
						show("您的当前余额为：￥" + PartnerConfig.amount);
					} else {
						String str = jobj.getJSONObject("d").getString("msg");
						show(str);
					}
				}catch(Exception e){
					show("未知错误，请重试");
				}
			} else {
				show("联网失败，请重试");
			}
		}
	};
	
	protected final int ID_UPDATE = 0;
	protected final int ID_EXIT = 1;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ID_UPDATE, 0, R.string.logout);
		menu.add(0, ID_EXIT, 0, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ID_UPDATE:
			SharedPreferences sharedPref = getSharedPreferences("LOGIN", 0);
			sharedPref.edit().putString("id", "").commit();
			sharedPref.edit().putString("code", "").commit();
			
			Intent intent = new Intent(this, MopsWelcomeActivity.class);
			startActivity(intent);
			finish();
			break;
			
		case ID_EXIT:
//			alterExit();
			break;
		}
		
		return false;
	};
}