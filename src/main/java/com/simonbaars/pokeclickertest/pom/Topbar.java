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

@Page(name = "Topbar", loadTimeout = 15)
public class Topbar extends PageObjectModel {
	@Element(xpath = "//p[text()=\"simon4853102132019\"]", name = "Username button")

	WebElement usernameButton;

	@TestAction(name = "Click username button")
	public void clickUsernameButton() {
		usernameButton.click();
	}
}
