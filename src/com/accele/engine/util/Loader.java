package com.accele.engine.util;

@FunctionalInterface
public interface Loader {

	public Object load(String location);
	
}
