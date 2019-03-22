/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;

import com.simonbaars.seleniumframework.core.SeleniumType;
import com.simonbaars.seleniumframework.core.common.SavePaths;
import com.simonbaars.seleniumframework.driver.browser.EnhancedChromeDriver;
import com.simonbaars.seleniumframework.driver.models.SeleniumDriver;

public class ChromeDriverProvider {

	private static final String OS_NAME = "os.name";

	private ChromeDriverProvider() {
	}

	private static final Logger logger = Logger.getLogger(ChromeDriverProvider.class.getName());

	private static final String USER_HOME = "user.home";
	private static final String CHROMEDRIVER_WEBSITE = "http://chromedriver.storage.googleapis.com/";

	/**
	 * The default classpath, path and url of the default WebDriver settings for Windows.
	 */
	private static final String[] WINDOWSDEFAULT = {"webdriver.chrome.driver",
			SavePaths.getApplicationDataFolder() + "chromedriver.exe", "/chromedriver_win32.zip"};
	/**
	 * The default classpath, path and url of the default WebDriver settings for Linux.
	 */
	private static final String[] LINUXDEFAULT = {"webdriver.gecko.driver",
			SavePaths.getApplicationDataFolder() + "chromedriver", "/chromedriver_linux64.zip"};
	/**
	 * The default classpath, path and url of the default WebDriver settings for Mac.
	 */
	private static final String[] MACDEFAULT = {"webdriver.chrome.driver",
			SavePaths.getApplicationDataFolder() + "chromedriver", "/chromedriver_mac64.zip"};

	/**
	 * Creates a WebDriver object with the given property key and value.
	 * 
	 * Example:
	 * <pre>
	 * {@code
	 * DriverProvider.createWebdriver("webdriver.chrome.driver", "/path/to/your/chromedriver.exe");
	 * }
	 * </pre>
	 * @param webDriverPool 
	 * @param propertyKey The property key is the classpath for the driver ("webdriver.chrome.driver" by default).
	 * @param driverExecutable The value key is the path to the driver on the local system.
	 */
	static SeleniumDriver createChromeWebdriver(Map<Integer, SeleniumDriver> webDriverPool, int driverNumber,
			String driverExecutable) {
		ChromeDriverService service = new ChromeDriverService.Builder().withSilent(true)
				.usingDriverExecutable(new File(driverExecutable)).build();

		WebDriver driver = null;
		try {
			service.start();
		} catch (Exception e) {
			logger.log(Level.WARNING, "The Chrome service could not be started.", e);
		}
		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		options.addArguments("disable-infobars");
		options.addArguments("--disable-default-apps");
		options.addArguments("--headless");
		options.addArguments("--window-size=1920,1080");
		LoggingPreferences logPrefs = new LoggingPreferences();
	    logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
	    logPrefs.enable(LogType.BROWSER, Level.ALL);
	    logPrefs.enable(LogType.DRIVER, Level.INFO);
	    options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

		HashMap<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", SavePaths.getApplicationDataFolder() + "Downloads");
		Map<String, String> perfLoggingPrefs = new HashMap<String, String>();
	    // Tracing categories, please note NO SPACE NEEDED after the commas
	    perfLoggingPrefs.put("traceCategories", "blink.console,disabled-by-default-devtools.timeline");
	    chromePrefs.put("perfLoggingPrefs", perfLoggingPrefs);
		options.setExperimentalOption("prefs", chromePrefs);
		driver = new EnhancedChromeDriver(service, options);

		if (webDriverPool.containsKey(driverNumber)) {
			webDriverPool.get(driverNumber).setDriver(driver);
			webDriverPool.get(driverNumber).setType(SeleniumType.BROWSER);
		} else {
			webDriverPool.put(driverNumber, new SeleniumDriver(SeleniumType.BROWSER, driver));
		}
		com.simonbaars.seleniumframework.reporting.enums.LogType.initiateDriverLoggers();
		return webDriverPool.get(driverNumber);
	}

