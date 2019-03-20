/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver.exception;

import com.simonbaars.seleniumframework.reporting.SeleniumTestcase;

public class GoToExpectedPage extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final Class<? extends SeleniumTestcase> testClass;

	public GoToExpectedPage(Class<? extends SeleniumTestcase> testClass) {
		super("Opening test "+testClass.getName());
		this.testClass = testClass;
	}
	
	public GoToExpectedPage(Class<? extends SeleniumTestcase> testClass, String message) {
		super(message);
		this.testClass = testClass;
	}

	public Class<? extends SeleniumTestcase> getTestClass() {
		return testClass;
	}
}
