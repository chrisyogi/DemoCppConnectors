package com.ericsson.ecut.collector.cpp.log;

import java.io.InputStream;

import com.ericsson.ecut.collector.cpp.log.domain.LogFile;

public class ParserThread implements Runnable{

	private InputStream input;
	private boolean done;
	private boolean error;
	private LogFile logFile;
	
	public ParserThread(InputStream input){
		this.input = input;
	}
	
	@Override
	public void run() {
		done = false;
		error = false;
		if(input!=null)
			try {
				this.logFile = Parser.parse(input);
			} catch (Exception e) {
				error = true;
			}
		done = true;
	}
	
	public void stopThread(){
		
	}
	
	public boolean isDone(){
		return done;
	}
	
	public boolean isError(){
		return error;
	}
	
	public LogFile getOutput(){
		return logFile;
	}

}
