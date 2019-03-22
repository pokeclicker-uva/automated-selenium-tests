/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.core;

import com.simonbaars.seleniumframework.driver.DriverProvider;

public enum SeleniumType {
	BROWSER("browser", "getDriver");

	private final String typeName;
	private final String getter;

	/**
	 * Returns the type of Selenium instance that is currently being tested to.
	 * @return
	 */
	public static SeleniumType getCurrentType() {
		return DriverProvider.getCurrentDriver() == null
				? SeleniumType.BROWSER
				: DriverProvider.getCurrentDriver().getType();
	}

	private SeleniumType(String name, String getter) {
		this.typeName = name;
		this.getter = getter;
	}

	@Override
	public String toString() {
		return typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
	}

	public static SeleniumType getTypeForName(String label) {
		for (SeleniumType type : values()) {
			if (type.typeName.equalsIgnoreCase(label)) {
				return type;
			}
		}
		return null;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getGetter() {
		return getter;
	}
}
