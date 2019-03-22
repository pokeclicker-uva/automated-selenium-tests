package com.simonbaars.seleniumframework.reporting;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.simonbaars.seleniumframework.reporting.enums.LogType;
import com.simonbaars.seleniumframework.reporting.interfaces.Broadcast;

public class Logger {
	
	public static final Broadcast loggerBroadcast = LogType.TEST_LOG.getBroadcast();

	public static void log(String string) {
		//loggerBroadcast.broadcast(string);
		System.out.println(string);
	}

	public static void log(String string, Exception e) {
		//loggerBroadcast.broadcast(string);
		//loggerBroadcast.broadcast(ExceptionUtils.getStackTrace(e));
		System.out.println(string);
		e.printStackTrace();
	}

}
