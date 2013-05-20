package com.ericsson.ecut.collector.cpp.log.converters;

import com.ericsson.ecut.collector.cpp.log.domain.LogInfoRecord;
import com.ericsson.ecut.collector.cpp.log.domain.RecordContent;
import com.ericsson.ecut.collector.cpp.log.domain.Result;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class RecordContentConverter implements Converter{

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return type.equals(RecordContent.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		RecordContent content = new RecordContent();
		if(!reader.getValue().equals("\n"))
			content.setData(reader.getValue());
		while(reader.hasMoreChildren()){
			reader.moveDown();
			if(reader.getNodeName().equalsIgnoreCase("LogInfoRecord")){
				LogInfoRecord logInfoRecord = new LogInfoRecord();
				while(reader.hasMoreChildren()){
					reader.moveDown();
					logInfoRecord.addProperties(reader.getNodeName(), reader.getValue());
					reader.moveUp();
				}
				content.setLogInfoRecord(logInfoRecord);
			}else if(reader.getNodeName().equalsIgnoreCase("Result")){
				Result result = new Result();
				result.setValue(reader.getAttribute("value"));
				content.setResult(result);
			}
			setValue(reader.getNodeName(), reader.getValue(), content);
			reader.moveUp();
		}
		return content;
	}
	
	private void setValue(String name, String value, RecordContent content){
		if(name.equalsIgnoreCase("event"))
			content.setEvent(value);
		else if(name.equalsIgnoreCase("info"))
			content.setInfo(value);
		else if(name.equalsIgnoreCase("termname"))
			content.setTermname(value);
		else if(name.equalsIgnoreCase("user"))
			content.setUser(value);
		else if(name.equalsIgnoreCase("request"))
			content.setRequest(value);
	}

}
