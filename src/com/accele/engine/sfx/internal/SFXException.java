package com.accele.engine.sfx.internal;

public class SFXException extends Exception {
	
	private static final long serialVersionUID = 1029758613734156463L;
	
	public SFXException() {
		super();
	}
	
	public SFXException(String message) {
		super(message);
	}
	
	public SFXException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SFXException(Throwable cause) {
		super(cause);
	}

}
