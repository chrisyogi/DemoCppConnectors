package com.ericsson.ecut.collector.cpp.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.net.ftp.FTPFile;

import com.ericsson.ecut.collector.cpp.connectors.ftp.CPPFTPClient;
import com.ericsson.ecut.collector.exception.CommunicationException;
import com.ericsson.ecut.collector.exception.ConnectException;

/**
 * Class that parses restart information from syslog. Should do the same
 * work as MOSHELL command lgsr
 * @author emarege
 *
 */
public class RestartChecker {
	
	private CPPFTPClient client;	

	/**
	 * Used if the syslog should be collected directly from the node
	 * @param client, CPPFTPClient to use
	 */
	public RestartChecker(CPPFTPClient client){
		this.client = client;
	}
	
	private InputStream getSyslog(String filename) throws CommunicationException, ConnectException, UnknownHostException{
		if(client == null)
			throw new CommunicationException("Not connected to node.");
		
		if(!client.isConnected())
			client.connect();
		
		//client.setFileType(FTP.ASCII_FILE_TYPE);
		
		return client.getFileStream("/c/logfiles/systemlog/", filename/*"00000syslog"*/);
	}
	
	/**
	 * Parses the syslog for "Restart rank" actions and compares these with the give date range
	 * @param startdate
	 * @param enddate
	 * @param syslog
	 * @return
	 * @throws CommunicationException
	 * @throws ConnectException 
	 * @throws UnknownHostException 
	 */
	public int getNumberOfRestarts(Calendar startdate, Calendar enddate) throws CommunicationException, ConnectException, UnknownHostException{
		if(client==null)
			throw new ConnectException("Client null");
		if(!client.isConnected())
			client.connect();
		
		List<FTPFile> files = new ArrayList<FTPFile>();
		List<FTPFile> filesToGet = new ArrayList<FTPFile>();
		
		for(FTPFile file:client.getDirectoryListing("/c/logfiles/systemlog/"))
			files.add(file);
		
		Collections.sort(files, new Comparator<FTPFile>() {
			@Override
			public int compare(FTPFile o1, FTPFile o2) {
				return o1.getTimestamp().compareTo(o2.getTimestamp());
			}
		});
		
//		boolean firstMatch = true;
//		FTPFile prevFile = null;
//		for(FTPFile file:files){
//			if(file.getTimestamp().compareTo(startdate)>=0 && file.getTimestamp().compareTo(enddate)<=0){
//				if(firstMatch){
//					if(file.getTimestamp().after(startdate) && prevFile!=null)
//						filesToGet.add(prevFile);
//				}
//				filesToGet.add(file);
//			} else if(prevFile!=null && prevFile.getTimestamp().before(startdate) && file.getTimestamp().after(startdate)){
//				filesToGet.add(prevFile);
//			}
//			
//			if(startdate.before(file.getTimestamp()) && enddate.before(file.getTimestamp()))
//				break;
//			prevFile = file;
//		}
		boolean found = false;
		for(FTPFile file:files){
			if(file.getTimestamp().compareTo(startdate)>=0)
				found = true;
			if(found)
				filesToGet.add(file);
			if(file.getTimestamp().after(enddate))
				break;
		}
		
		if(filesToGet.size()==0 && files.size()>0 && startdate.after(files.get(files.size()-1).getTimestamp()))
			filesToGet.add(files.get(files.size()-1));
			
		int nrOfRestart = 0;
		
		if(filesToGet.size()==0)
			return -1;
		
		for(FTPFile file:filesToGet){
			
			try{
				nrOfRestart+=getNumberOfRestarts(startdate, enddate, getSyslog(file.getName()));
			} catch(Exception e){
				e.printStackTrace();
			    if(nrOfRestart>0)
			    	return nrOfRestart;
			    return -1;
			}
		}
		return nrOfRestart;
	}
	
	/**
	 * Parses the syslog for "Restart rank" actions and compares these with the give date range
	 * @param startdate
	 * @param enddate
	 * @param syslog
	 * @return
	 * @throws CommunicationException
	 */
	private int getNumberOfRestarts(Calendar startdate, Calendar enddate, InputStream syslog) throws CommunicationException{

		int nr = 0;
		
		try {
			String prevLine = readLine(syslog);
			if(prevLine == null)
				return 0; 
			
			String line = null;
			//Read line for line a search for Restart rank
			while((line = readLine(syslog))!=null){
				if(line.contains("Restart rank")){
					int index;
					//Search previous line for time info
					if((index = prevLine.indexOf("time="))!=-1){
						int index2 = prevLine.indexOf(',', index);
						String time = null;
						if(index2>0)
							time = prevLine.substring(index + 5, index2);
						else
							time = prevLine.substring(index + 5);
						
						try{
							//Parse date (Jun 15 12:00:00 2011
							SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm:ss yyyy", Locale.US);
							Calendar cal = Calendar.getInstance();
							cal.setTime(sdf.parse(time));
							//Check if date is in the date range
							if(startdate.getTimeInMillis()<= cal.getTimeInMillis() && enddate.getTimeInMillis()>= cal.getTimeInMillis())
								nr++;
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
				prevLine = line;
			}
		} catch (IOException e) {
			throw new CommunicationException("Read error in syslog", e);
		} finally {
			try {
				syslog.close();
				if(!client.completePendignCommand()){
					client.disconnect();
				}
				
			} catch (IOException e) {
				client.disconnect();
				throw new CommunicationException("Read error in syslog", e);
			}
		}
		
		return nr;
	}
	
	private String readLine(InputStream in) throws IOException{
		StringBuilder line = new StringBuilder();
		int i;
		while ((i = in.read())!=-1){
			char c = (char) i; 
			if(c != '\r' && (c != '\n' ))
				line.append(c);
			else if((c == '\n' ))
				return line.toString();
		}
		if(line.length()>0)
			return line.toString();
		else
			return null;
	}
	
	
	public static void main(String[] args) throws CommunicationException, ConnectException, UnknownHostException{
		//InputStream syslog = CPPUtil.getFileStream("files/00000syslog");
		CPPFTPClient client = new CPPFTPClient("10.62.10.19", "kienb4009");
		RestartChecker checker = new RestartChecker(client);
		
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		startDate.set(2011, 7, 30, 13, 0, 0);
		endDate.set(2011, 7, 30, 15, 0, 0);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		
//		startDate.set(Calendar.MINUTE, 0);
//		startDate.set(Calendar.SECOND, 0);
//		
//		endDate.set(Calendar.MINUTE, 0);
//		endDate.set(Calendar.SECOND, 0);
//
//		if(startDate.get(Calendar.DST_OFFSET)>0){
//			startDate.add(Calendar.HOUR, -3);
//			endDate.add(Calendar.HOUR, -2);
//		}else{
//			startDate.add(Calendar.HOUR, -2);
//			endDate.add(Calendar.HOUR, -1);
//		}
		
		System.out.println(sdf.format(startDate.getTime()) + "-" + endDate.getTime());
		
		System.out.println(checker.getNumberOfRestarts(startDate, endDate));
	}
}
