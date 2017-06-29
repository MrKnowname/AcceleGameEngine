package com.accele.engine.terrain;

import java.util.List;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.Graphics;
import com.accele.engine.gfx.Light;
import com.accele.engine.gfx.TerrainTexture;
import com.accele.engine.gfx.Texture;
import com.accele.engine.gfx.shader.TerrainShader;
import com.accele.engine.model.TerrainModel;
import com.accele.engine.util.Utils;

public class DefaultFlatTerrain extends Terrain {

	private TerrainShader shader;
	private List<Light> lights;
	
	public DefaultFlatTerrain(Engine engine, String registryID, String localizedID, float size, int vertexCount,
			int gridX, int gridZ, TerrainTexture texture, Texture blendMap, List<Light> lights) {
		super(engine, registryID, localizedID, size, vertexCount, gridX, gridZ, null, null);
		model = new TerrainModel(engine, Utils.Dim3.generateFlatTerrainModel(registryID + "_model", localizedID + "_model", engine.getModelLoader(), size, vertexCount), texture, blendMap);
		shader = (TerrainShader) engine.getRegistry().getShader("internal:terrain");
		this.lights = lights;
	}

	@Override
	public void onUpdate() {
		
	}

	@Override
	public void onRender(Graphics g) {
		shader.start();
		shader.loadLights(lights);
		shader.loadViewMatrix(engine.getCamera());
		g.drawTerrain(this, shader);
		shader.stop();
	}

}
