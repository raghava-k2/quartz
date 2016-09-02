package com.gli.util;

public class StringUtil {
	public static Boolean isStringNullOrEmpty(String string) {
		if (string != null && !string.isEmpty())
			return false;
		return true;
	}
}
