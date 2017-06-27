package com.accele.engine.model;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.TerrainTexture;
import com.accele.engine.gfx.Texture;

public class TerrainModel extends Model {

	private TerrainTexture texture;
	private Texture blendMap;
		
	public TerrainModel(Engine engine, RawModel model, TerrainTexture texture, Texture blendMap) {
		super(model.registryID, model.localizedID, model.vaoID, model.vertexCount);
		
		this.texture = texture;
		this.blendMap = blendMap;
		
		engine.getModelLoader().addTexture(texture);
		engine.getModelLoader().addTexture(blendMap);
	}
	
	public TerrainTexture getTexture() {
		return texture;
	}
	
	public Texture getBlendMap() {
		return blendMap;
	}
	
}
