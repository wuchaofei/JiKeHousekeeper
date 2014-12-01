/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.jike.shanglv.pos.alipay.android.mpos.demo.pos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.alipay.android.mobilepos.IAlixMobilepos;
import com.alipay.android.mobilepos.IRemoteServiceCallback;
import com.jike.shanglv.pos.alipay.android.mpos.demo.helper.MD5Util;
import com.jike.shanglv.pos.alipay.android.mpos.demo.helper.PartnerConfig;
import com.jike.shanglv.pos.jike.mpos.newversion.BaseActivity;

/**
 * @author jianmin.jiang
 * 
 * @version $Id: BaseHelper.java, v 0.1 2012-2-18 涓嬪崍1:34:05 jianmin.jiang Exp $
 */
public class MPosConntion {

	private Object lock;
	private IAlixMobilepos pos;

	private MPosConntion() {
		lock = new Object();
	}

	public static MPosConntion create() {
		return new MPosConntion();
	}

	public void process(final BaseActivity context, final StringBuffer order,
			final ResultCallback cb) {
		context.hiddenInputSoft();

		context.showProgress("");
		// 加密方式按照约定的来
		String sign = MD5Util.sign(order.toString(), PartnerConfig.MD5_KEY);
		order.append("&");
		order.append(new ParamsItem("sign_type", "md5").getParams());
		order.append("&");
		order.append(new ParamsItem("sign", sign).getParams());

		final String orderInfo = order.toString();

		final IRemoteServiceCallback callback = new IRemoteServiceCallback.Stub() {

			public void startActivity(String packageName, String className,
					int iCallingPid, Bundle bundle) throws RemoteException {
				Intent intent = new Intent(Intent.ACTION_MAIN, null);
				if (bundle == null)
					bundle = new Bundle();
				try {
					bundle.putInt("CallingPid", iCallingPid);
					intent.putExtras(bundle);
				} catch (Exception e) {
					e.printStackTrace();
				}
				intent.setClassName(packageName, className);
				context.startActivity(intent);
			}
		};
		new Thread(new Runnable() {

			public void run() {
				try {
					bindService(context);
					pos.registerCallback(callback);
					String result = pos.doProcess(orderInfo);
					pos.unregisterCallback(callback);
					context.getApplicationContext().unbindService(conn);
					if (cb != null) {
						cb.setResult(result);
						context.runOnUiThread(cb);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					context.closeProgress();
				}
			}
		}).start();

	}

	private void bindService(Context context) {
		Intent intent = new Intent("com.alipay.android.action.Mobilepos");
		context.getApplicationContext().bindService(intent, conn,
				Context.BIND_AUTO_CREATE);
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private ServiceConnection conn = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			pos = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(MPosConntion.class.getName(), "bind service sucess");
			pos = IAlixMobilepos.Stub.asInterface(service);
			synchronized (lock) {
				lock.notifyAll();
			}
		}
	};
}
