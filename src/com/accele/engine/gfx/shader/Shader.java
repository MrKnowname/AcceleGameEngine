package com.accele.engine.gfx.shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Indexable;

public abstract class Shader implements Indexable {

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	private String registryID;
	private String localizedID;
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	public Shader(String registryID, String localizedID, String vertexFile, String fragmentFile) {
		this.registryID = registryID;
		this.localizedID = localizedID;
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		
		getAllUniformLocations();
	}
	
	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	protected abstract void getAllUniformLocations();
	protected abstract void bindAttributes();
	
	public void start() {
		GL20.glUseProgram(programID);
	}
	
	public void stop() {
		GL20.glUseProgram(0);
	}
	
	public void cleanUp() {
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}
	
	protected void bindAttribute(int attribute, String varName) {
		GL20.glBindAttribLocation(programID, attribute, varName);
	}
	
	protected void loadUniformFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}
	
	protected void loadUniformVector(int location, Vector3f value) {
		GL20.glUniform3f(location, value.x, value.y, value.z);
	}
	
	protected void loadUniformBoolean(int location, boolean value) {
		GL20.glUniform1f(location, value ? 1 : 0);
	}
	
	protected void loadUniformMatrix(int location, Matrix4f value) {
		value.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}
	
	protected void loadUniformInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}
	
	private static int loadShader(String file, int type) {
		StringBuilder src = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			
			while ((line = br.readLine()) != null)
				src.append(line + "\n");
			
			br.close();
		} catch (IOException e) {
			System.err.println("ShaderError: Could not read file \"" + file + "\".");
			e.printStackTrace();
			System.exit(1);
		}
		
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, src);
		GL20.glCompileShader(shaderID);
		
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("ShaderError: Could not compile shader \"" + file + "\".");
			System.exit(1);
		}
		
		return shaderID;
	}
	
	@Override
	public String getRegistryID() {
		return registryID;
	}

	@Override
	public String getLocalizedID() {
		return localizedID;
	}
	
}
