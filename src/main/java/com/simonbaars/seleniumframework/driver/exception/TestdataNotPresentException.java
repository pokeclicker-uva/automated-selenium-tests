/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver.exception;

public class TestdataNotPresentException extends RuntimeException {
	private static final long serialVersionUID = 7485022179545753989L;

	public TestdataNotPresentException(String testdata) {
		super("The testdata \""+testdata+"\" does not seem to be present.");
	}
}
