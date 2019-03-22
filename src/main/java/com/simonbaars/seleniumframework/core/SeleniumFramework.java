/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.simonbaars.seleniumframework.driver.DriverProvider;
import com.simonbaars.seleniumframework.reporting.Logger;
import com.simonbaars.seleniumframework.reporting.SeleniumTestcase;
import com.simonbaars.seleniumframework.reporting.annotations.UsesDriver;
import com.simonbaars.seleniumframework.reporting.enums.LogType;
import com.simonbaars.seleniumframework.reporting.exception.DriverInstantiationException;
import com.simonbaars.seleniumframework.reporting.types.Testcase;

/**
 * The SeleniumFramework class is used for performing some actions on execution start and finish.
 */
public class SeleniumFramework {
	private static boolean destroyDriver = true;
	private static Class<? extends SeleniumTestcase>[] startFromTest = null;
	private static final String testrunTimestamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
	
	private SeleniumFramework() {}
	
	/**
	 * The method that will be called when you end your program.
	 */
	public static void onExecutionFinish() {
		DriverProvider.destroyDriver(destroyDriver);
		LogType.stop();
		System.out.println("DONE!");
	}
	
	public static void keepDriverAlive() {
		destroyDriver = false;
	}
	
	/**
	 * Execute the current test on chrome (default).
	 */
	public static void testOnBrowser(){
		DriverProvider.loadDriver(SeleniumType.BROWSER);
	}
	
	/**
	 * Returns the name of the application that is currently being tested.
	 * @return
	 */
	public static String getApp() {
		return DriverProvider.getCurrentDriver().getApplication();
	}
	
	/**
	 * Returns the name of the application that is currently being tested.
	 * @return
	 */
	public static String getApp(int driverNumber) {
		return DriverProvider.getWebDriverPool().get(driverNumber).getApplication();
	}

	public static void beforeTest(Testcase test) {
		UsesDriver usesDriver = test.getTest().getClass().getAnnotation(UsesDriver.class);
		try {
			DriverProvider.usesDriver(usesDriver.type(), usesDriver.application());
		} catch (Exception e) {
			Logger.log("Could not load the "+usesDriver.type().name()+" driver (with application "+usesDriver.application()+") that this testcase uses.");
			throw new DriverInstantiationException(e);
		}
	}
	
	public static void afterTest(Testcase test) {
		
	}

	public static synchronized Class<? extends SeleniumTestcase>[] getStartFromTest() {
		return startFromTest;
	}

	public static synchronized void setStartFromTest(Class<? extends SeleniumTestcase>[] startFromTest) {
		SeleniumFramework.startFromTest = startFromTest;
	}

	public static String getTestData(String columnName) {
		return columnName;
	}
	
	public static String getTestrunTimestamp() {
		return testrunTimestamp;
	}

	
}

