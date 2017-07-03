package com.accele.engine.entity;

import java.net.InetAddress;

import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.shader.Shader;
import com.accele.engine.model.TexturedModel;

public abstract class MultiplayerEntity extends Entity3D {

	protected InetAddress ip;
	protected int port;
	
	public MultiplayerEntity(Engine engine, String registryID, String localizedID, Vector3f pos, float xRot, float yRot,
			float zRot, float scale, TexturedModel model, Shader shader, int textureIndex, InetAddress ip, int port) {
		super(engine, registryID, localizedID, pos, xRot, yRot, zRot, scale, model, shader, textureIndex);
		this.ip = ip;
		this.port = port;
	}
	
	public InetAddress getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}

}
