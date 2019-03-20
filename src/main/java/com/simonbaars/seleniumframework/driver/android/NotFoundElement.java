/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver.android;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

public class NotFoundElement implements WebElement {
	private String xpath;
	
	public NotFoundElement(String xpath) {
		super();
		this.xpath = xpath;
	}

	@Override
	public boolean isDisplayed() {
		return false;
	}
	
	@Override
	public boolean isEnabled() {
		return false;
	}
	
	@Override
	public boolean isSelected() {
		return false;
	}
	
	@Override
	public String getText() {
		throw new NoSuchElementException(getMessage());
	}

	private String getMessage() {
		return "The element by xpath "+xpath+" was not found.";
	}
	
	@Override
	public String getAttribute(String s) {
		throw new NoSuchElementException(getMessage());
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target) {
		return null;
	}

	@Override
	public void click() {
		throw new NoSuchElementException(getMessage());
	}

	@Override
	public void submit() {
		throw new NoSuchElementException(getMessage());
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		throw new NoSuchElementException(getMessage());
	}

	@Override
	public void clear() {
		throw new NoSuchElementException(getMessage());
	}

	@Override
	public String getTagName() {
		return null;
	}

	@Override
	public List<WebElement> findElements(By by) {
		return new ArrayList<>();
	}

	@Override
	public WebElement findElement(By by) {
		return null;
	}

	@Override
	public Point getLocation() {
		return null;
	}

	@Override
	public Dimension getSize() {
		return null;
	}

	@Override
	public Rectangle getRect() {
		return null;
	}

	@Override
	public String getCssValue(String propertyName) {
		return null;
	}
}
