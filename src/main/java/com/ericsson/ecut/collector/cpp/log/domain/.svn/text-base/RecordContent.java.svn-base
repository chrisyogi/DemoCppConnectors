package com.ericsson.ecut.collector.cpp.log.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("RecordContent")
public class RecordContent {
	@XStreamAlias("User")
	private String user;
	@XStreamAlias("Termname")
	private String termname;
	@XStreamAlias("Event")
	private String event;
	@XStreamAlias("Info")
	private String info;
	@XStreamAlias("LogInfoRecord")
	private LogInfoRecord logInfoRecord;
	@XStreamAlias("result")
	private Result result;
	
	@XStreamAlias("Request")
	private String request;
	
	private String data;
	
	public void setUser(String user) {
		this.user = user;
	}
	public String getUser() {
		if(user!=null)
			return user.trim();
		return null;
	}
	public void setTermname(String termname) {
		this.termname = termname.trim();
	}
	public String getTermname() {
		if(termname!=null)
			return termname.trim();
		return null;
	}
	public void setEvent(String event) {
		this.event = event.trim();
	}
	public String getEvent() {
		if(event!=null)
			return event.trim();
		return null;
	}
	public void setInfo(String info) {
		this.info = info.trim();
	}
	public String getInfo() {
		if(info!=null)
			return info.trim();
		return null;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getData() {
		if(data!=null)
			return data.trim();
		else return null;
	}
	@Override
	public String toString(){
		return (user!=null?"User: " + getUser():"") + (termname!=null?" termname: " + getTermname():"") + (event!=null?" event: " + getEvent():"") + (info!=null?" info: " + getInfo():"") + (data!=null?"\nData:" + data:"") + " logInfoRecord: " + logInfoRecord +
				(result!=null?" Result: " + result:"") + (request!=null?" Request: " + getRequest():"");
	}
	public void setLogInfoRecord(LogInfoRecord logInfoRecord) {
		this.logInfoRecord = logInfoRecord;
	}
	public LogInfoRecord getLogInfoRecord() {
		return logInfoRecord;
	}
	public String getRequest() {
		if(request!=null)
			return request.trim();
		return null;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
}
