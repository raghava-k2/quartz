package com.gli.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtil {
	private static SimpleDateFormat format = new SimpleDateFormat();

	public static java.sql.Date getSqlDate(String date, String pattern) throws ParseException {
		format.applyPattern(pattern);
		return new java.sql.Date(format.parse(date).getTime());
	}

	public static java.util.Date getDate(String date, String pattern) throws ParseException {
		format.applyPattern(pattern);
		return new java.util.Date(format.parse(date).getTime());
	}

	public static String getStringDate(java.util.Date date, String pattern) {
		if (date == null)
			return null;
		format.applyPattern(pattern);
		return format.format(date);
	}
}
