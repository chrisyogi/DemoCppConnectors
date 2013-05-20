package com.ericsson.ecut.collector.cpp.log.domain;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("LogInfoRecord")
public class LogInfoRecord {
	private List<Property> properties;

	public LogInfoRecord(){
		setProperties(new ArrayList<Property>());
	}
	
	public List<Property> getProperty(String name){
		List<Property> prop = new ArrayList<Property>();
		if(properties!=null){
			for(Property property:properties)
				if(property.getName().equalsIgnoreCase(name))
					prop.add(property);
		}
		return prop;
	}
	
	public void addProperties(String name, String value){
		properties.add(new Property(name, value));
	}
	
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("Properties: ");
		if(properties!=null){
			for(Property property:properties)
				sb.append("\n Name: " + property.getName() + " Value: " + property.getValue());
		}
		return sb.toString();
	}


	public List<Property> getProperties() {
		return properties;
	}


	public class Property{
		private String name;
		private String value;
		
		public Property(String name, String value){
			this.setName(name);
			this.setValue(value);
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
