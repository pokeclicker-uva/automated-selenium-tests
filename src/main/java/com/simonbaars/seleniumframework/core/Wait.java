package com.simonbaars.seleniumframework.core;

import java.util.function.BooleanSupplier;

import org.openqa.selenium.TimeoutException;

import com.simonbaars.seleniumframework.core.common.TestingCommons;

public class Wait {
	private final int timeout;
	private final int interval;
	
	public Wait(int timeout) {
		this.timeout=timeout;
		this.interval=400;
	}
	
	public Wait(int timeout, int interval) {
		this.timeout=timeout;
		this.interval=interval;
	}
	
	public void until(BooleanSupplier t) {
		until(t, "The timeout of "+timeout+" seconds expired.");
	}

	public void until(BooleanSupplier t, String exceptionMessage) {
		long endTime = System.currentTimeMillis()+(timeout*1000);
		while(System.currentTimeMillis()<endTime) {
			if(t.getAsBoolean()) return;
			TestingCommons.sleep(interval);
		}
		throw new TimeoutException(exceptionMessage);
	}
}
