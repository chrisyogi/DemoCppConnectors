package com.ericsson.ecut.collector.cpp.connectors.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.ericsson.ecut.collector.cpp.connectors.exception.TimeoutException;
import com.ericsson.ecut.collector.cpp.log.Parser;
import com.ericsson.ecut.collector.cpp.log.ParserWrapper;
import com.ericsson.ecut.collector.cpp.log.domain.LogFile;
import com.ericsson.ecut.collector.cpp.log.domain.LogFiles;
import com.ericsson.ecut.collector.cpp.log.domain.LogRecordBlock;
import com.ericsson.ecut.collector.cpp.util.DateUtil;
import com.ericsson.ecut.collector.exception.CommunicationException;
import com.ericsson.ecut.collector.exception.ConnectException;
import com.ericsson.ecut.collector.sample.domain.TimeStep;

public class CPPFTPClient {

	private Log logger = LogFactory.getLog("CPPFTPClient");
	private FTPClient f;
	private String password;
	private String host;
	
	private static final String STD_USERNAME = "moshell-collector";
	

	public CPPFTPClient(String host, String password){
		this.host = host;
		this.password = password;
	}
	
	public void connect() throws ConnectException, UnknownHostException{
		if(logger.isDebugEnabled())logger.debug("Connecting to " + host);
	    if(f!=null && f.isConnected())
			try {
				f.disconnect();
			} catch (IOException e) {}
	    	
		f = new FTPClient();
		f.setConnectTimeout(60 * 1000);
		f.setDataTimeout(60 * 1000);
		f.setDefaultTimeout(30 * 1000);
		//f.setControlKeepAliveTimeout(3);
	    try {
	    	f.connect(host);
			f.setSoTimeout(40 * 1000);
			int reply = f.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)){
				f.disconnect();
				throw new ConnectException("Could not connect to host " + host);
	        }
	    } catch(UnknownHostException e){
	    	throw e;
	    } catch(SocketTimeoutException e){
	    	throw new UnknownHostException("Timeout connecting to " + host);
		} catch (IOException e) {
			throw new ConnectException("Could not connect to " + host, e);
		}
		
		try {
			if(!f.login(STD_USERNAME, password)){
				try {
					f.disconnect();
				} catch (IOException e1) {}
				throw new ConnectException("Could not connect to host " + host + ". Could be wrong password used " + password);
			}
		} catch (IOException e) {
			try {
				f.disconnect();
			} catch (IOException e1) {}
			throw new ConnectException("Could not connect to host " + host + ". Could be wrong password used " + password, e);
		}
		if(logger.isDebugEnabled())logger.debug("Connected to host " + host);
	}
	
	public boolean isConnected(){
		if(f!=null)
			return f.isConnected();
		return false;
	}
	
	public void disconnect() throws CommunicationException{
		if(logger.isDebugEnabled())logger.debug("Disconnecting from host " + host);
		if(f!=null && f.isConnected())
			try {
				f.disconnect();
			} catch (IOException e) {
				throw new CommunicationException("Could not disconnect from host " + host, e);
			}
	}
	
	public FTPFile[] getDirectoryListing(String directory) throws CommunicationException{
		if(logger.isDebugEnabled())logger.debug("Listing folder " + directory);
		if(f==null || (f!=null  && !f.isConnected()))
			throw new CommunicationException("Not connected to host " + host);
		
		try{
			return f.listFiles(directory);
		}catch(Exception e){
			throw new CommunicationException("Could not list files", e);
		}
	}
	
	public String getTextFile(String folder, String fileName) throws CommunicationException{
		if(f==null || (f!=null  && !f.isConnected()))
			throw new CommunicationException("Not connected to host" + host);
		
		if(!folder.endsWith("/"))
			folder+="/";
		
		if(logger.isDebugEnabled())logger.debug("Downloading textfile " + folder + fileName);
		
		InputStream istream = null;
		try {
			StringBuilder sb = new StringBuilder();
			istream =  new BufferedInputStream(f.retrieveFileStream(folder + fileName), 1024);
			
			//int b;
			byte[] buffer = new byte[1024];
			int bytes;
			//while((b = istream.read()) !=-1){
			while ((bytes = istream.read(buffer)) != -1){
				if (bytes == 0)
                {
                    bytes = istream.read();
                    if (bytes < 0)
                        break;
                    sb.append(bytes);
     
                    continue;
                }
				for(int i=0;i<bytes;i++)
					sb.append((char)buffer[i]);
				//sb.append((char) b);
			}
			return sb.toString();
		} catch (IOException e) {
			throw new CommunicationException("Could not download " + folder + fileName, e);
		}finally{
			if(istream!=null){
				try {
					istream.close();
				} catch (IOException e) {}
				try {
					if(!f.completePendingCommand()) {
						 throw new CommunicationException("Could not download " + folder + fileName);
					 }
				} catch (IOException e) {}
			}
		}
	}
	
	public InputStream getFileStream(String folder, String fileName) throws CommunicationException{
		try {
			if(logger.isDebugEnabled())logger.debug("Getting stream for " + folder + fileName);
			return f.retrieveFileStream(folder + fileName);
		} catch (IOException e) {
			throw new CommunicationException("Could not get stream for " + folder + fileName, e);
		}
	}
		
	public boolean completePendignCommand() throws CommunicationException{
		 try {
			if(f!=null && f.isConnected())
				return f.completePendingCommand();
			
			throw new CommunicationException("Not connected to host" + host);
		} catch (IOException e) {
			 throw new CommunicationException("Error executing command", e);
		}
	}
	
	public boolean logout() throws CommunicationException  {
		if(logger.isDebugEnabled())logger.debug("Disconnecting from host " + host);
		if(f!=null && f.isConnected()){
			try {
				return f.logout();
			} catch (IOException e) {
				throw new CommunicationException("Could not disconnect from host " + host, e);
			}
		}
		return false;
		
	}
	
	public static void main(String args[]){
		//CPPFTPClient ftp = new CPPFTPClient("lienb0687", "lienb0687");
		CPPFTPClient ftp = new CPPFTPClient("mgw15", "mgw15");
		try {
			ftp.connect();
			
			writeFile("files/CORBA_AUDITTRAIL_LOG3.xml", ftp.getTextFile("/c/logfiles/audit_trail/", "CORBA_AUDITTRAIL_LOG.xml"));
			writeFile("files/SHELL_AUDITTRAIL_LOG3.xml", ftp.getTextFile("/c/logfiles/audit_trail/", "SHELL_AUDITTRAIL_LOG.xml"));
			//writeFile("files/AUDIT_TRAIL_LOG3.xml", ftp.getTextFile("/c/logfiles/audit_trail/", "AUDIT_TRAIL_LOG.xml"));
			//writeFile("files/CELLO_AUDITTRAIL_LOG3.xml", ftp.getTextFile("/c/logfiles/audit_trail/", "CELLO_AUDITTRAIL_LOG.xml"));
			//writeFile("files/00000syslog", ftp.getTextFile("/c/logfiles/systemlog", "00000syslog"));
			
			Calendar startDate = Calendar.getInstance();
			Calendar endDate = Calendar.getInstance();
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);
			
			endDate.set(Calendar.MINUTE, 0);
			endDate.set(Calendar.SECOND, 0);
	
//			if(startDate.get(Calendar.DST_OFFSET)>0){
//				startDate.add(Calendar.HOUR, -3);
//				endDate.add(Calendar.HOUR, -2);
//			}else{
//				startDate.add(Calendar.HOUR, -2);
//				endDate.add(Calendar.HOUR, -1);
//			}
			
			// Unknown Wed Sep 21 2011, 12:15:15
			startDate.set(2011, 9, 14, 13, 21);
			endDate.set(2011, 9, 14, 14, 00);
					
			//ftp.getNumberOfRestarts(startDate, endDate);
			//LogFile[] logFiles = ftp.getLogfiles();
			
			LogFile[] logFiles = new LogFile[1];
			
			try {
				InputStream in = new BufferedInputStream(new FileInputStream("files/CORBA_AUDITTRAIL_LOG3.xml"));
				logFiles[0] = Parser.parse(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			int timeOffset = DateUtil.getUTCOffset();
			
//			Calendar startDate = Calendar.getInstance();
//			startDate.set(Calendar.HOUR_OF_DAY, 0);
//			startDate.set(Calendar.MINUTE, 0);
//			startDate.set(Calendar.SECOND, 0);
//			startDate.set(Calendar.MILLISECOND, 0);
//			startDate.set(Calendar.MONTH, 6);
//			startDate.set(Calendar.DATE, 3);
//			Calendar endDate = Calendar.getInstance();
//			endDate.set(2011, 9, 20, 23, 59, 59);
						
			for(LogFile logFile:logFiles){
				System.out.println("--------");
				System.out.println("Logfile: " + logFile.getLogCreated().getLogName());
				for(LogRecordBlock block:logFile.getLogFileBlocks(TimeStep.hour, startDate, endDate, new String[]{"moshell-collector"}, new String[]{"telnet", "tty", "ssh"}, new String[]{"SET", "ACTION", "DELETE", "CREATE", "restart"}, timeOffset))
					System.out.println(block.getYear() + "-" + block.getMonth() + "-" + block.getDay() + " " + block.getHour() + ": " + block.getRecords().size() + " ");
			}
			
			LogFiles logFilesContainer = new LogFiles(logFiles);
			//Lagg till urval!
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println(sdf.format(logFilesContainer.getFirstLogDate(timeOffset).getTime()));
			for(LogRecordBlock block:logFilesContainer.getLogFileBlocks(TimeStep.hour, startDate, endDate, new String[]{"moshell-collector"}, new String[]{"telnet", "tty", "ssh"}, new String[]{"SET", "ACTION", "DELETE", "CREATE", "restart"}, timeOffset)){
				System.out.println(block.getYear() + "-" + block.getMonth() + "-" + block.getDay() + " " + block.getHour() + " total blocks: " + block.getRecords().size() + " settings: " + block.getSettingRecords().size() + " shell: " + block.getShellRecords().size());
			}
		}catch (CommunicationException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}finally{
			if(ftp!=null && ftp.isConnected())
				try {
					ftp.disconnect();
				} catch (CommunicationException e) {
					e.printStackTrace();
				}
		}
	}
	
	public LogFiles getLogFiles() throws CommunicationException{
		LogFile[] logFiles = getLogfiles();
		return new LogFiles(logFiles);
	}
	
	private static void writeFile(String fileName, String output){
		try{
			// Create file 
			FileWriter fstream = new FileWriter(fileName);
		  	BufferedWriter out = new BufferedWriter(fstream);
		  	out.write(output);
		  	//Close the output stream
		  	out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	/**
	 * Parses the syslog for "Restart rank" actions and compares these with the give date range
	 * @param startdate
	 * @param enddate
	 * @param syslog
	 * @return
	 * @throws CommunicationException
	 * @throws ConnectException 
	 */
	public int getNumberOfRestarts(Calendar startdate, Calendar enddate) throws CommunicationException, ConnectException{
		if(f==null)
			throw new ConnectException("Client null");
		if(!isConnected())
			try {
				connect();
			} catch (UnknownHostException e) {
				throw new ConnectException(e.getMessage(), e);
			}
		
		List<FTPFile> files = new ArrayList<FTPFile>();
		List<FTPFile> filesToGet = new ArrayList<FTPFile>();
		
		for(FTPFile file:getDirectoryListing("/c/logfiles/systemlog/"))
			files.add(file);
		
		Collections.sort(files, new Comparator<FTPFile>() {
			@Override
			public int compare(FTPFile o1, FTPFile o2) {
				return o1.getTimestamp().compareTo(o2.getTimestamp());
			}
		});

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
		
		//InputStream istream = null;
		for(FTPFile file:filesToGet){			
			try{
				nrOfRestart+=getNumberOfRestarts(startdate, enddate, /*istream =  */ getFileStream("/c/logfiles/systemlog/", file.getName()));
			} catch(Exception e){
				e.printStackTrace();
			    if(nrOfRestart>0)
			    	return nrOfRestart;
			    return -1;
			}
			finally{
//				try {
//					istream.close();
//				} catch (IOException e) {}
//				completePendignCommand();
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
			//boolean done = false;
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
							//else if(cal.getTimeInMillis()>= enddate.getTimeInMillis())
								//done=true;
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
				if(!completePendignCommand()){
					disconnect();
				}
				
			} catch (IOException e) {
				disconnect();
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
	
	public LogFile[] getLogfiles() throws CommunicationException{
		return getLogfiles(-1);
	}
	
	public LogFile[] getLogfiles(int timeout) throws CommunicationException{
		if(f==null || (f!=null  && !f.isConnected()))
			throw new CommunicationException("Not connected to host" + host);
		Set<String> availableFiles = new HashSet<String>();
		try {
			for(FTPFile file:getDirectoryListing("/c/logfiles/audit_trail/"))
				for(String logFile:LogFiles.getLogFileTypes().keySet()){
					if(file.getName().equalsIgnoreCase(logFile + ".xml"))
						availableFiles.add(logFile + ".xml");
				}
		} catch (CommunicationException e) {
			throw new CommunicationException("Could not list files on node.", e);	
		}
		
		List<LogFile> logFiles = new ArrayList<LogFile>(availableFiles.size());
		InputStream istream = null;
		boolean error = false;
		for(String logFile:availableFiles){
			
			try {
				istream = getFileStream("/c/logfiles/audit_trail/", logFile);
				LogFile log = ParserWrapper.executeParser(istream, 120*1000);
				logFiles.add(log);
			} catch (TimeoutException e) {
				error = true;
			} catch (Exception e){
				System.out.println("Could not parse log file " +logFile + " for " + host);
				error = true;
			}finally{
				try {
					istream.close();
				} catch (Exception e) {}
				if(error){
					disconnect();
					try {
						connect();
					} catch (Exception e) {
						throw new CommunicationException("Error reading logfiles");
					}
				}else
					try{
						completePendignCommand();
					} catch(Exception e){
						logger.error("Could not send completePendingCommand() for " + host);
						disconnect();
						try {
							connect();
						} catch (Exception e1) {
							logger.error("Could not reconnect to " + host);
						}
					}
			}
		}
		
		return logFiles.toArray(new LogFile[0]);
	}
	
	public void downloadFile(String folder, String filename, String outputFolder) throws CommunicationException{
		downloadFile(folder, filename, outputFolder, filename);
	}
	
	public void downloadFile(String folder, String filename, String outputFolder, String outputFilename) throws CommunicationException{
		FileOutputStream fos = null;
		
		if(f==null || (f!=null  && !f.isConnected()))
			throw new CommunicationException("Not connected to host" + host);

        try {
           
            //
            // The remote filename to be downloaded.
            //
        	if(!folder.endsWith("/"))
        		folder+= "/";
        	
        	if(outputFolder.contains("/")){
        		if(!outputFolder.endsWith("/"))
        			outputFolder+= "/";
        	}else if(outputFolder.contains("\\"))
        		if(!outputFolder.endsWith("\\"))
        			outputFolder+= "\\";
        	
        	
            try{
            File file = new File(outputFolder);
            if(!file.exists())
            	file.mkdirs();
            } catch (Exception e){
            	throw new CommunicationException("Could not create outputfolder", e);
            }

            fos = new FileOutputStream(outputFolder + outputFilename);
            f.setFileType(FTP.BINARY_FILE_TYPE);
            //
            // Download file from FTP server
            //
            if(!f.retrieveFile(folder + filename, fos)){
            	throw new CommunicationException("Error downloading file");
            }
        } catch (IOException e) {
            e.printStackTrace();
			throw new CommunicationException("Could not download file", e);
        } finally {
        	try {
				fos.close();
			} catch (IOException e) {}
        }
		
	}
	
	public boolean setFileType(int fileType) throws CommunicationException {
        try {
			return f.setFileType(fileType);
		} catch (IOException e) {
			throw new CommunicationException(e.getMessage(), e);
		}

	}
	
	public String getHost() {
		return host;
	}
}
