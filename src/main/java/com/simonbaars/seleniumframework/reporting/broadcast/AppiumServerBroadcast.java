package com.simonbaars.seleniumframework.reporting.broadcast;

import java.io.InputStream;
import java.io.PipedInputStream;

import io.appium.java_client.service.local.AppiumDriverLocalService;

public class AppiumServerBroadcast extends RunningTaskBroadcast {
	private final InputStream appiumServerByteStream = new PipedInputStream();
	private AppiumDriverLocalService appiumServer;
	
	@Override
	public InputStream getInputStream() {
		return appiumServerByteStream;
	}

	public void start(AppiumDriverLocalService appiumServer) {
		super.start();
		this.appiumServer = appiumServer;
		appiumServer.start();
	}
	
	@Override
	public void stop() {
		appiumServer.stop();
		super.stop();
	}
}
