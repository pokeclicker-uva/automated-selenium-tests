package com.simonbaars.seleniumframework.ui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.text.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.simonbaars.seleniumframework.core.common.ResourceCommons;
import com.simonbaars.seleniumframework.reporting.enums.LogLevel;
import com.simonbaars.seleniumframework.reporting.enums.LogType;
import com.simonbaars.seleniumframework.ui.thread.TestRunnerThread;

import javafx.scene.web.WebEngine;

public class JavaBridge {
	private final WebEngine webEngine;

	public JavaBridge(WebEngine engine) {
		this.webEngine = engine;
	}
	
	public void log(String text) {
        System.out.println(text);
    }
	
	void listToSelect(List<String> devices, String id) {
		StringBuilder selectStatement = new StringBuilder("<div class=\"form-group\"><select class=\"form-control\" id=\""+id+"_select\">");
		for(String device : devices)
			selectStatement.append("<option>"+device+"</option>");
		selectStatement.append("</select></div>");
		webEngine.executeScript("document.getElementById(\""+id+"\").innerHTML = \""+StringEscapeUtils.escapeEcmaScript(selectStatement.toString())+"\";");
	}
	
	@SuppressWarnings("unchecked")
	public void parseXMLNodes(String fileName) {
		try {
			File file = ResourceCommons.getResource(fileName);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			dbf.setValidating(false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			DocumentBuilder dBuilder = dbf.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			JSONObject rootObj = new JSONObject();
			Node scenario = doc.getElementsByTagName("scenario").item(0);
			rootObj.put("text", scenario.getAttributes().getNamedItem("name").getNodeValue());
			JSONObject state = new JSONObject();
			state.put("opened", true);
			state.put("selected", true);
			rootObj.put("state", state);
			rootObj.put("opened", true);
			JSONArray jsonNodes = new JSONArray();
			rootObj.put("children", jsonNodes);
			parseTestGroup(rootObj, scenario, parsePackageOrClass(scenario, false), new JSONArray());
			webEngine.executeScript("setTree("+rootObj.toJSONString()+");");
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private String parsePackageOrClass(Node scenario, boolean isClass) {
		Node namedItem = isClass ? scenario.getAttributes().getNamedItem("class") : scenario.getAttributes().getNamedItem("package");
		if(isClass)
			return namedItem.getNodeValue();
		else if(namedItem!=null)
			return namedItem.getNodeValue() + ".";
		return "";
	}

	@SuppressWarnings("unchecked")
	private void parseTestGroup(JSONObject parentObj, Node scenario, String currentPackage, JSONArray datasets) {
		JSONArray parentNodes = new JSONArray();
		parentObj.put("children", parentNodes);
		if(scenario.getAttributes().getNamedItem("dataset")!=null) 
			datasets.add(scenario.getAttributes().getNamedItem("dataset").getNodeValue());
			
		NodeList childNodes = scenario.getChildNodes();
		for(int i = 0; i<childNodes.getLength(); i++) {
			JSONObject nodeObj = new JSONObject();
			Node childNode = childNodes.item(i);
			switch(childNode.getNodeName()) {
				case "test": 
					JSONObject data = new JSONObject();
					data.put("class", currentPackage + parsePackageOrClass(childNode, true));
					data.put("dataset", datasets);
					nodeObj.put("data", data);
					nodeObj.put("icon", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXBIWXMAAAsRAAALEQF/ZF+RAAAC7UlEQVQ4T21TS0hVQRj+Zs7L6xWf+cgeUhSGkoXZqohoUZQFgdGuRUQZbVoVES1cBG0KahOUQrsWLhJUJKIHWJY9LAzJR4QaKt702vV2r/eex8z0z7EkoeHMmXPm/N/3f98//2H4ZzR3t2z3VHAYjNUrpiohVRSMAhhLM8VmoNRHm5s99xpbhv7C9GfaVzjdee2SNNQVBRSDblLKFeqQg3O6hdcCD9iNB43XbzKLwdBRE3XuUcFkWyBkVPgBpBCaYZnkz+p6WXieT2JYBEwd6Bx+/mmw/dUYO3/nKkuuE49g4njghgH/utLyECiBfRsbkGPZeDH+Dsw2wAN0lMeLmszZpVhFjizcoXyKlaSdlK4MevWEh63FVWjefRJ+EKB/chC/vAzl4zsnEl8ruBG1akhykRTasyISuTKFFMTHcaL2EBzTxtMvfYinEmDaGWFYxKoxiWotJTYJpdXCFySFhmVYcAMXe6vq0bC+Fj8S8+gYegJmsOUEYCYzeQUXUjoaLPWkjHXl1agt2wLXd5Fr5qCp9mCoon2gB3NeAgbjYazGkELHFL5Mc6mUlpvv5OHinlOI2rl4PPoSLvnfXLIBn78P49m3N7BzdC6SSXXWFMITSzwzn5wmtK+rn0wl0f66Cz/TCRyp2Y/GbfuRzKTw8H0nspxOSFdJyyevhPEy8dSUkV+Sny2srjhB2kpJCAanRtD/ZUBXGZXFZegdeYuesV7YDmXXQF0og1Mh1cRM39it8NCP3T3XZhTYZ0R2uQ9cz4NIeaiMrEFCpiAinDDL56stGBEbYjHb1nWh9Wy4m5yYuy8yQYxTg2gVlmXBKcxFjC8icLRlRgVW4TfuGBAZL7Y4PteqsWErT/aNTK+pKht18iKbwFkp53RApgHTJiO0Mk5CQ9/KlWn/Q3xo6nLf7e4XGruqb/OKCiqrj++qj5YXVBNFGTd4bihbyCUZiFh6Njk62jnwKbWwOLPqb1zd/CtvJj39bWzdpsH/4n4DlbuAtmBzlBMAAAAASUVORK5CYII=");
					break;
				case "group":
					JSONArray datasets2 = new JSONArray();
					datasets2.addAll(datasets);
					parseTestGroup(nodeObj, childNode, currentPackage + parsePackageOrClass(childNode, false), datasets2);
					break;
				default:
					continue;
			}
			parentNodes.add(nodeObj);
			if(childNode.getAttributes().getNamedItem("name")!=null) 
				nodeObj.put("text", childNode.getAttributes().getNamedItem("name").getNodeValue());
			else nodeObj.put("text", classNameToName(childNode.getAttributes().getNamedItem("class").getNodeValue()));
		}
	}

	private String classNameToName(String nodeValue) {
		if(nodeValue.indexOf('.')!=-1) nodeValue = nodeValue.substring(nodeValue.lastIndexOf('.')+1);
		StringBuilder s = new StringBuilder(nodeValue);
		for(int i = 1; i<s.length(); i++) {
			if(Character.isUpperCase(s.charAt(i))) {
				s.insert(i, ' ');
				i++;
			}
		}
		return s.toString();
	}

	public void startTestExecution(String testScenario, String runTests, String logLevel) {
		try {
			LogLevel.setCurrentLogLevel(logLevel);
			new TestRunnerThread(((JSONArray)new JSONParser().parse(runTests)), testScenario);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void pauseTest() {
		TestRunnerThread.stopped = true;
	}
	
	public void continueTest() {
		TestRunnerThread.stopped = false;
	}
	
	public void requestLogs(String id) {
		for(LogType logType : LogType.values()) {
			byte[] fileContents = logType.getFileContents(id);
			String c;
			if(logType != LogType.SCREENSHOT)
				c = escape(new String(fileContents, StandardCharsets.UTF_8));
			else if(fileContents.length > 100)
				c = "<img src=\\\"data:image/png;base64,"+new String(Base64.getEncoder().encode(fileContents))+"\\\"/>";
			else c = new String(fileContents, StandardCharsets.UTF_8);
			JavaScriptBridge.showLogType(logType, c);
		}
	}
	
	public static String escape(String s) {
	    return StringEscapeUtils.escapeEcmaScript(s);
	}

}
