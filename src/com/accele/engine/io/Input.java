package com.accele.engine.io;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Indexable;
import com.accele.engine.core.Tickable;

public abstract class Input implements Indexable, Tickable {

	protected final Engine engine;
	protected final String registryID;
	protected final String localizedID;
	
	public Input(Engine engine, String registryID, String localizedID) {
		this.engine = engine;
		this.registryID = registryID;
		this.localizedID = localizedID;
	}
	
	@Override
	public final String getRegistryID() {
		return registryID;
	}

	@Override
	public final String getLocalizedID() {
		return localizedID;
	}

}
