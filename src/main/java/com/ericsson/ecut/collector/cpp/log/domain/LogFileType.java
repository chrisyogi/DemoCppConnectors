package com.ericsson.ecut.collector.cpp.log.domain;

public enum LogFileType{
	setting("setting"),
	shell("shell"),
	other("other");
	
	private String typeString;

	private LogFileType(String type){
		this.typeString = type;
	}

	public String getTypeString() {
		return typeString;
	}
	
	public static LogFileType parse(String desc){
		for(LogFileType type:values())
			if(type.getTypeString().equalsIgnoreCase(desc))
				return type;
			
		return null;
	}
}