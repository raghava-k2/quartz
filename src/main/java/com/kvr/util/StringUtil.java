package com.kvr.util;

public class StringUtil {
	public static Boolean isStringNullOrEmpty(String string) {
		if (string != null && !string.isEmpty())
			return false;
		return true;
	}
}
