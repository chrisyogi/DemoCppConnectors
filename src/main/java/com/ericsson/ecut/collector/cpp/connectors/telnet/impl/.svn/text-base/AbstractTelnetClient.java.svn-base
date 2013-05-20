package com.ericsson.ecut.collector.cpp.connectors.telnet.impl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.ericsson.ecut.collector.cpp.connectors.IClient;
import com.ericsson.ecut.collector.cpp.connectors.util.SocketClient;
import com.ericsson.ecut.collector.exception.CommunicationException;
import com.ericsson.ecut.collector.exception.ConnectException;

abstract class AbstractTelnetClient implements IClient
{
    //==========================================================================
    // Protected static constants.
    //==========================================================================
    
    
    //==========================================================================
    // Protected variables.
    //==========================================================================

    //==========================================================================
    // Private variables.
    //==========================================================================
    private final long defaultTimeoutMillis;
    private final SocketClient socketClient;
    
    protected String username;
    protected String password;


    //==========================================================================
    // Public methods.
    //==========================================================================
    public AbstractTelnetClient(String hostname, int port, long defaultTimeoutMillis) throws UnknownHostException
    {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(hostname), port);
        socketClient = new SocketClient(socketAddress, "\r\n");
        
        this.defaultTimeoutMillis = defaultTimeoutMillis;
    }
    
    //--------------------------------------------------------------------------
    // AxeClient interface implementations.
    //--------------------------------------------------------------------------
    public abstract void open(String user, String password) throws ConnectException;
    
    public void open(String user, String password, String prompt, String usernamePrompt, String passwordPrompt) throws ConnectException{
    	open(user, password, prompt, usernamePrompt, passwordPrompt, null);
    }

    
    public void open(String user, String password, String prompt, String usernamePrompt, String passwordPrompt, String loginOkText)
        throws ConnectException
    {
        try
        {
            socketClient.open();

            // Login to NODE.
            socketClient.readToPrompt(usernamePrompt, defaultTimeoutMillis);
            send(user, passwordPrompt);
            String result = send(password, new String[]{prompt, "Login failed."});
            if(loginOkText!=null && !result.contains(loginOkText))
            	throw new CommunicationException("Could not login to node");            
        }
        catch (Exception e)
        {
            close();
            throw new ConnectException("Could not connect to h",e);
        }
    }
    
    @Override
    public void close()
    {       
        socketClient.close();
    }
    
    @Override
    public void disconnect()
    {       
        close();
    }
    
    @Override
    public String send(final String command)
        throws CommunicationException
    {
        return send(command, defaultTimeoutMillis);
    }
    
    @Override
    public abstract String send(String command, long timeoutMillis)
        throws CommunicationException;
    
    
    //==========================================================================
    // Protected methods.
    //==========================================================================
    protected void negotiateIac()
    	throws CommunicationException
    { }
    
    protected void enterTerminalMode()
        throws CommunicationException
    { }
    
    protected final String send(String cmd, String prompt)
        throws CommunicationException
    {
        return send(cmd, prompt, defaultTimeoutMillis);
    }
    
    protected final String send(String cmd, String[] prompts)
        throws CommunicationException
    {
        return send(cmd, prompts, defaultTimeoutMillis);
    }
    
    protected final String send(String cmd, String prompt, long timeoutMillis)
        throws CommunicationException
    {
        String[] prompts = { prompt };
        return send(cmd, prompts, timeoutMillis);
        
    }
    
    protected final String send(String cmd, String[] prompts, long timeoutMillis)
        throws CommunicationException
    {
        try
        {
            return socketClient.send(cmd, prompts, timeoutMillis);
        }
        catch (Exception e)
        {
            throw new CommunicationException(e);
        }
    }
    
	@Override
	public void connect(String user, String password) throws ConnectException {
		open(user, password);
	}
}

