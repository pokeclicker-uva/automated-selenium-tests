package com.simonbaars.seleniumframework.reporting.broadcast;

import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenshotBroadcast extends BroadcastOutput {
	@Override
	public void broadcast(int data) {
		if(getOutputStreams().size() > 0) {
			FileOutputStream output = getOutputStreams().values().toArray(new FileOutputStream[0])[getOutputStreams().size()-1];
			try {
				output.write(data);
			} catch (IOException e) { }
		}
	}
}
