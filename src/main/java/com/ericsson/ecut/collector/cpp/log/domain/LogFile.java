package com.ericsson.ecut.collector.cpp.log.domain;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ericsson.ecut.collector.sample.domain.TimeStep;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("Log")
public class LogFile {
	@XStreamAlias("LogCreated")
	private LogCreated logCreated;
	@XStreamAlias("LogRecord")
	@XStreamImplicit
	private List<LogRecord> logRecords;
	@XStreamOmitField
	private Calendar firstDate;
	@XStreamOmitField
	private static final String[] mosshellAuditLogs = new String[]{/*"AUDIT_TRAIL_LOG",*/ "CELLO_AUDITTRAIL_LOG", "CELLO_SECURITYEVENT_LOG","CORBA_AUDITTRAIL_LOG","SHELL_AUDITTRAIL_LOG"};
	@XStreamOmitField
	private static final String[] mosshellAuditLogTypes = new String[]{/*"other",*/ "other", "other", "setting","shell"};

	public LogFile(){
		//firstDate = Calendar.getInstance();
	}
	
	public Calendar getFirstLogDate(){
		if(firstDate==null)
			firstDate = getFirstLogDateInternal();
		return firstDate;
	}
	
	private Calendar getFirstLogDateInternal(){
		if(this.logCreated!=null && this.logCreated.getTimeStamp()!=null)
			return logCreated.getTimeStamp().getCalendar();
			
		Calendar cal = Calendar.getInstance();
		if(logRecords!=null)
			for(LogRecord record:logRecords)
				if(record.getCalendar()!=null && record.getCalendar().before(cal))
					cal = (Calendar) record.getCalendar().clone();
		
		return cal;			
	}
	
	public void setLogCreated(LogCreated logCreated) {
		this.logCreated = logCreated;
	}

	public LogCreated getLogCreated() {
		return logCreated;
	}

	public void setLogRecord(List<LogRecord> logRecords) {
		this.logRecords = logRecords;
	}

