/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.core;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;

import com.simonbaars.seleniumframework.core.common.TestingCommons;
import com.simonbaars.seleniumframework.driver.DriverProvider;
import com.simonbaars.seleniumframework.reporting.Logger;
import com.simonbaars.seleniumframework.reporting.annotations.AlwaysOnPage;
import com.simonbaars.seleniumframework.reporting.annotations.Element;
import com.simonbaars.seleniumframework.reporting.annotations.Page;
import com.simonbaars.seleniumframework.reporting.enums.ComparingMethod;
import com.simonbaars.seleniumframework.reporting.interfaces.HasCustomValidation;
import com.simonbaars.seleniumframework.reporting.invocationhandler.ElementInvocationHandler;

public abstract class PageObjectModel {
	protected final WebDriver driver;
	
	protected PageObjectModel() {
		this.driver = DriverProvider.getDriver();
	}
	
	protected PageObjectModel(WebDriver driver) {
		this.driver = driver;
	}
	
	public boolean validatePage(Class<? extends PageObjectModel> pageObjectModel){
		return validatePage(pageObjectModel, getContext(pageObjectModel), getWindow(pageObjectModel));
	}
	
	public void waitForPageToValidate(Class<? extends PageObjectModel> pageObjectModel) {
		int loadTimeout = getLoadTimeout(pageObjectModel);
		new Wait(loadTimeout).until(() -> validatePage(pageObjectModel), "Waiting "+loadTimeout+" seconds for page "+this.getName(pageObjectModel)+" failed.");
	}

	public boolean validatePage(Class<? extends PageObjectModel> pageObjectModel, String context, String window) {
		return validatePage(pageObjectModel, ComparingMethod.CONTAINS, context, window);
	}
	
	@SuppressWarnings("unchecked")
	private boolean validatePage(Class<? extends PageObjectModel> pageObjectModel, ComparingMethod comparingMethod, String context, String window) {
		try {
			return tryToSeekXPaths(pageObjectModel);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.log("Something went wrong while validating page", e);
			return false;
		}
	}
	
	private boolean tryToSeekXPaths(Class<? extends PageObjectModel> pageObjectModel) {
		List<String> xpaths = Arrays.asList(pageObjectModel.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(AlwaysOnPage.class)).map(f -> f.getAnnotation(Element.class).xpath()).collect(Collectors.toList());
		
		if(this instanceof HasCustomValidation && !((HasCustomValidation)this).executeCustomValidation(xpaths))
			return false;
		
		return xpaths.stream().allMatch(xpath -> {
			List<WebElement> findElements = driver.findElements(By.xpath(TestingCommons.resolveVariables(xpath)));
			System.out.println("In "+driver.getTitle()+ " found "+findElements.size()+ " matching elements with xpath "+xpath);
			return !findElements.isEmpty();
		});
	}

	public WebDriver getDriver() {
		return driver;
	}
	
	public String getContext(Class<? extends PageObjectModel> pomClass) {
		return pomClass.getAnnotation(Page.class).context();
	}
	
	public String getWindow(Class<? extends PageObjectModel> pomClass) {
		return pomClass.getAnnotation(Page.class).window();
	}
	
	public String getName(Class<? extends PageObjectModel> pomClass) {
		return pomClass.getAnnotation(Page.class).name();
	}
	
	public int getLoadTimeout(Class<? extends PageObjectModel> pomClass) {
		return pomClass.getAnnotation(Page.class).loadTimeout();
	}
	
	public void reproxyElements() {
		initElements(driver, (Class<? extends PageObjectModel>)this.getClass());
	}
	
	public void initElements(WebDriver driver, Class<? extends PageObjectModel> pomClass) {
		List<Field> elements = Arrays.stream(pomClass.getDeclaredFields())
				.filter(f -> f.getAnnotation(Element.class) != null).collect(Collectors.toList());
		for (Field field : elements) {
			field.setAccessible(true);
			try {
				field.set(this,
						(WebElement) Proxy.newProxyInstance(pomClass.getClassLoader(),
								new Class[]{WebElement.class, WrapsElement.class, Locatable.class},
								new ElementInvocationHandler(driver, field)));
			} catch (Exception e) {
				Logger.log("Element could not be proxied, and will not work " + field.getName(), e);
			}
		}
	}
}

