/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import com.simonbaars.seleniumframework.core.common.TestingCommons;
import com.simonbaars.seleniumframework.driver.android.ADBCommands;
import com.simonbaars.seleniumframework.driver.android.EnhancedAndroidDriver;
import com.simonbaars.seleniumframework.driver.exception.DriverException;
import com.simonbaars.seleniumframework.driver.models.AppiumDriver;

public class AndroidDriverProvider {
	private static final int APPIUM_SERVER_TIMEOUT = 40000;
	private static final Logger logger = Logger.getLogger(AndroidDriverProvider.class.getName());
	private static final Map<Integer, AppiumDriver> drivers = new HashMap<>();
	private static String targetDevice = null;
	private static boolean wipeAll = true;

	private AndroidDriverProvider() {
	}

	/**
	 * Creates a WebDriver object with the given property key and value.
	 * 
	 * Example:
	 * <pre>
	 * {@code
	 * DriverProvider.createWebdriver("webdriver.chrome.driver", "/path/to/your/chromedriver.exe");
	 * }
	 * </pre>
	 * @param propertyKey The property key is the classpath for the driver ("webdriver.chrome.driver" by default).
	 * @param propertyValue The value key is the path to the driver on the local system.
	 */
	public static void createAndroidWebdriver(int driverNumber, String application) {
		logger.log(Level.INFO, "Creating a new AndroidDriver {0}", application);
		DesiredCapabilities capabilities = new DesiredCapabilities();
		try {
			drivers.put(driverNumber, loadPhysicalDevice(capabilities));
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning("Trying to load a physical device ended with error " + e.getLocalizedMessage()
					+ ". Defaulting to an Android emulator...");
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android emulator");
			capabilities.setCapability("newCommandTimeout", 10000);
		}
		if (application == null) {
			capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
		} else {
			if (!application.contains("/"))
				application = AndroidDriverProvider.class.getClassLoader().getResource(application).getFile();
			capabilities.setCapability(MobileCapabilityType.APP, application);
		}
		try {
			DriverProvider.getWebDriverPool().get(driverNumber).setDriver(new EnhancedAndroidDriver(drivers.get(driverNumber).getAppiumServer().getUrl(), capabilities));
		} catch (Exception e) {
			throw new DriverException("Driver could not be created", e);
		}
	}

	private static AppiumDriver loadPhysicalDevice(DesiredCapabilities capabilities) throws InterruptedException, IOException {
		AppiumDriver driver = new AppiumDriver();
		if(getTargetDevice() == null) {
			List<String> devices = ADBCommands.getDeviceProperties();
			if (devices.isEmpty()) {
				logger.info("There are no physical devices available!");
				throw new DriverException("No physical devices are available!");
			}
			logger.info("Device uid in start ADB Appium: " + devices.get(0));
			setTargetDevice(devices.get(0));
		} 
		driver.setUuid(getTargetDevice());

		ADBCommands.clearLogcat(getTargetDevice());
		ADBCommands.captureLogcat(getTargetDevice());
		driver.startLogging();

		addDesiredCapabilities(capabilities, driver.getUuid());
		return driver;
	}

	public static void stopAndroidDriver(int driverNumber) {
		if (drivers.containsKey(driverNumber))
			drivers.remove(driverNumber);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void saveLogFile(String fileName, Iterable it) {
		final StringBuilder saveString = new StringBuilder();
		it.forEach(f -> {
			String appendStr = f instanceof LogEntry
					? "[" + ((LogEntry) f).getTimestamp() + "] " + ((LogEntry) f).getMessage()
					: f.toString();
			saveString.append(appendStr + System.lineSeparator());
		});
		if (saveString.length() == 0)
			return;
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		try {
			TestingCommons.writeStringToFile(new File("logs/" + timeStamp + "-" + fileName + ".log"),
					saveString.toString());
		} catch (IOException e1) {
			logger.warning("Could not save " + fileName + " log");
		}
	}

	private static void addDesiredCapabilities(DesiredCapabilities cap, String uuid) {
		cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, "Appium");
		cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
		cap.setCapability(MobileCapabilityType.DEVICE_NAME, uuid);
		cap.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 3600);
		cap.setCapability(MobileCapabilityType.NO_RESET, !wipeAll);
		cap.setCapability(MobileCapabilityType.FULL_RESET, wipeAll);
		cap.setCapability("autoLaunch", true);
		cap.setCapability(MobileCapabilityType.UDID, uuid);
		cap.setCapability(AndroidMobileCapabilityType.RECREATE_CHROME_DRIVER_SESSIONS, 90);
		cap.setCapability(AndroidMobileCapabilityType.AUTO_GRANT_PERMISSIONS, true);
		cap.setCapability("clearSystemFiles", true);
		cap.setCapability(AndroidMobileCapabilityType.AUTO_WEBVIEW_TIMEOUT, 40000);
		cap.setCapability(AndroidMobileCapabilityType.DISABLE_ANDROID_WATCHERS, true);
		cap.setCapability(MobileCapabilityType.TAKES_SCREENSHOT, true);
	}
	
	public static String getTargetDevice() {
		return targetDevice;
	}

	public static void setTargetDevice(String targetDevice) {
		AndroidDriverProvider.targetDevice = targetDevice;
	}			

	public static boolean isWipeAll() {
		return wipeAll;
	}

	public static void setWipeAll(boolean wipeAll) {
		AndroidDriverProvider.wipeAll = wipeAll;
	}
}
