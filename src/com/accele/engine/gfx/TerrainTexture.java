package com.accele.engine.gfx;

import com.accele.engine.util.Resource;

public class TerrainTexture extends Texture {

	private org.newdawn.slick.opengl.Texture rTexture;
	private org.newdawn.slick.opengl.Texture gTexture;
	private org.newdawn.slick.opengl.Texture bTexture;
	
	public TerrainTexture(String registryID, String localizedID, Resource backgroundTexture, Resource rTexture, Resource gTexture, Resource bTexture) {
		super(registryID, localizedID, backgroundTexture);
		rTexture.load();
		gTexture.load();
		bTexture.load();
		this.rTexture = (org.newdawn.slick.opengl.Texture) rTexture.getValue();
		this.gTexture = (org.newdawn.slick.opengl.Texture) gTexture.getValue();
		this.bTexture = (org.newdawn.slick.opengl.Texture) bTexture.getValue();
	}
	
	public final org.newdawn.slick.opengl.Texture getBackground() {
		return image;
	}
	
	public final org.newdawn.slick.opengl.Texture getR() {
		return rTexture;
	}
	
	public final org.newdawn.slick.opengl.Texture getG() {
		return gTexture;
	}
	
	public final org.newdawn.slick.opengl.Texture getB() {
		return bTexture;
	}

}
