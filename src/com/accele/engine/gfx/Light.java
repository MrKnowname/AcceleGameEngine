package com.accele.engine.gfx;

import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Indexable;

public class Light implements Indexable {

	private String registryID;
	private String localizedID;
	private Vector3f pos;
	private Vector3f color;
	private Vector3f attenuation;
	
	public Light(String registryID, String localizedID, Vector3f pos, Vector3f color, Vector3f attenuation) {
		this.registryID = registryID;
		this.localizedID = localizedID;
		this.pos = pos;
		this.color = color;
		this.attenuation = attenuation;
	}
	
	public Light(String registryID, String localizedID, Vector3f pos, Vector3f color) {
		this(registryID, localizedID, pos, color, new Vector3f(1, 0, 0));
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
	
	public Vector3f getAttenuation() {
		return attenuation;
	}
	
}