	public List<LogRecord> getLogRecords() {
		return logRecords;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if(logCreated!=null){
			sb.append("Logcreated: ");
			sb.append(logCreated);
		}
		if(logRecords!=null){
			sb.append("\n");
			sb.append("Records");
			sb.append("\n");
			for(LogRecord record:logRecords){
				sb.append(record);
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	public Set<LogRecordBlock> getLogFileBlocks(int hourShift){
		return getLogFileBlocks(TimeStep.hour, hourShift);
	}
	
	public LogRecordBlock[] getLogFileBlocks(TimeStep step, Calendar startDate, Calendar endDate, int hourShift){
		return getLogFileBlocks(step, startDate, endDate, null, null, null, hourShift);
	}
	
	public LogRecordBlock[] getLogFileBlocks(TimeStep step, Calendar startDate, Calendar endDate, String[] excludedUsers, String[] includedTermnames, String[] includedCorbaCommands, int hourShift){
		LogRecordBlock[] logRecordBlocks = getLogFileBlockSet(step, startDate, endDate, excludedUsers, includedTermnames, includedCorbaCommands, hourShift).toArray(new LogRecordBlock[0]);
		Arrays.sort(logRecordBlocks);
		return logRecordBlocks;
	}
	
	/**
	 * Get log blocks based on date and exclude records based on users, term names and corba command type
	 * @param step
	 * @param startDate
	 * @param endDate
	 * @param excludedUsers
	 * @param includedTermnames
	 * @param includedCorbaCommands
	 * @param hourShift
	 * @return
	 */
	public Set<LogRecordBlock> getLogFileBlockSet(TimeStep step, Calendar startDate, Calendar endDate, String[] excludedUsers, String[] includedTermnames, String[] includedCorbaCommands, int hourShift){
		//System.out.println("Getting log file blocks interval: " + Util.getCalendarString(startDate) + " - " + Util.getCalendarString(endDate));
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//System.out.println("Starddate: " + sdf.format(startDate.getTime()));
		//System.out.println("Enddate: " + sdf.format(endDate.getTime()));
		
		Set<LogRecordBlock> records = new HashSet<LogRecordBlock>();
		for(LogRecord record:logRecords){
			Calendar cal = record.getCalendar(hourShift);
			
//			if(record.getNumber()>=756137){
//				System.out.println("Starddate: " + sdf.format(startDate.getTime()) + " " + startDate.getTimeInMillis());
//				System.out.println("Enddate: " + sdf.format(endDate.getTime()) + " " + endDate.getTimeInMillis());
//				System.out.println("Date: " + sdf.format(cal.getTime()) + " " + cal.getTimeInMillis());
//			}
			
			if(getFirstDate()==null || cal.before(getFirstDate()))
				firstDate = (Calendar) record.getCalendar().clone();
			if(cal!=null && !cal.before(startDate) && !cal.after(endDate)){
				boolean exclude = false;
				if(record.getRecordContent()!=null && record.getRecordContent().getUser() != null && excludedUsers!=null){
					for(String username:excludedUsers)
						if(record.getRecordContent().getUser().equalsIgnoreCase(username)){
							exclude = true;
							break;
						}
					
					if(exclude)
						continue;
				}
				
				if(record.getRecordContent()!=null && record.getRecordContent().getTermname() != null && includedTermnames!=null && !exclude){
					exclude = true;
					for(String termname:includedTermnames)
						if(record.getRecordContent().getTermname().indexOf(termname)>0){
							exclude = false;
							break;
						}
					
					if(exclude)
						continue;
				}
				
				if((logCreated==null ||  (logCreated.getLogName()!=null && logCreated.getLogName().equalsIgnoreCase("CORBA_AUDITTRAIL_LOG"))) && record.getRecordContent()!=null && record.getRecordContent().getData() != null && includedCorbaCommands!=null && !exclude){
					exclude = true;
					String c = getCorbaCallType(record.getRecordContent());
					for(String command:includedCorbaCommands)
						if(c.equalsIgnoreCase(command)){
							exclude = false;
							break;
						}
					
					if(exclude)
						continue;
				}
				
				
				if(!exclude){
					LogRecordBlock block;
					if(step == TimeStep.hour)
						block = new LogRecordBlock(cal);
					else{
						int period = cal.get(Calendar.MINUTE)/15;
						block = new LogRecordBlock(cal, period);
					}
						
					if(!records.contains(block))
						records.add(block);
					
					
					//System.out.println("Adding record with time stamp: " + Util.getCalendarString(record.getCalendar()) + " with shift: " + Util.getCalendarString(cal));
					getRecordBlock(block, records).addLogRecord(record, getLogFileType());
				}
			}
		}
		return records;
	}
	
	private String getCorbaCallType(RecordContent content){
		if(content.getData()==null)
			return "";
		//Find second row
		int pos1 = content.getData().indexOf("\n");
		int pos2 = content.getData().indexOf("\n", pos1+1);
		if(pos1>0&&pos2>0&&pos1<pos2)
			return content.getData().substring(pos1+1, pos2);
		else
			return "";
	}
	
	public Set<LogRecordBlock> getLogFileBlocks(TimeStep step, int hourShift){
		
		
		Set<LogRecordBlock> records = new HashSet<LogRecordBlock>();
		for(LogRecord record:logRecords){
			Calendar cal = record.getCalendar(hourShift);
			if(cal!=null && cal.before(getFirstDate()))
				firstDate = (Calendar) record.getCalendar().clone(); 
			if(cal!=null){
				LogRecordBlock block;
				if(step == TimeStep.hour)
					block = new LogRecordBlock(cal);
				else{
					int period = cal.get(Calendar.MINUTE)/15;
					block = new LogRecordBlock(cal, period);
				}
					
				if(!records.contains(block))
					records.add(block);
				
				getRecordBlock(block, records).addLogRecord(record, getLogFileType());
			}
		}
		return records;
	}
	
	public LogRecordBlock getRecordBlock(LogRecordBlock element, Set<LogRecordBlock> records) {
		LogRecordBlock result = null;
	    boolean found = false;
	    for (Iterator<LogRecordBlock> it = records.iterator(); !found && it.hasNext();) {
		    if (true) {
		    	LogRecordBlock current = it.next();
		        if (current.equals(element)) {
		        	result = current;
		        	found = true;
		        }
		    }
		}
		return result;
	}
	
	public LogFileType getLogFileType(){
		return getLogFileTypes().get(getLogCreated().getLogName());
	}
	
	public static Map<String, LogFileType> getLogFileTypes(){
		Map<String, LogFileType> logs = new HashMap<String, LogFileType>();
		for(int i=0;i<mosshellAuditLogs.length;i++)
			logs.put(mosshellAuditLogs[i], LogFileType.parse(mosshellAuditLogTypes[i]));
		return logs;
	}
	
	public Calendar getFirstDate() {
		return getFirstLogDate();
	}
}
