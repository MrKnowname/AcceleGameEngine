package com.accele.engine.terrain;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Indexable;
import com.accele.engine.core.Renderable;
import com.accele.engine.core.Tickable;
import com.accele.engine.model.TerrainModel;
import com.accele.engine.util.Utils;

public abstract class Terrain implements Indexable, Tickable, Renderable {

	protected Engine engine;
	protected String registryID;
	protected String localizedID;
	protected float size;
	protected int vertexCount;
	protected float x;
	protected float z;
	protected TerrainModel model;
	protected float[][] heights;
	
	public Terrain(Engine engine, String registryID, String localizedID, float size, int vertexCount, int gridX, int gridZ, TerrainModel model, float[][] heights) {
		this.engine = engine;
		this.registryID = registryID;
		this.localizedID = localizedID;
		this.size = size;
		this.vertexCount = vertexCount;
		this.x = gridX * size;
		this.z = gridZ * size;
		this.model = model;
		this.heights = heights;
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
	
	public float getHeight(float worldX, float worldZ) {
		float terrainX = worldX - x;
		float terrainZ = worldZ - z;
		float gridSquareSize = size / (float) (heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		if (!Utils.inRange(gridX, 0, heights.length - 1) || !Utils.inRange(gridZ, 0, heights.length - 1))
			return 0;
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float result = 0;
		if (xCoord <= 1 - zCoord)
			result = Utils.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		else
			result = Utils.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		return result;
	}
	
}
