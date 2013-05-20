package com.ericsson.ecut.collector.cpp.rop;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPFile;

import com.ericsson.ecut.collector.cpp.connectors.exception.TimeoutException;
import com.ericsson.ecut.collector.cpp.connectors.ftp.CPPFTPClient;
import com.ericsson.ecut.collector.cpp.task.ProcessWrapper;
import com.ericsson.ecut.collector.cpp.util.DateUtil;
import com.ericsson.ecut.collector.exception.CommunicationException;
import com.ericsson.ecut.collector.exception.ConnectException;
import com.ericsson.ecut.collector.sample.domain.Counter;
import com.ericsson.ecut.collector.sample.domain.CounterBlocks;
import com.ericsson.ecut.collector.sample.domain.TimeStep;

public class RopCollector {
	private Log logger = LogFactory.getLog("RopCollector");
	private CPPFTPClient client;
	private static final String ROP_FILES_LOCATION = "/c/pm_data/";
	private static final int baseTime = 15;
	private static String DEFAULT_MOSHELL_PATH = "/usr/share/moshell/";
	private String moshellPath;

	public RopCollector(CPPFTPClient client){
		this(new CounterBlocks(TimeStep.hour), client);
	}
	
	public RopCollector(String host, String password){
		this(new CounterBlocks(TimeStep.hour), host, password);
	}
	
	public RopCollector(CounterBlocks counters, String host, String password){
		this(counters, new CPPFTPClient(host, password));
	}
	
	public RopCollector(CounterBlocks counters, CPPFTPClient client){
		this.client = client;
		this.moshellPath = System.getProperty("moshell.path", DEFAULT_MOSHELL_PATH);
		if(!moshellPath.endsWith("/"))
			this.moshellPath = moshellPath + "/";
			
		
	}
	public CounterBlocks getCounters(String moFilter, String counterFilter, Calendar startDate, Calendar endDate) throws ConnectException, CommunicationException{
		return getCounters(moFilter, counterFilter, startDate, endDate, new CounterBlocks(TimeStep.hour));
	}
	
	/**
	 * 
	 * @param MoFilter
	 * @param MoGroup
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ConnectException
	 * @throws CommunicationException
	 */
	public CounterBlocks getCounters(String moFilter, String counterFilter, Calendar startDate, Calendar endDate, CounterBlocks blocks) throws ConnectException, CommunicationException{
		return getCounters(new CounterFilter(moFilter, counterFilter), startDate, endDate, blocks);
	}
	
	public CounterBlocks getCounters(CounterFilter counterFilter, Calendar startDate, Calendar endDate, CounterBlocks blocks) throws ConnectException, CommunicationException{
		List<CounterFilter> counterFilters = new ArrayList<CounterFilter>();
		counterFilters.add(counterFilter);
		return getCounters(counterFilters, startDate, endDate, blocks);
	}
	
	public CounterBlocks getCounters(List<CounterFilter> counterFilters, Calendar startDate, Calendar endDate) throws ConnectException, CommunicationException{
		return getCounters(counterFilters, startDate, endDate, new CounterBlocks(TimeStep.hour), 0);
	}

	public CounterBlocks getCounters(List<CounterFilter> counterFilters, Calendar startDate, Calendar endDate, int hourShift) throws ConnectException, CommunicationException{
		return getCounters(counterFilters, startDate, endDate, new CounterBlocks(TimeStep.hour), hourShift);
	}
	
	public CounterBlocks getCounters(List<CounterFilter> counterFilters, Calendar startDate, Calendar endDate, CounterBlocks blocks) throws ConnectException, CommunicationException{
		return getCounters(counterFilters, startDate, endDate, new CounterBlocks(TimeStep.hour), 0);
	}
	
