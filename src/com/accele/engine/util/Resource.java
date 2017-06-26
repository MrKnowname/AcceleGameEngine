package com.accele.engine.util;

import com.accele.engine.util.internal.InternalLoader;

public final class Resource {

	private String location;
	private Loader loadingOperation;
	private Object value;
	
	public Resource(String location, Loader loadingOperation) {
		this.location = location;
		this.loadingOperation = loadingOperation;
	}
	
	public Resource(Object meta, InternalLoader loadingOperation) {
		this.value = meta;
		this.loadingOperation = loadingOperation;
	}
	
	public void load() {
		if (loadingOperation instanceof InternalLoader)
			value = ((InternalLoader) loadingOperation).load(value);
		else
			value = loadingOperation.load(location);
	}
	
	public Object getValue() {
		return value;
	}
	
}
