package com.accele.engine.gfx;

import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Indexable;
import com.accele.engine.core.Tickable;

public abstract class Camera implements Indexable, Tickable {

	protected String registryID;
	protected String localizedID;
	protected Vector3f pos;
	protected float pitch;
	protected float yaw;
	protected float roll;
	
	public Camera(Vector3f pos) {
		this.pos = pos;
	}
	
	public Vector3f getPos() {
		return pos;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getRoll() {
		return roll;
	}

	@Override
	public String getRegistryID() {
		return registryID;
	}

	@Override
	public String getLocalizedID() {
		return localizedID;
	}
	
}
