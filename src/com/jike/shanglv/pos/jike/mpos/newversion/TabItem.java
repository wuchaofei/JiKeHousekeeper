/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.jike.shanglv.pos.jike.mpos.newversion;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.alipay.wireless.util.ResUtil;
import com.alipay.wireless.widget.TabbarItem;
import com.jike.shanglv.R;

/**
 * 
 * @author jianmin.jiang
 * 
 * @version $Id: TabItem.java, v 0.1 2012-2-18 ÏÂÎç12:55:56 jianmin.jiang Exp $
 */
public class TabItem extends TabbarItem {

	private Activity context;
	private ImageView icon;
	private TextView text;
	private int selectedIconResource;
	private int unSelectedIconResource;
	private String tabName;
	private Object tabCode;

	public TabItem(Activity context) {
		this.context = context;
	}

	@Override
	protected View createItem() {
		View view = context.getLayoutInflater()
				.inflate(R.layout.mpos_tab_item, null);
		this.icon = (ImageView) view.findViewById(R.id.tab_icon);
		this.text = (TextView) view.findViewById(R.id.tab_text);
		return view;
	}

	@Override
	protected void setSelectedStyle(View convertView, int position, int count) {
		this.icon.setImageResource(selectedIconResource);
		this.text.setText(tabName);
		this.text.setTextColor(0xFF333333);
		convertView.setBackgroundResource(R.drawable.tab_selected);
	}

	@Override
	protected void fillUnSelectedStyle(View convertView, int position, int count) {
		this.icon.setImageResource(unSelectedIconResource);
		this.text.setText(tabName);
		this.text.setTextColor(0xFFFFFFFF);
		convertView.setBackgroundResource(R.drawable.tab_unselected);
	}

	/**
	 * @return Returns the tabName.
	 */
	public final String getTabName() {
		return tabName;
	}

	/**
	 * @param tabName
	 *            The tabName to set.
	 */
	public final TabItem setTabName(String tabName) {
		this.tabName = tabName;
		return this;
	}

	/**
	 * @return Returns the tabCode.
	 */
	public final Object getTabCode() {
		return tabCode;
	}

	/**
	 * @param tabCode
	 *            The tabCode to set.
	 */
	public final TabItem setTabCode(Object tabCode) {
		this.tabCode = tabCode;
		return this;
	}

	/**
	 * @param selectedIconResource
	 *            The selectedIconResource to set.
	 */
	public final TabItem setSelectedIconResource(int selectedIconResource) {
		this.selectedIconResource = selectedIconResource;
		return this;
	}

	/**
	 * @param unSelectedIconResource
	 *            The unSelectedIconResource to set.
	 */
	public final TabItem setUnSelectedIconResource(int unSelectedIconResource) {
		this.unSelectedIconResource = unSelectedIconResource;
		return this;
	}

}
