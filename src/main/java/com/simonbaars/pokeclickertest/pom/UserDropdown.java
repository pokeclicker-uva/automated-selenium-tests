package com.simonbaars.pokeclickertest.pom;

import org.openqa.selenium.WebElement;

import com.simonbaars.seleniumframework.core.PageObjectModel;
import com.simonbaars.seleniumframework.reporting.annotations.Element;
import com.simonbaars.seleniumframework.reporting.annotations.Page;
import com.simonbaars.seleniumframework.reporting.annotations.TestAction;

@Page(name = "User dropdown", loadTimeout = 15)
public class UserDropdown extends PageObjectModel {
	@Element(xpath = "//li[text()=\"Logout\"]", name = "Logout button")
	WebElement logoutButton;

	@TestAction(name = "Click logout button")
	public void clickLogoutButton() {
		logoutButton.click();
	}
}
