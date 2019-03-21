package com.simonbaars.pokeclickertest.pom;

import org.openqa.selenium.WebElement;

import com.simonbaars.seleniumframework.core.PageObjectModel;
import com.simonbaars.seleniumframework.reporting.annotations.AlwaysOnPage;
import com.simonbaars.seleniumframework.reporting.annotations.Element;
import com.simonbaars.seleniumframework.reporting.annotations.Page;
import com.simonbaars.seleniumframework.reporting.annotations.TestAction;

import io.appium.java_client.TouchAction;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

@Page(name = "User dropdown", loadTimeout = 15)
public class UserDropdown extends PageObjectModel {
	@Element(xpath = "//li[text()=\"Logout\"]", name = "Logout button")

	WebElement logoutButton;

	@TestAction(name = "Click logout button")
	public void clickLogoutButton() {
		logoutButton.click();
	}
}
