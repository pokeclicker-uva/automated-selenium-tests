package com.simonbaars.seleniumframework.reporting.broadcast;

import java.io.IOException;
import java.io.InputStream;

import com.simonbaars.seleniumframework.core.common.TestingCommons;
import com.simonbaars.seleniumframework.reporting.interfaces.Broadcast;

public abstract class RunningTaskBroadcast extends BroadcastOutput implements Broadcast {	
	private final Thread task = new Thread(() -> {
		while(outputStreams.size() > 0) {
			try {
				while(getInputStream().available()>0) {
					int read = getInputStream().read();
					broadcast(read);
				}
			} catch (IOException e) {
				return;
			}
			TestingCommons.sleep(100);
		}
	});

	public abstract InputStream getInputStream();
	
	public void start() {
		task.start();
	}
	
	@Override
	public void stop() {
		try {
			if(getInputStream()!=null) getInputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Broadcast.super.stop();
	}

}
