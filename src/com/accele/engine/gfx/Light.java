package com.accele.engine.gfx;

import org.lwjgl.util.vector.Vector3f;

public class Light {

	private Vector3f pos;
	private Vector3f color;
	
	public Light(Vector3f pos, Vector3f color) {
		this.pos = pos;
		this.color = color;
	}

	public Vector3f getPos() {
		return pos;
	}

	public Vector3f getColor() {
		return color;
	}
	
}
