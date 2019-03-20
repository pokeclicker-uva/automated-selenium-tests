package com.simonbaars.seleniumframework.reporting.broadcast;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.simonbaars.seleniumframework.reporting.interfaces.Broadcast;

public class BroadcastOutput implements Broadcast {
	protected final Map<String, FileOutputStream> outputStreams = new HashMap<>();
	private int lineCount = 0;
	
	@Override
	public Map<String, FileOutputStream> getOutputStreams(){
		return outputStreams;
	}
	
	@Override
	public int getLineCount() {
		return lineCount;
	}
	
	@Override
	public void addLine() {}
}
