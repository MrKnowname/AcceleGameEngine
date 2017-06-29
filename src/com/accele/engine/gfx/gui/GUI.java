package com.accele.engine.gfx.gui;

import org.lwjgl.util.vector.Vector2f;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Indexable;
import com.accele.engine.core.Renderable;
import com.accele.engine.core.Tickable;
import com.accele.engine.gfx.Texture;
import com.accele.engine.gfx.shader.Shader;

public abstract class GUI implements Indexable, Tickable, Renderable {

	protected Engine engine;
	protected String registryID;
	protected String localizedID;
	protected Texture texture;
	protected Vector2f pos;
	protected Vector2f rotation;
	protected Vector2f scale;
	protected Shader shader;
	
	public GUI(Engine engine, String registryID, String localizedID, Texture texture, Vector2f pos, Vector2f rotation, Vector2f scale, Shader shader) {
		this.engine = engine;
		this.registryID = registryID;
		this.localizedID = localizedID;
		this.texture = texture;
		this.pos = pos;
		this.rotation = rotation;
		this.scale = scale;
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

	public Texture getTexture() {
		return texture;
	}

	public Vector2f getPos() {
		return pos;
	}
	
	public Vector2f getRotation() {
		return rotation;
	}

	public Vector2f getScale() {
		return scale;
	}
	
	public Shader getShader() {
		return shader;
	}
	
}
