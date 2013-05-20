package com.ericsson.ecut.collector.cpp.log.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("LogCreated")
public class LogCreated {
	@XStreamAlias("LogName")
	private String logName;
	@XStreamAlias("TimeStamp")
	private TimeStamp timeStamp;
	@XStreamAlias("AdditionalLogInfo")
	private String additionalLogInfo;
	
	public void setLogName(String logName) {
		this.logName = logName.trim();
	}
	public String getLogName() {
		return logName.trim();
	}
	public void setTimeStamp(TimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}
	public void setAdditionalLogInfo(String additionalLogInfo) {
		this.additionalLogInfo = additionalLogInfo.trim();
	}
	public String getAdditionalLogInfo() {
		return additionalLogInfo;
	}
	
	@Override
	public String toString(){
		return "Logname: " + getLogName() + getTimeStamp()!=null?" timestamp: " + timeStamp:"" + getAdditionalLogInfo()!=null?" additionalLogInfo: " +  additionalLogInfo:"";
	}
}