package com.synthverse.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class DateUtils {

    private static final String FORMAT_STRING = "MM_dd_yyyy_HH_mm";
    private static DateFormat dateFormat = null;

    static {
	dateFormat = new SimpleDateFormat(FORMAT_STRING);
    }

    private DateUtils() {
	throw new AssertionError("DateUtils constructor is restricted");
    }

    public static final String getFileNameDateStamp() {

	return dateFormat.format(new java.util.Date());

    }

}
