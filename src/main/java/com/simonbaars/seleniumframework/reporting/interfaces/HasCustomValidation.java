/*******************************************************************************
 * Copyright (c) 2018 Simon Baars.
 * All rights reserved.
 * All code is written by Simon Baars, http://simonbaars.com/.
 ******************************************************************************/
package com.simonbaars.seleniumframework.reporting.interfaces;

import java.util.List;

public interface HasCustomValidation {
	public boolean executeCustomValidation(List<String> xpaths);
}
