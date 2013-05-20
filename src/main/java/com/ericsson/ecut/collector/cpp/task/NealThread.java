package com.ericsson.ecut.collector.cpp.task;

import java.net.UnknownHostException;

import com.ericsson.ecut.collector.cpp.neal.MOCounterCollector;
import com.ericsson.ecut.collector.exception.ConnectException;

public class NealThread implements IThread, Runnable {

	private boolean done;
	private MOCounterCollector collector;
	private Exception error;
	
	public NealThread(MOCounterCollector collector) {
		done = false;
		error = null;
		this.collector = collector;
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.ecut.collector.cpp.task.IThread#run()
	 */
	@Override
	public void run() {
		try {
			collector.connect();
		} catch (UnknownHostException e) {
			error = e;
		} catch (ConnectException e) {
			error = e;
		}
		done = true;
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.ecut.collector.cpp.task.IThread#containsError()
	 */
	@Override
	public boolean containsError() {
		return error!=null;
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.ecut.collector.cpp.task.IThread#getError()
	 */
	@Override
	public Exception getError() {
		return error;
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.ecut.collector.cpp.task.IThread#hasError()
	 */
	@Override
	public boolean hasError() {
		return error != null;
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.ecut.collector.cpp.task.IThread#isDone()
	 */
	@Override
	public boolean isDone() {
		return done;
	}
	
	public MOCounterCollector getCollector() {
		return collector;
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.ecut.collector.cpp.task.IThread#stopThread()
	 */
	@Override
	public void stopThread() {
		if(collector!=null){
			collector.stop();
		}
	}
}
