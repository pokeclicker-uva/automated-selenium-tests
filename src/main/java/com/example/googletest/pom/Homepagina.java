package com.example.googletest.pom;

import org.openqa.selenium.WebElement;

import com.simonbaars.seleniumframework.core.PageObjectModel;
import com.simonbaars.seleniumframework.reporting.annotations.AlwaysOnPage;
import com.simonbaars.seleniumframework.reporting.annotations.Element;
import com.simonbaars.seleniumframework.reporting.annotations.Page;
import com.simonbaars.seleniumframework.reporting.annotations.TestAction;

import org.openqa.selenium.Keys;

@Page(name = "Homepagina", loadTimeout = 15)
public class Homepagina extends PageObjectModel {
	@Element(xpath = "//input[@id=\"lst-ib\"]", name = "Google zoekknop")

	WebElement googleZoekknop;

	@TestAction(name = "Zoek op \"Hallo\"")
	public void zoekOpHallo() {
		googleZoekknop.sendKeys("Hallo" + Keys.ENTER);
	}
}
