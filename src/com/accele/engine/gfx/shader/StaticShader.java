package com.accele.engine.gfx.shader;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.Camera;
import com.accele.engine.gfx.Light;
import com.accele.engine.util.Utils;

public class StaticShader extends Shader {

	private static final String VERTEX_FILE = "src/com/accele/engine/gfx/shader/defaultVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/com/accele/engine/gfx/shader/defaultFragmentShader.txt";
	private static final int MAX_LIGHTS_PER_ENTITY = 4;
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightPosition;
	private int[] location_lightColor;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_fogDensity;
	private int location_fogGradient;
	private int location_skyColor;
	private int location_numRows;
	private int location_offset;
	private int[] location_attenuation;
	private int location_celShadingLevels;
	private int location_plane;
	
	public StaticShader(Engine engine, String registryID, String localizedID) {
		super(engine, registryID, localizedID, VERTEX_FILE, FRAGMENT_FILE);
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
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_fogDensity = super.getUniformLocation("fogDensity");
		location_fogGradient = super.getUniformLocation("fogGradient");
		location_skyColor = super.getUniformLocation("skyColor");
		location_numRows = super.getUniformLocation("numRows");
		location_offset = super.getUniformLocation("offset");
		
		location_lightPosition = new int[MAX_LIGHTS_PER_ENTITY];
		location_lightColor = new int[MAX_LIGHTS_PER_ENTITY];
		location_attenuation = new int[MAX_LIGHTS_PER_ENTITY];
		
		for (int i = 0; i < location_lightPosition.length; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		
		location_celShadingLevels = super.getUniformLocation("celShadingLevels");
		location_plane = super.getUniformLocation("plane");
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
	
	public void loadLights(List<Light> lights) {
		for (int i = 0; i < location_lightPosition.length; i++) {
			if (i < lights.size()) {
				super.loadUniformVector(location_lightPosition[i], lights.get(i).getPos());
				super.loadUniformVector(location_lightColor[i], lights.get(i).getColor());
				super.loadUniformVector(location_attenuation[i], lights.get(i).getAttenuation());
			} else {
				super.loadUniformVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadUniformVector(location_lightColor[i], new Vector3f(0, 0, 0));
				super.loadUniformVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
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
	
	public void loadNumRows(int numRows) {
		super.loadUniformInt(location_numRows, numRows);
	}
	
	public void loadOffset(Vector2f offset) {
		super.loadUniformVector(location_offset, offset);
	}
	
	public void loadCelShadingLevels(int celShadingLevels) {
		super.loadUniformInt(location_celShadingLevels, celShadingLevels);
	}
	
	public void loadClipPlane(Vector4f plane) {
		super.loadUniformVector(location_plane, plane);
	}

}
