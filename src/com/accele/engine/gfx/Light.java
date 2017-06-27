package com.accele.engine.gfx;

import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Indexable;

public class Light implements Indexable {

	private String registryID;
	private String localizedID;
	private Vector3f pos;
	private Vector3f color;
	
	public Light(String registryID, String localizedID, Vector3f pos, Vector3f color) {
		this.registryID = registryID;
		this.localizedID = localizedID;
		this.pos = pos;
		this.color = color;
	}
	
	@Override
	public String getRegistryID() {
		return registryID;
	}
	
	@Override
	public String getLocalizedID() {
		return localizedID;
	}

	public Vector3f getPos() {
		return pos;
	}

	public Vector3f getColor() {
		return color;
	}
	
}
