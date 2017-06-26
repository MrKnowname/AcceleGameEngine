package com.accele.engine.gfx;

import com.accele.engine.core.Indexable;
import com.accele.engine.util.Resource;

public class Texture implements Indexable {

	protected final String registryID;
	protected final String localizedID;
	protected final org.newdawn.slick.opengl.Texture image;
	
	public Texture(String registryID, String localizedID, Resource image) {
		this.registryID = registryID;
		this.localizedID = localizedID;
		image.load();
		this.image = (org.newdawn.slick.opengl.Texture) image.getValue();
	}
	
	@Override
	public final String getRegistryID() {
		return registryID;
	}

	@Override
	public final String getLocalizedID() {
		return localizedID;
	}
	
	public final org.newdawn.slick.opengl.Texture getImage() {
		return image;
	}

}
