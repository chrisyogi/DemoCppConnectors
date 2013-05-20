package com.ericsson.ecut.collector.cpp.task;

import com.ericsson.ecut.collector.cpp.connectors.exception.TimeoutException;
import com.ericsson.ecut.collector.cpp.neal.MOCounterCollector;

public class NealWrapper {
	
	public static void getNealConnection(MOCounterCollector collector) throws TimeoutException, Exception{
		NealThread task = new NealThread(collector);
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
			task.getError().printStackTrace();
			throw task.getError();
		}
	}
}
