package com.simonbaars.seleniumframework.ui;

import org.apache.commons.text.StringEscapeUtils;

import net.sf.cglib.proxy.Enhancer;
import com.simonbaars.seleniumframework.driver.exception.DriverException;
import com.simonbaars.seleniumframework.reporting.enums.LogType;
import com.simonbaars.seleniumframework.reporting.invocationhandler.JSInvocationHandler;

/**
 * NOTHING IN THIS CLASS MAY RETURN NULL!!
 * @author simon
 *
 */
public class JavaScriptBridge {
	
	private static JavaScriptBridge instance = null;
	
	public static JavaScriptBridge getInstance() {
		if(instance == null) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(JavaScriptBridge.class);
			enhancer.setCallback(new JSInvocationHandler());
			instance = (JavaScriptBridge) enhancer.create();
		}
		return instance;
	}
	
	public String addAction(String testcaseId, String actionName) {
		Object executeScript = Main.engine.executeScript("addAction(\""+StringEscapeUtils.escapeEcmaScript(testcaseId)+"\", \""+StringEscapeUtils.escapeEcmaScript(actionName)+"\");");
		if(executeScript instanceof Boolean)
			throw new DriverException("Could not create action! "+testcaseId+", "+actionName);
		return (String) executeScript;
	}

	public static void showLogType(LogType logType, String escape) {
		if(escape.isEmpty())
			escape = "No logs of type "+logType.getName()+" have been recorded!";
		Main.engine.executeScript("document.getElementById('"+logType.getPrefix()+"').innerHTML = \""+escape+"\";");
	}
}
