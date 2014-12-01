//web支付页面
package com.jike.shanglv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Activity_Web_Pay extends Activity {

	public static final String URL = "zhifu_url";
	public static final String TITLE = "activity_title";
	private ImageButton back;
	private TextView chongzhi_finish;
	private WebView webView_zhifu;
	private LinearLayout loading_ll;
	private ImageView frame_ani_iv;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_web_pay);
			((MyApplication) getApplication()).addActivity(this);

			loading_ll = (LinearLayout) findViewById(R.id.loading_ll);
			frame_ani_iv = (ImageView) findViewById(R.id.frame_ani_iv);
			back = (ImageButton) findViewById(R.id.back);
			back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			chongzhi_finish = (TextView) findViewById(R.id.chongzhi_finish);
			chongzhi_finish.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});

			webView_zhifu = new WebView(this);
			webView_zhifu = (WebView) findViewById(R.id.webView_zhifu);
			WebSettings webSettings = webView_zhifu.getSettings();
			webSettings.setJavaScriptEnabled(true);// 在WebView中使用JavaScript，若页面中用了JavaScript，必须为WebView使能JavaScript
			String url = (String) getIntent().getExtras().get(URL);
			String title = (String) getIntent().getExtras().get(TITLE);
			if (!title.equals(""))
				((TextView) findViewById(R.id.title)).setText(title);

			webView_zhifu.setWebViewClient(new WebViewClient() {// /
																// 不重写的话，会跳到手机浏览器中

						@Override
						public void onReceivedError(WebView view,
								int errorCode, String description,
								String failingUrl) { // Handle the error
						}

						@Override
						public boolean shouldOverrideUrlLoading(WebView view,
								String url) {
							view.loadUrl(url);
							return true;
						}

						@Override
						public void onPageFinished(WebView view, String url) {
							super.onPageFinished(view, url);
							loading_ll.setVisibility(View.GONE);
							webView_zhifu.setVisibility(View.VISIBLE);
						}
					});
			webView_zhifu.loadUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		try {
			loading_ll.setVisibility(View.VISIBLE);
			frame_ani_iv.setBackgroundResource(R.anim.frame_rotate_ani);
			AnimationDrawable anim = (AnimationDrawable) frame_ani_iv
					.getBackground();
			anim.setOneShot(false);
			anim.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// http://gatewayv3.51jp.cn/PayMent/BeginPay.aspx?
	// orderID=&amount=657&userid=12369&paysystype=15&siteid=65
	// &sign=d0c12d722788cd73e433aba2a6ddbd32

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
