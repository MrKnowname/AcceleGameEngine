package com.accele.engine.model;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.ModelTexture;

public class TexturedModel extends Model {

	private ModelTexture texture;
	
	public TexturedModel(Engine engine, RawModel model, ModelTexture texture) {
		super(model.registryID, model.localizedID, model.vaoID, model.vertexCount);
		this.texture = texture;
		
		engine.getModelLoader().addTexture(texture);
	}
	
	public ModelTexture getTexture() {
		return texture;
	}
	
}