	/**
	 * Downloads the Chrome WebDriver file from the given url to the given outputLocation.
	 * @param outputLocation Where the driver will be downloaded to, by default your documents folder.
	 * @param url Where the driver will be downloaded from.
	 */
	private static void downloadDriver(String outputLocation, String url) {
		URL website;
		try {
			File zipFile = new File(outputLocation.replace(".exe", "") + ".zip");
			File outputFile = new File(outputLocation);
			outputFile.getParentFile().mkdirs();
			website = new URL(CHROMEDRIVER_WEBSITE + getLatestVersion() + url);
			logger.log(Level.INFO, "Latest ChromeDriver version is {0}", website.toString());
			try (ReadableByteChannel rbc = Channels.newChannel(website.openStream())) {
				try (FileOutputStream fos = new FileOutputStream(zipFile)) {
					fos.getChannel().transferFrom(rbc, 0, Integer.MAX_VALUE);
					fos.flush();
				}
			}
			unzipFile(zipFile, outputFile);
			if (!Files.deleteIfExists(zipFile.toPath())) {
				logger.log(Level.WARNING, "The downloaded driver at {0} could not be deleted!", outputLocation);
			}
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING,
					"The download urls for the WebDriver seem to have changed... Please ask the developer of the SeleniumFramework to update the links in the code. For now, use the createWebdriver(DriverProvider.WINDOWSDEFAULT) to create a webdriver (use MACDEFAULT or LINUXDEFAULT for Mac or Linux computers).",
					e);
		} catch (IOException e) {
			logger.log(Level.WARNING,
					"The WebDriver could not be downloaded. Probably you have no internet connection?", e);
		}

	}

	private static String getLatestVersion() throws IOException {
		URL oracle = new URL("https://chromedriver.storage.googleapis.com/LATEST_RELEASE");
		URLConnection yc = oracle.openConnection();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()))) {
			return in.readLine();
		}
	}

	/**
	 * The WebDriver comes in a zipfile. The unzipFile method unpacks that zip and places the file inside at the outputFile location.
	 * @param zipFile The zipfile containing the WebDriver.
	 * @param outputFile Where the WebDriver should be placed.
	 */
	private static void unzipFile(File zipFile, File outputFile) {
		byte[] buffer = new byte[1024];
		try {
			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
				ZipEntry ze = zis.getNextEntry();

				while (ze != null) {
					new File(outputFile.getParent()).mkdirs();
					try (FileOutputStream fos = new FileOutputStream(outputFile)) {

						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						fos.flush();
					}
					ze = zis.getNextEntry();
				}
				zis.closeEntry();
			}

		} catch (IOException ex) {
			logger.log(Level.WARNING, "The zip file \"" + zipFile.getAbsolutePath()
					+ "\" could not be extracted. Probably your disk is full?", ex);
		}
	}

	/**
	 * Returns the properties array by an os string.
	 * @param os The os string ("win", "mac" or "nix").
	 * @return Returns the properties array.
	 */
	private static String[] getPropertiesForOs(String os) {
		if (os.indexOf("win") >= 0)
			return WINDOWSDEFAULT;
		if (os.indexOf("mac") >= 0)
			return MACDEFAULT;
		else
			return LINUXDEFAULT;
	}

	/**
	 * Creates a WebDriver by parsing your system properties for os information then downloading the WebDriver file from the internet.
	 * 
	 * Example:
	 * <pre>
	 * {@code
	 * DriverProvider.createWebdriver();
	 * }
	 * </pre>
	 */
	static SeleniumDriver createChromeWebdriver(Map<Integer, SeleniumDriver> webDriverPool, int driverNumber) {
		String[] osProperties = getPropertiesForOs(System.getProperty(OS_NAME).toLowerCase());
		if (!new File(osProperties[1]).exists()) {
			logger.info("No ChromeDriver found... Downloading default ChromeDriver for your system: "
					+ System.getProperty(OS_NAME) + "...");
			downloadDriver(osProperties[1], osProperties[2]);
			if (osProperties == MACDEFAULT) {
				setChromedriverExecutable(MACDEFAULT[1]);
			} else if (osProperties == LINUXDEFAULT) {
				setChromedriverExecutable(LINUXDEFAULT[1]);
			}
		}
		return createChromeWebdriver(webDriverPool, driverNumber, osProperties[1]);
	}

	/**
	 * By default, when downloading the chromedriver on a mac computer it doesn't have the "executable flag" yet. It needs to be set before we can execute it.
	 */
	private static void setChromedriverExecutable(String filePath) {
		String args = "chmod +x " + filePath;
		try {
			Process process = Runtime.getRuntime().exec(args);
			process.waitFor();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not set " + filePath + " executable. Errors expected.", e);
		}
	}

}
