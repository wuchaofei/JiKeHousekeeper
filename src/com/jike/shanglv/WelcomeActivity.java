package com.jike.shanglv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.Platform;
import com.jike.shanglv.Enums.SPkeys;


/**
 *     class desc: 启动画面 (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 *     (2)是，则进入GuideActivity；否，则进入MainActivity (3)3s后执行(2)操作
 */
public class WelcomeActivity  extends Activity {
	boolean isFirstIn = false;
	SharedPreferences preferences;
	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	// 延迟3秒
	private static final long SPLASH_DELAY_MILLIS = 2000;
	Context mContext;
	/**
	 * Handler:跳转到不同界面
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		mContext=this;
		init();
		((MyApplication)getApplication()).addActivity(this);
	}

	private void init() {
		MyApp mApp=new MyApp(getApplicationContext());	
		((ImageView)findViewById(R.id.welcome_iv)).setBackgroundResource((Integer) mApp.getHm().get(PackageKeys.WELCOME_DRAWABLE.getString()));
		// 读取SharedPreferences中需要的数据
		// 使用SharedPreferences来记录程序的使用次数
		preferences = getSharedPreferences(
				SPkeys.SPNAME.getString(), MODE_PRIVATE);
		
		try {
			isFirstIn = preferences.getBoolean(SPkeys.isFirstIn.getString()+mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0).versionCode, false);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (isFirstIn) {
			// 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			//			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
			Message msg = new Message();
			msg.what=GO_GUIDE;
			mHandler.sendMessage(msg);
//			preferences.edit().putBoolean(SPkeys.isFirstIn.getString(), true).commit();
		}
	}

	private void goHome() {
		Intent intent =null;
//		if((new MyApp(WelcomeActivity.this).getHm().get(PackageKeys.PLATFORM.getString())==Platform.B2C)){
//			intent = new Intent(WelcomeActivity.this, MainActivity.class);
//		}else if((new MyApp(WelcomeActivity.this).getHm().get(PackageKeys.PLATFORM.getString())==Platform.B2B)){
//			intent = new Intent(WelcomeActivity.this, ActivityBMenuNew.class);
//		}
		intent = new Intent(WelcomeActivity.this, MainActivity.class);
		WelcomeActivity.this.startActivity(intent);
		WelcomeActivity.this.finish();
	}

	private void goGuide() {
		Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
		WelcomeActivity.this.startActivity(intent);
		WelcomeActivity.this.finish();
	}
}
