package com.jike.shanglv.ShipCalendar;

import android.text.TextUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class DateTimeUtils {
	public static final int DATETIME_FIELD_REFERSH = 20;
	public static final String HH_mm = "HH:mm";
	public static final String HH_mm_ss = "HH:mm:ss";
	public static final String MM_Yue_dd_Ri = "MM月dd日";
	public static final String MM_yy = "MM/yy";
	public static final String M_Yue_d_Ri = "M月d日";
	public static final long ONE_DAY = 86400000L;
	public static final long ONE_HOUR = 3600000L;
	public static final long ONE_MINUTE = 60000L;
	public static final long ONE_SECOND = 1000L;
	private static final String[] PATTERNS = { "yyyy-MM-dd HH:mm:ss",
			"yyyy-MM-dd HH:mm", "yyyy-MM-dd", "yyyyMMdd" };
	public static final String dd_MM = "dd/MM";
	public static boolean hasServerTime = false;
	public static long tslgapm = 0L;
	public static String tss;
	private static String[] weekdays = { "", "周日", "周一", "周二", "周三", "周四",
			"周五", "周六" };
	private static String[] weekdays1 = { "", "星期日", "星期一", "星期二", "星期三",
			"星期四", "星期五", "星期六" };
	public static final String yyyyMMdd = "yyyyMMdd";
	public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
	public static final String yyyy_MM = "yyyy-MM";
	public static final String yyyy_MM_dd = "yyyy-MM-dd";
	public static final String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
	public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	public static final String yyyy_Nian_MM_Yue_dd_Ri = "yyyy年MM月dd日";

	public static void cleanCalendarTime(Calendar paramCalendar) {
		paramCalendar.set(11, 0);
		paramCalendar.set(12, 0);
		paramCalendar.set(13, 0);
		paramCalendar.set(14, 0);
	}

	private static String fixDateString(String paramString) {
		if (TextUtils.isEmpty(paramString))
			return paramString;
		String[] arrayOfString = paramString.split("[年月日]");
		if (arrayOfString.length == 1)
			arrayOfString = paramString.split("-");
		for (int i = 0; i < 3; i++) {
			if (arrayOfString[i].length() != 1)
				continue;
			arrayOfString[i] = ("0" + arrayOfString[i]);
		}
		return arrayOfString[0] + "-" + arrayOfString[1] + "-"
				+ arrayOfString[2];
	}

	public static <T> Calendar getCalendar(T paramT) {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.setLenient(false);
		if (paramT == null)
			return null;
		if ((paramT instanceof Calendar)) {
			localCalendar1.setTimeInMillis(((Calendar) paramT)
					.getTimeInMillis());
			return localCalendar1;
		}
		if ((paramT instanceof Date)) {
			localCalendar1.setTime((Date) paramT);
			return localCalendar1;
		}
		if ((paramT instanceof Long)) {
			localCalendar1.setTimeInMillis(((Long) paramT).longValue());
			return localCalendar1;
		}
		if ((paramT instanceof String)) {
			String str = (String) paramT;
			try {
				if (Pattern.compile("\\d{4}年\\d{1,2}月\\d{1,2}日").matcher(str)
						.find()) {
					str = fixDateString(str);
					return getCalendarByPattern(str, "yyyy-MM-dd");
				}
				Calendar localCalendar2 = getCalendarByPatterns(str, PATTERNS);
				return localCalendar2;
			} catch (Exception localException) {
				try {
					localCalendar1.setTimeInMillis(Long.valueOf(str)
							.longValue());
					return localCalendar1;
				} catch (NumberFormatException localNumberFormatException) {
					throw new IllegalArgumentException(
							localNumberFormatException);
				}
			}
		}
		throw new IllegalArgumentException();
	}

	public static <T> Calendar getCalendar(T paramT, Calendar paramCalendar) {
		if (paramT != null)
			try {
				Calendar localCalendar = getCalendar(paramT);
				return localCalendar;
			} catch (Exception localException) {
			}
		return (Calendar) paramCalendar.clone();
	}

	public static Calendar getCalendarByPattern(String paramString1,
			String paramString2) {
		try {
			SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
					paramString2, Locale.US);
			localSimpleDateFormat.setLenient(false);
			Date localDate = localSimpleDateFormat.parse(paramString1);
			Calendar localCalendar = Calendar.getInstance();
			localCalendar.setLenient(false);
			localCalendar.setTimeInMillis(localDate.getTime());
			return localCalendar;
		} catch (Exception localException) {
			throw new IllegalArgumentException(localException);
		}

	}

	public static Calendar getCalendarByPatterns(String paramString,
			String[] paramArrayOfString) {
		int i = paramArrayOfString.length;
		int j = 0;
		while (j < i) {
			String str = paramArrayOfString[j];
			try {
				Calendar localCalendar = getCalendarByPattern(paramString, str);
				return localCalendar;
			} catch (Exception localException) {
				j++;
			}
		}
		throw new IllegalArgumentException();
	}

	public static Calendar getCurrentDateTime() {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setLenient(false);
		if (hasServerTime)
			localCalendar.setTimeInMillis(localCalendar.getTimeInMillis()
					+ tslgapm);
		return localCalendar;
	}

	public static Calendar getDateAdd(Calendar paramCalendar, int paramInt) {
		if (paramCalendar == null)
			return null;
		Calendar localCalendar = (Calendar) paramCalendar.clone();
		localCalendar.add(5, paramInt);
		return localCalendar;
	}

	public static <T> int getIntervalDays(T paramT1, T paramT2) {
		Calendar localCalendar1 = getCalendar(paramT1);
		Calendar localCalendar2 = getCalendar(paramT2);
		cleanCalendarTime(localCalendar1);
		cleanCalendarTime(localCalendar2);
		return (int) getIntervalTimes(localCalendar1, localCalendar2, 86400000L);
	}

	public static int getIntervalDays(String paramString1, String paramString2,
			String paramString3) {
		if ((paramString1 == null) || (paramString2 == null))
			return 0;
		return getIntervalDays(
				getCalendarByPattern(paramString1, paramString3),
				getCalendarByPattern(paramString2, paramString3));
	}

	public static long getIntervalTimes(Calendar paramCalendar1,
			Calendar paramCalendar2, long paramLong) {
		if ((paramCalendar1 == null) || (paramCalendar2 == null))
			return 0L;
		return Math.abs(paramCalendar1.getTimeInMillis()
				- paramCalendar2.getTimeInMillis())
				/ paramLong;
	}

	public static Calendar getLoginServerDate() {
		return getCalendar(tss);
	}

	public static String getWeekDayFromCalendar(Calendar paramCalendar) {
		if (paramCalendar == null)
			throw new IllegalArgumentException();
		return weekdays[paramCalendar.get(7)];
	}

	public static String getWeekDayFromCalendar1(Calendar paramCalendar) {
		if (paramCalendar == null)
			throw new IllegalArgumentException();
		return weekdays1[paramCalendar.get(7)];
	}

	public static boolean isLeapyear(String paramString) {
		Calendar localCalendar = getCalendar(paramString);
		if (localCalendar != null) {
			int i = localCalendar.get(1);
			return (i % 4 == 0) && ((i % 100 != 0) || (i % 400 == 0));
		}
		return false;
	}

	public static boolean isRefersh(long paramLong) {
		return isRefersh(1200000L, paramLong);
	}

	public static boolean isRefersh(long paramLong1, long paramLong2) {
		return new Date().getTime() - paramLong2 >= paramLong1;
	}

	public static String printCalendarByPattern(Calendar paramCalendar,
			String paramString) {
		if ((paramCalendar == null) || (paramString == null))
			return null;
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				paramString, Locale.US);
		localSimpleDateFormat.setLenient(false);
		return localSimpleDateFormat.format(paramCalendar.getTime());
	}

}