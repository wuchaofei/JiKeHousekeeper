/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.jike.shanglv.pos.jike.mpos.newversion;

import java.io.File;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.alipay.wireless.exception.AppErrorException;
import com.alipay.wireless.exception.FailOperatingException;
import com.alipay.wireless.exception.NetErrorException;
import com.alipay.wireless.exception.ValifyException;
import com.alipay.wireless.https.FileDownloader;
import com.alipay.wireless.https.HttpVisitConnection;
import com.alipay.wireless.task.TaskProcessor;
import com.alipay.wireless.task.TaskQueueMain;
import com.alipay.wireless.util.ApkInstallUtil;
import com.alipay.wireless.util.StringUtil;
import com.jike.shanglv.R;

public class BaseActivity extends Activity {

	public static final String SERVICE_URL = "https://mobilepos.alipay.com/latest_version.htm?os=android";
	private AlertDialog dialog;
	private ProgressDialog progressDlg;
	private String path;
	protected Integer lock;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lock = new Integer(0);
		context = this;
	}

	@Override
	protected void onDestroy() {
		if (dialog != null) {
			dialog.dismiss();
		}
		if (progressDlg != null) {
			progressDlg.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	public void showAlter(String msg) {
		dialog = createCustomAlterDialog();
		dialog.setTitle(R.string.warn);
		dialog.setMessage(msg);
		dialog.setButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		dialog.show();
	}

	public void showNetError(NetErrorException e,
			DialogInterface.OnClickListener okListener,
			DialogInterface.OnClickListener cancelListener) {
		String message = null;
		if (e != null && e.getErrorCode() == NetErrorException.SERVER_ERROR) {
			message = getString(R.string.net_server_error);
		} else {
			message = getString(R.string.net_error);
		}
		dialog = createCustomAlterDialog();
		dialog.setTitle(R.string.warn);
		dialog.setMessage(message);
		dialog.setButton(getString(R.string.redo), okListener);
		dialog.setButton2(getString(R.string.cancel), cancelListener);

		dialog.show();
	}

	@SuppressWarnings("unchecked")
	public <T> T createCustomAlterDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
		dialog = new AlertDialog.Builder(this).create();
		return (T) dialog;
	}

	public void showProgress(String msg) {
		if (progressDlg != null) {
			progressDlg.dismiss();
		}
		progressDlg = new ProgressDialog(this);
		if (!StringUtil.isEmpty(msg)) {
			progressDlg.setMessage(msg);
		}

		progressDlg.show();
	}

	public void closeProgress() {
		if (progressDlg != null) {
			progressDlg.dismiss();
		}

	}

	public void hiddenInputSoft() {
		try {
			InputMethodManager im = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
		}
	}

	public boolean canConnetionService() {
		PackageManager pm = this.getPackageManager();
		try {
			pm.getPackageGids("com.alipay.android.mobilepos");
			return true;
		} catch (NameNotFoundException e) {
			this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					dialog = createCustomAlterDialog();
					dialog.setMessage(getString(R.string.no_pos_service));
					dialog.setButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									new Thread(new ServiceInstaller()).start();
								}

							});
					dialog.setButton2(getString(R.string.cancel),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
					dialog.show();
				}
			});
			return false;
		}
	}


	protected void installApplication(String url) {
		path = getFilesDir().getAbsolutePath() + File.separator + "tmp.apk";
		
		FileDownloader downloader = new FileDownloader();
		downloader.setFileUrl(url);
		downloader.setSavePath(path);
		downloader.setProgressOutput(new ShowProgress());
		downloader.start();

		if (progressDlg != null) {
			progressDlg.dismiss();
		}
		progressDlg = new ProgressDialog(this);
		progressDlg.setSecondaryProgress(ProgressDialog.STYLE_HORIZONTAL);
		progressDlg.show();
	}

//	protected final void alterExit() {
//		dialog = createCustomAlterDialog();
//		dialog.setMessage(getString(R.string.exit_client));
//		dialog.setButton(getString(R.string.ok),
//				new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog, int which) {
//						finish();
//					}
//				});
//		dialog.setButton2(getString(R.string.cancel),
//				new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog, int which) {
//
//					}
//				});
//		dialog.show();
//	}

	protected void showToast(int res) {
		Toast toast = Toast.makeText(this, res, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM, 0, 20);
		toast.show();
	}

	private class ServiceInstaller implements Runnable {

		public void run() {
			HttpVisitConnection conn = new HttpVisitConnection();
			try {
				byte[] data = conn.downloadData(SERVICE_URL);
				String strResp = new String(data, "UTF-8");
				JSONObject json = new JSONObject(strResp);
				downloadService(json.getString("url"));
			} catch (final NetErrorException e) {
				System.out.println("1111111111111111111111");
				TaskQueueMain.getInstance().addTask(new TaskProcessor() {

					@Override
					protected void doProcess(ITaskCallback callback)
							throws NetErrorException, FailOperatingException,
							AppErrorException, ValifyException {
						onNetError(e);
					}
				});
			} catch (Exception e) {
				showAlter(getString(R.string.download_fail));
				e.printStackTrace();
			}
		}

		private void downloadService(final String url) {
			System.out.println("222222222222222");
			TaskQueueMain.getInstance().addTask(new TaskProcessor() {

				@Override
				protected void doProcess(ITaskCallback callback)
						throws NetErrorException, FailOperatingException,
						AppErrorException, ValifyException {
					installApplication(url);
				}
			});
		}

		private void onNetError(NetErrorException e) {
			showNetError(e, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					new Thread(new ServiceInstaller()).start();
				}

			}, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					closeProgress();
				}
			});
		}
	}

	private class ShowProgress implements FileDownloader.IDownloadProgress {

		public void downloadProgress(float progress) {
			if(progressDlg != null){
				progressDlg.setProgress((int) progress);
			}
		}

		public void downloadSucess() {
			closeProgress();
			ApkInstallUtil.chmod("777", path);
			ApkInstallUtil.install(path);
			finish();
		}

		public void downloadFail() {
			closeProgress();
			showAlter(getString(R.string.download_fail));
		}

	}
}
