/**
 * @(#)LogUtils.java  5:38:39 PM Mar 1, 2009
 * 
 * Copyright (c) 2004-2009 Sadat Chowdhury (sadatc@gmail.com)
 * 
 * The author reserves all rights to this software. Please contact the author 
 * (sadatc@gmail.com) to obtain permission to use, modify, or redistribute this 
 * software in any form. 
 * 
 * Unless otherwise stated, the author issues no guarantees of success and 
 * offers no warranties against damages of any kind. 
 * 
 * Last Revision : $Rev:: 6                     $: 
 * Last Updated  : $Date:: 2012-10-14 22:50:12 #$:
 * 
 */

package com.synthverse.util;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.synthverse.stacks.Config;

public final class LogUtils {

    public static final int MAX_FILE_RECORD_LIMIT = 1000000;
    public static final int MAX_FILE_COUNT = 10;
    public static final boolean APPEND_TO_LOG_FILE = true;

    public static final Level DEFAULT_LOG_LEVEL = Level.CONFIG;
    // public static final LogFormatter DEFAULT_LOG_FORMATTER =
    // LogFormatter.CUSTOM_NEAT;
    public static final LogFormatter DEFAULT_LOG_FORMATTER = LogFormatter.TIMED_BAREBONES;

    // private static final String FORMAT_STRING =
    // "EEE yyyy/MM/dd HH:mm:ss:SSS";
    private static final String FORMAT_STRING = Config.LOG_FORMAT_STRING;

    private static final String WARNING_SYMBOL = "***";
    private static final String SEVERE_SYMBOL = "!!!";
    private static final String INFO_SYMBOL = "==>";
    private static final String CONFIG_SYMBOL = "##>";
    private static final String FINE_SYMBOL = "~  ";
    private static final String FINER_SYMBOL = "~~ ";
    private static final String FINEST_SYMBOL = "~~~";

    private static final String EMPTY_STRING = "";
    private static DateFormat dateFormat = null;

    static {
	dateFormat = new SimpleDateFormat(FORMAT_STRING);
    }

    /**
     * An enumeration of some LogFormats. Each value is associated with an
     * instance of a customized {@link java.util.logging.Formatter} object that
     * is accessed with {@link #getFormatter()}
     * 
     * @author sadat
     * 
     */
    public enum LogFormatter {

	DEFAULT(new LogUtils.BareBonesFormatter()), TIMED_BAREBONES(new LogUtils.TimedBareBonesFormatter()), CUSTOM_SIMPLE(
		new LogUtils.CustomSimplestLogFormatter()), CUSTOM_DETAILED_2(
		new LogUtils.CustomDetailedLogFormatter2()), CUSTOM_DETAILED(new LogUtils.CustomDetailedLogFormatter()), CUSTOM_NEAT(
		new LogUtils.NeatLogFormatter());
	;

	private final Formatter formatter;

	LogFormatter(Formatter formatter) {
	    this.formatter = formatter;
	}

	public final Formatter getFormatter() {
	    return formatter;
	}
    }

    private static class StdoutConsoleHandler extends ConsoleHandler {
	protected void setOutputStream(OutputStream out) {
	    super.setOutputStream(System.out);
	}

    }

    private static class BareBonesFormatter extends Formatter {
	public final String format(LogRecord record) {
	    String msg = record.getMessage();
	    if (msg != null) {
		msg += Config.LINE_SEPARATOR;
	    }
	    return msg;
	}

	public final String formatMessage(LogRecord record) {
	    return format(record);
	}

	@SuppressWarnings("unused")
	public final String getHead() {
	    return EMPTY_STRING;
	}

	@SuppressWarnings("unused")
	public final String getTail() {
	    return EMPTY_STRING;
	}

    }

    private static class TimedBareBonesFormatter extends java.util.logging.SimpleFormatter {
	public String format(LogRecord record) {

	    StringBuilder SB = new StringBuilder("");
	    SB.append(dateFormat.format(new Date(record.getMillis())));
	    SB.append("|");

	    SB.append(record.getMessage());
	    SB.append(Config.LINE_SEPARATOR);
	    return SB.toString();
	}
    }

