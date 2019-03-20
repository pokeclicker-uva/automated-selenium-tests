/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.core.common;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.simonbaars.seleniumframework.core.Wait;
import com.simonbaars.seleniumframework.driver.DriverProvider;
import com.simonbaars.seleniumframework.reporting.Logger;

/**
 * The SeleniumCommons class provides all kinds of methods to extend the functionality of Selenium WebDriver.
 *
 */
public class SeleniumCommons {
	private static int maxTimeout = 15;
	public static WebElement foundElement;

	private SeleniumCommons() {
	}

	/**
	 * Waits for a WebElement to be clickable and, if so, returns that WebElement. Timeout after 15 seconds. 
	 * 
	 * Example:
	 * <pre>
	 * {@code
	 * WebElement someButton = SeleniumCommons.findElementSafe(By.id("button_id")); //Wait max 15 seconds for the element to become visible
	 * someButton.click(); //Now we can click the button safely
	 * }
	 * </pre>
	 * @param by The criterion to search an element by.
	 * @return The WebElement that was found.
	 */
	public static WebElement findElementSafe(final By by) {
		return findElementSafe(DriverProvider.getDriver(), by, maxTimeout);
	}

	public static WebElement findElementSafe(WebDriver driver, final By by) {
		return findElementSafe(driver, by, maxTimeout);
	}

	/**
	 * Waits the given amount of seconds for a WebElement to be clickable and then returns that WebElement. 
	 * 
	 * Example:
	 * <pre>
	 * {@code
	 * WebElement someButton = SeleniumCommons.findElementSafe(By.id("button_id"), 5); //Wait max 5 seconds for the element to become visible
	 * someButton.click(); //Now we can click the button safely
	 * }
	 * </pre>
	 * @param by The criterion to search an element by.
	 * @param seconds The amount of seconds the program will wait.
	 * @return The WebElement that was found.
	 */
	public static WebElement findElementSafe(final By by, int seconds) {
		return findElementSafe(DriverProvider.getDriver(), by, seconds);
	}

	public static WebElement findElementSafe(WebDriver driver, final By by, int seconds) {
		new Wait(seconds).until(() -> elementClickable(driver, by));
		return driver.findElement(by);
	}

	/**
	 * Waits the given amount of seconds for a WebElement to be clickable and then returns that WebElement. 
	 * 
	 * Example:
	 * <pre>
	 * {@code
	 * WebElement someButton = SeleniumCommons.findElementSafe(By.id("buttom_id"), 5); //Wait max 5 seconds for the element to become visible
	 * someButton.click(); //Now we can click the button safely
	 * }
	 * </pre>
	 * @param by The criterion to search an element by.
	 * @param seconds The amount of seconds the program will wait.
	 * @return The WebElement that was found.
	 */
	public static List<WebElement> findElementsSafe(final By by) {
		return findElementsSafe(DriverProvider.getDriver(), by);
	}

	public static List<WebElement> findElementsSafe(WebDriver driver, final By by) {
		new Wait(maxTimeout).until(() -> elementClickable(by));
		return driver.findElements(by);
	}

	public static boolean isElementSafeToGet(final By by) {
		return isElementSafeToGet(DriverProvider.getDriver(), by);
	}

	public static boolean isElementSafeToGet(WebDriver driver, final By by) {
		try {
			new Wait(maxTimeout).until(() -> elementExists(by));
		} catch (TimeoutException e) {
			return false;
		}
		return true;
	}

	/**
	 * Waits for a WebElement to exist. Timeout after 15 seconds.
	 * 
	 * Example:
	 * <pre>
	 * {@code
	 * SeleniumCommons.useElementSafe(someExistingElementInstance).click();
	 * }
	 * </pre>
	 * @param element The same WebElement as the input WebElement.
	 * @return The input WebElement.
	 */
	@SuppressWarnings("ucd")
	public static WebElement useElementSafe(final WebElement element) {
		new Wait(maxTimeout).until(() -> elementExists(element));
		return element;
	}

	/**
	 * Switches the driver to an IFrame found by the specified criterium.
	 * 
	 * Example:
	 * <pre>
	 * {@code
	 * openIFrame(By.id("someIframe"));
	 * }
	 * </pre>
	 * @param by The WebElement that was found.
	 */
	public static void openIFrame(final By by) {
		openIFrame(DriverProvider.getDriver(), by);
	}

	public static void openIFrame(WebDriver driver, final By by) {
		driver.switchTo().frame(findElementSafe(by));
	}

