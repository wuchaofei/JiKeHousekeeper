/**************************************************************************************
 * [Project]
 *       MyProgressDialog
 * [Package]
 *       com.lxd.widgets
 * [FileName]
 *       CustomProgressDialog.java
 * [Copyright]
 *       Copyright 2012 LXD All Rights Reserved.
 * [History]
 *       Version          Date              Author                        Record
 *--------------------------------------------------------------------------------------
 *       1.0.0           2012-4-27         lxd (rohsuton@gmail.com)        Create
 **************************************************************************************/

package com.jike.shanglv.pos.jike.mpos.newversion;

import com.jike.shanglv.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/********************************************************************
 * [Summary] TODO 请在此处简要描述此类所实现的功能。因为这项注释主要是为了在IDE环境中生成tip帮助，务必简明扼要 [Remarks]
 * TODO 请在此处详细描述类的功能、调用方法、注意事项、以及与其它类的关系.
 *******************************************************************/

public class CustomProgressBar extends Dialog {
	private Context context = null;
	private static CustomProgressBar customProgressDialog = null;

	public CustomProgressBar(Context context) {
		super(context);
		this.context = context;
	}

	public CustomProgressBar(Context context, int theme) {
		super(context, theme);
	}

	public static CustomProgressBar createDialog(Context context) {
		customProgressDialog = new CustomProgressBar(context,
				R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.mpos_css_processbar);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		return customProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressDialog == null) {
			return;
		}

	}

	/**
	 * 
	 * [Summary] setTitile 标题
	 * @param strTitle
	 * @return
	 * 
	 */
	public CustomProgressBar setTitile(String strTitle) {
		return customProgressDialog;
	}

	/**
	 * 
	 * [Summary] setMessage 提示内容
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public CustomProgressBar setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog
				.findViewById(R.id.message);

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return customProgressDialog;
	}

	public CustomProgressBar setMax(int m) {
		ProgressBar pb = (ProgressBar) customProgressDialog
				.findViewById(R.id.pb);

		if (pb != null) {
			pb.setMax(m);
		}

		return customProgressDialog;
	}

	public CustomProgressBar setProgress(int f) {
		ProgressBar pb = (ProgressBar) customProgressDialog
				.findViewById(R.id.pb);

		if (pb != null) {
			pb.setProgress(f);
		}

		return customProgressDialog;
	}

}