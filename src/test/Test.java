package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Engine;
import com.accele.engine.entity.Entity3D;
import com.accele.engine.gfx.DefaultEntityCamera;
import com.accele.engine.gfx.Graphics;
import com.accele.engine.gfx.Light;
import com.accele.engine.gfx.ModelTexture;
import com.accele.engine.gfx.TerrainTexture;
import com.accele.engine.gfx.Texture;
import com.accele.engine.gfx.shader.StaticShader;
import com.accele.engine.gfx.shader.WaterShader;
import com.accele.engine.gfx.skybox.RenderOnlySkybox;
import com.accele.engine.gfx.skybox.Skybox;
import com.accele.engine.io.KeyControllable;
import com.accele.engine.io.KeyInput;
import com.accele.engine.io.MousePicker;
import com.accele.engine.model.TexturedModel;
import com.accele.engine.property.Property;
import com.accele.engine.state.State;
import com.accele.engine.terrain.DefaultTerrainWithHeightMap;
import com.accele.engine.terrain.Terrain;
import com.accele.engine.util.Batch;
import com.accele.engine.util.Resource;
import com.accele.engine.util.Utils;

public class Test {

	private Engine engine;
	
	public Test() {
		engine = new Engine(1280, 720, 1);
		engine.getRegistry().getProperty("internal:title").set("Test AcceleEngine v" + engine.getRegistry().getProperty("internal:version").get());
		engine.getRegistry().registerAll(new StateImpl(engine, "acl.test.state.impl", "impl"));
		engine.getStateHandler().setCurrentState("impl", false, true);
		engine.run();
	}
	
	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", "/Users/MrKnowname/Desktop/BesiegeTest/lib/native/");
		
