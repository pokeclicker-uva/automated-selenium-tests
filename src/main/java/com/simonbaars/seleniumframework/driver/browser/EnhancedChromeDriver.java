package com.simonbaars.seleniumframework.driver.browser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.simonbaars.seleniumframework.driver.exception.DriverException;

public class EnhancedChromeDriver extends ChromeDriver {
	public static final String FRAME_XPATH = "//*[self::iframe or self::frame]";
	List<Document> currentDOM;
	DocumentBuilder builder;
	XPathFactory xPathfactory;
	
	public EnhancedChromeDriver(ChromeDriverService service, ChromeOptions options) {
		super(service, options);
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xPathfactory = XPathFactory.newInstance();
		} catch (ParserConfigurationException e) {
			throw new DriverException("Could not load DOM builder.", e);
		}
	}
	
	public void parseSource() {
		currentDOM.clear();
		scanFrames(e -> {
			try {
				currentDOM.add(builder.parse(e.getPageSource()));
			} catch (SAXException | IOException e1) {
				throw new DriverException(e1);
			}
		});
	}

	/*public List<WebElement> findElementsInDOM(String xpath) {
		XPath xpathEx = xPathfactory.newXPath();
		try {
			XPathExpression expr = xpathEx.compile(xpath);
			NodeList nl = (NodeList) expr.evaluate(currentDOM, XPathConstants.NODESET);
			List<WebElement> nodes = new ArrayList<>();
			for(int i = 0; i<nl.getLength(); i++) {
				nodes.add(new EnhancedRemoteWebElement(this, nl.item(i), xpath));
			}
			return nodes;
		} catch (XPathExpressionException e) {
			throw new DriverException(e);
		}
	}*/
	
	@Override
	public List<WebElement> findElementsByXPath(String xpath){
		List<WebElement> nodes = new ArrayList<>();
		nodes.addAll(super.findElementsByXPath(xpath));
		findElementsByXPath(xpath, nodes, findFrames(), new ArrayList<>());
		return nodes;
	}
	
	@Override
	public WebElement findElementByXPath(String xpath){
		return findElementsByXPath(xpath).get(0);
	}
	
	public void scanFrames(Consumer<? super EnhancedChromeDriver> action) {
		action.accept(this);
		parseFrames(action, findFrames());
	}

	private void parseFrames(Consumer<? super EnhancedChromeDriver> action, List<WebElement> findElementsByXPath) {
		for(WebElement frame : findElementsByXPath) {
			switchTo().frame(frame);
			action.accept(this);
			parseFrames(action, findFrames());
			switchTo().parentFrame();
		}
	}
	
	private void findElementsByXPath(String xpath, List<WebElement> nodes, List<WebElement> findElementsByXPath, List<WebElement> inFrames) {
		for(WebElement frame : findElementsByXPath) {
			inFrames.add(frame);
			switchTo().frame(frame);
			nodes.addAll(super.findElementsByXPath(xpath).stream().map(e -> new EnhancedRemoteWebElement(this, new ArrayList<>(inFrames), e, xpath)).collect(Collectors.toList()));
			findElementsByXPath(xpath, nodes, findFrames(), inFrames);
			switchTo().parentFrame();
			inFrames.remove(frame);
		}
	}

	public List<WebElement> findFrames() {
		return super.findElementsByXPath(FRAME_XPATH);
	}
}
