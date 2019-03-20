/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.reporting.enums;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public enum ComparingMethod {
	EQUALS("Gelijk aan", "gelijk is aan", "[[key]]=[[value]]"), 
	CONTAINS("Bevat", "bevat", "contains([[key]], [[value]])"), 
	EQUALSIGNORECASE("Gelijk aan (niet hoofdlettergevoelig)", "gelijk is aan (niet hoofdlettergevoelig)", "translate([[key]],\"abcdefghijklmnopqrstuvwxyz\",\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\")=translate([[value]],\"abcdefghijklmnopqrstuvwxyz\",\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\")"), 
	CONTAINSIGNORECASE("Bevat (niet hoofdlettergevoelig)", "bevat (niet hoofdlettergevoelig)", "contains(translate([[key]],\"abcdefghijklmnopqrstuvwxyz\",\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\"), translate([[value]],\"abcdefghijklmnopqrstuvwxyz\",\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\"))"), 
	BEGINSWITH("Begint met", "begint met", "starts-with([[key]], [[value]])"), 
	ENDSWITH("Eindigt met", "eindigt met", "substring([[key]], string-length([[key]]) - string-length([[value]]) +1) = [[value]]");

	private final String name;
	private final String comparisonName;
	private final String xPath;

	private ComparingMethod(String name, String comparisonName, String xPath) {
		this.name = name;
		this.comparisonName = comparisonName;
		this.xPath = xPath;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean compare(String compare, String compareWith) {
		switch (this) {
			case CONTAINS :
				return compare.contains(compareWith);
			case EQUALS :
				return compare.equals(compareWith);
			case EQUALSIGNORECASE :
				return compare.equalsIgnoreCase(compareWith);
			case CONTAINSIGNORECASE :
				return StringUtils.containsIgnoreCase(compare, compareWith);
			case BEGINSWITH :
				return compare.startsWith(compareWith);
			case ENDSWITH :
				return compare.endsWith(compareWith);
		}
		return false;
	}
	
	public static ComparingMethod getByName(String string) {
		return Arrays.stream(values()).filter(e -> e.name.equals(string)).findAny().orElse(null);
	}

	public String getName() {
		return name;
	}

	public String getComparisonName() {
		return comparisonName;
	}

	public String getxPath() {
		return xPath;
	}
}
