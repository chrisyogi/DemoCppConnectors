package com.ericsson.ecut.collector.cpp.log.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("LogRecord")
public class LogRecord {
	@XStreamAlias("Number")
	@XStreamAsAttribute
	private long number;
	@XStreamAlias("TimeStamp")
	private TimeStamp timeStamp;
	@XStreamAlias("RecordContent")
	private RecordContent recordContent;
	@XStreamOmitField
	private Calendar date;
	
	@XStreamOmitField
	private LogFileType type;
	
	public void setNumber(long number) {
		this.number = number;
	}
	public long getNumber() {
		return number;
	}
	public void setTimeStamp(TimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}
	public void setRecordContent(RecordContent recordContent) {
		this.recordContent = recordContent;
	}
	public RecordContent getRecordContent() {
		return recordContent;
	}
	
	public Calendar getCalendar(int hourShift){
		if(getCalendar()!=null){
			Calendar cal = (Calendar) getCalendar().clone();
			cal.add(Calendar.HOUR_OF_DAY, hourShift);
			return cal;
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1970);
		return cal;
		
	}
	
	public Calendar getCalendar(){
		if(this.date!=null)
			return this.date;
		else if(timeStamp!=null){
			this.date = (Calendar) timeStamp.getCalendar().clone();
			return this.date;
		}else if(recordContent!=null && recordContent.getData()!=null){
			//Tue May 24 2011, 11:53:55
			String data = recordContent.getData();
			return getDateInternal(data);
//			if(data.startsWith("//") && data.indexOf(" ", 4)>0){
//				String date = data.substring(data.indexOf(" ", 4)+5, data.indexOf("\n"));
//				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy, HH:mm:ss", Locale.US);
//				try{
//					this.date = Calendar.getInstance();
//					this.date.setTime(sdf.parse(date));
//					return this.date;
//				}catch(Exception e){e.printStackTrace();return null;}
//			}
		}else if(recordContent!=null && recordContent.getRequest()!=null){
			String request = recordContent.getRequest();
			return getDateInternal(request);
		}
		return null;
	}
	
	private Calendar getDateInternal(String data){
		if(data==null)
			return null;
		//Tue May 24 2011, 11:53:55
		if(data.startsWith("//") && data.indexOf(" ", 4)>0){
			String date = data.substring(data.indexOf(" ", 4)+5, data.indexOf("\n"));
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy, HH:mm:ss", Locale.US);
			try{
				this.date = Calendar.getInstance();
				this.date.setTime(sdf.parse(date));
				return this.date;
			}catch(Exception e){e.printStackTrace();return null;}
		}else
			System.out.println("check dates");
		return null;
	}
	
	@SuppressWarnings("unused")
	private int getMonth(String month){
		if(month=="Jan")
			return 1;
		else if(month=="Feb")
			return 2;
		else if(month=="Mar")
			return 3;
		else if(month=="Apr")
			return 4;
		else if(month=="May")
			return 5;
		else if(month=="Jun")
			return 6;
		else if(month=="Jul")
			return 7;
		else if(month=="Aug")
			return 8;
		else if(month=="Sep")
			return 9;
		else if(month=="Oct")
			return 10;
		else if(month=="Nov")
			return 11;
		else
			return 12;
			
	}
	
	@Override
	public String toString(){
		return "Number: " + number + (timeStamp!=null?" timestamp: " + timeStamp:"") + (recordContent!=null?" recordContent: " + recordContent:"");
	}
	public LogRecord setType(LogFileType type) {
		this.type = type;
		return this;
	}
	public LogFileType getType() {
		return type;
	}
	
}
