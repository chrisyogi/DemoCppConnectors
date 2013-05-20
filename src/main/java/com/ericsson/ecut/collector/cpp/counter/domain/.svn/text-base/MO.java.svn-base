package com.ericsson.ecut.collector.cpp.counter.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MO{
	private String name;
	private Map<String, Double> values;
	private Integer id;
	
	public MO(String name){
		this(name, 0);
	}
	
	public MO(String name, int id){
		this.name = name;
		this.values = new HashMap<String, Double>();
	}
	
	public MO addValue(String attributeName, Double value){
		values.put(attributeName, value);
		return this;
	}
	
	public String getName(){
		return name;
	}
	
	public Double getAttributeValue(String attibuteName){
		if (values.containsKey(attibuteName))
			return values.get(attibuteName);
		return null;
	}
	
	public boolean containsAttribute(String valueName){
		return values.containsKey(valueName);
	}
	
	public Map<String, Double> getValues(){
		return values;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer(name);
		if(id!=null)
			sb.append("=" + id);
		
		for(Entry<String,Double>value:values.entrySet()){
			sb.append(" ");
			sb.append(value.getKey());
			sb.append(":");
			sb.append(value.getValue());
		}
		return sb.toString();
	}
}
