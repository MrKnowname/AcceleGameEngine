package com.accele.engine.gfx.shader;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.Camera;
import com.accele.engine.property.Property;
import com.accele.engine.util.Utils;

public class SkyboxShader extends Shader {

	private static final String VERTEX_FILE = "src/com/accele/engine/gfx/shader/defaultSkyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/com/accele/engine/gfx/shader/defaultSkyboxFragmentShader.txt";
	
	private Property rotationSpeed;
	private Property secondsPerFrame;
	private float rotation;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_upperLimit;
	private int location_lowerLimit;
	private int location_fogColor;
	private int location_dayMap;
	private int location_nightMap;
	private int location_blendFactor;
	
	public SkyboxShader(Engine engine, String registryID, String localizedID) {
		super(engine, registryID, localizedID, VERTEX_FILE, FRAGMENT_FILE);
		rotationSpeed = engine.getRegistry().getProperty("internal:skyboxRotationSpeed");
		secondsPerFrame = engine.getRegistry().getProperty("internal:secondsPerFrame");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_upperLimit = super.getUniformLocation("upperLimit");
		location_lowerLimit = super.getUniformLocation("lowerLimit");
		location_fogColor = super.getUniformLocation("fogColor");
		location_dayMap = super.getUniformLocation("dayMap");
		location_nightMap = super.getUniformLocation("nightMap");
		location_blendFactor = super.getUniformLocation("blendFactor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadUniformMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f matrix = Utils.Dim3.createViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		rotation += (float) rotationSpeed.get() * (float) secondsPerFrame.get();
		if (rotation >= 360f)
			rotation = 0f;
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);
		super.loadUniformMatrix(location_viewMatrix, matrix);
	}
	
	public void loadUpperLimit(float upperLimit) {
		super.loadUniformFloat(location_upperLimit, upperLimit);
	}
	
	public void loadLowerLimit(float lowerLimit) {
		super.loadUniformFloat(location_lowerLimit, lowerLimit);
	}
	
	public void loadLimits(float upperLimit, float lowerLimit) {
		super.loadUniformFloat(location_upperLimit, upperLimit);
		super.loadUniformFloat(location_lowerLimit, lowerLimit);
	}
	
	public void loadFogColor(Vector3f fogColor) {
		super.loadUniformVector(location_fogColor, fogColor);
	}
	
	public void connectTextureUnits() {
		super.loadUniformInt(location_dayMap, 0);
		super.loadUniformInt(location_nightMap, 1);
	}
	
	public void loadBlendFactor(float blendFactor) {
		super.loadUniformFloat(location_blendFactor, blendFactor);
	}

}
