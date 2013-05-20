package com.ericsson.ecut.collector.cpp.connectors.ssh;

import com.ericsson.ecut.collector.exception.CommunicationException;
import com.ericsson.ecut.collector.exception.ConnectException;

public class CPPSSHClient extends SSHClient{

	private String prompt = "$ ";
	
	public CPPSSHClient(String host) throws CommunicationException {
		super(host);
	}

	public CPPSSHClient(String host, String username, String password, int port)
			throws CommunicationException {
		super(host, username, password, port);
	}



	public CPPSSHClient(String host, String username, String password)
			throws CommunicationException {
		super(host, username, password);
	}



	public static void main(String[] args) {
		SSHClient client = null;
		try {
			client = new CPPSSHClient("10.74.120.20", "ecutadmin", "rbs");
			client.connect();
			System.out.println(client.send("readclock"));
			client.close();
		} catch (CommunicationException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			e.printStackTrace();
		} finally{
			if(client!=null)
				client.disconnect();
		}
	}
	
	public String getPrompt(){
		return prompt;
	}
	
	public String getNewLine(){
		return "\r\n";
	}

	@Override
	public void open(String user, String password, String prompt)
			throws ConnectException {
		this.prompt = prompt;
		open(username, password);
	}

	@Override
	public void open(String user, String password) throws ConnectException {
		open(user, password, prompt);
		
	}
}
