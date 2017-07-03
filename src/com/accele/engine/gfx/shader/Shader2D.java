package com.accele.engine.gfx.shader;

import org.lwjgl.util.vector.Matrix4f;

import com.accele.engine.core.Engine;

public class Shader2D extends Shader {

	private int location_transformationMatrix;
	
	public Shader2D(Engine engine, String registryID, String localizedID, String vertexFile, String fragmentFile) {
		super(engine, registryID, localizedID, vertexFile, fragmentFile);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadUniformMatrix(location_transformationMatrix, matrix);
	}

}
