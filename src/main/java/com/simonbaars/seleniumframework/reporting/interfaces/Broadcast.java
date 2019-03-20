package com.simonbaars.seleniumframework.reporting.interfaces;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.simonbaars.seleniumframework.core.common.SavePaths;

public interface Broadcast {
	public Map<String, FileOutputStream> getOutputStreams();
	public int getLineCount();
	public void addLine();
	
	public default void registerOutput(String outputPrefix, String fileName) {
		if(!getOutputStreams().containsKey(fileName)) {
			try {
				getOutputStreams().put(fileName, getFOS(outputPrefix, fileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public default FileOutputStream getFOS(String outputPrefix, String fileName) throws FileNotFoundException {
		return new FileOutputStream(getFilename(outputPrefix, fileName));
	}
	
	public default String getFilename(String outputPrefix, String fileName) {
		return SavePaths.getReportDirectory()+outputPrefix+"_"+fileName+".txt";
	}

	public default void removeOutput(String fileName) {
		if(getOutputStreams().containsKey(fileName)) {
			try {
				getOutputStreams().get(fileName).close();
				getOutputStreams().remove(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public default void broadcast(String data) {
		for(byte b : data.getBytes())
			broadcast(b);
	}
	
	public default void broadcast(byte[] data) {
		for(byte b : data)
			broadcast(b);
	}

	public default void broadcast(int data) {
		if(data == '\n')
			addLine();
		List<String> output = new ArrayList<>(getOutputStreams().keySet());
		for(int i = 0; i < output.size(); i++) {
			try {
				getOutputStreams().get(output.get(i)).write(data);
			} catch (IOException | NullPointerException e) {}
		}
	}
	
	public default void stop() {
		List<String> output = new ArrayList<>(getOutputStreams().keySet());
		for(int i = 0; i < output.size(); i++)
			removeOutput(output.get(i));
	}
}
