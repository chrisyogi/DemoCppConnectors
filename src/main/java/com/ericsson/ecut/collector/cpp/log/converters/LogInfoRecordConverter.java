package com.ericsson.ecut.collector.cpp.log.converters;

import com.ericsson.ecut.collector.cpp.log.domain.LogInfoRecord;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class LogInfoRecordConverter implements Converter{

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return type.equals(LogInfoRecord.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		LogInfoRecord logInfoRecord = new LogInfoRecord();
		while(reader.hasMoreChildren()){
			reader.moveDown();
			logInfoRecord.addProperties(reader.getNodeName(), reader.getValue());
			reader.moveUp();
		}
		return logInfoRecord;
	}

}
