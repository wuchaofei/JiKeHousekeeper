/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.jike.shanglv.pos.alipay.android.mpos.demo.pos;

/**
 * 
 * @author jianmin.jiang
 * 
 * @version $Id: ResultCallback.java, v 0.1 2012-2-18 下午3:10:43 jianmin.jiang
 *          Exp $
 */
public abstract class ResultCallback implements Runnable {

	private String result;

	public ResultCallback() {

	}

	public void run() {
		onResultBack(result);
	}

	protected final void setResult(String result) {
		this.result = result;
	}

	protected abstract void onResultBack(String result);

}