package com.accele.engine.gfx.skybox;

import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.Graphics;
import com.accele.engine.gfx.shader.SkyboxShader;
import com.accele.engine.util.Utils;

public class RenderOnlySkybox extends Skybox {

	private final int tex0;
	private final int tex1;
	private float blendFactor;
	private float time;
	
	public RenderOnlySkybox(Engine engine, String registryID, String localizedID, int dayMapTextureID, int nightMapTextureID) {
		super(engine, registryID, localizedID, dayMapTextureID, nightMapTextureID, engine.getRegistry().getShader("internal:skybox"));
		tex0 = dayMapTextureID;
		tex1 = nightMapTextureID;
	}

	private static final int FACTOR = 1;
	
	@Override
	public void onUpdate() {
		time += (float) engine.getRegistry().getProperty("internal:secondsPerFrame").get() * 1000 / FACTOR;
		time %= 24000 / FACTOR;
		if (Utils.inRange(time, 0, 4999 / FACTOR)) {
			dayMapTextureID = tex1;
			nightMapTextureID = tex1;
			blendFactor = time / 5000 / FACTOR;
		} else if (Utils.inRange(time, 5000 / FACTOR, 7999 / FACTOR)) {
			dayMapTextureID = tex1;
			nightMapTextureID = tex0;
			blendFactor = (time - 5000 / FACTOR) / (8000 / FACTOR - 5000 / FACTOR);
		} else if (Utils.inRange(time, 8000 / FACTOR, 20999 / FACTOR)) {
			dayMapTextureID = tex0;
			nightMapTextureID = tex0;
			blendFactor = (time - 8000 / FACTOR) / (21000 / FACTOR - 8000 / FACTOR);
		} else {
			dayMapTextureID = tex0;
			nightMapTextureID = tex1;
			blendFactor = (time - 21000 / FACTOR) / (24000 / FACTOR - 21000 / FACTOR);
		}
	}

	@Override
	public void onRender(Graphics g) {
		shader.start();
		((SkyboxShader) shader).connectTextureUnits();
		((SkyboxShader) shader).loadFogColor((Vector3f) engine.getRegistry().getProperty("internal:clearColor").get());
		((SkyboxShader) shader).loadViewMatrix(engine.getCamera());
		((SkyboxShader) shader).loadBlendFactor(blendFactor);
		g.drawSkybox(this);
		shader.stop();
	}

}
