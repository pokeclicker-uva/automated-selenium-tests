package com.simonbaars.seleniumframework.reporting.enums;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.simonbaars.seleniumframework.core.common.SavePaths;
import com.simonbaars.seleniumframework.core.common.TestingCommons;
import com.simonbaars.seleniumframework.driver.DriverProvider;
import com.simonbaars.seleniumframework.reporting.Logger;
import com.simonbaars.seleniumframework.reporting.broadcast.LogEntryBroadcast;
import com.simonbaars.seleniumframework.reporting.broadcast.LoggerBroadcast;
import com.simonbaars.seleniumframework.reporting.broadcast.ScreenshotBroadcast;
import com.simonbaars.seleniumframework.reporting.interfaces.Broadcast;

public enum LogType {
	BROWSER("JavaScript Logs", "javascript", new LogEntryBroadcast(org.openqa.selenium.logging.LogType.BROWSER)),
	DRIVER("ChromeDriver Logs", "browser", new LogEntryBroadcast(org.openqa.selenium.logging.LogType.DRIVER)),
	PERFORMANCE("Performance Logs", "performance", new LogEntryBroadcast(org.openqa.selenium.logging.LogType.PERFORMANCE)),
	TEST_LOG("Testcase Logs", "testcase", new LoggerBroadcast()),
	ASSERT_LOG("Assertions", "assert", new LoggerBroadcast()),
	SCREENSHOT("Screenshot", "screenshot", new ScreenshotBroadcast());
	
	private final String name;
	private final String prefix;
	private final Broadcast broadcast;
	private static boolean driverLoggersInitiated = false;

	private LogType(String name, String prefix, Broadcast broadcast) {
		this.name = name;
		this.prefix = prefix;
		this.broadcast = broadcast;
	}

	public String getPrefix() {
		return prefix;
	}

	public Broadcast getBroadcast() {
		return broadcast;
	}
	
	public String getName() {
		return name;
	}
	
	public byte[] getFileContents(String id) {
		try {
			String fileName = SavePaths.getReportDirectory()+prefix+"_"+id+".txt";
			File file = new File(fileName);
			if(!file.exists())
				return ("Oops! No logs have been recorded (File name "+fileName+").").getBytes(StandardCharsets.UTF_8);
			return TestingCommons.getFileBytes(file);
		} catch (IOException e) {
			return ("Oops! Could not fetch file contents: "+e.getMessage()).getBytes(StandardCharsets.UTF_8);
		}
	}
	
	public void registerOutput(String fileName) {
		getBroadcast().registerOutput(prefix, fileName);
	}
	
	public static void registerOutputs(String fileName) {
		for(LogType log : values())
			log.registerOutput(fileName);
	}
	
	public static void removeOutputs(String fileName) {
		for(LogType log : values())
			log.getBroadcast().removeOutput(fileName);
	}

	public static void stop() {
		for(LogType log : values())
			log.getBroadcast().stop();
	}
	
	public static void takeScreenshot(LogLevel...logLevel) {
		try {
			if(Arrays.stream(logLevel).anyMatch(e -> e == LogLevel.getCurrentLogLevel()))
				SCREENSHOT.broadcast.broadcast(((TakesScreenshot)DriverProvider.getDriver()).getScreenshotAs(OutputType.BYTES));
		} catch (Exception e) {
			Logger.log("Taking a screenshot failed.", e);
		}
	}

	public static void initiateDriverLoggers() {
		if(!driverLoggersInitiated) {
			((LogEntryBroadcast)BROWSER.getBroadcast()).start();
			((LogEntryBroadcast)DRIVER.getBroadcast()).start();
			((LogEntryBroadcast)PERFORMANCE.getBroadcast()).start();
			driverLoggersInitiated = true;
		}
	}
}
