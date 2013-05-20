package com.ericsson.ecut.collector.cpp.connectors.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import com.ericsson.ecut.collector.cpp.connectors.IClient;
import com.ericsson.ecut.collector.exception.CommunicationException;
import com.ericsson.ecut.collector.exception.ConnectException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public abstract class SSHClient implements IClient{
	
	private JSch jsch;
	private Session     session;
	private Channel     channel;
	protected String username;
	protected String password;
	protected String host;
	private int port = 22;
	
	private BufferedReader in;
	private OutputStream out;

	private Pattern alphaNumeric = Pattern.compile("([^a-zA-z0-9])");
	private String lastCommand;
	
	/**
	 * 
	 * @param host
	 * @throws JSchException
	 */
	public SSHClient(String host) throws CommunicationException {
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
	public SSHClient(String host, String username, String password) throws CommunicationException {
		this(host, username, password, 22);
	}
	
	public SSHClient(String host, String username, String password, int port) throws CommunicationException{
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
			throws ConnectException {
		this.username = username;
		this.password = password;
		
		if(this.username ==null || this.password == null)
			throw new ConnectException("Username or password not set");
		
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
		

		try {
			session = jsch.getSession(username, this.host, port);
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			channel = session.openChannel("shell");
			// set the channel pty type to VT100 to avoid terminator emulator problem
			((ChannelShell)channel).setPtyType("VT100");
			in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
			out = channel.getOutputStream();
			channel.connect();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		} catch (JSchException e) {
			throw new ConnectException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ConnectException(e.getMessage(), e);
		}
	}
	
	/**
	 * Used if the user credentials was provided in constructor
	 * @throws CommunicationException
	 */
	public void connect() throws ConnectException {
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

	@Override
	public String send(String command) throws CommunicationException {
		if(!isConnected())
			try {
				connect();
			} catch (ConnectException e) {
				throw new CommunicationException("Not connected. Tried to connect but failed", e);
			}
		try {
			clearPrintouts();
			out.write(command.getBytes());
			out.write(getNewLine().getBytes());
			out.flush();
			setLastCommand(new String(command));
			
			String result = readToPrompt(new String[]{getPrompt()}, 30000);

			if(result.startsWith(command))
				result = result.substring(command.length());
			if(result.startsWith(getNewLine()))
				result = result.substring(getNewLine().length());

			result = result.substring(0, result.length()-getPrompt().length());
			if(result.lastIndexOf(getNewLine())>-1)
				result = result.substring(0, result.lastIndexOf(getNewLine()));
			
			return result.replaceAll(escape(getPrompt()), "").trim();
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommunicationException(e);
		}
	}
	@Override
	public String send(String command, long timeoutMillis)
			throws CommunicationException {
		return send(command);
	}
	@Override
	public void close() {
		disconnect();
	}

	
	private String escape(String subjectString){
		return alphaNumeric.matcher(subjectString).replaceAll("\\\\$1");
	}
	public String getLastCommand() {
		return lastCommand;
	}
	public void setLastCommand(String lastCommand) {
		this.lastCommand = lastCommand;
	}
	
	private String readToPrompt(String[] prompts, long timeoutMillis)
			throws IOException, TimeoutException
		{
			StringBuilder printout = new StringBuilder();
			
			// Read printout to next prompt.
			long startTimeMillis = System.currentTimeMillis();
			char[] buffer = new char[2048];
			while (true)
			{
				if (in.ready())
				{
					// Read input.
					int numCharsRead = in.read(buffer);
					if (numCharsRead == -1)
					{
						// The server socket has been closed.
						String msg = String.format("Could not read printout: Server socket was closed");
						throw new IOException(msg);
					}
					
					// Append to printout. remove last empty space
					printout.append(buffer, 0, numCharsRead-1);
					if (endsWithPrompt(printout, prompts))
					{
					    // We have found a prompt.
					    break;
					}
				}

				if (System.currentTimeMillis() > startTimeMillis + timeoutMillis)
				{
					// Command timed out.
					String msg = String.format("Timed out while waiting for prompt");
					throw new TimeoutException(msg + " data: |" + printout + "|");
				}
				
				if (System.currentTimeMillis() > (startTimeMillis + 25000)){
					//System.out.println();
				}
				
				try
				{
				    Thread.sleep(20);
				}
				catch (InterruptedException e)
				{
				    Thread.currentThread().interrupt();
				}
			}
			
			return printout.toString();
		}
	
	private boolean endsWithPrompt(StringBuilder printout, String[] prompts){
	    for (String prompt : prompts){
            if (printout.toString().endsWith(prompt) || endsWithPrompt(printout, prompt)){
                return true;
            }
        }
	    
	    return false;
	}
	
	private boolean endsWithPrompt(StringBuilder printout, String prompt){
		int lastNewLine = printout.lastIndexOf(getNewLine());
		if(lastNewLine>0){
			if(printout.substring(0, lastNewLine).endsWith(prompt))
				return true;
		}
		return false;
	}
	
	public void clearPrintouts() throws IOException {
		char[] buffer = new char[1024];
		while (in.ready()) {
			in.read(buffer);
		}
	}
	
	public abstract String getPrompt();
	public abstract String getNewLine();
}
