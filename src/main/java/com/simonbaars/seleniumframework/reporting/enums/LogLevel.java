package com.simonbaars.seleniumframework.reporting.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum LogLevel {
	SCREENSHOTSONLYAFTERERROR("Only take a screenshot after an error"),
	AFTER("Take screenshots after every action"),
	BEFORE("Take screenshots before every action"),
	ALWAYS("Take screenshots before and after every action"),
	NOSCREENSHOTS("Don't take screenshots at all");
	
	private final String description;
	private static LogLevel currentLogLevel = SCREENSHOTSONLYAFTERERROR;

	private LogLevel(String description) {
		this.description = description;
	}
	
	public String toString() {
		return description;
	}

	public static LogLevel getCurrentLogLevel() {
		return currentLogLevel;
	}
	
	public static void setCurrentLogLevel(LogLevel currentLogLevel) {
		LogLevel.currentLogLevel = currentLogLevel;
	}

	public static List<String> names() {
		return Arrays.stream(values()).map(e -> e.description).collect(Collectors.toList());
	}

	public static void setCurrentLogLevel(String logLevel) {
		setCurrentLogLevel(Arrays.stream(values()).filter(e -> e.description.equals(logLevel)).findAny().get());
	}
}
