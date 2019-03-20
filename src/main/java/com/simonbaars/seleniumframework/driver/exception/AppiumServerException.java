/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver.exception;

import java.util.Arrays;
import java.util.List;

public class AppiumServerException extends DriverException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -558736395333716881L;

	public AppiumServerException(List<String> serverLog) {
		super(Arrays.toString(serverLog.toArray()));
	}

	public AppiumServerException(String exception) {
		super(exception);
	}
}
