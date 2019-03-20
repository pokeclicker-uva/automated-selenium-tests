package com.simonbaars.seleniumframework.reporting.broadcast;

import com.simonbaars.seleniumframework.reporting.interfaces.Broadcast;

public class LoggerBroadcast extends BroadcastOutput implements Broadcast {	
	@Override
	public void broadcast(String data) {
		Broadcast.super.broadcast(data + System.lineSeparator());
	}
}