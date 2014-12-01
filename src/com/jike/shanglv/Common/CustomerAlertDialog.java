package com.jike.shanglv.Common;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.jike.shanglv.R;

public class CustomerAlertDialog {
	Context context;
	android.app.AlertDialog ad;
	TextView dialog_title_tv, dialog_content_tv, dialog_cancel_tv,
			dialog_ok_tv;

	public CustomerAlertDialog(Context context, Boolean isOneBtn) {
		try {
			this.context = context;
			ad = new android.app.AlertDialog.Builder(context).create();
			ad.setCancelable(false);
			ad.show();
			// 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
			Window window = ad.getWindow();
			window.setContentView(R.layout.customer_alertdialog_towbtn);
			dialog_title_tv = (TextView) window
					.findViewById(R.id.dialog_title_tv);
			dialog_content_tv = (TextView) window
					.findViewById(R.id.dialog_content_tv);
			dialog_cancel_tv = (TextView) window
					.findViewById(R.id.dialog_cancel_tv);
			dialog_ok_tv = (TextView) window.findViewById(R.id.dialog_ok_tv);
			if (isOneBtn)
				dialog_cancel_tv.setVisibility(View.GONE);
			else
				dialog_cancel_tv.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTitle(int resId) {
		dialog_title_tv.setText(resId);
	}

	public void setTitle(String title) {
		dialog_title_tv.setText(title);
	}

	public void setMessage(int resId) {
		dialog_content_tv.setText(resId);
	}

	public void setMessage(String message) {
		dialog_content_tv.setText(message);
	}

	public void setPositiveButtonText(String message) {
		dialog_ok_tv.setText(message);
	}

	public void setNegativeButtonText(String message) {
		dialog_cancel_tv.setText(message);
	}

	/**
	 * 设置按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text,
			final View.OnClickListener listener) {
		dialog_ok_tv.setText(text);
		dialog_ok_tv.setOnClickListener(listener);
	}

	public void setPositiveButton(String text, OnClickListener onClickListener) {
		dialog_ok_tv.setText(text);
		dialog_ok_tv
				.setOnClickListener((android.view.View.OnClickListener) onClickListener);
	}

	/**
	 * 设置按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton1(String text,
			final View.OnClickListener listener) {
		dialog_cancel_tv.setText(text);
		dialog_cancel_tv.setOnClickListener(listener);
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}
}
