package com.simonbaars.pokeclickertest.tests;

import static com.simonbaars.seleniumframework.core.SeleniumType.BROWSER;

import com.simonbaars.pokeclickertest.pom.GameScreen;
import com.simonbaars.pokeclickertest.pom.LoginPage;
import com.simonbaars.seleniumframework.reporting.SeleniumTestcase;
import com.simonbaars.seleniumframework.reporting.annotations.UsesDriver;

@UsesDriver(type = BROWSER, application = "http://pokeclicker.uva-se.nl/")
public class Login extends SeleniumTestcase {
	@Override
	public void run() {
		LoginPage pomLoginPage = initPOM(LoginPage.class);
		pomLoginPage.enterUsername(getTestdata("Username"));
		pomLoginPage.enterPassword(getTestdata("Password"));

		GameScreen pomGameScreen = initPOM(GameScreen.class);
		pomGameScreen.checkIfPokeballIsDisplayed();
	}
}
