/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver.android;

import static com.simonbaars.seleniumframework.driver.android.enums.AndroidContextType.NATIVE;
import static com.simonbaars.seleniumframework.driver.android.enums.AndroidContextType.WEBVIEW;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.http.HttpClient.Factory;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import com.simonbaars.seleniumframework.core.Wait;
import com.simonbaars.seleniumframework.driver.AndroidDriverProvider;
import com.simonbaars.seleniumframework.driver.DriverProvider;
import com.simonbaars.seleniumframework.driver.android.enums.AndroidContextType;
import com.simonbaars.seleniumframework.driver.android.interfaces.CanQuitPartially;

@SuppressWarnings("rawtypes")
public class EnhancedAndroidDriver extends AndroidDriver implements CanQuitPartially {
	private static final Logger logger = Logger.getGlobal();

	public EnhancedAndroidDriver(AppiumDriverLocalService service, Capabilities desiredCapabilities) {
		super(service, desiredCapabilities);
	}

	public EnhancedAndroidDriver(AppiumDriverLocalService service, Factory httpClientFactory,
			Capabilities desiredCapabilities) {
		super(service, httpClientFactory, desiredCapabilities);
	}

	public EnhancedAndroidDriver(AppiumServiceBuilder builder, Capabilities desiredCapabilities) {
		super(builder, desiredCapabilities);
	}

	public EnhancedAndroidDriver(AppiumServiceBuilder builder, Factory httpClientFactory,
			Capabilities desiredCapabilities) {
		super(builder, httpClientFactory, desiredCapabilities);
	}

	public EnhancedAndroidDriver(Capabilities desiredCapabilities) {
		super(desiredCapabilities);
	}

	public EnhancedAndroidDriver(Factory httpClientFactory, Capabilities desiredCapabilities) {
		super(httpClientFactory, desiredCapabilities);
	}

	public EnhancedAndroidDriver(HttpCommandExecutor executor, Capabilities capabilities) {
		super(executor, capabilities);
	}

	public EnhancedAndroidDriver(URL remoteAddress, Capabilities desiredCapabilities) {
		super(remoteAddress, desiredCapabilities);
	}

	public EnhancedAndroidDriver(URL remoteAddress, Factory httpClientFactory, Capabilities desiredCapabilities) {
		super(remoteAddress, httpClientFactory, desiredCapabilities);
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target) {
		return target.convertFromPngBytes(ADBCommands.takeScreenshotAsByteArray(AndroidDriverProvider.getTargetDevice()));
	}

	@Override
	public void quit() {
		quit(true);
	}

	@Override
	public void quit(boolean b) {
		int driverId = DriverProvider.getDriverId(this);
		if (b)
			super.quit();
		AndroidDriverProvider.stopAndroidDriver(driverId);
	}

	public AndroidContextType getCurrentContextType() {
		return getContext().contains(WEBVIEW.name()) ? WEBVIEW : NATIVE;
	}

	public boolean isNative() {
		return getCurrentContextType() == NATIVE;
	}

	@SuppressWarnings("unchecked")
	public boolean setContextType(AndroidContextType type) {
		Optional<String> findAny = ((AndroidDriver<AndroidElement>) this).getContextHandles().stream()
				.filter(e -> e.contains(type.name())).findAny();
		if (!findAny.isPresent()) {
			logger.log(Level.WARNING, "This context type is not existant: {0}", type.name());
			return false;
		}
		context(findAny.get());
		return true;
	}

	@Override
	public String getTitle() {
		AndroidContextType currentContext = getCurrentContextType();
		if (currentContext == NATIVE)
			return "Native Android App";
		return super.getTitle();
	}

	public Dimension getWebviewScreenDimension() {
		if (!setContextType(WEBVIEW))
			return null;
		return new Dimension(((Long) executeScript("return window.innerWidth || document.body.clientWidth")).intValue(),
				((Long) executeScript("return window.innerHeight || document.body.clientHeight")).intValue());
	}

	public Dimension getNativeScreenDimension() {
		if (!setContextType(NATIVE))
			return null;
		return manage().window().getSize();
	}

	public List<String> getAvailableWindows() {
		String currentWindow = null;
		try {
			currentWindow = getWindowHandle();
		} catch (Exception e) {}
		List<String> windows = new ArrayList<>();
		for (String windowHandle : getWindowHandles()) {
			switchTo().window(windowHandle);
			windows.add(getTitle());
		}
		if(currentWindow!=null) {
			try {
				switchTo().window(currentWindow);
			} catch (Exception e) {}
		}
		return windows;
	}
	
	public String getWindow() {
		return getTitle();
	}

	public boolean switchToWindow(String window) {
		for (String windowHandle : getWindowHandles()) {
			switchTo().window(windowHandle);
			if (getTitle().equals(window))
				return true;
		}
		return false;
	}

	public void waitTillContextIsAvailable(String context, int timeout) {
		new Wait(timeout).until(() -> this.getContextHandles().contains(context));
	}

	public void waitTillWidgetIsAvailable(String widget, int timeout) {
		new Wait(timeout).until(() -> this.getAvailableWindows().contains(widget));
	}
	
	public String swipe(int startx, int starty, int endx, int endy, int duration) {
		return ADBCommands.swipe(startx, starty, endx, endy, duration);
	}
}
