package com.accele.engine.gfx.shader;

import org.lwjgl.util.vector.Matrix4f;

import com.accele.engine.core.Engine;

public class GUIShader extends Shader {

	private static final String VERTEX_FILE = "src/com/accele/engine/gfx/shader/defaultGUIVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/com/accele/engine/gfx/shader/defaultGUIFragmentShader.txt";
	
	private int location_transformationMatrix;
	
	public GUIShader(Engine engine, String registryID, String localizedID) {
		super(engine, registryID, localizedID, VERTEX_FILE, FRAGMENT_FILE);
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
