package com.accele.engine.terrain;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Indexable;
import com.accele.engine.core.Renderable;
import com.accele.engine.core.Tickable;
import com.accele.engine.model.TerrainModel;

public abstract class Terrain implements Indexable, Tickable, Renderable {

	protected Engine engine;
	protected String registryID;
	protected String localizedID;
	protected float size;
	protected int vertexCount;
	protected float x;
	protected float z;
	protected TerrainModel model;
	
	public Terrain(Engine engine, String registryID, String localizedID, float size, int vertexCount, int gridX, int gridZ, TerrainModel model) {
		this.engine = engine;
		this.registryID = registryID;
		this.localizedID = localizedID;
		this.size = size;
		this.vertexCount = vertexCount;
		this.x = gridX * size;
		this.z = gridZ * size;
		this.model = model;
	}

	@Override
	public String getRegistryID() {
		return registryID;
	}

	@Override
	public String getLocalizedID() {
		return localizedID;
	}

	public float getSize() {
		return size;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public TerrainModel getModel() {
		return model;
	}
	
}
