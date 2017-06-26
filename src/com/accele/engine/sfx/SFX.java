package com.accele.engine.sfx;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Indexable;

public abstract class SFX implements Indexable {

	protected final Engine engine;
	protected final String registryID;
	protected final String localizedID;
	
	protected SFX(Engine engine, String registryID, String localizedID) {
		this.engine = engine;
		this.registryID = registryID;
		this.localizedID = localizedID;
	}
	
	public abstract void play();
	public abstract void play(float pitch, float volume);
	public abstract void loop();
	public abstract void loop(float pitch, float volume);
	public abstract void stop();
	public abstract boolean isPlaying();
	
	@Override
	public final String getRegistryID() {
		return registryID;
	}
	
	@Override
	public final String getLocalizedID() {
		return localizedID;
	}
	
}