	public CounterBlocks getCounters(List<CounterFilter> counterFilters, Calendar startDate, Calendar endDate, CounterBlocks blocks, int hourShift) throws ConnectException, CommunicationException{
		List<String> downloadedFiles = downloadFiles(startDate, endDate);
		Collections.sort(downloadedFiles);
		
		for(CounterFilter counterFilter:counterFilters){
			if(logger.isDebugEnabled())logger.debug("Getting counter " + counterFilter);
			StringBuilder sb = new StringBuilder();
			List<String> commands = new ArrayList<String>();
		
			commands.add("gzip");
			commands.add("-dc");
		
			
			for(String file:downloadedFiles){
				commands.add(file);
			}
	
			commands.add("|");
			commands.add(moshellPath + "pmExtract");
			commands.add("-u");
			commands.add("\""+ counterFilter.getMoFilter() + "\" \"" + counterFilter.getCounterFilter() + "\"");
			commands.add("|");
			commands.add(moshellPath + "pmXtab");
			commands.add("-cols");
			commands.add("time");
			commands.add("-fmt");
			commands.add("txt");
			commands.add("-m");
			commands.add("\"(?:\\w+=[^,]+,){1,2}((\\w+=[^,]+,*)+$)\"");
	
			for(String s:commands)
				sb.append(s + " ");
			
			String result = null;
			try {
				result = ProcessWrapper.execute(new String[]{"/bin/sh", "-c", sb.toString()});
			} catch (Exception e) {
				if(e instanceof TimeoutException)
					throw new CommunicationException("Timeout exeception parsing output of pmx for " + client.getHost(), e);
				throw new CommunicationException("Exeception parsing output of pmx for " + client.getHost(), e);
			}
			
			if(result==null)
				throw new CommunicationException("Error getting counters for " + client.getHost());
	
			//result = readFile("C:\\Users\\emarege\\Documents\\rop2.txt");
			
			StringTokenizer lineTok = new StringTokenizer(result, "\n");
			
			if(blocks==null)
				blocks = new CounterBlocks(TimeStep.hour);
	
			String date = null;
			List<String> time = new ArrayList<String>();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
			while(lineTok.hasMoreTokens()){
				String line = lineTok.nextToken();
					if(line.startsWith("Object")){
						StringTokenizer st = new StringTokenizer(line, " ");
							if(st.countTokens()>2){
								//Object
								st.nextToken();
								//Counter
								st.nextToken();
								while(st.hasMoreTokens()){
									String s = st.nextToken();
									time.add(s);
								}
							}
					} else if(line.startsWith("Date:")){
						//new block
						date = line.substring(6);
						time.clear();
					} else if(time.size()>0){
						StringTokenizer st = new StringTokenizer(line, " ");
						if(st.countTokens()>2){
							String object = st.nextToken();
							String counter = st.nextToken();
							int i = 0;
							while(st.hasMoreElements() && i<time.size()){
								try{
									Calendar sDate = Calendar.getInstance();
									sDate.setTime(sdf.parse(date + " " + time.get(i++)));
									sDate.add(Calendar.HOUR_OF_DAY, hourShift);
									Calendar eDate = (Calendar) sDate.clone();
									eDate.add(Calendar.MINUTE, 14);
									eDate.add(Calendar.SECOND, 59);
									String s = st.nextToken();
									if(!s.contains(",")){
										double value = Double.valueOf(s);
										blocks.addCounter(new Counter("", object, counter, value, sDate, eDate));
									} else
										blocks.addCounter(new Counter("", object, counter, 0.0, sDate, eDate));
								} catch(Exception e){
									e.printStackTrace();
								}
							}
								
						}
					}
			}
		}
		clearCacheFolder();
		return blocks;
		
	}
	
	private List<String> downloadFiles(Calendar startDate, Calendar endDate) throws CommunicationException, ConnectException{
		if(logger.isDebugEnabled())logger.debug("downloadFiles from time range: ");
		if(logger.isDebugEnabled())logger.debug(DateUtil.getDateString(startDate, "yyyy-MM-dd HH:mm:ss") + " - " + DateUtil.getDateString(endDate, "yyyy-MM-dd HH:mm:ss"));

		if(client == null)
			throw new CommunicationException("Client is null");
		
		if(!client.isConnected())
			try {
				client.connect();
			} catch (UnknownHostException e) {
				throw new ConnectException(e.getMessage(), e);
			}
		
		List<String> filesToGet = new ArrayList<String>();
		Set<String> filesInCalRange = getFilesInCalRage(startDate, endDate);
		if(logger.isDebugEnabled())logger.debug("filesInCalRange:" + filesInCalRange);
		
		for(FTPFile file:client.getDirectoryListing(ROP_FILES_LOCATION))
			if(containedInKey(filesInCalRange, file.getName()))
				filesToGet.add(file.getName());
		
		if(filesToGet.size()==0)
			throw new CommunicationException("No counter files to collect");
		
		for(String filename:filesToGet)
			client.downloadFile(ROP_FILES_LOCATION, filename, "files/" + client.getHost());
		
		File currentDir = new File (".");
		
		List<String> files = new ArrayList<String>();
		try {
			for(String filename:filesToGet)
				files.add(currentDir.getCanonicalPath() + "/files/" + client.getHost() + "/" + filename);
		} catch (IOException e) {
			throw new CommunicationException("Could not download files");
		}
		
		return files;
		
	}
	
	public void clearCacheFolder(){
		FileFilter filter = new FileFilter() {
		    public boolean accept(File file) {
		        return !file.isDirectory();
		    }
		};
		File folder = new File("./files/" + client.getHost() + "/");
		for(File file:folder.listFiles(filter))
			file.delete();
	}
	
