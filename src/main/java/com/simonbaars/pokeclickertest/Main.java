package com.simonbaars.pokeclickertest;

import org.json.simple.JSONArray;

import com.simonbaars.seleniumframework.reporting.enums.LogLevel;
import com.simonbaars.seleniumframework.ui.thread.TestRunnerThread;

public class Main {

	public static void main(String[] args) {
		LogLevel.setCurrentLogLevel(LogLevel.NOSCREENSHOTS);
		JSONArray arr = new JSONArray();
		arr.add("Login");
		arr.add("Register");
		new TestRunnerThread(arr, "HappyFlow");
	}

}
