package com.simonbaars.pokeclickertest.pom;

import org.openqa.selenium.WebElement;

import com.simonbaars.seleniumframework.core.PageObjectModel;
import com.simonbaars.seleniumframework.reporting.annotations.AlwaysOnPage;
import com.simonbaars.seleniumframework.reporting.annotations.Element;
import com.simonbaars.seleniumframework.reporting.annotations.Page;
import com.simonbaars.seleniumframework.reporting.annotations.TestAction;

import com.simonbaars.seleniumframework.reporting.Assert;

@Page(name = "GameScreen", loadTimeout = 15)
public class GameScreen extends PageObjectModel {
	@Element(xpath = "//div[@class=\"Clicking\"]//img", name = "Pokeball")
	WebElement pokeball;

	@TestAction(name = "Check if pokeball is displayed")
	public void checkIfPokeballIsDisplayed() {
		Assert.assertTrue(pokeball.isDisplayed(), "Check if pokeball is displayed");
	}
	@TestAction(name = "Check if pokeball is shown")
	public void checkIfPokeballIsShown() {
		Assert.assertTrue(pokeball.isDisplayed(), "Check if pokeball is shown");
	}
}
