package com.ericsson.ecut.collector.cpp.connectors.exception;

public class TimeoutException extends Exception{

	private static final long serialVersionUID = -3090394055034142995L;

	public TimeoutException() {
		super();
	}

	public TimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeoutException(String message) {
		super(message);
	}

	public TimeoutException(Throwable cause) {
		super(cause);
	}
	
}
