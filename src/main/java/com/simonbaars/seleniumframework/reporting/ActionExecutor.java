/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.reporting;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.simonbaars.seleniumframework.core.common.TestingCommons;
import com.simonbaars.seleniumframework.reporting.annotations.TestAction;
import com.simonbaars.seleniumframework.reporting.types.Testcase;
import com.simonbaars.seleniumframework.ui.thread.TestRunnerThread;

public class ActionExecutor {
	
	private static String currentId;
	
	private ActionExecutor() {
	}

	public static void executeAction(TestAction testAction, Object proxy, Method method, Object[] args) {
		while(TestRunnerThread.stopped) TestingCommons.sleep(100);
		Testcase thisTestcase = TestRunnerThread.currentlyExecutingTestcase;
		//JavaScriptBridge.getInstance().addAction(TestRunnerThread.currentlyExecutingTestcase.getJstreeID(), testAction.name());
		//String thisId = (String) JSInvocationHandler.awaitResult();
		//LogType.registerOutputs(thisId);
		//LogType.takeScreenshot(LogLevel.ALWAYS, LogLevel.BEFORE);
		Logger.log("Executing action \""+testAction.name()+"\" in method "+method.getName()+" invoked with args "+Arrays.toString(args));
		//currentId = thisId;
	}
	
	public static void afterExecution(TestAction testAction, Object proxy, Method method, Object[] args, Object retObj) {
		Logger.log("Execution succeeded!");
		//LogType.takeScreenshot(LogLevel.ALWAYS, LogLevel.AFTER);
		//LogType.removeOutputs(currentId);
	}
}
