package com.simonbaars.seleniumframework.ui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.sun.javafx.webkit.WebConsoleListener;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import com.simonbaars.seleniumframework.core.SeleniumFramework;
import com.simonbaars.seleniumframework.core.common.ResourceCommons;
import com.simonbaars.seleniumframework.core.common.SavePaths;
import com.simonbaars.seleniumframework.core.common.TestingCommons;
import com.simonbaars.seleniumframework.driver.exception.DriverException;
import com.simonbaars.seleniumframework.reporting.enums.LogLevel;
import com.simonbaars.seleniumframework.ui.thread.TestRunnerThread;

public class Main extends Application {
	private Scene scene;
	Browser browser = new Browser();
	public static WebEngine engine;

	@Override public void start(Stage stage) {
		// create the scene
		scene = new Scene(browser,1280,720, Color.web("#666970"));
		engine = browser.webEngine;
		stage.setScene(scene);
		stage.setTitle("Automated Regressiontests");
		stage.setOnCloseRequest((event) -> {
		      if(TestRunnerThread.thisInstance!=null) {
		    	  TestRunnerThread.thisInstance.interrupt();
		    	  TestingCommons.sleep(100);
		    	  SeleniumFramework.onExecutionFinish();
		      }
		});
		stage.show();
	}

	public static void main(String[] args){
		SavePaths.setAlternativeDataFolder(".regression");
		ResourceCommons.extractResources("regression"+File.separator);
		SeleniumFramework.keepDriverAlive();
		launch(args);
	}
}
class Browser extends Region {

	final WebView browser = new WebView();
	final WebEngine webEngine = browser.getEngine();
	final JavaBridge javaBridge = new JavaBridge(webEngine);

	public Browser() {
		//apply the styles
		getStyleClass().add("browser");
		// load the web page
		try {
			webEngine.setUserStyleSheetLocation(ResourceCommons.getResource("app/css/styles.css").toURI().toURL().toString());
			webEngine.loadContent(TestingCommons.getResourceAsString("/app/index.html"));
			browser.setContextMenuEnabled(false);
		} catch (IOException e) {
			throw new DriverException(e);
		}
		
		//add the web view to the scene
		getChildren().add(browser);
		webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.SUCCEEDED) {
				setupLogger(javaBridge);

				File[] files = ResourceCommons.getResource("").listFiles((dir, name) -> {
					return name.toLowerCase().endsWith(".xml");
				});
				javaBridge.listToSelect(Arrays.stream(files).map(File::getName).collect(Collectors.toList()), "scenarios");
				webEngine.executeScript("$(\"#scenarios_select\").change(reloadTree);");

				javaBridge.parseXMLNodes(files[0].getName());
				javaBridge.listToSelect(LogLevel.names(), "loglevel");
				
				Platform.runLater(() -> ((JSObject)webEngine.executeScript("window")).setMember("javaApplication", javaBridge));
				WebConsoleListener.setDefaultListener(new WebConsoleListener(){
				    @Override
				    public void messageAdded(WebView webView, String message, int lineNumber, String sourceId) {
				        System.out.println("Console: [" + sourceId + ":" + lineNumber + "] " + message);
				    }
				});
			}
		});
	}

	private void setupLogger(final JavaBridge javaBridge) {
		((JSObject)webEngine.executeScript("window")).setMember("javaApp", javaBridge);
	}

	@Override protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
	}

	@Override protected double computePrefWidth(double height) {
		return 750;
	}

	@Override protected double computePrefHeight(double width) {
		return 500;
	}
}
