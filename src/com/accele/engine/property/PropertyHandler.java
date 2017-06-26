package com.accele.engine.property;

import com.accele.engine.core.Engine;

public final class PropertyHandler {

	private Engine engine;
	private boolean running;
	private Property[] internalProperties;
	
	public PropertyHandler(Engine engine, Property[] internalProperties) {
		this.engine = engine;
		this.internalProperties = internalProperties;
	}
	
	public boolean isGameRunning() {
		return running;
	}
	
	public void setGameRunning(boolean running) {
		this.running = running;
	}
	
	public Property[] getAllInternalProperties() {
		return internalProperties;
	}
	
	public Property getProperty(String localizedID) {
		return engine.getRegistry().getProperty(localizedID);
	}
	
}
