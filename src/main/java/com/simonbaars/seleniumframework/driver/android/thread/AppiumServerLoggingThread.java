/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver.android.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.simonbaars.seleniumframework.driver.exception.AppiumServerException;
import com.simonbaars.seleniumframework.driver.exception.DriverException;

public class AppiumServerLoggingThread extends Thread {
	private static final Logger logger = Logger.getLogger(AppiumServerLoggingThread.class.getName());
	private Process appiumServer;
	private BufferedReader appiumServerStreamReader;
	private List<String> appiumServerLog;
	private boolean initialized = false;
	private static final int DEFAULT_PORT = 4723;
	private static final int RETRIES = 100;
	private int port;
	private boolean loggerAttached = true;

	public AppiumServerLoggingThread() {
		initPort(DEFAULT_PORT);
		appiumServerLog = new ArrayList<>();
		start();
	}

	private void initPort(int port) {
		logger.log(Level.INFO, "Loading Appium Server at port {0}", port);
		this.port = port;
		ProcessBuilder pbADB = new ProcessBuilder("appium", "-p" + port, "--session-override");
		try {
			appiumServer = pbADB.start();
			appiumServerStreamReader = new BufferedReader(new InputStreamReader(appiumServer.getInputStream()));
		} catch (IOException e) {
			throw new DriverException("Process could not start... Are you sure appium is added to your PATH? ", e);
		}
	}

	@Override
	public void run() {
		while (DEFAULT_PORT + RETRIES > port) {
			while (appiumServer.isAlive()) {
				fetchDataFromStreamReader();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			fetchDataFromStreamReader();
			try {
				appiumServerStreamReader.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Exception while stopping the Appium Server log.", e);
			}
			if (!initialized) {
				appiumServer.destroy();
				initPort(port + 1);
				if (DEFAULT_PORT + RETRIES < port)
					throw new DriverException(
							"Appium Server died unexpectedly. Check the Appium Server Log for more information."
									+ Arrays.toString(appiumServerLog.toArray()));
			} else
				break;
		}
	}

	private void fetchDataFromStreamReader() {
		String line;
		try {
			if (appiumServerStreamReader.ready() && (line = appiumServerStreamReader.readLine()) != null) {
				logger.log(Level.FINE, "Appium Server: {0}", line);
				if(loggerAttached) appiumServerLog.add(line);
				if (!initialized && !line.isEmpty() && line.contains("Appium REST http interface listener started")) {
					initialized = true;
				}
			}
		} catch (IOException e1) {
			logger.log(Level.WARNING, "Unexpected IOException while running the appium server.", e1);
		}
	}

	public void stopRunning() {
		logger.log(Level.INFO, "Stop server with initialisation status: {0}", initialized);
		appiumServer.destroy();
	}

	public boolean isInitialized() {
		if (!isAlive())
			throw new AppiumServerException(appiumServerLog);
		return initialized;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<String> getLog() {
		return appiumServerLog;
	}

	public void detachLogger() {
		this.loggerAttached = false;
	}
}
