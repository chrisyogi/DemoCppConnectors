package com.ericsson.ecut.collector.cpp.log.domain;

import javax.xml.bind.annotation.XmlAnyAttribute;

public class Result {
	@XmlAnyAttribute
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString(){
		return value;
	}
}
