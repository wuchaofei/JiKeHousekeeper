package com.jike.shanglv.Common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.net.ParseException;

@SuppressLint("SimpleDateFormat")
public class DateUtil {
	/*
	 * 比较两个日期时间的大小
	 */
	public static boolean isDateBefore(String date1, String date2) {
		try {
			DateFormat df = DateFormat.getDateTimeInstance();
			return df.parse(date1).before(df.parse(date2));
		} catch (Exception e) {
			return false;
		}
	}

	public static String GetTodayDate() {
		String temp_str = "";
		Date dt = new Date();
		// "yyyy-MM-dd HH:mm:ss aa" 最后的aa表示“上午”或“下午” HH表示24小时制 如果换成hh表示12小时制
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		temp_str = sdf.format(dt);
		return temp_str;
	}

	public static String GetNow() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return df.format(new Date());
	}

	/**
	 * 获取今天之后i天的日期，明天:GetDateAfterToday(1)
	 * */
	public static String GetDateAfterToday(int i) {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, i);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		return dateString;
	}

	public static Boolean isOverThirtyMinite(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return true;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 你设定的时间
		long calmill = cal.getTimeInMillis(); // 比较时，换算成毫秒数
		// 系统时间的毫秒数
		long sysmill = System.currentTimeMillis();
		System.out.println(sysmill);
		if (sysmill > calmill + 1800000) // 1800000毫秒是30分钟
		{
			return true;// 不能支付
		}
		return false;
	}

	/**
	 * 获得指定日期的前一天
	 * 
	 * @param specifiedDay
	 * @return
	 * @throws java.text.ParseException
	 * @throws Exception
	 */
	public static String getSpecifiedDayBefore(String specifiedDay)
			throws java.text.ParseException {// 可以用new
												// Date().toLocalString()传递参数
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - 1);

		String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c
				.getTime());
		return dayBefore;
	}

	/**
	 * 获得指定日期的后一天
	 * 
	 * @param specifiedDay
	 * @return
	 * @throws java.text.ParseException
	 */
	public static String getSpecifiedDayAfter(String specifiedDay)
			throws java.text.ParseException {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);

		String dayAfter = new SimpleDateFormat("yyyy-MM-dd")
				.format(c.getTime());
		return dayAfter;
	}

	/**
	 * 判断制定日期是否大于今天
	 */
	public static Boolean IsMoreThanToday(String specifiedDay) {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Date d = null;
		try {
			date = dfs.parse(GetDateAfterToday(0));
			d = dfs.parse(specifiedDay);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return false;
		}
		if (d.after(date))
			return true;
		else
			return false;
	}

	/**
	 * 获得指定日期时间的HH:mm
	 */
	public static String getTime(String dateString)
			throws java.text.ParseException {
		String time = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(dateString);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			// Calendar.HOUR 12小时制 Calendar.HOUR_OF_DAY 24小时制的时间
			String minute = "";
			String hour = "";
			if (c.get(Calendar.MINUTE) < 10) {
				minute = "0" + Integer.toString(c.get(Calendar.MINUTE));
			} else
				minute = Integer.toString(c.get(Calendar.MINUTE));
			if (c.get(Calendar.HOUR_OF_DAY) < 10) {
				hour = "0" + Integer.toString(c.get(Calendar.HOUR_OF_DAY));
			} else
				hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
			time = hour + ":" + minute;
		} catch (Exception e) {
			time = dateString.substring(dateString.length() - 8,
					dateString.length() - 3);
		}
		return time;
	}

	/**
	 * 获得指定日期时间的YYYY-MM:dd 年-月-日
	 */
	public static String getDate(String dateString)
			throws java.text.ParseException {
		String time = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(dateString);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			String month = "";
			if (c.get(Calendar.MONTH) < 9)
				month = "0" + (c.get(Calendar.MONTH) + 1);
			else
				month = String.valueOf(c.get(Calendar.MONTH) + 1);
			time = c.get(Calendar.YEAR) + "-" + month + "-"
					+ c.get(Calendar.DAY_OF_MONTH);
		} catch (Exception e) {
			e.printStackTrace();
			return dateString.substring(0, dateString.indexOf(" "));
		}
		return time;
	}
	/**
	 * 获得指定日期时间的MM:dd 月-日
	 */
	public static String getMonthDayDate(String dateString)
			throws java.text.ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(dateString);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		String month = "", day = "";
		if (c.get(Calendar.MONTH) < 9)
			month = "0" + (c.get(Calendar.MONTH) + 1);
		else
			month = String.valueOf(c.get(Calendar.MONTH) + 1);
		if (c.get(Calendar.DAY_OF_MONTH) < 9)
			day = "0" + c.get(Calendar.DAY_OF_MONTH);
		else
			day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		String time = month + "-" + day;
		return time;
	}

	/**
	 * 获得指定日期时间为星期几
	 */
	public static String getDayOfWeek(String dateString)
			throws java.text.ParseException {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String today = formatter.format(date);

		String str1 = DateUtil.getDate(dateString);
		String tomorrow = DateUtil.getSpecifiedDayAfter(today);
		String houtian = DateUtil.getSpecifiedDayAfter(DateUtil
				.getSpecifiedDayAfter(today));

		if (DateUtil.getDate(dateString).equals(today)) {
			return "今天";
		} else if (DateUtil.getDate(dateString).trim()
				.equals(DateUtil.getSpecifiedDayAfter(today).trim())) {
			return "明天";
		} else if (DateUtil.getDate(dateString).equals(
				DateUtil.getSpecifiedDayAfter(DateUtil
						.getSpecifiedDayAfter(today)))) {
			return "后天";
		}

		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date2 = sdf.parse(dateString);
		Calendar c = Calendar.getInstance();
		c.setTime(date2);
		Integer time = c.get(Calendar.DAY_OF_WEEK);
		return weekDays[time - 1];
	}

	public static Boolean compareDateIsBefore(String DATE1, String DATE2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				// System.out.println("dt1 >dt2");
				return true;
			} else if (dt1.getTime() < dt2.getTime()) {
				// System.out.println("dt1<dt2");
				return false;
			} else {// 同一天
				return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}

	/**
	 * 两个日期之间相隔天数的共通
	 * 
	 * @param from
	 *            開始時間
	 * @param to
	 *            　終了時間
	 * @return　天数
	 */
	public static String getDaysBetweenTwoDates(String dateFrom, String dateEnd) {
		Date dtFrom = null;
		Date dtEnd = null;
		dtFrom = toDate(dateFrom, "yyyyMMdd");
		dtEnd = toDate(dateEnd, "yyyyMMdd");
		long begin = dtFrom.getTime();
		long end = dtEnd.getTime();
		long inter = end - begin;
		if (inter < 0) {
			inter = inter * (-1);
		}
		long dateMillSec = 24 * 60 * 60 * 1000;

		long dateCnt = inter / dateMillSec;

		long remainder = inter % dateMillSec;

		if (remainder != 0) {
			dateCnt++;
		}
		return String.valueOf(dateCnt);
	}

	/**
	 * 字符窜(yyyyMMdd)转换成为java.util.Date
	 * 
	 * @param sDate
	 *            字符窜(yyyyMMdd)
	 * @param sFmt
	 *            format
	 * @return Date java.util.Date日期
	 */
	public static Date toDate(String sDate, String sFmt) {
		Date dt = null;
		try {
			dt = new SimpleDateFormat(sFmt).parse(sDate);
		} catch (Exception e) {
			return dt;
		}
		return dt;
	}

	// 获取字体大小
	public static int adjustFontSize(int screenWidth, int screenHeight) {
		screenWidth = screenWidth > screenHeight ? screenWidth : screenHeight;
		/**
		 * 1. 在视图的 onsizechanged里获取视图宽度，一般情况下默认宽度是320，所以计算一个缩放比率 rate = (float)
		 * w/320 w是实际宽度 2.然后在设置字体尺寸时 paint.setTextSize((int)(8*rate));
		 * 8是在分辨率宽为320 下需要设置的字体大小 实际字体大小 = 默认字体大小 x rate
		 */
		int rate = (int) (6 * (float) screenWidth / 320); // 我自己测试这个倍数比较适合，当然你可以测试后再修改
		return rate < 15 ? 15 : rate; // 字体太小也不好看的
	}

	/**
	 * List去重
	 * */
	public static List removeDuplicateWithOrder(List list) {
		Set set = new HashSet();
		List newList = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		return newList;
	}
}
