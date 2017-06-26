package com.accele.engine.state;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Indexable;
import com.accele.engine.core.Renderable;
import com.accele.engine.core.Tickable;

public abstract class State implements Indexable, Tickable, Renderable {

	protected final Engine engine;
	protected final String registryID;
	protected final String localizedID;
	
	public State(Engine engine, String registryID, String localizedID) {
		this.engine = engine;
		this.registryID = registryID;
		this.localizedID = localizedID;
	}
	
	public abstract void onInit();
	public abstract void onExit();
	
	@Override
	public final String getRegistryID() {
		return registryID;
	}

	@Override
	public final String getLocalizedID() {
		return localizedID;
	}

}