		new Test();
	}
	
	private static class StateImpl extends State implements KeyControllable {

		private List<Light> lights;
		//private GUI gui;
		private Skybox skybox;
		private MousePicker mp;
		private WaterShader waterShader;
		
		public StateImpl(Engine engine, String registryID, String localizedID) {
			super(engine, registryID, localizedID);
			lights = new ArrayList<>();
			waterShader = (WaterShader) engine.getRegistry().getShader("internal:water");
			//gui = new RenderOnlyGUI(engine, "acl.test.gui.rogui", "rogui", new Texture("acl.test.texture.grass", "grass", new Resource("res/grass.png", Utils.DEFAULT_TEXTURE_LOADER)), new Vector2f(1, 1), new Vector2f(0, 0), new Vector2f(1, 1));
			engine.getRegistry().getProperty("internal:shaderFogDensity").set(0.0035f);
			engine.getRegistry().getProperty("internal:shaderFogGradient").set(5f);
			engine.getEntityHandler().addStaticShaderLight(Utils.addAndReturn(lights, new Light("acl.test.light.sun", "sun", new Vector3f(0, 1000, -7000), new Vector3f(0.4f, 0.4f, 0.4f))));
			engine.getEntityHandler().addStaticShaderLight(Utils.addAndReturn(lights, new Light("acl.test.light.l1", "l1", new Vector3f(185, 10, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f))));
			engine.getEntityHandler().addStaticShaderLight(Utils.addAndReturn(lights, new Light("acl.test.light.l2", "l2", new Vector3f(370, 17, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f))));
			engine.getEntityHandler().addStaticShaderLight(Utils.addAndReturn(lights, new Light("acl.test.light.l3", "l3", new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f))));
			TexturedModel model = new TexturedModel(engine, engine.getModelLoader().loadModel("acl.test.model.impl", "impl", new Resource("res/person.obj", Utils.DEFAULT_MODEL_LOADER)), new ModelTexture("acl.test.model.texture.impl", "impl", new Resource("res/flowers.png", Utils.DEFAULT_TEXTURE_LOADER), false, true));
			ModelTexture texture;
			Player player;
			engine.getRegistry().register(new TerrainTexture("acl.test.texture.terrain0", "terrain0", new Resource("res/grass.png", Utils.DEFAULT_TEXTURE_LOADER), new Resource("res/flowers.png", Utils.DEFAULT_TEXTURE_LOADER), new Resource("res/mud.png", Utils.DEFAULT_TEXTURE_LOADER), new Resource("res/path.png", Utils.DEFAULT_TEXTURE_LOADER)));
			engine.getRegistry().register(new Texture("acl.test.texture.blendMap", "blendMap", new Resource("res/blendMap.png", Utils.DEFAULT_TEXTURE_LOADER)));
			engine.getRegistry().register(texture = new ModelTexture("acl.test.texture.default", "default", new Resource("res/stall_texture.png", Utils.DEFAULT_TEXTURE_LOADER), 10, 1, false, false, 1));
			engine.getRegistry().register(new EntityImpl(engine, "acl.test.entity.impl", "impl", new Vector3f(0, -5, -25), 0, 0, 0, 1, new TexturedModel(engine, engine.getModelLoader().loadModel("acl.test.model.stall", "stall", new Resource("res/stall.obj", Utils.DEFAULT_MODEL_LOADER)), texture), lights));
			engine.getRegistry().registerAll(player = new Player(engine, "acl.test.entity.player", "player", new Vector3f(0, -5, -25), 0, 0, 0, 1, new TexturedModel(engine, engine.getModelLoader().loadModel("acl.test.model.person", "person", new Resource("res/person.obj", Utils.DEFAULT_MODEL_LOADER)), texture), lights));
			engine.getRegistry().registerAll(new DefaultEntityCamera(player));
			mp = new MousePicker(engine);
			Terrain terrain = null;
			engine.getRegistry().register(terrain = new DefaultTerrainWithHeightMap(engine, "acl.test.thm", "thm", 800, 128, 0, -1, (TerrainTexture) engine.getRegistry().getTexture("terrain0"), engine.getRegistry().getTexture("blendMap"), new Resource("res/heightMap.png", Utils.DEFAULT_IMAGE_LOADER), lights));
			engine.getRegistry().register(new DefaultTerrainWithHeightMap(engine, "acl.test.thm", "thm", 800, 128, 1, 0, (TerrainTexture) engine.getRegistry().getTexture("terrain0"), engine.getRegistry().getTexture("blendMap"), new Resource("res/heightMap.png", Utils.DEFAULT_IMAGE_LOADER), lights));
			
			Random rand = engine.getRand();
			for (int i = 0; i < 400; i++) {
				if (i % 3 == 0) {
					float x = rand.nextFloat() * 800;
					float z = rand.nextFloat() * -600;
					float y = terrain.getHeight(x, z);

					engine.getRegistry().register(new EntityImpl(engine, "acl.test.entity.impl2", "impl2",
							new Vector3f(x, y, z), 0, rand.nextFloat() * 360, 0, 0.9f, model, lights));
				}
				if (i % 1 == 0) {
					float x = rand.nextFloat() * 800;
					float z = rand.nextFloat() * -600;
					float y = terrain.getHeight(x, z);
					engine.getRegistry().register(new EntityImpl(engine, "acl.test.entity.impl3", "impl3",
							new Vector3f(x, y, z), 0, rand.nextFloat() * 360, 0, 0.9f, model, lights));
				}
			}
			
			skybox = new RenderOnlySkybox(engine, "acl.test.skybox.ro", "ro",
				engine.getModelLoader().loadCubeMap(new String[] { 
					"res/right.png", "res/left.png", "res/top.png",
					"res/bottom.png", "res/back.png", "res/front.png" 
				}), engine.getModelLoader().loadCubeMap(new String[] {
					"res/nightRight.png", "res/nightLeft.png", "res/nightTop.png",
					"res/nightBottom.png", "res/nightBack.png", "res/nightFront.png" 
			}));
			
			//engine.getRegistry().getProperty("internal:celShadingLevels").set(3);
		}

		@Override
		public void onUpdate() {
			engine.getCamera().onUpdate();
			engine.getTerrainHandler().onUpdate();
			engine.getEntityHandler().onUpdate();
			skybox.onUpdate();
			mp.onUpdate();
		}

		@Override
		public void onRender(Graphics g) {
			engine.getTerrainHandler().onRender(g);
			engine.getEntityHandler().onRender(g);
			skybox.onRender(g);
			waterShader.start();
			g.drawWaterQuad(200, -150, 0, 50, waterShader);
			waterShader.stop();
			//gui.onRender(g);
		}

		@Override
		public void onInit() {
			
		}

		@Override
		public void onExit() {
			
		}

		@Override
		public void onKeyPress(int key) {
			if (key == KeyInput.KEY_ESCAPE)
				engine.exit();
		}

		@Override
		public void onKeyRelease(int key) {
			
		}

		@Override
		public void onKeyHold(int key) {
			
		}
		
	}
	
	@Batch
	private static class EntityImpl extends Entity3D {

		private List<Light> lights;
		
		public EntityImpl(Engine engine, String registryID, String localizedID, Vector3f pos, float xRot, float yRot,
				float zRot, float scale, TexturedModel model, List<Light> lights) {
			super(engine, registryID, localizedID, pos, xRot, yRot, zRot, scale, model, engine.getRegistry().getShader("internal:static"), 0);
			this.lights = lights;
		}

		@Override
		public void onUpdate() {
			//Vector3f.add((Vector3f) pos, new Vector3f(0, 0, -0.1f), (Vector3f) pos);
			//xRot += 1f;
			yRot += 1f;
		}

		@Override
		public void onRender(Graphics g) {
			shader.start();
			((StaticShader) shader).loadLights(lights);
			((StaticShader) shader).loadViewMatrix(engine.getCamera());
			g.drawEntity(this, (StaticShader) shader);
			shader.stop();
		}
		
	}
	
	private static class Player extends Entity3D implements KeyControllable {

		private static final float VELOCITY_FACTOR = 40f;
		private static final float TURN_SPEED = 160f;
		private static final float GRAVITY = -50f;
		private static final float JUMP_POWER = 30;
		
		private List<Light> lights;
		private float currentSpeed;
		private float currentTurnSpeed;
		private float currentUpSpeed;
		private boolean airborne;
		private Property secondsPerFrame;
		
		public Player(Engine engine, String registryID, String localizedID, Vector3f pos, float xRot, float yRot,
				float zRot, float scale, TexturedModel model, List<Light> lights) {
			super(engine, registryID, localizedID, pos, xRot, yRot, zRot, scale, model, engine.getRegistry().getShader("internal:static"), 0);
			this.lights = lights;
			this.secondsPerFrame = engine.getRegistry().getProperty("internal:secondsPerFrame");
		}

		@Override
		public void onUpdate() {
			yRot += currentTurnSpeed * (float) secondsPerFrame.get();
			float distance = currentSpeed * (float) secondsPerFrame.get();
			float dx = (float) (distance * Math.sin(Math.toRadians(yRot)));
			float dz = (float) (distance * Math.cos(Math.toRadians(yRot)));
			((Vector3f) pos).x += dx;
			((Vector3f) pos).z += dz;
			currentUpSpeed += GRAVITY * (float) secondsPerFrame.get();
			((Vector3f) pos).y += currentUpSpeed * (float) secondsPerFrame.get();
			Terrain t = Utils.Dim3.getTerrainBelowEntity(engine.getTerrainHandler().getTerrains(), this);
			float terrainHeight = t == null ? 0 : t.getHeight(((Vector3f) pos).x, ((Vector3f) pos).z);
			if (((Vector3f) pos).y < terrainHeight) {
				currentUpSpeed = 0;
				((Vector3f) pos).y = terrainHeight;
				airborne = false;
			}
		}

		@Override
		public void onRender(Graphics g) {
			shader.start();
			((StaticShader) shader).loadLights(lights);
			((StaticShader) shader).loadViewMatrix(engine.getCamera());
			g.drawEntity(this, (StaticShader) shader);
			shader.stop();
		}

		@Override
		public void onKeyPress(int key) {
			if (key == KeyInput.KEY_W)
				currentSpeed = VELOCITY_FACTOR;
			else if (key == KeyInput.KEY_A)
				currentTurnSpeed = TURN_SPEED;
			else if (key == KeyInput.KEY_S)
				currentSpeed = -VELOCITY_FACTOR;
			else if (key == KeyInput.KEY_D)
				currentTurnSpeed = -TURN_SPEED;
			else if (key == KeyInput.KEY_SPACE && !airborne) {
				currentUpSpeed = JUMP_POWER;
				airborne = true;
			} else if (key == KeyInput.KEY_LSHIFT)
				currentSpeed *= 3;
		}

		@Override
		public void onKeyRelease(int key) {
			if (key == KeyInput.KEY_W)
				currentSpeed = 0;
			else if (key == KeyInput.KEY_A)
				currentTurnSpeed = 0;
			else if (key == KeyInput.KEY_S)
				currentSpeed = 0;
			else if (key == KeyInput.KEY_D)
				currentTurnSpeed = 0;
			else if (key == KeyInput.KEY_LSHIFT)
				currentSpeed = currentSpeed == 0 ? 0 : (currentSpeed / Math.abs(currentSpeed)) * VELOCITY_FACTOR;
		}

		@Override
		public void onKeyHold(int key) {
			
		}
		
	}
	
}
