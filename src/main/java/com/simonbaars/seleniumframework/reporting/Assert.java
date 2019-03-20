package com.simonbaars.seleniumframework.reporting;

import com.simonbaars.seleniumframework.reporting.enums.LogLevel;
import com.simonbaars.seleniumframework.reporting.enums.LogType;
import com.simonbaars.seleniumframework.reporting.interfaces.Broadcast;
import com.simonbaars.seleniumframework.reporting.interfaces.Testdata;

public class Assert {
	
	public static final Broadcast assertBroadcast = LogType.ASSERT_LOG.getBroadcast();
	
	private Assert() {}
	
	public static void assertEquals(String...s) {
		for(int i = 1; i<s.length; i++) {
			assertBroadcast.broadcast("Checking if \""+s[0]+"\" is equal to \""+s[i]+"\"");
			broadcastResult(s[i].equals(s[0]));
		}
	}

	public static void assertTrue(boolean contains, String readableAssertion) {
		assertBroadcast.broadcast("Executing validation: "+ readableAssertion);
		broadcastResult(contains);
	}

	private static void broadcastResult(boolean contains) {
		assertBroadcast.broadcast("Result: " + (contains ? "SUCCESS" : "FAILURE"));
		if(!contains) LogType.takeScreenshot(LogLevel.ALWAYS, LogLevel.BEFORE, LogLevel.AFTER, LogLevel.SCREENSHOTSONLYAFTERERROR);
	}

	public static void assertEquals(Testdata testdata, String text) {
		String testdata2 = testdata.getValue();
		assertBroadcast.broadcast("Executing validation: Checking if the testdata \""+testdata.getColumnName()+ "\" with value \""+testdata2+ "\" is equal to \""+text+"\".");
		broadcastResult(testdata2.equals(text));
	}
}
