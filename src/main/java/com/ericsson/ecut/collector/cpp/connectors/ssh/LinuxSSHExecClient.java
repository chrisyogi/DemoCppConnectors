package com.ericsson.ecut.collector.cpp.connectors.ssh;
import com.ericsson.ecut.collector.exception.CommunicationException;
import com.ericsson.ecut.collector.exception.ConnectException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.*; 

public class LinuxSSHExecClient {
	
	
	private JSch jsch;
	private Session session;
	private Channel channel;
	protected String username;
	protected String password;
	protected String host;
	private int port = 22;
	
	//private InputStream in;
	//private OutputStream out;
	
	
	public LinuxSSHExecClient(String host, String username, String password, int port) 
			throws CommunicationException {		
		jsch = new JSch();
		this.host = host;
		this.username = username;
		this.password = password;
		this.port = port;
	}

	public LinuxSSHExecClient(String host, String username, String password)
			throws CommunicationException {		
		this(host, username, password, 22);
	}

	public void connectSession() throws ConnectException {
		
		if(this.username ==null || this.password == null)
			throw new ConnectException("host, Username or password not set");
		
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");

			// connect the session
			session = jsch.getSession(username, host, port);
			session.setConfig(config);
			session.setPassword(password);
			session.connect();			
		} catch (JSchException e) {
			throw new ConnectException(e.getMessage(), e);
		}	
	}
	

	public String sendCommand(String command) throws CommunicationException {  
		String response = "";
		try{  			
			//connect the channel
			channel=session.openChannel("exec");  
			((ChannelExec)channel).setCommand(command);  
			channel.setInputStream(null);  
			((ChannelExec)channel).setErrStream(System.err);  

			InputStream in=channel.getInputStream();
			OutputStream out = channel.getOutputStream();			
			channel.connect();  

			// read the response from the command
			byte[] tmp=new byte[2048];  
			while(true){  
				while(in.available()>0){  
					int i=in.read(tmp, 0, 2048);  
					if(i<0)break;  
					System.out.print(new String(tmp, 0, i));  
					response += new String(tmp, 0, i);
				}  
				if(channel.isClosed()){  
					//System.out.println("exit-status: "+channel.getExitStatus());  
					break;  
				}  
				try{Thread.sleep(1000);}catch(Exception ee){}  
			}  
			channel.disconnect();  
		}  
		catch(Exception e){  
			System.out.println(e);  
		}  		
		return response;
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
	 * Check if client is connected to the session
	 * @return
	 */
	public boolean isSessionConnected() {
		if(session==null)
			return false;
		
		return session.isConnected();
	}
	
	public static void main(String[] args) {
		LinuxSSHExecClient client = null;
		try {
			client = new LinuxSSHExecClient("mgwsim702.nala", "root", "shroot");
			client.connectSession();
			System.out.println(client.sendCommand("date +%Z"));
			//System.out.println(client.sendCommand("ls -al"));			
			client.disconnect();
		} catch (CommunicationException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			e.printStackTrace();
		} finally{
			if(client!=null)
				client.disconnect();
		}
	}
}
