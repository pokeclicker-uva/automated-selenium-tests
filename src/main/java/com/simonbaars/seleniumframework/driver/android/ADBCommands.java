/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.driver.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import com.simonbaars.seleniumframework.core.common.TestingCommons;
import com.simonbaars.seleniumframework.driver.exception.DriverException;

public class ADBCommands {
	private static final String COMMAND_EXECUTION_ERROR = "Error while executing command: ";
	private static final String MATCH_STRING_START = "export ";
	private static final Logger logger = Logger.getGlobal();
	private static String adbPath = null;
	
	private ADBCommands() {}
	
	public static byte[] takeScreenshotAsByteArray() {
		logger.log(Level.FINE, "Taking screenshot by exec out adb command");
		return executeAsByteArray("exec-out screencap -p");
	}

	public static byte[] takeScreenshotAsByteArray(String uuid) {
		logger.log(Level.FINE, "Taking screenshot by exec out adb command");
		return executeAsByteArray("-s "+uuid+" exec-out screencap -p");
	}
	
	public static String getADBDevices() {
		logger.log(Level.FINE, "Getting adb devices");
		return executeAsString("devices");
	}

	public static String clearLogcat() {
		logger.log(Level.FINE, "Clearing all logcat logs");
		return executeAsString("logcat -c");
	}
	
	public static String clearLogcat(String uuid) {
		logger.log(Level.FINE, "Clearing all logcat logs of device "+uuid);
		return executeAsString("-s "+uuid+" logcat -c");
	}
	
	public static String swipe(int startx, int starty, int endx, int endy, int duration) {
		return executeAsString("shell input touchscreen swipe "+startx+" "+starty+" "+endx+" "+endy+" "+duration);
	}

	public static void dumpLogcat(File file) {
		logger.log(Level.FINE, "Requesting the logcat logs");
		executeAsFile("logcat -d", file);
	}
	
	public static void captureLogcat(String uuid) {
		logger.log(Level.FINE, "Requesting the logcat logs for device "+uuid);
		try {
			String[] com;
			String command = getADBPath()+" -s "+uuid+" logcat";
			if(TestingCommons.getOS().isUnix())
				com = new String[]{"bash", "-c", command};
			else 
				com = command.split(" ");
			ProcessBuilder prb = new ProcessBuilder(com);
			Process pr = prb.start();
			//((LogEntryBroadcast)LogType.ADB_LOGCAT.getBroadcast()).start(pr); TODO Fix up Android
		} catch (IOException e) {
			throw new DriverException(COMMAND_EXECUTION_ERROR + "adb -s"+uuid+" logcat", e);
		}
	}
	
	/*
	this method returns list of uid of all connected devices ex. "67e9648d"
	this method will be called before starting appium driver and its results is used in Capabilities
	 */
	public static List<String> getDeviceProperties() throws IOException {
		String path = getADBPath();
		ProcessBuilder pbADB = new ProcessBuilder(path, "devices");
		Process pcADB = pbADB.start();

		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(pcADB.getInputStream()));
		List<String> deviceList = new ArrayList<>();
		while ((line = input.readLine()) != null) {
			if (!line.isEmpty() && line.contains("device") && !line.contains("devices")) {
				logger.log(Level.FINE, "result of adb devices is {0}", line);
				deviceList.add(line.split("\\s+")[0]);
			}
		}
		input.close();
		pcADB.destroy();
		return deviceList;
	}

	public static String encoder(File file) {
		String base64Image = "";
		try (FileInputStream imageInFile = new FileInputStream(file)) {
			// Reading a Image file from file system
			byte[] imageData = new byte[(int) file.length()];
			if(imageInFile.read(imageData) > 0)
				base64Image = Base64.getEncoder().encodeToString(imageData);
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Image not found", e);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Exception while reading the Image ", ioe);
		}
		return base64Image;
	}

	private static String executeAsString(String command) {
		try {
			command = getADBPath()+" "+command;
			Process pr = execute(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = input.readLine()) != null) {
				if (!line.isEmpty()) {
					sb.append(line);
				}
				sb.append("\n");
			}
			input.close();
			pr.destroy();
			logger.log(Level.FINE, "result of {0} is {1}", new Object[] {command, sb});
			return sb.toString();
		} catch (Exception e) {
			throw new DriverException(COMMAND_EXECUTION_ERROR + command, e);
		}
	}

	private static byte[] executeAsByteArray(String command) {
		try {
			command = getADBPath()+" "+command;
			Process pr = execute(command);
			byte[] byteArray = ByteStreams.toByteArray(pr.getInputStream());
			pr.destroy();
			return byteArray;
		} catch (Exception e) {
			throw new DriverException(COMMAND_EXECUTION_ERROR + command, e);
		}
	}

	private static Process execute(String command) throws IOException, InterruptedException {
		Process pr = getRunningProcess(command);
		try {
			pr.waitFor(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return pr;
	}

	private static Process getRunningProcess(String command) throws IOException {
		String[] com;
		if(TestingCommons.getOS().isUnix())
			com = new String[]{"bash", "-c", command};
		else 
			com = command.split(" ");
		ProcessBuilder prb = new ProcessBuilder(com);
		Process pr = prb.start();
		return pr;
	}

	private static void executeAsFile(String command, File file) {
		try {
			byte[] byteArray = executeAsByteArray(command);
			Files.write(byteArray, file);
		} catch (Exception e) {
			throw new DriverException(COMMAND_EXECUTION_ERROR + command, e);
		}
	}
	
	private static String getADBPath() {
		if(adbPath == null) {
			File appiumLocation = TestingCommons.getResourceFile("adb.location");
			if(appiumLocation.exists()) {
				try {
					adbPath = TestingCommons.getFileAsString(appiumLocation);
					return adbPath;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
			adbPath = "adb";
		}
		return adbPath;
	}
}
