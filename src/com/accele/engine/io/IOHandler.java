package com.accele.engine.io;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Tickable;

public final class IOHandler implements Tickable {

	private Engine engine;
	
	public IOHandler(Engine engine) {
		this.engine = engine;
	}
	
	@Override
	public void onUpdate() {
		for (Input i : engine.getRegistry().getAllInputs()) {
			i.onUpdate();
		}
	}
	
}
