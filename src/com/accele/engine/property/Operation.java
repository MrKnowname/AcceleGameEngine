package com.accele.engine.property;

import com.accele.engine.core.Engine;

@FunctionalInterface
public interface Operation {

	public void run(Engine engine, Object value);
	
}
