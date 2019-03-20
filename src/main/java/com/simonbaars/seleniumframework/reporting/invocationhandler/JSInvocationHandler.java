/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.reporting.invocationhandler;

import java.lang.reflect.Method;

import javafx.application.Platform;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import com.simonbaars.seleniumframework.core.PageObjectModel;
import com.simonbaars.seleniumframework.core.common.TestingCommons;
import com.simonbaars.seleniumframework.driver.exception.DriverException;

public class JSInvocationHandler implements MethodInterceptor {
	
	public static Class<? extends PageObjectModel> skipPage = null;
	private static Object result = null;

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		try {
			Platform.runLater(() ->	{
				try {
					result = proxy.invokeSuper(obj, args);
				} catch (Throwable e) {
					throw new DriverException(e);
				}
			});
		} catch (Exception e) {
			throw new DriverException(e);
		}
		return null;
	}
	
	public static Object awaitResult() {
		while(result == null) {
			TestingCommons.sleep(10);
		}
		Object obj = result;
		result = null;
		return obj;
	}
}
