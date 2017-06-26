package com.accele.engine.gfx.shader;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.gfx.Camera;
import com.accele.engine.gfx.Light;
import com.accele.engine.util.Utils;

public class StaticShader extends Shader {

	private static final String VERTEX_FILE = "src/com/accele/engine/gfx/shader/defaultVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/com/accele/engine/gfx/shader/defaultFragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColor;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_fogDensity;
	private int location_fogGradient;
	private int location_skyColor;
	
	public StaticShader(String registryID, String localizedID) {
		super(registryID, localizedID, VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_lightPosition = super.getUniformLocation("lightPosition");
		location_lightColor = super.getUniformLocation("lightColor");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_fogDensity = super.getUniformLocation("fogDensity");
		location_fogGradient = super.getUniformLocation("fogGradient");
		location_skyColor = super.getUniformLocation("skyColor");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadUniformMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadUniformMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera) {
		super.loadUniformMatrix(location_viewMatrix, Utils.Dim3.createViewMatrix(camera));
	}
	
	public void loadLight(Light light) {
		super.loadUniformVector(location_lightPosition, light.getPos());
		super.loadUniformVector(location_lightColor, light.getColor());
	}
	
	public void loadShineVariables(float shineDamper, float reflectivity) {
		super.loadUniformFloat(location_shineDamper, shineDamper);
		super.loadUniformFloat(location_reflectivity, reflectivity);
	}
	
	public void loadFakeLighting(boolean useFakeLighting) {
		super.loadUniformBoolean(location_useFakeLighting, useFakeLighting);
	}
	
	public void loadFogDensity(float fogDensity) {
		super.loadUniformFloat(location_fogDensity, fogDensity);
	}
	
	public void loadFogGradient(float fogGradient) {
		super.loadUniformFloat(location_fogGradient, fogGradient);
	}
	
	public void loadSkyColor(Vector3f skyColor) {
		super.loadUniformVector(location_skyColor, skyColor);
	}

}
