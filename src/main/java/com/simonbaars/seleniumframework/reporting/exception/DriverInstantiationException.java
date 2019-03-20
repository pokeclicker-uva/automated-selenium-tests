package com.simonbaars.seleniumframework.reporting.exception;

public class DriverInstantiationException extends RuntimeException {
	
	private static final long serialVersionUID = 6323635787935662234L;

	public DriverInstantiationException() {
		super();
	}

	public DriverInstantiationException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public DriverInstantiationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DriverInstantiationException(String arg0) {
		super(arg0);
	}

	public DriverInstantiationException(Throwable arg0) {
		super(arg0);
	}
	
}
