package com.ericsson.ecut.collector.cpp.log.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LogRecordBlock implements Comparable<LogRecordBlock>{
	private int year;
	private int month;
	private int day;
	private int hour;
	private int period;
	private List<LogRecord> settingRecords;
	private List<LogRecord> shellRecords;
	private List<LogRecord> otherRecords;
	
	public LogRecordBlock(Calendar cal){
		this(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), 0);
	}

	public LogRecordBlock(Calendar cal, int period){
		this(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), period);
	}
	
	public LogRecordBlock(int year, int month, int day, int hour){
		this(year, month, day, hour, 0);
	}

	public LogRecordBlock(int year, int month, int day, int hour, int period){
		settingRecords = new ArrayList<LogRecord>();
		shellRecords = new ArrayList<LogRecord>();
		otherRecords = new ArrayList<LogRecord>();
		this.period = period;
		this.year = year;
		this.month = month;
		this.hour = hour;
		this.day = day;
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
//	public List<LogRecord> getRecords() {
//		List<LogRecord>
//		return records;
//	}
	public void addRecord(List<LogRecord> records, LogFileType type) {
		switch(type){
			case shell:
				this.shellRecords.addAll(records);
				return;
			case setting:
				this.settingRecords.addAll(records);
				return;
			default:
				this.otherRecords.addAll(records);
				return;
		}
	}
	
	public Calendar getCalendar(){
		return getStartDate();
	}
	
	public Calendar getStartDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day, hour, period*15, 0);
		return cal;
	}
	
	public Calendar getEndDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day, hour, 59, 59);
		return cal;
	}
	
	@Override
	public int hashCode(){
		return (year-2009)*(365*24*5)+month*(32*24*5)+(day-1)*24*5+hour*5+period;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof LogRecordBlock))
			return false;
		LogRecordBlock block = (LogRecordBlock) o;
		return block.year == year && block.day == day && block.month == month && block.hour == hour && block.period == period;	
	}
	
	public void addLogRecord(LogRecord record, LogFileType type){
		//records.add(record);
		switch(type){
		case shell:
			this.shellRecords.add(record);
			return;
		case setting:
			this.settingRecords.add(record);
			return;
		default:
			this.otherRecords.add(record);
			return;
		}
	}

	@Override
	public int compareTo(LogRecordBlock o) {
		return getStartDate().compareTo(o.getStartDate());
	}

	public void setSettingRecords(List<LogRecord> settingsRecords) {
		this.settingRecords = settingsRecords;
	}

	public List<LogRecord> getSettingRecords() {
		return settingRecords;
	}

	public void setShellRecords(List<LogRecord> shellRecords) {
		this.shellRecords = shellRecords;
	}

	public List<LogRecord> getShellRecords() {
		return shellRecords;
	}

	public void setOtherRecords(List<LogRecord> otherRecords) {
		this.otherRecords = otherRecords;
	}

	public List<LogRecord> getOtherRecords() {
		return otherRecords;
	}
	
	public List<LogRecord> getLogRecords(){
		List<LogRecord> records = new ArrayList<LogRecord>();
		for(LogRecord record:settingRecords)
			records.add(record.setType(LogFileType.setting));
		for(LogRecord record:shellRecords)
			records.add(record.setType(LogFileType.shell));
		for(LogRecord record:otherRecords)
			records.add(record.setType(LogFileType.other));
		
		return records;
	}

	public void addLogRecordBlock(LogRecordBlock recordBlock, LogFileType type) {
		switch(type){
		case shell:
			for(LogRecord record:recordBlock.getLogRecords())
				this.shellRecords.add(record);
			return;
		case setting:
			for(LogRecord record:recordBlock.getLogRecords())
				this.settingRecords.add(record);
			return;
		default:
			for(LogRecord record:recordBlock.getLogRecords())
				this.otherRecords.add(record);
			return;
		}
	}

	public List<LogRecord> getRecords() {
		return getLogRecords();
	}
}
