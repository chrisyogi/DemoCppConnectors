package com.ericsson.ecut.collector.cpp.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ConfigurationVersion {
	private String name;
	private String identity;
	private String type;
	private String upgradePackageId;
	private String operatorName;
	private String operatorComment;
	private String status;
	private String date;
	
	public ConfigurationVersion(){}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUpgradePackageId() {
		return upgradePackageId;
	}
	public void setUpgradePackageId(String upgradePackageId) {
		this.upgradePackageId = upgradePackageId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getOperatorComment() {
		return operatorComment;
	}
	public void setOperatorComment(String operatorComment) {
		this.operatorComment = operatorComment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public Calendar getCalendar(){
		if(date==null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
		Calendar cal = new GregorianCalendar();
		try {
			cal.setTime(sdf.parse(date));
			cal.get(Calendar.MINUTE);
			return cal;
		} catch (ParseException e) {
			return null;
		}
	}
}
