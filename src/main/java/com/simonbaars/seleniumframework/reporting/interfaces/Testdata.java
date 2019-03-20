package com.simonbaars.seleniumframework.reporting.interfaces;

import com.simonbaars.seleniumframework.reporting.SeleniumTestcase;

public interface Testdata extends CharSequence {
	public String getColumnName();
	
	@Override
	public default char charAt(int arg0) {
		return toString().charAt(arg0);
	}

	@Override
	public default int length() {
		return toString().length();
	}

	@Override
	public default CharSequence subSequence(int arg0, int arg1) {
		return toString().subSequence(arg0, arg1);
	}
	
	public default String getValue() {
		return SeleniumTestcase.getTestdata(getColumnName());
	}
}