	/**
	 * Sets the max time to search for an element when no duration is specified.
	 * @param newMaxTimeout
	 */
	public static void setMaxTimeout(int newMaxTimeout) {
		maxTimeout = newMaxTimeout;
	}

	/**
	 * Checks if a webelement has a certain attribute.
	 * @param element
	 * @param attribute
	 * @return
	 */
	public static boolean isAttributePresent(WebElement element, String attribute) {
		try {
			String value = element.getAttribute(attribute);
			return value != null && !value.isEmpty();
		} catch (Exception e) {
			Logger.log("Exception thrown while checking attribute present.", e);
		}
		return false;
	}

	/**
	 * Returns all attributes that a WebElement has.
	 * @param element
	 * @return
	 */
	public static Map<String, String> getAvailableAttributes(WebElement element) {
		return getAvailableAttributes(DriverProvider.getDriver(), element);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getAvailableAttributes(WebDriver driver, WebElement element) {
		Map<String, String> availableAttributes = (Map<String, String>) ((JavascriptExecutor) driver).executeScript(
				"var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;",
				element);
		String text = element.getText();
		if (text != null) {
			availableAttributes = new HashMap<>(availableAttributes);
			availableAttributes.put("text()", text);
		}
		return availableAttributes;
	}

	/**
	 * Returns the amount of elements that were found for a certain xPath.
	 * @param xPath
	 * @return
	 */
	public static int getAmountOfElementsForXPath(String xPath) {
		return getAmountOfElementsForXPath(DriverProvider.getDriver(), xPath);
	}

	public static int getAmountOfElementsForXPath(WebDriver driver, String xPath) {
		return driver.findElements(By.xpath(xPath)).size();
	}

	/**
	 * Checks if the given element exists and is displayed.
	 */
	public static boolean elementExists(WebElement element) {
		try {
			return element.isEnabled() && element.isDisplayed();
		} catch (StaleElementReferenceException e) {
			Logger.log("Exception thrown while checking element existance.", e);
		}
		return false;
	}

	public static boolean elementExists(By by) {
		return elementExists(DriverProvider.getDriver(), by);
	}

	public static boolean elementExists(WebDriver driver, By by) {
		try {
			foundElement = driver.findElement(by);
			return true;
		} catch (Exception e) {
			Logger.log("Exception thrown while checking element existance.", e);
		}
		return false;
	}
	public static boolean elementClickable(By by) {
		return elementClickable(DriverProvider.getDriver(), by);
	}

	public static boolean elementClickable(WebDriver driver, By by) {
		try {
			foundElement = driver.findElement(by);
			return foundElement.isEnabled() && foundElement.isDisplayed();
		} catch (Exception e) {
			Logger.log("Exception thrown while checking element clickable.", e);
		}
		return false;
	}

	public static String downloadFile(WebElement webElement, String fileName, int timeout) {
		return downloadFile(DriverProvider.getDriver(), webElement, fileName, timeout);
	}

	public static String downloadFile(WebDriver driver, WebElement webElement, String fileName, int timeout) {
		try {
			String downloadsFolder = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;
			((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2])",
					webElement, "download", "");

			webElement.click();

			Thread.sleep(timeout);
			File file = new File(downloadsFolder + fileName);
			if (!file.exists()) {
				Logger.log("The file was not found. Are you sure the filename you entered is correct?");
				return null;
			}

			String variableValue = null;
			//if (file.getName().endsWith(".pdf")) {
			//	PDDocument pdDoc = PDDocument.load(file);
			//	variableValue = new PDFTextStripper().getText(pdDoc).replace("\n", "").replace("\r", "");
			//	pdDoc.close();
			//} else {
				variableValue = TestingCommons.getFileAsString(file);
			//}
			Files.deleteIfExists(file.toPath());
			return variableValue;
		} catch (Exception e) {
			Logger.log("Could not download the file.", e);
			return null;
		}
	}

	public static WebElement getElementSafe(WebDriver driver, By by) {
		try {
			return driver.findElement(by);
		} catch (Exception e) {
			return null;
		}
	}

	public static WebElement getElementSafe(int driverNumber, By by) {
		return getElementSafe(DriverProvider.getDriver(driverNumber), by);
	}

	public static WebElement getElementSafe(By by) {
		return getElementSafe(DriverProvider.getDriver(), by);
	}

	public static WebElement getElementSafe(int driverNumber, String xpath) {
		return getElementSafe(driverNumber, By.xpath(xpath));
	}
	
	public static void goBack() {
		DriverProvider.getDriver().navigate().back();
		TestingCommons.sleep(50);
	}
}
