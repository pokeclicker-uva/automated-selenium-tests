/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.reporting.enums;

public enum VariableDelimiters {
	VARIABLE_DELIMITER_BEGIN("[["), VARIABLE_DELIMITER_END("]]");
	String value;

	private VariableDelimiters(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
