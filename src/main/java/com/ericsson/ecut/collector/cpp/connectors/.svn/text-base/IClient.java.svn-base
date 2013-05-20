package com.ericsson.ecut.collector.cpp.connectors;

import com.ericsson.ecut.collector.exception.CommunicationException;
import com.ericsson.ecut.collector.exception.ConnectException;

public interface IClient
{
	/**
	 * Login to node.
	 */
	void open(String user, String password, String prompt) throws ConnectException;
	void open(String user, String password) throws ConnectException;
	void connect(String user, String password) throws ConnectException;
	/**
	 * Send a command and return the printout. If confirmation or terminal
	 * release is necessary this will be performed automatically.
	 * 
	 * @throws OmAxeException
	 */
	public String send(String command) throws CommunicationException;
	
	public String send(String command, long timeoutMillis) throws CommunicationException;
	
	/**
	 * Close connection.
	 */
	void close();
	void disconnect();
}
