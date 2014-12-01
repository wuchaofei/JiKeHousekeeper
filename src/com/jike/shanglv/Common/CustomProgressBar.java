package com.jike.shanglv.Common;

import com.jike.shanglv.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;

/********************************************************************
 * [Summary] TODO 请在此处简要描述此类所实现的功能。因为这项注释主要是为了在IDE环境中生成tip帮助，务必简明扼要 [Remarks]
 * TODO 请在此处详细描述类的功能、调用方法、注意事项、以及与其它类的关系.
 *******************************************************************/

public class CustomProgressBar extends Dialog {
	private Context context = null;
	private static CustomProgressBar customProgressBar = null;

	public CustomProgressBar(Context context) {
		super(context);
		this.context = context;
	}

	public CustomProgressBar(Context context, int theme) {
		super(context, theme);
	}

	public static CustomProgressBar createDialog(Context context) {
		customProgressBar = new CustomProgressBar(context,
				R.style.CustomProgressDialog);
		customProgressBar.setContentView(R.layout.custom_progrssbar);
		customProgressBar.getWindow().getAttributes().gravity = Gravity.CENTER;

		return customProgressBar;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressBar == null) {
			return;
		}

	}

	/**
	 * [Summary] setTitile 标题
	 * 
	 * @param strTitle
	 * @return
	 * 
	 */
	public CustomProgressBar setTitile(String strTitle) {
		return customProgressBar;
	}

	/**
	 * [Summary] setMessage 提示内容
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public CustomProgressBar setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressBar
				.findViewById(R.id.message);

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
		return customProgressBar;
	}

	public CustomProgressBar setMax(int m) {
		ProgressBar pb = (ProgressBar) customProgressBar
				.findViewById(R.id.pb);
		if (pb != null) {
			pb.setMax(m);
		}
		return customProgressBar;
	}

	public CustomProgressBar setProgress(int f) {
		ProgressBar pb = (ProgressBar) customProgressBar
				.findViewById(R.id.pb);
		if (pb != null) {
			pb.setProgress(f);
		}
		return customProgressBar;
	}
}