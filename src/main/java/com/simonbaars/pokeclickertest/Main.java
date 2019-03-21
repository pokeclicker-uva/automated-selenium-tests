package com.simonbaars.pokeclickertest;

import org.json.simple.JSONArray;

import com.simonbaars.seleniumframework.reporting.enums.LogLevel;
import com.simonbaars.seleniumframework.ui.thread.TestRunnerThread;

public class Main {

	public static void main(String[] args) {
		LogLevel.setCurrentLogLevel(LogLevel.NOSCREENSHOTS);
		new TestRunnerThread(null, "HappyFlow");
	}

}
