package com.ericsson.ecut.collector.cpp.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessThread implements Runnable, IThread {
	private String[] commands;
	private Exception error;
	private Process p;
	private boolean done;
	StringBuffer result;
	private boolean timeout;
	private BufferedReader stdInput;
	private BufferedReader errorInput;
	private StringBuffer errorOutput;

	public ProcessThread(String[] commands){
		this.commands = commands;
		timeout = false;
	}

	@Override
	public void run() {
		result = new StringBuffer();
		errorOutput = new StringBuffer();

		try {
			this.done = false;
			p = Runtime.getRuntime().exec(commands);

			stdInput = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
			
			errorInput = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			String s = null;
			
			if(timeout)
				return;
			
			while ((s = stdInput.readLine()) != null && !timeout) {
				//System.out.println(s);
				result.append(s + "\n");
			}
			
			while ((s = errorInput.readLine()) != null && !timeout) {
				//System.out.println(s);
				errorOutput.append(s + "\n");
			}

			if(timeout)
				return;
			
		}catch (Exception e) {
			if(e!=null && e.getMessage()!=null && e.getMessage().equals("Stream closed"))
				return;
			this.error = e;
			e.printStackTrace();
		}
		this.done = true;
	}

	public Exception getError() {
		return error;
	}
	
	public void stopThread(){
		timeout = true;
		if(p!=null){
			p.destroy();
		}
		
		if(stdInput!=null){
			try {
				stdInput.close();
			} catch (IOException e) {}
		}
	}

	public boolean isDone() {
		return done;
	}

	public String getOutput() {
		if(result!=null)
			return result.toString();
		return "";
	}
	
	public String getErrorOutput() {
		if(errorOutput!=null)
			return errorOutput.toString();
		return "";
	}
	
	public boolean containsError(){
		if (result.length()>=2)
				return false;
		return (error!=null || getErrorOutput().length()>2 && !getErrorOutput().equals("s\n"));
	}

	@Override
	public boolean hasError() {
		return containsError();
	}
	
}
