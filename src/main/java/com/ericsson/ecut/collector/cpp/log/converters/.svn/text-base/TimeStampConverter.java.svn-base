package com.ericsson.ecut.collector.cpp.log.converters;

import com.ericsson.ecut.collector.cpp.log.domain.TimeStamp;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class TimeStampConverter implements Converter {

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return type.equals(TimeStamp.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		TimeStamp timeStamp = new TimeStamp();
		
		while(reader.hasMoreChildren()){
			reader.moveDown();
			setValue(reader.getNodeName(), reader.getValue(), timeStamp);
			reader.moveUp();
		}
		return timeStamp;
	}
	
	private void setValue(String name, String value, TimeStamp timeStamp){
		int i;
		try{
			i = Integer.parseInt(value.trim());
		}catch(Exception e){
			return;
		}
		
		if(name.equalsIgnoreCase("year"))
			timeStamp.setYear(i);
		else if(name.equalsIgnoreCase("month"))
			timeStamp.setMonth(i);
		else if(name.equalsIgnoreCase("day"))
			timeStamp.setDay(i);
		else if(name.equalsIgnoreCase("hour"))
			timeStamp.setHour(i);
		else if(name.equalsIgnoreCase("minute"))
			timeStamp.setMinute(i);
		else if(name.equalsIgnoreCase("second"))
			timeStamp.setSecond(i);
			
	}

}
