/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver.exception;

public class DriverException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4403903873119100213L;

	public DriverException() {
		super();
	}

	public DriverException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public DriverException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DriverException(String arg0) {
		super(arg0);
	}

	public DriverException(Throwable arg0) {
		super(arg0);
	}

}