    private static class NeatLogFormatter extends java.util.logging.SimpleFormatter {
	public String format(LogRecord record) {

	    StringBuilder SB = new StringBuilder("");
	    SB.append(dateFormat.format(new Date(record.getMillis())));
	    SB.append("|");

	    if (record.getLevel().equals(Level.INFO)) {
		SB.append(INFO_SYMBOL);
	    } else if (record.getLevel().equals(Level.FINE)) {
		SB.append(FINE_SYMBOL);
	    } else if (record.getLevel().equals(Level.FINEST)) {
		SB.append(FINEST_SYMBOL);
	    } else if (record.getLevel().equals(Level.FINER)) {
		SB.append(FINER_SYMBOL);
	    } else if (record.getLevel().equals(Level.WARNING)) {
		SB.append(WARNING_SYMBOL);
	    } else if (record.getLevel().equals(Level.SEVERE)) {
		SB.append(SEVERE_SYMBOL);
	    } else if (record.getLevel().equals(Level.CONFIG)) {
		SB.append(CONFIG_SYMBOL);
	    }

	    SB.append("|");

	    SB.append(record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf('.') + 1));
	    SB.append("|");
	    SB.append(record.getSourceMethodName());
	    SB.append("| ");
	    SB.append(record.getMessage());
	    SB.append("\n");
	    return SB.toString();
	}
    }

    private static class CustomDetailedLogFormatter extends java.util.logging.SimpleFormatter {
	public String format(LogRecord record) {
	    StringBuilder SB = new StringBuilder("");
	    SB.append(record.getMillis());
	    SB.append(":");
	    SB.append(record.getLevel().toString());
	    SB.append(":");
	    SB.append(record.getSourceMethodName());
	    SB.append("():");
	    SB.append(record.getMessage());
	    SB.append("\n");
	    return SB.toString();
	}
    }

    private static class CustomDetailedLogFormatter2 extends java.util.logging.SimpleFormatter {
	public String format(LogRecord record) {
	    return record.getLevel() + "  :  " + record.getSourceClassName() + " -:- " + record.getSourceMethodName()
		    + " -:- " + record.getMessage() + "\n";
	}
    }

    private static class CustomSimplestLogFormatter extends java.util.logging.SimpleFormatter {
	public String format(LogRecord record) {
	    StringBuilder SB = new StringBuilder("");
	    SB.append(record.getMessage());
	    SB.append("\n");
	    return SB.toString();
	}
    }

    public static void applyLoggerSettings(Logger logger, LogFormatter formatter, Level level) {
	// set level
	logger.setLevel(level);

	// detach parent handlers
	logger.setUseParentHandlers(false);

	// check to see if it already has a consolehandler
	boolean consoleHandlerExists = false;
	for (Handler existingHandler : logger.getHandlers()) {
	    if (existingHandler instanceof ConsoleHandler) {
		consoleHandlerExists = true;
	    }
	}

	// add a console handler
	if (!consoleHandlerExists) {
	    //Handler consoleHandler = new ConsoleHandler();
	    Handler consoleHandler = new StdoutConsoleHandler();
	    consoleHandler.setLevel(level);
	    consoleHandler.setFormatter(formatter.getFormatter());
	    logger.addHandler(consoleHandler);
	}

    }

    public static void applyLoggerSettings(Logger logger, LogFormatter formatter, Level level, String fileName) {
	// set level
	logger.setLevel(level);

	// detach parent handlers
	logger.setUseParentHandlers(false);

	// add a console handler
	try {
	    Handler fileHandler = new FileHandler(fileName, MAX_FILE_RECORD_LIMIT, MAX_FILE_COUNT, APPEND_TO_LOG_FILE);
	    fileHandler.setLevel(level);
	    fileHandler.setFormatter(formatter.getFormatter());
	    logger.addHandler(fileHandler);
	} catch (Exception e) {
	    System.out.println("EXCEPTION: opening logfile:" + fileName + " Trace:" + e.getMessage());
	    e.printStackTrace();
	}

    }

    public static void applyDefaultSettings(Logger logger) {
	applyLoggerSettings(logger, LogUtils.DEFAULT_LOG_FORMATTER, LogUtils.DEFAULT_LOG_LEVEL);
    }

    public static void applyDefaultSettings(Logger logger, Level level) {
	applyLoggerSettings(logger, LogUtils.DEFAULT_LOG_FORMATTER, level);
    }

    public static void applyDefaultSettings(Logger logger, String fileName) {
	applyLoggerSettings(logger, LogUtils.DEFAULT_LOG_FORMATTER, LogUtils.DEFAULT_LOG_LEVEL, fileName);
    }

    public static void applyDefaultSettings(Logger logger, Level level, String fileName) {
	applyLoggerSettings(logger, LogUtils.DEFAULT_LOG_FORMATTER, level, fileName);
    }

}
