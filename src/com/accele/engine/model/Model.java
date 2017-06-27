package com.accele.engine.model;

import com.accele.engine.core.Indexable;

public abstract class Model implements Indexable {

	protected String registryID;
	protected String localizedID;
	protected int vaoID;
	protected int vertexCount;
	
	public Model(String registryID, String localizedID, int vaoID, int vertexCount) {
		this.registryID = registryID;
		this.localizedID = localizedID;
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	@Override
	public String getRegistryID() {
		return registryID;
	}

	@Override
	public String getLocalizedID() {
		return localizedID;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
}
