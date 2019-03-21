package com.simonbaars.pokeclickertest.pom;

import java.time.LocalDateTime;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.simonbaars.seleniumframework.core.PageObjectModel;
import com.simonbaars.seleniumframework.reporting.annotations.AlwaysOnPage;
import com.simonbaars.seleniumframework.reporting.annotations.Element;
import com.simonbaars.seleniumframework.reporting.annotations.Page;
import com.simonbaars.seleniumframework.reporting.annotations.TestAction;

@Page(name = "RegistrationPage", loadTimeout = 15)
public class RegistrationPage extends PageObjectModel {
	@Element(xpath = "//form[1]/p[1]//input[1]", name = "Username Inputfield")
	@AlwaysOnPage
	WebElement usernameInputfield;
	@Element(xpath = "//input[@name=\"email\"]", name = "Email Inputfield")
	@AlwaysOnPage
	WebElement emailInputfield;
	@Element(xpath = "//p[3]//input[1]", name = "Password Inputfield")
	@AlwaysOnPage
	WebElement passwordInputfield;

	@TestAction(name = "Enter Username")
	public void enterUsername(String regUsername, String currentSecond, String currentMinute, String currentHour,
			String currentDay, String currentMonth, String currentYear) {
		usernameInputfield.sendKeys(regUsername + Integer.toString(LocalDateTime.now().getSecond())
				+ Integer.toString(LocalDateTime.now().getMinute()) + Integer.toString(LocalDateTime.now().getHour())
				+ Integer.toString(LocalDateTime.now().getDayOfMonth())
				+ Integer.toString(LocalDateTime.now().getMonth().getValue())
				+ Integer.toString(LocalDateTime.now().getYear()));
	}
	@TestAction(name = "Enter Email")
	public void enterEmail(String email, String currentSecond, String currentMinute, String currentHour,
			String currentDay, String currentMonth, String currentYear) {
		emailInputfield.sendKeys(email + Integer.toString(LocalDateTime.now().getSecond())
				+ Integer.toString(LocalDateTime.now().getMinute()) + Integer.toString(LocalDateTime.now().getHour())
				+ Integer.toString(LocalDateTime.now().getDayOfMonth())
				+ Integer.toString(LocalDateTime.now().getMonth().getValue())
				+ Integer.toString(LocalDateTime.now().getYear()));
	}
	@TestAction(name = "Enter Password")
	public void enterPassword(String regPassword) {
		passwordInputfield.sendKeys(regPassword + Keys.ENTER);
	}
}
