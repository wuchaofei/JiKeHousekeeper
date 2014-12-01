package com.jike.shanglv.pos.jike.mpos.newversion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.alipay.wireless.util.StringUtil;
import com.jike.shanglv.R;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.pos.alipay.android.mpos.demo.helper.ClientData;
import com.jike.shanglv.pos.alipay.android.mpos.demo.helper.MD5Util;
import com.jike.shanglv.pos.alipay.android.mpos.demo.helper.PartnerConfig;
import com.jike.shanglv.pos.alipay.android.mpos.demo.pos.MPosConntion;
import com.jike.shanglv.pos.alipay.android.mpos.demo.pos.ParamsItem;
import com.jike.shanglv.pos.alipay.android.mpos.demo.pos.ResultCallback;

public class MopsWelcomeActivity extends BaseActivity {

	private ClientData data;
	private Context context;
//	ProgressBar progressbar;
	LinearLayout login_bg;
	EditText id_input, code_input;
	Button submit;

	PackageInfo pi;
	boolean can;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		PartnerConfig.userid = sp.getString(SPkeys.userid.getString(), "");
		PartnerConfig.amount = sp.getString(SPkeys.amount.getString(), "");
		PartnerConfig.name = sp.getString(SPkeys.username.getString(), "");
		new Thread(new appinfo()).start();
		
		setContentView(R.layout.mpos_welcome);

		context = this;
		data = ClientData.create_new(context);

		login_bg = (LinearLayout) findViewById(R.id.login_bg);
		login_bg.setVisibility(View.GONE);
		id_input = (EditText) findViewById(R.id.id_input);
		code_input = (EditText) findViewById(R.id.code_input);
		submit = (Button) findViewById(R.id.submit);

		id_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				PartnerConfig.id = id_input.getText().toString();

