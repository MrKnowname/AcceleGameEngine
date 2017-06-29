package com.accele.engine.gfx.shader;

import org.lwjgl.util.vector.Matrix4f;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.Camera;
import com.accele.engine.util.Utils;

public class WaterShader extends Shader {

	private static final String VERTEX_FILE = "src/com/accele/engine/gfx/shader/defaultWaterVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/com/accele/engine/gfx/shader/defaultWaterFragmentShader.txt";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_modelMatrix;
	
	public WaterShader(Engine engine, String registryID, String localizedID) {
		super(engine, registryID, localizedID, VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_modelMatrix = super.getUniformLocation("modelMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadUniformMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera) {
		super.loadUniformMatrix(location_viewMatrix, Utils.Dim3.createViewMatrix(camera));
	}
	
	public void loadModelMatrix(Matrix4f matrix) {
		super.loadUniformMatrix(location_modelMatrix, matrix);
	}

}
