package com.simonbaars.seleniumframework.reporting.broadcast;

import java.util.HashMap;
import java.util.Map;

import com.simonbaars.seleniumframework.reporting.interfaces.Broadcast;

public class LogEntryBroadcast extends BroadcastOutput implements Broadcast {
	Map<Integer, Integer> traversedEntries = new HashMap<>();
	private final String type;

	private final Thread task = new Thread(() -> {
		/*while(outputStreams.size() > 0) {
			int driverNumber = DriverProvider.getCurrentDriverNumber();
			if(!traversedEntries.containsKey(driverNumber))
				traversedEntries.put(driverNumber, 0);
			LogEntries currentEntries = DriverProvider.getDriver(driverNumber).manage().logs().get(getType());
			List<LogEntry> all = currentEntries.getAll();
			int entriesSize = all.size();
			if(entriesSize > traversedEntries.get(driverNumber)) {
				for(int i = traversedEntries.get(driverNumber); i<entriesSize; i++) {
					LogEntry logEntry = all.get(i);
					broadcast("["+logEntry.getLevel().getName()+"]["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(logEntry.getTimestamp()))+"] "+logEntry.getMessage()+System.lineSeparator());
				}
				traversedEntries.put(driverNumber, entriesSize);
			}
			TestingCommons.sleep(100);
		}*/
	});
	
	
	public LogEntryBroadcast(String type) {
		super();
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public void start() {
		task.start();
	}
	
	@Override
	public void stop() {
		super.stop();
	}
}