				if (PartnerConfig.id != null && PartnerConfig.id.length() > 0
						&& PartnerConfig.code != null
						&& PartnerConfig.code.length() > 0) {
					can = true;
				} else {
					can = false;
				}
				SubmitClickable.setSubmitStyle(submit, can);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		code_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				PartnerConfig.code = code_input.getText().toString();
				if (PartnerConfig.id != null && PartnerConfig.id.length() > 0
						&& PartnerConfig.code != null
						&& PartnerConfig.code.length() > 0) {
					can = true;
				} else {
					can = false;
				}
				SubmitClickable.setSubmitStyle(submit, can);
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
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (can) {
					new Thread(new Login()).start();
				}
			}
		});
	}

	private void goRecieptForm(String mposId) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(MainActivity.MPOS_ID, mposId);
		startActivity(intent);
	}

	private void alterFail(String error) {
		if (StringUtil.isEmpty(error)) {
			error = getString(R.string.activate_fail);
		}
		Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void showProgress(String msg) {
	}

	@Override
	protected void showToast(int res) {
	}

	String result;
	private class Login implements Runnable {
		public void run() {
			result = MobileposApplication
					.getWeb(MobileposApplication.HTTPURL[MobileposApplication.Package_Index]
							+ "/api/json.aspx?action=login&sitekey="
							+ MobileposApplication.SITEKEY[MobileposApplication.Package_Index]
							+ "&str={\"uname\":\""
							+ PartnerConfig.id
							+ "\",\"upass\":\""
							+ PartnerConfig.code
							+ "\",\"attr\":\""
							+ MobileposApplication.ATTR[MobileposApplication.Package_Index]
							+ "\"}");
			Message message = new Message();
			h.sendMessage(message);
		}
	}
	
	private Handler h = new Handler() {
		public void handleMessage(Message msg) {
			if (result != null) {
				try {
					JSONObject jobj = new JSONObject(result);
					String status = jobj.getString("c");
					if (status.equals("0000")) {
						SharedPreferences sharedPref = context
								.getSharedPreferences("LOGIN", 0);
						sharedPref.edit().putString("id", PartnerConfig.id)
								.commit();
						sharedPref.edit().putString("code", PartnerConfig.code)
								.commit();
						JSONObject d = jobj.getJSONObject("d");

						PartnerConfig.userid = d.getString("userid");
						PartnerConfig.amount = d.getString("amount");
						PartnerConfig.name = d.getString("username");
						new Thread(new appinfo()).start();
					} else {
						String str = jobj.getJSONObject("d").getString("msg");
						show(str);
					}
				} catch (Exception e) {
					show("未知错误，请重试");
				}
			} else {
				show("联网失败，请重试");
			}
		}
	};

	

	private class appinfo implements Runnable {
		public void run() {
			result = MobileposApplication
					.getWeb(MobileposApplication.HTTPURL[MobileposApplication.Package_Index]
							+ "/api/json.aspx?action=apppay&sitekey="
							+ MobileposApplication.SITEKEY[MobileposApplication.Package_Index]
							+ "&str={\"appinfo\":\"appinfo\"}");
			Message message = new Message();
			h_appinfo.sendMessage(message);
		}
	}

	private Handler h_appinfo = new Handler() {
		public void handleMessage(Message msg) {
			if (result != null) {
				try {
					JSONObject jobj = new JSONObject(result);
					String status = jobj.getString("c");
					if (status.equals("0000")) {
						JSONObject d = jobj.getJSONObject("d");
						PartnerConfig.seller = d.getString("payaccount");
						PartnerConfig.notify_url = d.getString("notify_url");
						new Thread(new Checker()).start();
					} else {
						String str = jobj.getJSONObject("d").getString("msg");
						show(str);
					}
				} catch (Exception e) {
					show("未知错误，请重试");
				}
			} else {
				show("联网失败，请重试");
			}
		}
	};

	private class Checker implements Runnable {
		public void run() {
			PackageManager pm = getPackageManager();
			try {
				pm.getPackageGids("com.alipay.android.mobilepos");
			} catch (NameNotFoundException e) {
				Message message = new Message();
				h_show.sendMessage(message);
				return;
			}
			doPost();
		}
	}
	
	private Handler h_show = new Handler() {
		public void handleMessage(Message msg) {
			showUpdate_Must_pos();
		}
	};

	private void showUpdate_Must_pos() {
		new AlertDialog.Builder(context)
				.setMessage(getString(R.string.no_pos_service))
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								try {
									downloadTheFile("https://mobilepos.alipay.com/clients/mpos/android/2.3.2/mobilepos.apk");
									showDownload();
								}catch(Exception e){
									e.printStackTrace();
									show("下载失败");
								}
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								finish();
								System.exit(0);
							}
			}).show();
	}
	

	public void show(String msg) {
		new AlertDialog.Builder(this)
				.setMessage(msg)
				.setPositiveButton(getString(R.string.ok),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}

						}).show();
	}


	private void doPost() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(new ParamsItem("service", "merchant_init").getParams());
		buffer.append("&");

		buffer.append(new ParamsItem("partner", PartnerConfig.partner)
				.getParams());
		buffer.append("&");
		buffer.append(new ParamsItem("seller_name", "商旅管家"/* PartnerConfig.name */)
				.getParams());
		buffer.append("&");
		buffer.append(new ParamsItem("seller_logon_id", PartnerConfig.seller)
				.getParams());
		MPosConntion.create().process(this, buffer, cb);
	}

	private ResultCallback cb = new ResultCallback() {

		@Override
		protected void onResultBack(String result) {
			System.out.println(result);
			boolean isSucess = false;
			String mposId = null;
			String error = null;
			ParamsItem[] items = MD5Util.getResult(result);
			if (items != null) {
				for (ParamsItem paramsItem : items) {
					if ("is_success".equals(paramsItem.getKey())) {
						isSucess = "true".equals(paramsItem.getValue());
					}
					if ("mpos_id".equals(paramsItem.getKey())) {
						mposId = paramsItem.getValue();
						continue;
					}
					if ("error_msg".equals(paramsItem.getKey())) {
						error = paramsItem.getValue();
						continue;
					}
				}
				if (isSucess) {
					isSucess = MD5Util.checkSign(result, PartnerConfig.MD5_KEY);
				}
			}
			if (isSucess) {
				data.saveMposId(PartnerConfig.partner, PartnerConfig.seller,
						mposId);
				goRecieptForm(mposId);
			} else {
				alterFail(error);
			}
		}
	};
	
	private boolean cango;
	// 文件当前路径
	private String currentFilePath = "";
	// 安装包文件临时路径
	private String currentTempFilePath = "";
	// 获得文件扩展名字符串
	private String fileEx = "";
	// 获得文件名字符串
	private String fileNa = "";
	private int filesize;
	private int downloadsize;
	private CustomProgressBar progressdialog;
	public HttpURLConnection conn;

	private void downloadTheFile(final String strPath) {

		// 获得文件文件扩展名字符串
		fileEx = strPath.substring(strPath.lastIndexOf(".") + 1,
				strPath.length()).toLowerCase();
		// 获得文件文件名字符串
		fileNa = strPath.substring(strPath.lastIndexOf("/") + 1,
				strPath.lastIndexOf("."));
		try {
			if (strPath.equals(currentFilePath)) {
				doDownloadTheFile(strPath);
			}
			currentFilePath = strPath;
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					try {
						// 执行下载
						doDownloadTheFile(strPath);
					} catch (Exception e) {
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static final String SAVE_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/jike";
	/**
	 * 执行新版本进行下载，并安装
	 * 
	 * @param strPath
	 * @throws Exception
	 */
	private void doDownloadTheFile(String strPath) throws Exception {
		// 判断strPath是否为网络地址
		if (!URLUtil.isNetworkUrl(strPath) || !strPath.endsWith("apk")) {
			showError("下载目录无效");
		} else {
			conn = (HttpURLConnection) (new URL(strPath)).openConnection();
			InputStream is = conn.getInputStream();
			filesize = conn.getContentLength();
			Message m = new Message();
			m.what = 1;
			handler_download.sendMessage(m);
			if (is == null) {
				throw new RuntimeException("stream is null");
			}
			// 生成一个临时文件
			File myTempFile = new File(SAVE_PATH);

			if (!myTempFile.exists())
				myTempFile.mkdirs();
			if (!myTempFile.isDirectory()) {
				myTempFile.mkdirs();
			}
			myTempFile = new File(SAVE_PATH + "/" + fileNa + "." + fileEx);// File.createTempFile(fileNa,
																					// "."
																					// +
																					// fileEx);
			// 安装包文件临时路径
			currentTempFilePath = myTempFile.getAbsolutePath();
			FileOutputStream fos = new FileOutputStream(myTempFile);
			byte buf[] = new byte[128];
			do {
				int numread = is.read(buf);
				if (numread <= 0) {
					break;
				}
				fos.write(buf, 0, numread);
				downloadsize += 128;
				Message mm = new Message();
				mm.what = 2;
				handler_download.sendMessage(mm);
			} while (true);
			// 打开文件
			progressdialog.dismiss();
			openFile(myTempFile);
			try {
				is.close();
			} catch (Exception ex) {
			}
		}
	}

	private void showError(String str) {
		new AlertDialog.Builder(context)
				.setTitle("")
				.setMessage(str)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}

	private void showDownload() {

		progressdialog = CustomProgressBar.createDialog(context);
		progressdialog.setCancelable(true);
		progressdialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {

				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			}
		});
		progressdialog.show();

	}

	private Handler handler_download = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				progressdialog.setMax(filesize);
				break;
			case 2:
				progressdialog.setProgress(downloadsize);
				int res = downloadsize * 100 / filesize;
				progressdialog.setMessage("已下载：" + res + "%");
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 打开文件进行安装
	 * 
	 * @param f
	 */
	private void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		// 获得下载好的文件类型
		String type = getMIMEType(f);
		// 打开各种类型文件
		intent.setDataAndType(Uri.fromFile(f), type);
		// 安装
		startActivityForResult(intent, 0);
	}

	/**
	 * 删除临时路径里的安装包
	 */
	public void delFile() {
		File myFile = new File(currentTempFilePath);
		if (myFile.exists()) {
			myFile.delete();
		}
	}

	/**
	 * 获得下载文件的类型
	 * 
	 * @param f
	 *            文件名称
	 * @return 文件类型
	 */
	private String getMIMEType(File f) {
		String type = "";
		// 获得文件名称
		String fName = f.getName();
		// 获得文件扩展名
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}

}