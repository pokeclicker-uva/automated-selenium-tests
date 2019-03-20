/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.reporting.invocationhandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.DefaultElementLocator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;

import com.simonbaars.seleniumframework.core.Wait;
import com.simonbaars.seleniumframework.core.common.SeleniumCommons;
import com.simonbaars.seleniumframework.core.common.TestingCommons;
import com.simonbaars.seleniumframework.driver.android.NotFoundElement;
import com.simonbaars.seleniumframework.reporting.annotations.Element;

public class ElementInvocationHandler extends LocatingElementHandler {

	int waitForElementToBeClickable;
	int waitForElementToBePresent;
	
	String elementXPath;
	
	WebDriver driver;

	public ElementInvocationHandler(WebDriver driver, Field field) {
		super(new DefaultElementLocator(driver, field));
		Element element = field.getAnnotation(Element.class);
		this.waitForElementToBeClickable = element.waitTillClickable();
		this.waitForElementToBePresent = element.waitTillExists();
		this.elementXPath = element.xpath();
		this.driver = driver;
	}

	@Override
	public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
		WebElement elementObject = null;
		String xpath = TestingCommons.resolveVariables(elementXPath);
		if (waitForElementToBePresent != 0)
			new Wait(waitForElementToBePresent).until(() -> SeleniumCommons.elementExists(By.xpath(xpath)));
		else if (waitForElementToBeClickable != 0) {
			new Wait(waitForElementToBeClickable).until(() -> SeleniumCommons.elementClickable(By.xpath(xpath)));
		} else {
			try {
				elementObject = driver.findElement(By.xpath(xpath));
			} catch (NoSuchElementException e) {
				elementObject = new NotFoundElement(xpath);
			}
		}
		if(elementObject == null) elementObject = SeleniumCommons.foundElement;
		return method.invoke(elementObject, objects);
	}
}
