package com.accele.engine.model;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.accele.engine.gfx.Texture;
import com.accele.engine.gfx.internal.TextureData;
import com.accele.engine.util.Resource;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class ModelLoader {

	private List<Integer> vaos;
	private List<Integer> vbos;
	private List<Integer> textures;
	
	public ModelLoader() {
		vaos = new ArrayList<>();
		vbos = new ArrayList<>();
		textures = new ArrayList<>();
	}
	
	public RawModel loadModel(String registryID, String localizedID, float[] positions, int[] indices, float[] textureCoords, float[] normals) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		return new RawModel(registryID, localizedID, vaoID, indices.length);
	}
	
	public RawModel loadModel(String registryID, String localizedID, Resource model) {
		model.load();
		if (!(model.getValue() instanceof Object[]))
			throw new IllegalArgumentException("ResourceError: Expected model resource value to be of type Object[], received " + model.getValue().getClass().getSimpleName() + ".");
		Object[] data = (Object[]) model.getValue();
		return loadModel(registryID, localizedID, (float[]) data[0], (int[]) data[1], (float[]) data[2], (float[]) data[3]);
	}
	
	public RawModel loadModel(String registryID, String localizedID, float[] positions, int dimensions) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();
		return new RawModel(registryID, localizedID, vaoID, positions.length / dimensions);
	}
	
	/** Order: 0 = right, 1 = left, 2 = top, 3 = bottom, 4 = back, 5 = front */
	public int loadCubeMap(String[] textures) {
		int textureID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
		
		for (int i = 0; i < textures.length; i++) {
			TextureData td = decode(textures[i]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, td.getWidth(), td.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, td.getData());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		this.textures.add(textureID);
		return textureID;
	}
	
	public void addTexture(Texture texture) {
		textures.add(texture.getImage().getTextureID());
	}
	
	public void clear() {
		for (int vao : vaos)
			GL30.glDeleteVertexArrays(vao);
		
		for (int vbo : vbos)
			GL15.glDeleteBuffers(vbo);
		
		for (int texture : textures)
			GL11.glDeleteTextures(texture);
	}
	
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = floatArrayToBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}
	
	private FloatBuffer floatArrayToBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = intArrayToBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	private IntBuffer intArrayToBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private TextureData decode(String location) {
		int width = 0;
		int height = 0;
		ByteBuffer data = null;
		try {
			FileInputStream fis = new FileInputStream(location);
			PNGDecoder decoder = new PNGDecoder(fis);
			width = decoder.getWidth();
			height = decoder.getHeight();
			data = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(data, width * 4, Format.RGBA);
			data.flip();
			fis.close();
		} catch (Exception e) {
			System.err.println("InternalEngineError: Unable to decode PNG file \"" + location + "\".");
		}
		
		return new TextureData(width, height, data);
	}
	
}
