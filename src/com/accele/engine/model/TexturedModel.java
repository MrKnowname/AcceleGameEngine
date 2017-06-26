package com.accele.engine.model;

import com.accele.engine.gfx.ModelTexture;

public class TexturedModel {

	private RawModel rawModel;
	private ModelTexture texture;
	
	public TexturedModel(ModelLoader loader, RawModel rawModel, ModelTexture texture) {
		this.rawModel = rawModel;
		this.texture = texture;
		
		loader.addTexture(texture);
	}
	
	public RawModel getRawModel() {
		return rawModel;
	}
	
	public ModelTexture getTexture() {
		return texture;
	}
	
}
