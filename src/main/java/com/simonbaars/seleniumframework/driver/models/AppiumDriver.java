/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver.models;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import com.simonbaars.seleniumframework.core.common.TestingCommons;
import com.simonbaars.seleniumframework.reporting.Logger;
import com.simonbaars.seleniumframework.reporting.broadcast.AppiumServerBroadcast;

public class AppiumDriver {
	String uuid;
	AppiumDriverLocalService appiumServer;

	public void startLogging() {
		AppiumServiceBuilder b = new AppiumServiceBuilder().usingAnyFreePort().withIPAddress("127.0.0.1").withArgument(CustomServerFlag.RELAXED_SECURITY);
		File appiumLocation = TestingCommons.getResourceFile("appium.location");
		if(appiumLocation.exists()) {
			try {
				String fileAsString = TestingCommons.getFileAsString(appiumLocation);
				if(!fileAsString.isEmpty())
					b.withAppiumJS(new File(fileAsString));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Logger.log("You did not specify a path at which your appium server instance is located. Appium will now automatically try to find this, but this is very likely to fail.");
		}
		appiumServer = AppiumDriverLocalService.buildService(b);
		
		try {
			AppiumServerBroadcast appiumServerBroadcast = null;//(AppiumServerBroadcast)LogType.APPIUM_SERVER.getBroadcast(); TODO FIX UP ANDROID
			//appiumServer.clearOutPutStreams();
			appiumServer.addOutPutStream(new PipedOutputStream((PipedInputStream) appiumServerBroadcast.getInputStream()));
			appiumServerBroadcast.start(appiumServer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.log("Appium started at "+appiumServer.getUrl().toString());
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public AppiumDriverLocalService getAppiumServer() {
		return appiumServer;
	}

	public void setAppiumServer(AppiumDriverLocalService appiumServer) {
		this.appiumServer = appiumServer;
	}
}
