package com.ericsson.ecut.collector.cpp.counter.domain;

import java.util.Calendar;

import com.ericsson.ecut.collector.sample.domain.CounterBlock;

public class CounterLogBlock implements Comparable<CounterLogBlock>{
	private int year;
	private int month;
	private int day;
	private int hour;
	private int period;
	private long nrSettingRecords = -1;
	private long nrShellRecords = -1;
	private long nrOtherRecords = -1;
	private CounterBlock counters;
	
	public CounterLogBlock(Calendar cal){
		this(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), 0);
	}

	public CounterLogBlock(Calendar cal, int period){
		this(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), period);
	}
	
	public CounterLogBlock(int year, int month, int day, int hour, int period) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.period = period;
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
	public long getNrSettingRecords() {
		return nrSettingRecords;
	}
	public void setNrSettingRecords(long nrSettingRecords) {
		this.nrSettingRecords = nrSettingRecords;
	}
	public long getNrShellRecords() {
		return nrShellRecords;
	}
	public void setNrShellRecords(long nrShellRecords) {
		this.nrShellRecords = nrShellRecords;
	}
	public long getNrOtherRecords() {
		return nrOtherRecords;
	}
	public void setNrOtherRecords(long nrOtherRecords) {
		this.nrOtherRecords = nrOtherRecords;
	}
	public CounterBlock getCounters() {
		return counters;
	}
	public void setCounters(CounterBlock counters) {
		this.counters = counters;
	}
	
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof CounterLogBlock))
			return false;
		CounterLogBlock block = (CounterLogBlock) o;
		return block.year == year && block.day == day && block.month == month && block.hour == hour && block.period == period;	
	}

	@Override
	public int compareTo(CounterLogBlock o) {
		return getStartDate().compareTo(o.getStartDate());
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
		if(period == 0)
			cal.set(year, month-1, day, hour, 59, 59);
		else
			cal.set(year, month-1, day, hour, period*15+14, 59);
		return cal;
	}
	
	@Override
	public int hashCode(){
		System.out.println(((year-2009)*(365*24*5)+month*(32*24*5)+(day-1)*24*5+hour*5+period));
		return (year-2009)*(365*24*5)+month*(32*24*5)+(day-1)*24*5+hour*5+period;
	}

}
