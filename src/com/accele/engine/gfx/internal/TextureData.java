package com.accele.engine.gfx.internal;

import java.nio.ByteBuffer;

public final class TextureData {

	private int width;
	private int height;
	private ByteBuffer data;
	
	public TextureData(int width, int height, ByteBuffer data) {
		this.width = width;
		this.height = height;
		this.data = data;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ByteBuffer getData() {
		return data;
	}
	
}
