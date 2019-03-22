/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.reporting.invocationhandler;

import java.lang.reflect.Method;

import com.simonbaars.seleniumframework.core.PageObjectModel;
import com.simonbaars.seleniumframework.driver.exception.DriverException;
import com.simonbaars.seleniumframework.reporting.ActionExecutor;
import com.simonbaars.seleniumframework.reporting.annotations.TestAction;
import com.simonbaars.seleniumframework.reporting.interfaces.HasBeforeTestAction;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ActionInvocationHandler implements MethodInterceptor {
	
	public static Class<? extends PageObjectModel> skipPage = null;

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		try {
			if(method.isAnnotationPresent(TestAction.class)) {
				if(skipPage != null && obj.getClass().isAssignableFrom(skipPage))
					return null;
				else skipPage = null;
				
				ActionExecutor.executeAction(method.getAnnotation(TestAction.class), obj, method, args);
			
				if(obj instanceof HasBeforeTestAction)
					((HasBeforeTestAction)obj).beforeTestAction();
			}
			Object retObj = proxy.invokeSuper(obj, args);
			if(method.isAnnotationPresent(TestAction.class)) {
				ActionExecutor.afterExecution(method.getAnnotation(TestAction.class), proxy, method, args, retObj);
			}
			return retObj;
		} catch (Exception e) {
			throw new DriverException(e);
		}
	}
}
