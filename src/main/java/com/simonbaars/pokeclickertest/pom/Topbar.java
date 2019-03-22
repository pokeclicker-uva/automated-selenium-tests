package com.simonbaars.pokeclickertest.pom;

import org.openqa.selenium.WebElement;

import com.simonbaars.seleniumframework.core.PageObjectModel;
import com.simonbaars.seleniumframework.reporting.annotations.Element;
import com.simonbaars.seleniumframework.reporting.annotations.Page;
import com.simonbaars.seleniumframework.reporting.annotations.TestAction;

@Page(name = "Topbar", loadTimeout = 15)
public class Topbar extends PageObjectModel {
	@Element(xpath = "//p[@class=\"UserMenu-userdetails\"]", name = "Username button")	
	WebElement usernameButton;

	@TestAction(name = "Click username button")
	public void clickUsernameButton() {
		usernameButton.click();
	}
}
