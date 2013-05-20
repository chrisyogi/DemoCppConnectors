package com.ericsson.ecut.collector.cpp.log.domain;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ericsson.ecut.collector.sample.domain.TimeStep;

public class LogFiles {
	private LogFile[] logFiles;

	public LogFiles(LogFile[] logFiles){
		setLogFiles(logFiles);
	}
	
	public LogFiles setLogFiles(LogFile[] logFiles) {
		this.logFiles = logFiles;
		return this;
	}

	public LogFile[] getLogFiles() {
		return logFiles;
	}
	
	public Calendar getFirstLogDate(int hourShift){
		Calendar cal = Calendar.getInstance();
		for(LogFile logFile:logFiles){
			//for(LogRecord record:logFile.getLogRecords())
			//	if(record.getDate().before(cal)){
			//		cal = record.getDate();
			//		found = true;
			//	}
			if(logFile!=null)
				if(logFile.getFirstLogDate().before(cal)){
					cal = (Calendar) logFile.getFirstLogDate().clone();
				cal.add(Calendar.HOUR_OF_DAY, hourShift);
				}
		}
		
		return cal;
	}
	
	public LogRecordBlock[] getLogFileBlocks(int hourShift){
		if(logFiles==null)
			return new LogRecordBlock[0];
		
		Set<LogRecordBlock> records = new HashSet<LogRecordBlock>();
		
		for(LogFile logFile:logFiles){
			Set<LogRecordBlock> logRecords = logFile.getLogFileBlocks(hourShift);
			for(LogRecordBlock logRecord:logRecords){
				if(!records.contains(logRecord))
					records.add(new LogRecordBlock(logRecord.getCalendar()));
				
				LogRecordBlock record = getRecordBlock(logRecord, records);
				record.addLogRecordBlock(logRecord, logFile.getLogFileType());
			}
		}
		
		LogRecordBlock[] logRecordBlocks = records.toArray(new LogRecordBlock[0]);

		Arrays.sort(logRecordBlocks);
		return logRecordBlocks;
	}
	
	public LogRecordBlock[] getLogFileBlocks(TimeStep step, Calendar startDate, Calendar endDate, String[] excludedUsers, String[] includedTermnames, String[] includedCorbaCommands , int hourShift){
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.SECOND, 0);
		if(logFiles==null)
			return new LogRecordBlock[0];
		
		Set<LogRecordBlock> records = new HashSet<LogRecordBlock>();
		
		for(LogFile logFile:logFiles){
			if(logFile !=null && logFile.getLogRecords() != null)
				if(logFile.getLogRecords().size()>0){
					Set<LogRecordBlock> logRecords = logFile.getLogFileBlockSet(step, startDate, endDate, excludedUsers, includedTermnames, includedCorbaCommands, hourShift);
					for(LogRecordBlock logRecord:logRecords){
						if(!records.contains(logRecord))
							records.add(new LogRecordBlock(logRecord.getCalendar()));
						
						LogRecordBlock record = getRecordBlock(logRecord, records);
						record.addLogRecordBlock(logRecord, logFile.getLogFileType());
					}
			}else{
				//System.out.println("No blocks in file");
			}
		}
		
		LogRecordBlock[] logRecordBlocks = records.toArray(new LogRecordBlock[0]);

		Arrays.sort(logRecordBlocks);
		return logRecordBlocks;
	}
	
	public LogRecordBlock getRecordBlock(LogRecordBlock element, Set<LogRecordBlock> records) {
		LogRecordBlock result = null;
	    boolean found = false;
	    for (Iterator<LogRecordBlock> it = records.iterator(); !found && it.hasNext();) {
		    if (true) {
		    	LogRecordBlock current = it.next();
		        if (current.equals(element)) {
		        	result = current;
		        	found = true;
		        }
		    }
		}
		return result;
	}
	
	public static Map<String, LogFileType> getLogFileTypes(){
		return LogFile.getLogFileTypes();
	}
}
