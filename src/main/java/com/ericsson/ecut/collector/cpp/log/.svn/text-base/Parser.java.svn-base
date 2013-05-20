package com.ericsson.ecut.collector.cpp.log;

import java.io.InputStream;
import java.util.Set;

import com.ericsson.ecut.collector.cpp.helper.CPPUtil;
import com.ericsson.ecut.collector.cpp.log.converters.LogInfoRecordConverter;
import com.ericsson.ecut.collector.cpp.log.converters.RecordContentConverter;
import com.ericsson.ecut.collector.cpp.log.converters.TimeStampConverter;
import com.ericsson.ecut.collector.cpp.log.domain.LogCreated;
import com.ericsson.ecut.collector.cpp.log.domain.LogFile;
import com.ericsson.ecut.collector.cpp.log.domain.LogInfoRecord;
import com.ericsson.ecut.collector.cpp.log.domain.LogRecord;
import com.ericsson.ecut.collector.cpp.log.domain.LogRecordBlock;
import com.ericsson.ecut.collector.cpp.log.domain.RecordContent;
import com.ericsson.ecut.collector.cpp.log.domain.TimeStamp;
import com.thoughtworks.xstream.XStream;

public class Parser {
	public static LogFile parse(String input){
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
		xstream.registerConverter(new TimeStampConverter());
		xstream.registerConverter(new RecordContentConverter());
		xstream.registerConverter(new LogInfoRecordConverter());
		xstream.processAnnotations(new Class[]{LogCreated.class, LogFile.class,LogRecord.class,RecordContent.class,TimeStamp.class, LogInfoRecord.class});		
		return (LogFile) xstream.fromXML(input);
	}
	
	public static LogFile parse(InputStream input) throws Exception{
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
		xstream.registerConverter(new TimeStampConverter());
		xstream.registerConverter(new RecordContentConverter());
		xstream.registerConverter(new LogInfoRecordConverter());
		xstream.processAnnotations(new Class[]{LogCreated.class, LogFile.class,LogRecord.class,RecordContent.class,TimeStamp.class, LogInfoRecord.class});
		return (LogFile) xstream.fromXML(input);
	}
	
	public static void main(String[] args) throws Exception {
		LogFile log = parse(CPPUtil.getFile("files/SHELL_AUDITTRAIL_LOG.xml"));
		log.toString();
		
		//LogFile log2 = parse(getFile("files/CORBA_AUDITTRAIL_LOG.xml"));
		//LogFile log2 = parse(getFileStream("files/CORBA_AUDITTRAIL_LOG.xml"));
		//LogFile log2 = parse(getFile("files/CELLO_AUDITTRAIL_LOG.xml"));
		//log2.toString();
		
		Set<LogRecordBlock> records = log.getLogFileBlocks(0);
		System.out.println(records.toString());
	}
}
