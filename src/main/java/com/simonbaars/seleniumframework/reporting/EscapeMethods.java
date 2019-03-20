/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.reporting;

public class EscapeMethods {
	
	private EscapeMethods() {}
	
	public static String filterAttributesFromXpath(String string) {
		StringBuilder stringBuilder = new StringBuilder(string);
		boolean eatChars = false;
		for (int i = 0; i < stringBuilder.length(); i++) {
			if (stringBuilder.charAt(i) == '@') {
				eatChars = true;
			}
			if (eatChars && (stringBuilder.charAt(i) == '@'
					|| (stringBuilder.charAt(i) >= 'a' && stringBuilder.charAt(i) <= 'z')
					|| (stringBuilder.charAt(i) >= 'A' && stringBuilder.charAt(i) <= 'Z'))) {
				stringBuilder.deleteCharAt(i);
				i--;
			} else {
				eatChars = false;
			}
		}
		return stringBuilder.toString();
	}

	public static String javaValid(String testName2) {
		return fileNameValid(testName2).toLowerCase();
	}

	/**
	 * @param testName2
	 * @return
	 */
	public static String fileNameValid(String testName2) {
		return testName2.replaceAll("[^A-Za-z]", "");
	}

	public static String javaIndentifierValid(String identifier) {
		StringBuilder builder = new StringBuilder(identifier);
		while (builder.length() > 0 && !Character.isJavaIdentifierStart(builder.charAt(0))) {
			builder = builder.deleteCharAt(0);
		}
		for (int i = 1; i < builder.length(); i++) {
			if (!Character.isJavaIdentifierPart(builder.charAt(i))) {
				if (builder.charAt(i) == ' ' && builder.length() != i + 1
						&& Character.isLowerCase(builder.charAt(i + 1))) {
					builder.setCharAt(i + 1, Character.toUpperCase(builder.charAt(i + 1)));
				}
				builder.deleteCharAt(i);
				i--;
			}
		}
		return builder.toString();
	}

	public static String javaObjectName(String testName2) {
		if (testName2.length() > 0) {
			testName2 = javaIndentifierValid(testName2);
			if (testName2.length() > 0) {
				return Character.toUpperCase(testName2.charAt(0)) + testName2.substring(1);
			}
		}
		return "";
	}

	public static String javaVariableName(String testName2) {
		if (testName2.length() > 0) {
			testName2 = javaIndentifierValid(testName2);
			return Character.toLowerCase(testName2.charAt(0)) + testName2.substring(1);
		}
		return "";
	}

	

	public static String escapeXPath(String getxPath) {
		StringBuilder builder = new StringBuilder(getxPath);
		boolean isParsingSymbols = true;
		for (int i = 0; i < builder.length(); i++) {
			if (isParsingSymbols && Character.isAlphabetic(builder.charAt(i))) {
				builder.setCharAt(i, Character.toUpperCase(builder.charAt(i)));
				isParsingSymbols = false;
			} else if (!Character.isAlphabetic(builder.charAt(i))) {
				isParsingSymbols = true;
			}
		}
		return filterAttributesFromXpath(builder.toString());
	}

	public static String javaConstant(String input) {
		return input.toUpperCase().replace(" ", "_");
	}
}
