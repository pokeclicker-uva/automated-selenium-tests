package com.simonbaars.pokeclickertest.pom;

import org.openqa.selenium.WebElement;

import com.simonbaars.seleniumframework.core.PageObjectModel;
import com.simonbaars.seleniumframework.reporting.annotations.AlwaysOnPage;
import com.simonbaars.seleniumframework.reporting.annotations.Element;
import com.simonbaars.seleniumframework.reporting.annotations.Page;
import com.simonbaars.seleniumframework.reporting.annotations.TestAction;

import org.openqa.selenium.Keys;

@Page(name = "LoginPage", loadTimeout = 15)
public class LoginPage extends PageObjectModel {
	@Element(xpath = "//form[2]/p[1]//input[1]", name = "Username Inputfield")

	WebElement usernameInputfield;
	@Element(xpath = "//form[2]/p[2]//input[1]", name = "Password Inputfield")

	WebElement passwordInputfield;

	@TestAction(name = "Enter Username")
	public void enterUsername(String username) {
		usernameInputfield.sendKeys(username);
	}
	@TestAction(name = "Enter Password")
	public void enterPassword(String password) {
		passwordInputfield.sendKeys(password + Keys.ENTER);
	}
}
