package com.accele.engine.terrain;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.Graphics;
import com.accele.engine.gfx.Light;
import com.accele.engine.gfx.TerrainTexture;
import com.accele.engine.gfx.Texture;
import com.accele.engine.gfx.shader.TerrainShader;
import com.accele.engine.model.RawModel;
import com.accele.engine.model.TerrainModel;
import com.accele.engine.util.ObjectPair;
import com.accele.engine.util.Resource;
import com.accele.engine.util.Utils;

public class DefaultTerrainWithHeightMap extends Terrain {

	private TerrainShader shader;
	private Light light;
	
	public DefaultTerrainWithHeightMap(Engine engine, String registryID, String localizedID, float size,
			int vertexCount, int gridX, int gridZ, TerrainTexture texture, Texture blendMap, Resource heightMap, Light light) {
		super(engine, registryID, localizedID, size, vertexCount, gridX, gridZ, null, null);
		ObjectPair<RawModel, float[][]> tmp = Utils.Dim3.generateTerrainModel(registryID + "_model", localizedID + "_model", engine.getModelLoader(), heightMap, size);
		model = new TerrainModel(engine, tmp.getFirst(), texture, blendMap);
		shader = (TerrainShader) engine.getRegistry().getShader("internal:terrain");
		heights = tmp.getSecond();
		this.light = light;
	}

	@Override
	public void onUpdate() {
		
	}

	@Override
	public void onRender(Graphics g) {
		shader.start();
		shader.loadLight(light);
		shader.loadViewMatrix(engine.getCamera());
		g.drawTerrain(this, shader);
		shader.stop();
	}

}
