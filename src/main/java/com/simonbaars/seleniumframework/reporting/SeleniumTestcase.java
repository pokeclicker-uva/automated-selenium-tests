/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;

import com.simonbaars.seleniumframework.core.PageObjectModel;
import com.simonbaars.seleniumframework.core.SeleniumFramework;
import com.simonbaars.seleniumframework.driver.DriverProvider;
import com.simonbaars.seleniumframework.reporting.interfaces.Initializes;
import com.simonbaars.seleniumframework.reporting.interfaces.Testdata;
import com.simonbaars.seleniumframework.reporting.invocationhandler.ActionInvocationHandler;

import net.sf.cglib.proxy.Enhancer;

public abstract class SeleniumTestcase {
	private static List<AssertionError> errors = new ArrayList<>();
	
	private static final Map<String, String> collectedVariables = new HashMap<>();
	private static final Map<String, String> currentTestdata = new HashMap<>();

	protected SeleniumTestcase() {}

	protected void finishTestExecution() {
		if (!errors.isEmpty()) {
			List<AssertionError> errorsTemp = new ArrayList<>(errors);
			errors.clear();
			throw new AssertionError(errorsTemp.stream().map(AssertionError::getMessage)
					.collect(Collectors.joining(System.lineSeparator())));
		}
	}

	protected <T extends PageObjectModel> T initPOM(Class<T> pomClass) {
		return initPOM(DriverProvider.getDriver(), pomClass);
	}

	@SuppressWarnings("unchecked")
	protected <T extends PageObjectModel> T initPOM(WebDriver driver, Class<T> pomClass) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(pomClass);
		enhancer.setCallback(new ActionInvocationHandler());
		T instance = (T) enhancer.create();
		if(instance instanceof Initializes)
			((Initializes)instance).init();
		instance.waitForPageToValidate(pomClass);
		instance.initElements(driver, pomClass);
		return instance;
	}
	
	public static String getTestdata(String testdata) {
		return getCollectedVariables().containsKey(testdata) ? getCollectedVariables().get(testdata) : getCurrentTestdata().get(testdata);
	}
	
	public static String getTestdata(Testdata data) {
		return getCollectedVariables().containsKey(data.getColumnName()) ? getCollectedVariables().get(data.getColumnName()) : getCurrentTestdata().get(data.getColumnName());
	}
	
	public void addTestdata(String name, Object value) {
		System.out.println("Added testdata "+name+" with value "+value.toString());
		getCollectedVariables().put(name, value.toString());
	}
	
	@SafeVarargs
	public final void skipTillTest(Class<? extends SeleniumTestcase>...test) {
		SeleniumFramework.setStartFromTest(test);
	}
	
	public abstract void run();
	
	public static Map<String, String> getCollectedVariables() {
		return collectedVariables;
	}

	public static Map<String, String> getCurrentTestdata() {
		return currentTestdata;
	}
}
