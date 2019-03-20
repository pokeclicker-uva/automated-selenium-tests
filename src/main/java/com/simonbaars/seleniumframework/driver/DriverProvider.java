/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import com.simonbaars.seleniumframework.core.SeleniumType;
import com.simonbaars.seleniumframework.driver.android.EnhancedAndroidDriver;
import com.simonbaars.seleniumframework.driver.android.interfaces.CanQuitPartially;
import com.simonbaars.seleniumframework.driver.browser.EnhancedChromeDriver;
import com.simonbaars.seleniumframework.driver.exception.DriverException;
import com.simonbaars.seleniumframework.driver.models.SeleniumDriver;

/**
 * The DriverProvider class can be used to retrieve the WebDriver. The WebDriver will initialize itself on first use and destroy itself when it's done by default. So to get the WebDriver object, all you basically have to do is:
 * 
 * <pre>
 * {@code
 * WebDriver driver = DriverProvider.getDriver();
 * }
 * </pre>
 * 
 * For most projects that's all you need of this class. You can also use above code if you've downloaded the WebDriver manually to your documents folder.
 * 
 * If you have downloaded the WebDriver manually to any folder other than the Documents folder you'll need this line BEFORE getting the WebDriver with getDriver():
 * 
 * <pre>
 * {@code
 * DriverProvider.createWebdriver("webdriver.chrome.driver", "/path/to/your/chromedriver.exe");
 * }
 * </pre>
 * 
 * Afterwards you can simply get the WebDriver with getDriver();
 *
 */
public class DriverProvider {
	private static final Logger logger = Logger.getLogger(DriverProvider.class.getName());
	private static Map<Integer, SeleniumDriver> webDriverPool = new HashMap<>();
	private static int currentDriver = 0;

	private DriverProvider() {
	}

	public static void setCurrentDriver(int driverNumber) {
		DriverProvider.currentDriver = driverNumber;
	}

	/**
	 * Returns the WebDriver object if it is available. If not, it'll create one using the createWebdriver method.
	 * 
	 * Example:
	 * <pre>
	 * {@code
	 * WebDriver driver = DriverProvider.getDriver();
	 * }
	 * </pre>
	 * @return The WebDriver object.
	 */
	public static WebDriver getDriver() {
		SeleniumDriver currentDriver = getCurrentDriver();
		if (currentDriver == null || currentDriver.getDriver() == null || webDriverPool.size() == 0)
			currentDriver = ChromeDriverProvider.createChromeWebdriver(webDriverPool, getFirstFreeWebDriverIndex());
		if (currentDriver == null) {
			logger.severe("Driver creation was unsuccesful.");
			return null;
		}
		return currentDriver.getDriver();
	}

	public static WebDriver getDriver(int driverNumber) {
		logger.log(Level.FINE, "Requesting driver for driverNumber {0}", driverNumber);
		if (!webDriverPool.containsKey(driverNumber) || webDriverPool.get(driverNumber).getDriver() == null) {
			logger.log(Level.INFO, "Creating a new driver for driverNumber {0}", driverNumber);
			if (webDriverPool.containsKey(driverNumber)
					&& webDriverPool.get(driverNumber).getType() == SeleniumType.ANDROID)
				AndroidDriverProvider.createAndroidWebdriver(driverNumber,
						webDriverPool.get(driverNumber).getApplication());
			else if (!webDriverPool.containsKey(driverNumber)
					|| webDriverPool.get(driverNumber).getType() == SeleniumType.BROWSER)
				ChromeDriverProvider.createChromeWebdriver(webDriverPool, driverNumber);
		}
		return webDriverPool.get(driverNumber).getDriver();
	}

	/**
	 * Closes the driver (the browser will close).
	 */
	public static void destroyDriver() {
		destroyDriver(true);
	}

	public static void destroyDriver(boolean doDestroy) {
		while (!webDriverPool.keySet().isEmpty()) {
			destroyDriver(webDriverPool.keySet().stream().findFirst().get(), doDestroy);
		}
	}

	public static void destroyDriver(int driverId) {
		destroyDriver(driverId, true);
	}

	public static void destroyDriver(int driverId, boolean doDestroy) {
		if(!webDriverPool.containsKey(driverId))
			return;
		if (getDriver(driverId) instanceof CanQuitPartially)
			((CanQuitPartially) getDriver(driverId)).quit(doDestroy);
		else
			getDriver(driverId).quit();
		webDriverPool.remove(driverId);
	}

	static void addToWebDriverPool(int driverNumber, SeleniumType seleniumType, WebDriver driver, String app) {
		webDriverPool.put(driverNumber, new SeleniumDriver(seleniumType, app, driver));
	}

