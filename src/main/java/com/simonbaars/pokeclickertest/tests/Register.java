package com.simonbaars.pokeclickertest.tests;

import static com.simonbaars.seleniumframework.core.SeleniumType.BROWSER;

import com.simonbaars.pokeclickertest.pom.GameScreen;
import com.simonbaars.pokeclickertest.pom.RegistrationPage;
import com.simonbaars.pokeclickertest.pom.Topbar;
import com.simonbaars.pokeclickertest.pom.UserDropdown;
import com.simonbaars.seleniumframework.reporting.SeleniumTestcase;
import com.simonbaars.seleniumframework.reporting.annotations.UsesDriver;

@UsesDriver(type = BROWSER, application = "http://pokeclicker.uva-se.nl/")
public class Register extends SeleniumTestcase {
	@Override
	public void run() {
		RegistrationPage pomRegistrationPage = initPOM(RegistrationPage.class);
		pomRegistrationPage.enterUsername(getTestdata("RegUsername"), getTestdata("currentSecond"),
				getTestdata("currentMinute"), getTestdata("currentHour"), getTestdata("currentDay"),
				getTestdata("currentMonth"), getTestdata("currentYear"));
		pomRegistrationPage.enterEmail(getTestdata("Email"), getTestdata("currentSecond"), getTestdata("currentMinute"),
				getTestdata("currentHour"), getTestdata("currentDay"), getTestdata("currentMonth"),
				getTestdata("currentYear"));
		pomRegistrationPage.enterPassword(getTestdata("RegPassword"));

		GameScreen pomGameScreen = initPOM(GameScreen.class);
		pomGameScreen.checkIfPokeballIsShown();

		Topbar pomTopbar = initPOM(Topbar.class);
		pomTopbar.clickUsernameButton();

		UserDropdown pomUserDropdown = initPOM(UserDropdown.class);
		pomUserDropdown.clickLogoutButton();
	}
}
