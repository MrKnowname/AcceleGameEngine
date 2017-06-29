package com.accele.engine.gfx.skybox;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Indexable;
import com.accele.engine.core.Renderable;
import com.accele.engine.core.Tickable;
import com.accele.engine.gfx.shader.Shader;

public abstract class Skybox implements Indexable, Tickable, Renderable {
	
	protected Engine engine;
	protected String registryID;
	protected String localizedID;
	protected int dayMapTextureID;
	protected int nightMapTextureID;
	protected Shader shader;
	
	public Skybox(Engine engine, String registryID, String localizedID, int dayMapTextureID, int nightMapTextureID, Shader shader) {
		this.engine = engine;
		this.registryID = registryID;
		this.localizedID = localizedID;
		this.dayMapTextureID = dayMapTextureID;
		this.nightMapTextureID = nightMapTextureID;
		this.shader = shader;
	}

	@Override
	public String getRegistryID() {
		return registryID;
	}

	@Override
	public String getLocalizedID() {
		return localizedID;
	}

	public int getDayMapTextureID() {
		return dayMapTextureID;
	}
	
	public int getNightMapTextureID() {
		return nightMapTextureID;
	}

	public Shader getShader() {
		return shader;
	}
	
}
