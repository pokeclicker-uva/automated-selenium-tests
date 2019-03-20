package com.simonbaars.seleniumframework.reporting.types;

import java.util.Arrays;
import java.util.List;

import com.simonbaars.seleniumframework.reporting.SeleniumTestcase;

public class Testcase {
	private SeleniumTestcase test;
	private String testName;
	private String[] usedDatasets;
	private List<Testgroup> groups;
	private String jstreeID;
	
	public Testcase(SeleniumTestcase test, String testName, String[] usedDatasets, List<Testgroup> list, String jstreeID) {
		super();
		this.test = test;
		this.testName = testName;
		this.usedDatasets = usedDatasets;
		this.groups = list;
		this.jstreeID = jstreeID;
	}

	public Testcase(SeleniumTestcase test, String testName, String[] usedDatasets, List<Testgroup> groups) {
		super();
		this.test = test;
		this.testName = testName;
		this.usedDatasets = usedDatasets;
		this.groups = groups;
	}

	public Testcase(SeleniumTestcase test, String testName, String[] usedDatasets) {
		super();
		this.test = test;
		this.testName = testName;
		this.usedDatasets = usedDatasets;
	}

	public Testcase(SeleniumTestcase test, String testName) {
		super();
		this.test = test;
		this.testName = testName;
	}

	public SeleniumTestcase getTest() {
		return test;
	}

	public void setTest(SeleniumTestcase test) {
		this.test = test;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String[] getUsedDatasets() {
		return usedDatasets;
	}

	public void setUsedDatasets(String[] usedDatasets) {
		this.usedDatasets = usedDatasets;
	}

	public List<Testgroup> getGroups() {
		return groups;
	}

	public void setGroups(List<Testgroup> groups) {
		this.groups = groups;
	}

	public String getJstreeID() {
		return jstreeID;
	}

	public void setJstreeID(String jstreeID) {
		this.jstreeID = jstreeID;
	}

	@Override
	public String toString() {
		return "Testcase [test=" + test + ", testName=" + testName + ", usedDatasets=" + Arrays.toString(usedDatasets)
				+ ", groups=" + Arrays.toString(groups.toArray()) + "]";
	}
}
