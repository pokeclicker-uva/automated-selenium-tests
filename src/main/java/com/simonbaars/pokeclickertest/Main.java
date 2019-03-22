package com.simonbaars.pokeclickertest;

import org.json.simple.JSONArray;

import com.simonbaars.seleniumframework.core.common.ResourceCommons;
import com.simonbaars.seleniumframework.reporting.enums.LogLevel;
import com.simonbaars.seleniumframework.ui.thread.TestRunnerThread;

public class Main {

	public static void main(String[] args) {
		ResourceCommons.extractResources();
		LogLevel.setCurrentLogLevel(LogLevel.NOSCREENSHOTS);
		new TestRunnerThread(null, "HappyFlow");
	}

}
