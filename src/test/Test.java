package test;

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
import com.accele.engine.io.KeyControllable;
import com.accele.engine.io.KeyInput;
import com.accele.engine.model.TexturedModel;
import com.accele.engine.property.Property;
import com.accele.engine.state.State;
import com.accele.engine.terrain.DefaultTerrainWithHeightMap;
import com.accele.engine.util.Batch;
import com.accele.engine.util.Resource;
import com.accele.engine.util.Utils;

public class Test {

	private Engine engine;
	
	public Test() {
		engine = new Engine(1280, 720, 1);
		engine.getRegistry().getProperty("internal:title").set("Test AcceleEngine v" + engine.getRegistry().getProperty("internal:version").get());
		engine.getRegistry().registerAll(new StateImpl(engine, "acl.test.state.impl", "impl"));
		engine.getStateHandler().setCurrentState("impl", false, false);
		engine.run();
	}
	
	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", "C:/Users/MrKnowname/Desktop/BesiegeTest/lib/native/");
		
		new Test();
	}
	
	private static class StateImpl extends State implements KeyControllable {

		Light sun;
		
		public StateImpl(Engine engine, String registryID, String localizedID) {
			super(engine, registryID, localizedID);
			engine.getRegistry().getProperty("internal:shaderFogDensity").set(0.00000001f);
			//engine.getRegistry().getProperty("internal:shaderFogGradient").set(0);
			engine.getEntityHandler().setStaticShaderLight(sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1)));
			ModelTexture texture;
			Player player;
			engine.getRegistry().register(new TerrainTexture("acl.test.texture.terrain0", "terrain0", new Resource("res/grass.png", Utils.DEFAULT_TEXTURE_LOADER), new Resource("res/flowers.png", Utils.DEFAULT_TEXTURE_LOADER), new Resource("res/mud.png", Utils.DEFAULT_TEXTURE_LOADER), new Resource("res/path.png", Utils.DEFAULT_TEXTURE_LOADER)));
			engine.getRegistry().register(new Texture("acl.test.texture.blendMap", "blendMap", new Resource("res/blendMap.png", Utils.DEFAULT_TEXTURE_LOADER)));
			engine.getRegistry().register(texture = new ModelTexture("acl.test.texture.default", "default", new Resource("res/stall_texture.png", Utils.DEFAULT_TEXTURE_LOADER), 10, 1, false, false));
			engine.getRegistry().register(new EntityImpl(engine, "acl.test.entity.impl", "impl", new Vector3f(0, -5, -25), 0, 0, 0, 1, new TexturedModel(engine.getModelLoader(), engine.getModelLoader().loadToVAO(new Resource("res/stall.obj", Utils.DEFAULT_MODEL_LOADER)), texture)));
			engine.getRegistry().registerAll(player = new Player(engine, "acl.test.entity.player", "player", new Vector3f(0, -5, -25), 0, 0, 0, 1, new TexturedModel(engine.getModelLoader(), engine.getModelLoader().loadToVAO(new Resource("res/person.obj", Utils.DEFAULT_MODEL_LOADER)), texture), sun));
			engine.getRegistry().registerAll(new DefaultEntityCamera(player));
			engine.getRegistry().register(new DefaultTerrainWithHeightMap(engine, "acl.test.thm", "thm", 800, 128, 0, 0, (TerrainTexture) engine.getRegistry().getTexture("terrain0"), engine.getRegistry().getTexture("blendMap"), new Resource("res/heightMap.png", Utils.DEFAULT_IMAGE_LOADER), sun));
			engine.getRegistry().register(new DefaultTerrainWithHeightMap(engine, "acl.test.thm", "thm", 800, 128, 1, 0, (TerrainTexture) engine.getRegistry().getTexture("terrain0"), engine.getRegistry().getTexture("blendMap"), new Resource("res/heightMap.png", Utils.DEFAULT_IMAGE_LOADER), sun));
		}

		@Override
		public void onUpdate() {
			engine.getCamera().onUpdate();
			engine.getTerrainHandler().onUpdate();
			engine.getEntityHandler().onUpdate();
		}

		@Override
		public void onRender(Graphics g) {
			engine.getTerrainHandler().onRender(g);
			engine.getEntityHandler().onRender(g);
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

		private Light light;
		
		public EntityImpl(Engine engine, String registryID, String localizedID, Vector3f pos, float xRot, float yRot,
				float zRot, float scale, TexturedModel model) {
			super(engine, registryID, localizedID, pos, xRot, yRot, zRot, scale, model, engine.getRegistry().getShader("internal:static"));
			this.light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));
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
			((StaticShader) shader).loadLight(light);
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
		private static final float TERRAIN_HEIGHT = 0;
		
		private Light light;
		private float currentSpeed;
		private float currentTurnSpeed;
		private float currentUpSpeed;
		private boolean airborne;
		private Property secondsPerFrame;
		
		public Player(Engine engine, String registryID, String localizedID, Vector3f pos, float xRot, float yRot,
				float zRot, float scale, TexturedModel model, Light light) {
			super(engine, registryID, localizedID, pos, xRot, yRot, zRot, scale, model, engine.getRegistry().getShader("internal:static"));
			this.light = light;
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
			if (((Vector3f) pos).y < TERRAIN_HEIGHT) {
				currentUpSpeed = 0;
				((Vector3f) pos).y = TERRAIN_HEIGHT;
				airborne = false;
			}
		}

		@Override
		public void onRender(Graphics g) {
			shader.start();
			((StaticShader) shader).loadLight(light);
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
			}
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
		}

		@Override
		public void onKeyHold(int key) {
			
		}
		
	}
	
}