	public long getS1GigabitCounter(Calendar startDate, Calendar endDate) throws ConnectException, CommunicationException{
		CounterBlocks blocks = getCounters("giga", "Octets", startDate, endDate);
		
		long value =  addValue("pmIfInOctetsLink1Hi", blocks);
			 value += addValue("pmIfInOctetsLink1Lo", blocks);
			 value += addValue("pmIfInOctetsLink2Hi", blocks);
			 value += addValue("pmIfInOctetsLink2Lo", blocks);
			 value += addValue("pmIfOutOctetsLink1Hi", blocks);
			 value += addValue("pmIfOutOctetsLink1Lo", blocks);
			 value += addValue("pmIfOutOctetsLink2Hi", blocks);
			 value += addValue("pmIfOutOctetsLink2Lo", blocks);
			
		return value;
	}
	
	public static long addValue(String counterName, CounterBlocks blocks){
		long counter = (long)blocks.accumulateCounterBlocks("pmIfInOctetsLink1Hi");
		if(counter>-1)
			return counter;
		
		return 0;
	}
	
	private boolean containedInKey(Set<String> set, String filename){
	    for (Iterator<String> it = set.iterator(); it.hasNext();)
	    	if(filename.contains(it.next()) && !filename.contains(".bin."))
	    		return true;
	    
	    return false;
	    		
	}
	
	/**
	 * Create a set of file using 20110710.1700-1715 pattern.
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private static Set<String> getFilesInCalRage(Calendar startDate, Calendar endDate){
		Set<String> files = new HashSet<String>();
		
		Calendar cal = (Calendar)startDate.clone();
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)/baseTime*baseTime);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		Calendar cal2 = (Calendar)startDate.clone();
		cal2.add(Calendar.MINUTE, baseTime);
		
		
		Calendar end = (Calendar)endDate.clone();
		if(cal2.get(Calendar.MINUTE)%baseTime > 0){
			int min = baseTime - cal2.get(Calendar.MINUTE)%baseTime;
			cal2.add(Calendar.MINUTE, min);
		}
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.MILLISECOND, 0);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmm-");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HHmm");
		
		while(cal2.before(endDate) || cal2.equals(endDate)){
			files.add(sdf.format(cal.getTime()) + sdf2.format(cal2.getTime()));
			cal.add(Calendar.MINUTE, baseTime);
			cal2.add(Calendar.MINUTE, baseTime);
		}
			
		
		return files;
	}
	
	
	public static void main(String[] args) throws ConnectException, CommunicationException{
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.SECOND, 0);

		if(startDate.get(Calendar.DST_OFFSET)>0){
			startDate.add(Calendar.HOUR, -3);
			endDate.add(Calendar.HOUR, -2);
		}else{
			startDate.add(Calendar.HOUR, -2);
			endDate.add(Calendar.HOUR, -1);
		}

		//RopCollector collector = new RopCollector("lienb0687", "lienb0687");
		RopCollector collector = new RopCollector("10.62.7.22", "kienb3012");
		//CounterBlocks counters  = collector.getS1GigabitCounter(startDate, endDate);
		
		//System.out.println("S1GigaBitCounter: " + collector.getS1GigabitCounter(startDate, endDate));
		List<CounterFilter> counterFilters = new ArrayList<CounterFilter>();
		counterFilters.add(new CounterFilter("DownlinkBaseBandPool", "pmCapacityAllocAttDlCe"));
//		counterFilters.add(new CounterFilter("cell", "pmErabEstabAttInit"));
//		counterFilters.add(new CounterFilter("cell", "pmPdcpVolDlDrb"));
//		counterFilters.add(new CounterFilter("cell", "pmPdcpVolUlDrb"));
//		counterFilters.add(new CounterFilter("cell", "pmErabEstabSuccInit"));
		CounterBlocks blocks = collector.getCounters(counterFilters, startDate, endDate, 2);
		
		System.out.println(blocks.accumulateCounterBlocks("pmPdcpVolDlDrb"));
		System.out.println(blocks.accumulateCounterBlocks("pmPdcpVolUlDrb"));

		System.out.println(blocks.accumulateCounterBlocks("pmErabEstabAttInit"));
		System.out.println(blocks.accumulateCounterBlocks("pmErabEstabSuccInit"));
		//System.out.println(collector.getS1GigabitCounter(startDate, endDate));

	}
	
	public static String readFile(String filename){
		DataInputStream in = null;
		StringBuffer sb = new StringBuffer();
		try{
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream(filename);
			  // Get the object of DataInputStream
			  in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  
			  while ((strLine = br.readLine()) != null)   {
				  sb.append(strLine);
				  sb.append("\n");
			  }
			  
		}catch (Exception e){/*Catch exception if any*/}
		finally {
			//Close the input stream
			if(in!=null)
				try {
					in.close();
				} catch (IOException e) {}
		}
		return sb.toString();
	}
}
