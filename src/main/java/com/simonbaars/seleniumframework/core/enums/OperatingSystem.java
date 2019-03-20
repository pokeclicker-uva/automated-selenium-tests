package com.simonbaars.seleniumframework.core.enums;

public enum OperatingSystem {
	WINDOWS, LINUX, MACOS;
	
	public boolean isUnix() {
		return this != WINDOWS;
	}
}
