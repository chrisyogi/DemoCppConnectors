package com.ericsson.ecut.collector.cpp.connectors.telnet.impl;

import java.net.UnknownHostException;

import com.ericsson.ecut.collector.exception.CommunicationException;
import com.ericsson.ecut.collector.exception.ConnectException;

public class CPPTelnetClient extends AbstractTelnetClient
{
    //==========================================================================
    // Private static constants.
    //==========================================================================
    private static String CPP_PROMPT = "$ ";
    
    //==========================================================================
    // Public static constants.
    //==========================================================================
    public CPPTelnetClient(String host, int port, long defaultTimeoutMillis) throws UnknownHostException
    {
        super(host, port, defaultTimeoutMillis);
    }
    
    @Override
    public String send(String command, long timeoutMillis)
        throws CommunicationException
    {
        String printout = send(command, CPP_PROMPT, timeoutMillis);
        
        // Remove first line (command echo).
        int startIndex = printout.indexOf("\r\n") + 2;
        if(startIndex<printout.length())
        	printout = printout.substring(startIndex);
        
        
        return printout.trim();
    }

	@Override
	public void open(String user, String password) throws ConnectException {
		open(user, password, CPP_PROMPT, "username: ", "password: ", "Welcome");
	}
	
	public static void main(String args[]){
		try {
			CPPTelnetClient client = new CPPTelnetClient("10.74.120.20", 23, 60*1000);
			client.open("mgw-collector", "rbs");
			//String printout1 = client.send("lhsh 000300 upload_stats");
			//String printout2 = client.send("lhsh 000500 upload_stats");
			//System.out.println(printout1);
			//System.out.println(printout2);
			System.out.println(client.send("readclock"));
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void open(String user, String password, String prompt)
			throws ConnectException {
		CPP_PROMPT = prompt;
	}
}
