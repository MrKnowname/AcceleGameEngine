package com.accele.engine.gfx;

public class NonInternalTexture extends Texture {

	private int textureID;
	
	public NonInternalTexture(String registryID, String localizedID, int textureID) {
		super(registryID, localizedID);
		this.textureID = textureID;
	}
	
	@Override
	public int getTextureID() {
		return textureID;
	}

}
