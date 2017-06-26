package com.accele.engine.model;

import com.accele.engine.gfx.TerrainTexture;
import com.accele.engine.gfx.Texture;

public class TerrainModel {

	private RawModel model;
	private TerrainTexture texture;
	private Texture blendMap;
		
	public TerrainModel(ModelLoader loader, RawModel model, TerrainTexture texture, Texture blendMap) {
		this.model = model;
		this.texture = texture;
		this.blendMap = blendMap;
		
		loader.addTexture(texture);
		loader.addTexture(blendMap);
	}

	public RawModel getRawModel() {
		return model;
	}
	
	public TerrainTexture getTexture() {
		return texture;
	}
	
	public Texture getBlendMap() {
		return blendMap;
	}
	
}
