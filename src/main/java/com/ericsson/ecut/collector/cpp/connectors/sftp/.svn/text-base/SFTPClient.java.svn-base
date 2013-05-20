package com.ericsson.ecut.collector.cpp.connectors.sftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.ericsson.ecut.collector.exception.CommunicationException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class SFTPClient {
	
	private JSch jsch;
	private Session     session;
	private Channel     channel;
	private String username;
	private String password;
	private String host;
	private int port = 22;

	/**
	 * 
	 * @param host
	 * @throws JSchException
	 */
	public SFTPClient(String host) throws CommunicationException {
		this(host, null, null);
	}
	/**
	 * 
	 * @param host
	 * @param username
	 * @param password
	 * @throws JSchException
	 * @throws CommunicationException 
	 */
	public SFTPClient(String host, String username, String password) throws CommunicationException {
		this(host, username, password, 22);
	}
	
	public SFTPClient(String host, String username, String password, int port) throws CommunicationException{
		jsch = new JSch();
		this.host = host;
		this.username = username;
		this.password = password;
		this.port = port;
	}
	
	/**
	 * Used if the user credentials not provided in constructor
	 * @param username
	 * @param password
	 * @throws CommunicationException
	 */
	public void connect(String username, String password)
			throws CommunicationException {
		this.username = username;
		this.password = password;
		
		if(this.username ==null || this.password == null)
			throw new CommunicationException("Username or password not set");
		
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		

		try {
			session = jsch.getSession(username, this.host, port);
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
		} catch (JSchException e) {
			throw new CommunicationException(e.getMessage(), e);
		}
	}
	
	/**
	 * Used if the user credentials was provided in constructor
	 * @throws CommunicationException
	 */
	public void connect() throws CommunicationException {
		if(this.username ==null || this.password == null)
			throw new CommunicationException("Username or password not set");
		
		connect(this.username, this.password);
	}

	/**
	 * Set the port that the communication should use
	 * @param port
	 */
	public void setPort(int port){
		this.port = port;
	}
	
	/**
	 * Get port
	 * @return
	 */
	public int getPort(){
		return port;
	}

	/**
	 * Disconnect from server
	 */
	public void disconnect() {
		if(channel!=null && channel.isConnected())
			channel.disconnect();
		if(session!=null && session.isConnected())
			session.disconnect();
	}

	/**
	 * Check if client is connected
	 * @return
	 */
	public boolean isConnected() {
		if(session==null || channel==null)
			return false;
		
		return session.isConnected() && channel.isConnected();
	}

	/**
	 * Download file from server
	 * @param folder
	 * @param downloadFile
	 * @param saveFile
	 * @throws CommunicationException
	 */
	public void getFile(String folder, String downloadFile, String saveFile) throws CommunicationException{
		if(!isConnected())
			connect();
		try {
			ChannelSftp channelSftp = (ChannelSftp)channel;
			channelSftp.cd(folder);
			File file = new File(saveFile);
			channelSftp.get(downloadFile, new FileOutputStream(file));
		} catch (SftpException e) {
			throw new CommunicationException("Could not download file", e);
		} catch (FileNotFoundException e){
			throw new CommunicationException("Could not save local file", e);
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * Get steram to file
	 * @param folder
	 * @param file
	 * @return
	 * @throws CommunicationException
	 */
	public BufferedInputStream getFileStream(String folder, String file) throws CommunicationException{
		if(!isConnected())
			connect();
		try {
			ChannelSftp channelSftp = (ChannelSftp)channel;
			channelSftp.cd(folder);
			return new BufferedInputStream(channelSftp.get(file));
		} catch (SftpException e) {
			e.printStackTrace();
			throw new CommunicationException("Could not get stream for file", e);
		}
	}
	
	/**
	 * Get text file as string
	 * @param folder
	 * @param file
	 * @return
	 * @throws CommunicationException
	 */
	public String getTextFile(String folder, String file) throws CommunicationException{
		BufferedInputStream bis = null;
		StringBuilder sb = new StringBuilder();
		try {
		
			bis = getFileStream(folder, file);
			byte[] buffer = new byte[1024];
			int readCount;
			while( (readCount = bis.read(buffer))>0) {
				sb.append(new String(buffer, 0, readCount));
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommunicationException("Could not read file", e);
		} catch (CommunicationException e) {
			e.printStackTrace();
			throw new CommunicationException("Could not get stream for file", e);
		} finally{
			if(bis!=null)
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Get file listing from folder
	 * @param folder
	 * @return
	 * @throws CommunicationException
	 */
	public List<String> getFilesInFolder(String folder) throws CommunicationException{
		if(!isConnected())
			connect();
		try {
			ChannelSftp channelSftp = (ChannelSftp)channel;
			@SuppressWarnings("unchecked")
			Vector<LsEntry> f = channelSftp.ls(folder);
			List<String> files = new ArrayList<String>();
			for(int i=0;i<f.size();i++)
				if(!f.get(i).getAttrs().isDir())
					files.add(f.get(i).getFilename());
			
			return files;
		} catch (SftpException e) {
			e.printStackTrace();
			throw new CommunicationException("Could not get stream for file", e);
		}
	}
	
	/**
	 * Get subdirectory list from folder
	 * @param folder
	 * @return
	 * @throws CommunicationException
	 */
	public List<String> getSubdirectories(String folder) throws CommunicationException{
		if(!isConnected())
			connect();
		try {
			ChannelSftp channelSftp = (ChannelSftp)channel;
			@SuppressWarnings("unchecked")
			Vector<LsEntry> f = channelSftp.ls(folder);
			List<String> files = new ArrayList<String>();
			for(int i=0;i<f.size();i++)
				if(f.get(i).getAttrs().isDir())
					files.add(f.get(i).getFilename());
			
			return files;
		} catch (SftpException e) {
			e.printStackTrace();
			throw new CommunicationException("Could not get stream for file", e);
		}
	}
	
	/**
	 * Get directory listing from server (both files and folder)
	 * @param folder
	 * @return
	 * @throws CommunicationException
	 */
	@SuppressWarnings("unchecked")
	public List<LsEntry> getDirectoryListing(String folder) throws CommunicationException{
		if(!isConnected())
			connect();
		try {
			ChannelSftp channelSftp = (ChannelSftp)channel;
			return new ArrayList<LsEntry>(channelSftp.ls(folder));
		} catch (SftpException e) {
			e.printStackTrace();
			throw new CommunicationException("Could not get stream for file", e);
		}
	}
	
	
	public static void main(String[] args) {
		SFTPClient client = null;
		try {
			client = new SFTPClient("ecut.lmera.ericsson.se", "ecutadmin", "");
			System.out.println(client.getDirectoryListing("/home/ecutadmin"));
			System.out.println(client.getTextFile("/home/ecutadmin", ".Xauthority"));
		} catch (CommunicationException e) {
			e.printStackTrace();
		} finally{
			if(client!=null)
				client.disconnect();
		}
	}
}
