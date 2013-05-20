package com.ericsson.ecut.collector.cpp.log;

import java.io.InputStream;

import com.ericsson.ecut.collector.cpp.connectors.exception.TimeoutException;

import com.ericsson.ecut.collector.cpp.log.domain.LogFile;

public class ParserWrapper {
	public static LogFile executeParser(InputStream input, int timeoutValue) throws TimeoutException{
		ParserThread task = new ParserThread(input);
		Thread thread = new Thread(task);
		
		int nrWaits = 0;
		boolean timeout = false;
		thread.start();
		while(!task.isDone() && !timeout){
			if(++nrWaits*100>timeoutValue)
				timeout = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				thread.interrupt();
				task.stopThread();
				throw new TimeoutException("Timeout executing parsing log file");
			}
		}
		if(timeout){
			task.stopThread();
			thread.interrupt();
			throw new TimeoutException("Timeout executing parsing log file");
		}
	
		return task.getOutput();
	}
}
