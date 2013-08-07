package com.synthverse.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class DateUtils {

    private static final String FILE_STAMP_FORMAT = "MM_dd_yyyy_HH_mm";
    private static final String REPORT_FORMAT = "MM/dd/yyyy HH:mm";
    private static DateFormat fileStampFormatter = null;
    private static DateFormat reportFormatter = null;

    static {
	fileStampFormatter = new SimpleDateFormat(FILE_STAMP_FORMAT);
	reportFormatter = new SimpleDateFormat(REPORT_FORMAT);
    }

    private DateUtils() {
	throw new AssertionError("DateUtils constructor is restricted");
    }

    public static final String getFileNameDateStamp() {

	return fileStampFormatter.format(new java.util.Date());

    }

    public static final String getReportFormattedDateString(java.util.Date date) {

	return reportFormatter.format(date);

    }

}
