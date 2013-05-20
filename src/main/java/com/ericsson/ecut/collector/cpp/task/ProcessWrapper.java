package com.ericsson.ecut.collector.cpp.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ericsson.ecut.collector.cpp.connectors.exception.TimeoutException;
import com.ericsson.ecut.collector.exception.ConnectException;

public class ProcessWrapper {
	private static Log logger = LogFactory.getLog("ProcessWrapper");

	public static String executeMoshell(String[] commands) throws Exception{
		ProcessThread task = new ProcessThread(commands);
		Thread thread = new Thread(task);
		
		if(commands!=null){
			StringBuilder sb = new StringBuilder();
			for(String s:commands){
				sb.append(s);
				sb.append(" ");
			}
				
			logger.debug("Executing : " + sb.toString());
		}
		
		int nrWaits = 0;
		boolean timeout = false;
		thread.start();
		while(!task.isDone() && !timeout){
			if(++nrWaits>400)
				timeout = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				thread.interrupt();
				task.stopThread();
				throw new TimeoutException("InterruptedException. Timeout", e);
			}
		}
		if(timeout){
			task.stopThread();
			thread.interrupt();
			throw new TimeoutException("Timeout");
		}else if(task.getError()!=null){
			task.getError().printStackTrace();
			//logger.error("Cound not execute moshell for resource " + resource.getName() + " using dnsname " + dnsname);
			throw task.getError();
		}else if(task.getOutput()!=null && task.getOutput().contains("Unable to connect to ")){
			throw new ConnectException("Could not connect to");
		}else if(task.getOutput()!=null && task.getOutput().contains("Cannot connect to MO service")){
			throw new ConnectException("No MO service");
		}

		return task.getOutput();
		
	}

	public static String execute(String[] commands) throws Exception {
		ProcessThread task = new ProcessThread(commands);
		Thread thread = new Thread(task);
		
		int nrWaits = 0;
		boolean timeout = false;
		thread.start();
		while(!task.isDone() && !timeout){
			if(++nrWaits>400)
				timeout = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				thread.interrupt();
				task.stopThread();
				throw new TimeoutException("InterruptedException. Timeout", e);
			}
		}
		if(timeout){
			task.stopThread();
			thread.interrupt();
			throw new TimeoutException("Timeout");
		}else if(task.containsError()){
			if(task.getError()!=null){
				throw task.getError();
			}
			throw new Exception("Error running procces: " +  task.getErrorOutput());
		}

		return task.getOutput();
	}
}
