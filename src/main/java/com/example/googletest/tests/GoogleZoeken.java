package com.example.googletest.tests;

import com.simonbaars.seleniumframework.reporting.SeleniumTestcase;
import com.simonbaars.seleniumframework.reporting.annotations.UsesDriver;

import com.example.googletest.pom.Homepagina;
import static com.simonbaars.seleniumframework.core.SeleniumType.BROWSER;

@UsesDriver(type = BROWSER, application = "http://google.nl")
public class GoogleZoeken extends SeleniumTestcase {
	@Override
	public void run() {
		Homepagina pomHomepagina = initPOM(Homepagina.class);
		pomHomepagina.zoekOpHallo();
	}
}
