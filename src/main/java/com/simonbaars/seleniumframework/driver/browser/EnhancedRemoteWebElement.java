package com.simonbaars.seleniumframework.driver.browser;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.w3c.dom.Node;

public class EnhancedRemoteWebElement extends RemoteWebElement {
	public Node myNode;
	public List<WebElement> frames;
	public WebElement webElement;
	
	public EnhancedRemoteWebElement(RemoteWebDriver driver, List<WebElement> frames, WebElement element, String xpath) {
		setFoundBy(driver, "xpath", xpath);
		setParent(driver);
		this.frames = frames;
		this.webElement = element;
		setId(((RemoteWebElement)webElement).getId());
	}
	
	@Override
	public void click() {
		openMyFrames();
		super.click();
		parent.switchTo().defaultContent();
	}

	private void openMyFrames() {
		for(WebElement frame : frames) {
			parent.switchTo().frame(frame);
		}
	}
}