/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.jike.shanglv.pos.jike.mpos.newversion;

import com.jike.shanglv.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * 
 * @author jianmin.jiang
 * 
 * @version $Id: SubmitClickable.java, v 0.1 2012-3-12 ÉÏÎç11:46:50 jianmin.jiang
 *          Exp $
 */
public class SubmitClickable {

	private Button submit;
	private EditText[] edits;

	public SubmitClickable(Button submit, EditText[] edits) {
		this.submit = submit;
		this.edits = edits;
		if (edits == null) {
			return;
		}
		for (EditText editText : edits) {
			if (editText != null) {
				editText.addTextChangedListener(watcher);
			}
		}
	}

	public static void setSubmitStyle(Button submit, boolean clickable) {
		Context context = submit.getContext();
		if (clickable) {
			submit.setClickable(true);
			submit.setBackgroundResource(R.drawable.button_clickable);
			submit.setTextColor(context.getResources().getColor(
					R.color.color_462C00));
		} else {
			submit.setClickable(false);
			submit.setBackgroundResource(R.drawable.button_cannot);
			submit.setTextColor(context.getResources().getColor(
					R.color.black6));
		}
	}

	private TextWatcher watcher = new TextWatcher() {

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			boolean canSubmit = false;
			for (EditText editText : edits) {
				if (editText != null) {
					if (editText.getVisibility() == View.GONE) {
						canSubmit = true;
						continue;
					}
					canSubmit = editText.getText().length() > 0;
					if (!canSubmit) {
						break;
					}
				}
			}
			setSubmitStyle(submit, canSubmit);
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		public void afterTextChanged(Editable s) {

		}
	};

}
