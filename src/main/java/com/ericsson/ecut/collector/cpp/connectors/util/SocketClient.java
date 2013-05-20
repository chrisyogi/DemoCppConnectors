package com.ericsson.ecut.collector.cpp.connectors.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeoutException;


public class SocketClient
{
	//==========================================================================
	// Private variables.
	//==========================================================================
	private InetSocketAddress serverAddress;
	private String newLine;
	
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	
	//==========================================================================
	// Public methods.
	//==========================================================================
	public SocketClient(InetSocketAddress serverAddress, String newLine)
	{
		this.serverAddress = serverAddress;
		this.newLine = newLine;
		
		socket = null;
		writer = null;
	}
	
	public void open()
		throws IOException
	{
		socket = new Socket();
		socket.connect(serverAddress);
		
		writer = new PrintWriter(socket.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public void close()
	{
	    if (writer != null)
	    {
	        writer.close();
	        writer = null;
	    }
		
	    if (reader != null)
	    {
	        try { reader.close(); } catch (IOException e) { }
	        reader = null;
	    }
		
	    if (socket != null)
	    {
	        try { socket.close(); } catch (IOException e) { }
	        socket = null;
	    }
	}
	
	public String send(String cmd, String[] prompts, long timeoutMillis)
		throws IOException, TimeoutException
	{
		// Remove old printouts (if any).
		clearPrintouts();
		
		// Send command.
		writer.print(cmd + newLine);
		writer.flush();
		
		// Read printout.
		String printoutWithPrompt = readToPrompt(prompts, timeoutMillis);
		
		// Remove last line (the prompt).
		String printoutWithoutPrompt = null;
		if(printoutWithPrompt.lastIndexOf(newLine)>-1)
			printoutWithoutPrompt = printoutWithPrompt.substring(0, printoutWithPrompt.lastIndexOf(newLine));
		
		return printoutWithoutPrompt;
	}
	
	public String readToPrompt(String prompt, long timeoutMillis)
	    throws IOException, TimeoutException
	{
	    String[] prompts = { prompt };
	    return readToPrompt(prompts, timeoutMillis);
	}
	
	public String readToPrompt(String[] prompts, long timeoutMillis)
		throws IOException, TimeoutException
	{
		StringBuilder printout = new StringBuilder();
		
		// Read printout to next prompt.
		long startTimeMillis = System.currentTimeMillis();
		char[] buffer = new char[2048];
		while (true)
		{
			if (reader.ready())
			{
				// Read input.
				int numCharsRead = reader.read(buffer);
				if (numCharsRead == -1)
				{
					// The server socket has been closed.
					String msg = String.format("Could not read printout: Server socket was closed");
					throw new IOException(msg);
				}
				
				// Append to printout.
				printout.append(buffer, 0, numCharsRead);

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
	
	public void clearPrintouts()
		throws IOException
	{
		char[] buffer = new char[1024];
		while (reader.ready())
		{
			reader.read(buffer);
		}
	}
	
	private boolean endsWithPrompt(StringBuilder printout, String[] prompts)
	{
	    for (String prompt : prompts)
        {
            if (printout.toString().endsWith(prompt) || endsWithPrompt(printout, prompt))
            {
                return true;
            }
        }
	    
	    return false;
	}
	
	private boolean endsWithPrompt(StringBuilder printout, String prompt){
		int lastNewLine = printout.lastIndexOf(newLine);
		if(lastNewLine>0){
			if(printout.substring(0, lastNewLine).endsWith(prompt))
				return true;
		}
		return false;
	}
}
