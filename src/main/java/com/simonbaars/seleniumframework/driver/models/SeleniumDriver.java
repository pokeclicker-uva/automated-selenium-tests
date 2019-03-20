/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver.models;

import org.openqa.selenium.WebDriver;

import com.simonbaars.seleniumframework.core.SeleniumType;

public class SeleniumDriver {
	private SeleniumType type;
	private String application;
	private WebDriver driver;

	public SeleniumDriver(SeleniumType type, String application, WebDriver driver) {
		this.type = type;
		this.application = application;
		this.driver = driver;
	}

	public SeleniumDriver(SeleniumType type, WebDriver driver) {
		this.type = type;
		this.driver = driver;
	}

	public SeleniumDriver(SeleniumType type, String application) {
		this.type = type;
		this.application = application;
	}

	public SeleniumDriver(SeleniumType type) {
		this.type = type;
	}

	public SeleniumType getType() {
		return type;
	}

	public void setType(SeleniumType type) {
		this.type = type;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	@Override
	public String toString() {
		return type.toString() + " Driver" + (application == null ? "" : " opening " + application);
	}
}
