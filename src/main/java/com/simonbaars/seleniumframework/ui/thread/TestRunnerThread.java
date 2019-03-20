package com.simonbaars.seleniumframework.ui.thread;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.simonbaars.seleniumframework.core.SeleniumFramework;
import com.simonbaars.seleniumframework.core.common.CSVUtils;
import com.simonbaars.seleniumframework.core.common.ResourceCommons;
import com.simonbaars.seleniumframework.reporting.Logger;
import com.simonbaars.seleniumframework.reporting.SeleniumTestcase;
import com.simonbaars.seleniumframework.reporting.enums.LogLevel;
import com.simonbaars.seleniumframework.reporting.enums.LogType;
import com.simonbaars.seleniumframework.reporting.types.Testcase;
import com.simonbaars.seleniumframework.reporting.types.Testgroup;

public class TestRunnerThread extends Thread {

	private Testcase[] includedTests;
	private String scenario;
	private static final Map<String, List<String[]>> datasets = new HashMap<>();
	public static TestRunnerThread thisInstance;
	public static Testcase currentlyExecutingTestcase;
	public static boolean stopped = false;
	

	public TestRunnerThread(JSONArray jsonArray, String testScenario) {
		thisInstance = this;
		setIncludedTests(jsonArray);
		this.scenario = testScenario;
		start();
	}

	@Override
	public void run() {
		try {
			Testcase prevTest = null;
			for(Testcase test : includedTests) {
				currentlyExecutingTestcase = test;
				registerLogger(prevTest, test);
				addAllTestdata(test);
				SeleniumFramework.beforeTest(test);
				test.getTest().run();
				SeleniumFramework.afterTest(test);
				SeleniumTestcase.getCurrentTestdata().clear();
				LogType.removeOutputs(test.getJstreeID());
				prevTest = test;
			}
			Logger.log("Finished execution of test scenario: "+scenario);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.log("An unexpected error occurred while executing the tests...", e);
			LogType.takeScreenshot(LogLevel.SCREENSHOTSONLYAFTERERROR);
		} finally {
			SeleniumFramework.onExecutionFinish();
		}
	}

	private void addAllTestdata(Testcase test) {
		for(String dataset : test.getUsedDatasets()) {
			List<String[]> list = datasets.get(dataset);
			if (!list.isEmpty()) {
				for (int i = 0; i < list.get(0).length; i++) {
					SeleniumTestcase.getCurrentTestdata().put(list.get(0)[i], list.get(1)[i]);
				}
			}
		}
	}

	private void registerLogger(Testcase prevTest, Testcase test) {
		if(prevTest!= null)
			prevTest.getGroups().stream().filter(e -> !test.getGroups().contains(e)).forEach(e -> LogType.removeOutputs(e.getId()));
		test.getGroups().stream().forEach(e -> LogType.registerOutputs(e.getId()));
		LogType.registerOutputs(test.getJstreeID());
	}

	private int indexOf(Testcase test, Testcase[] includedTests2) {
		int howMany = 0;
		for(int i = 0; i<includedTests2.length && !includedTests2[i].equals(test); i++)
			if(includedTests2[i].getTestName().equals(test.getTestName()))
				howMany ++;
		return howMany;
	}

	public Testcase[] getIncludedTests() {
		return includedTests;
	}

	@SuppressWarnings("unchecked")
	public void setIncludedTests(JSONArray collect) {
		Testcase[] tests = new Testcase[collect.size()];
		for(int i = 0; i<collect.size(); i++) {
			JSONObject obj = (JSONObject)collect.get(i);
			try {
				String[] testdata = (String[])((JSONArray)obj.get("dataset")).toArray(new String[0]);
				Arrays.stream(testdata).filter(dataset -> !datasets.containsKey(dataset)).forEach(dataset -> datasets.put(dataset, CSVUtils.parseCSV(ResourceCommons.getResource("testdata"+File.separator+dataset+".csv"))));
				tests[i] = new Testcase((SeleniumTestcase) Class.forName((String)obj.get("class")).newInstance(), (String)obj.get("name"), testdata, (List<Testgroup>)((JSONArray)obj.get("groups")).stream().map(e -> new Testgroup((String)((JSONObject)e).get("id"), (String)((JSONObject)e).get("name"))).collect(Collectors.toList()), (String)obj.get("id"));
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				Logger.log("Could not load the testcase with class: "+obj.get("class"), e);
			}
		}
		includedTests = tests;
	}
}
