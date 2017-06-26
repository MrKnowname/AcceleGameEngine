package com.accele.engine.gfx;

import java.awt.Font;

import org.newdawn.slick.TrueTypeFont;

import com.accele.engine.core.Indexable;

public class StoredFont implements Indexable {

	private final String registryID;
	private final String localizedID;
	private final Font internalFont;
	private final org.newdawn.slick.Font derivedFont;
	
	public StoredFont(String registryID, String localizedID, Font font) {
		this.registryID = registryID;
		this.localizedID = localizedID;
		this.internalFont = font;
		this.derivedFont = new TrueTypeFont(font, false);
	}
	
	@Override
	public final String getRegistryID() {
		return registryID;
	}

	@Override
	public final String getLocalizedID() {
		return localizedID;
	}
	
	public final Font getInternalFont() {
		return internalFont;
	}
	
	public final org.newdawn.slick.Font getDerivedFont() {
		return derivedFont;
	}

}