	/**
	 * @return
	 */
	private static int getFirstFreeWebDriverIndex() {
		int i = 0;
		while (webDriverPool.containsKey(i))
			i++;
		return i;
	}

	/**
	 * Creates an android driver if not yet available, then returns it.
	 * @return the android driver
	 */
	@SuppressWarnings("unchecked")
	public static AndroidDriver<AndroidElement> getAndroidDriver() {
		if (SeleniumType.getCurrentType() != SeleniumType.ANDROID) {
			loadDriver(SeleniumType.ANDROID);
		}
		return (AndroidDriver<AndroidElement>) getDriver();
	}

	@SuppressWarnings("unchecked")
	public static AndroidDriver<AndroidElement> getAndroidDriver(int driverNumber) {
		if (webDriverPool.get(driverNumber).getType() != SeleniumType.ANDROID) {
			loadDriver(driverNumber, SeleniumType.ANDROID, null);
		}
		return (AndroidDriver<AndroidElement>) getDriver(driverNumber);
	}

	public static EnhancedAndroidDriver getEnhancedAndroidDriver() {
		if (SeleniumType.getCurrentType() != SeleniumType.ANDROID) {
			loadDriver(SeleniumType.ANDROID);
		}
		return (EnhancedAndroidDriver) getDriver();
	}

	public static EnhancedAndroidDriver getEnhancedAndroidDriver(int driverNumber) {
		if (webDriverPool.get(driverNumber).getType() != SeleniumType.ANDROID) {
			loadDriver(driverNumber, SeleniumType.ANDROID, null);
		}
		return (EnhancedAndroidDriver) getDriver(driverNumber);
	}
	
	/**
	 * Returns the webdriver casted as a mobile driver. If a mobile driver is not currently loaded the android driver will be loaded by default.
	 * @return the android driver
	 */
	@SuppressWarnings("unchecked")
	public static MobileDriver<MobileElement> getMobileDriver() {
		if (!SeleniumType.isTestingOnMobile()) {
			loadDriver(SeleniumType.ANDROID);
		}
		return (MobileDriver<MobileElement>) getDriver();
	}

	@SuppressWarnings("unchecked")
	public static MobileDriver<MobileElement> getMobileDriver(int driverNumber) {
		if (webDriverPool.get(driverNumber).getType() != SeleniumType.ANDROID) {
			loadDriver(driverNumber, SeleniumType.ANDROID, null);
		}
		return (MobileDriver<MobileElement>) getDriver(driverNumber);
	}
	
	@SuppressWarnings("unchecked")
	public static EnhancedChromeDriver getChromeDriver(int driverNumber) {
		if (webDriverPool.get(driverNumber).getType() != SeleniumType.BROWSER) {
			loadDriver(driverNumber, SeleniumType.BROWSER, null);
		}
		return (EnhancedChromeDriver) getDriver(driverNumber);
	}

	public static Map<Integer, SeleniumDriver> getWebDriverPool() {
		return webDriverPool;
	}

	public static SeleniumDriver getCurrentDriver() {
		return webDriverPool.size() == 0 ? null : webDriverPool.get(currentDriver);
	}

	public static int getCurrentDriverNumber() {
		return currentDriver;
	}

	public static void usesDriver(SeleniumType type, String application) {
		int driverNumber = webDriverPool.entrySet().stream()
				.filter(e -> e.getValue().getType().equals(type) && e.getValue().getApplication().equals(application))
				.map(Entry::getKey).findAny().orElse(-1);
		if (driverNumber == -1)
			driverNumber = loadDriver(type, application);
		currentDriver = driverNumber;
	}

	public static int loadDriver(SeleniumType type, String application) {
		return loadDriver(getFirstFreeWebDriverIndex(), type, application);
	}

	/**
	 * @param type
	 * @param application
	 * @return
	 */
	public static int loadDriver(int driverNumber, SeleniumType type, String application) {
		WebDriver driver = null;
		addToWebDriverPool(driverNumber, type, driver, application);
		currentDriver = driverNumber;
		WebDriver d = getDriver(driverNumber);
		if (d instanceof ChromeDriver && application != null)
			d.get(application);
		return driverNumber;
	}

	public static int loadDriver(SeleniumType type) {
		return loadDriver(type, null);
	}

	public static int getDriverId(WebDriver driver) {
		Optional<Entry<Integer, SeleniumDriver>> id = webDriverPool.entrySet().stream()
				.filter(e -> e.getValue().getDriver() == driver).findFirst();
		if (!id.isPresent())
			throw new DriverException(
					"The driver " + driver + " does not exist in the webdriver pools!");
		return id.get().getKey();
	}
}
