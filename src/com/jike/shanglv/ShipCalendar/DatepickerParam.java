package com.jike.shanglv.ShipCalendar;

import java.io.Serializable;
import java.util.Calendar;

public class DatepickerParam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Calendar selectedDay = null;
	public Calendar startDate = null;
	//售票范围
	public int dateRange = 0;
	public String title = "出发日期";
}

