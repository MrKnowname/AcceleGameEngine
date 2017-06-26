package com.accele.engine.entity;

import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.shader.Shader;
import com.accele.engine.model.TexturedModel;

public abstract class Entity3D extends Entity {

	protected float xRot, yRot, zRot;
	protected float scale;
	protected TexturedModel model;
	protected Shader shader;
	
	public Entity3D(Engine engine, String registryID, String localizedID, Vector3f pos, float xRot, float yRot, float zRot, float scale, TexturedModel model, Shader shader) {
		super(engine, registryID, localizedID, pos);
		this.xRot = xRot;
		this.yRot = yRot;
		this.zRot = zRot;
		this.scale = scale;
		this.model = model;
		this.shader = shader;
	}
	
	public float getXRot() {
		return xRot;
	}

	public float getYRot() {
		return yRot;
	}

	public float getZRot() {
		return zRot;
	}

	public float getScale() {
		return scale;
	}

	public TexturedModel getModel() {
		return model;
	}
	
	public Shader getShader() {
		return shader;
	}

}
