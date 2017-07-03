package com.accele.engine.core;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;

import com.accele.engine.entity.EntityHandler;
import com.accele.engine.gfx.Camera;
import com.accele.engine.gfx.Graphics;
import com.accele.engine.gfx.StoredFont;
import com.accele.engine.gfx.internal.WaterFrameBufferHandler;
import com.accele.engine.gfx.shader.GUIShader;
import com.accele.engine.gfx.shader.Shader;
import com.accele.engine.gfx.shader.SkyboxShader;
import com.accele.engine.gfx.shader.StaticShader;
import com.accele.engine.gfx.shader.TerrainShader;
import com.accele.engine.gfx.shader.WaterShader;
import com.accele.engine.io.IOHandler;
import com.accele.engine.io.KeyInput;
import com.accele.engine.io.MouseInput;
import com.accele.engine.model.ModelLoader;
import com.accele.engine.net.Client;
import com.accele.engine.net.Server;
import com.accele.engine.property.OperationLocation;
import com.accele.engine.property.Property;
import com.accele.engine.property.PropertyHandler;
import com.accele.engine.sfx.AudioHandler;
import com.accele.engine.state.StateHandler;
import com.accele.engine.terrain.TerrainHandler;
import com.accele.engine.util.ModifiableObjectContainer;
import com.accele.engine.util.Utils;

/** 
 * The primary class for the AcceleEngine library.
 * <p>
 * All interactions with any part of the engine must be performed through an instance of this class.
 * In order to establish a new game instance, an instance of this class must be created followed by calling the {@code run} method.
 * The calling of the constructor initializes the engine and all associated objects, and as such,
 * no objects contained within the {@code Engine} instance or any methods related to the LWJGL framework may be called prior to this.
 * Additionally, the screen width and height as well as the window title may be set within the {@code Engine} constructor.
 * By default, the screen dimensions are 640x480 and the title is blank.
 * </p>
 * <p>
 * Upon calling the {@code run} method, the primary game loop will begin and will not terminate unless the {@code exit} or {@code forceExit} method is called.
 * As such, any initializations that the user may need to complete must be performed after the engine initialization and before the call to {@code run}.
 * This may include, but is not limited to, any calls to the {@code Registry}, any non-engine related initializations, or changes to internal or user-defined properties.
 * </p>
 * 
 * @author William Garland
 * @since 1.0.0
 */
public final class Engine {

	private Registry registry;
	private Graphics graphics;
	private IOHandler ioHandler;
	private AudioHandler aHandler;
	private PropertyHandler pHandler;
	private EntityHandler eHandler;
	private StateHandler sHandler;
	private TerrainHandler tHandler;
	private Random rand;
	private ModelLoader modelLoader;
	private Camera camera;
	private WaterFrameBufferHandler wHandler;
	
	private Server server;
	private Client client;
	private boolean runServer;
	
	private long lastFPS;
	private Property propertyFPS;
	private Property propertySecondsPerFrame;
	private Property propertyTargetFPS;
	
	/** 
	 * Constructs a new {@code Engine} with the specified {@code screenWidth}, {@code screenHeight}, {@code title}, and {@code gameType}.
	 * @param screenWidth The width of the screen
	 * @param screenHeight The height of the screen
	 * @param title The title of the screen
	 * @param gameType The dimensions that the game will be. 0 = 2D, 1 = 3D
	 */
	public Engine(int screenWidth, int screenHeight, String title, int gameType) {
		preInit();
		
		List<Property> internalProperties = new ArrayList<>();
		
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.version", "acl_internal_version", "4.1.2", true, true, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.aspectRatio", "acl_internal_aspectRatio", (float) screenWidth / (float) screenHeight, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.screenWidth", "acl_internal_screenWidth", screenWidth, true, false, Optional.of((engine, value) -> {
			engine.getRegistry().getProperty("aspectRatio").set(((Integer) value).floatValue() / ((Integer) engine.getRegistry().getProperty("screenHeight").get()).floatValue());
		}), OperationLocation.RUN_ON_SET)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.screenHeight", "acl_internal_screenHeight", screenHeight, true, false, Optional.of((engine, value) -> {
			engine.getRegistry().getProperty("aspectRatio").set(((Integer) engine.getRegistry().getProperty("screenWidth").get()).floatValue() / ((Integer) value).floatValue());
		}), OperationLocation.RUN_ON_SET)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.title", "acl_internal_title", title, true, false, Optional.of((engine, value) -> {
			Display.setTitle((String) value);
		}), OperationLocation.RUN_ON_SET)));
		
		ModifiableObjectContainer<Matrix4f> projectionMatrix = new ModifiableObjectContainer<>(null);
		init(internalProperties, gameType, projectionMatrix);
		
		graphics = new Graphics(this, projectionMatrix.value);
		pHandler = new PropertyHandler(this, internalProperties.toArray(new Property[internalProperties.size()]));
	}
	
	/** 
	 * Constructs a new {@code Engine} with the specified {@code screenWidth} and {@code screenHeight}.
	 * <p>
	 * The {@code title} by default is left blank and the {@code gameType} is 0.
	 * </p>
	 * @param screenWidth The width of the screen
	 * @param screenHeight The height of the screen
	 */
	public Engine(int screenWidth, int screenHeight) {
		this(screenWidth, screenHeight, "", 0);
	}
	
	/** 
	 * Constructs a new {@code Engine} with the specified {@code screenWidth}, {@code screenHeight}, and {@code gameType}.
	 * <p>
	 * The {@code title} by default is left blank.
	 * </p>
	 * @param screenWidth The width of the screen
	 * @param screenHeight The height of the screen
	 * @param gameType The dimensions that the game will be. 0 = 2D, 1 = 3D
	 */
	public Engine(int screenWidth, int screenHeight, int gameType) {
		this(screenWidth, screenHeight, "", gameType);
	}
	
	/** 
	 * Constructs a new {@code Engine} with the specified {@code title}.
	 * <p>
	 * The {@code screenWidth}, {@code screenHeight}, and {@code gameType} by default are 640, 480, and 0, respectively.
	 * </p>
	 * @param title The title of the screen
	 */
	public Engine(String title) {
		this(640, 480, title, 0);
	}
	
	/** 
	 * Constructs a new {@code Engine} with the specified {@code title} and {@code gameType}.
	 * <p>
	 * The {@code screenWidth} and {@code screenHeight} by default are 640 and 480, respectively.
	 * </p>
	 * @param title The title of the screen
	 * @param gameType The dimensions that the game will be. 0 = 2D, 1 = 3D
	 */
	public Engine(String title, int gameType) {
		this(640, 480, title, gameType);
	}
	
	/** 
	 * Constructs a new {@code Engine}.
	 * <p>
	 * The {@code screenWidth}, {@code screenHeight}, and {@code title} by default are 640, 480, and blank, respectively.
	 * </p>
	 * @param gameType The dimensions that the game will be. 0 = 2D, 1 = 3D
	 */
	public Engine(int gameType) {
		this(640, 480, "", gameType);
	}
	
	/** 
	 * Constructs a new {@code Engine}.
	 * <p>
	 * The {@code screenWidth}, {@code screenHeight}, {@code title}, and {@code gameType} by default are 640, 480, blank, and 0, respectively.
	 * </p>
	 */
	public Engine() {
		this(640, 480, "", 0);
	}
	
	/**
	 * Initializes various internal values utilized by the engine.
	 * <p>
	 * This is the primary stage of initialization and runs directly
	 * before the {@code init} method.
	 * </p>
	 */
	private void preInit() {
		registry = new Registry(this);
		ioHandler = new IOHandler(this);
		aHandler = new AudioHandler(this);
		eHandler = new EntityHandler(this);
		sHandler = new StateHandler(this);
		tHandler = new TerrainHandler();
		rand = ThreadLocalRandom.current();
		modelLoader = new ModelLoader();
		
		if (JOptionPane.showConfirmDialog(null, "Do you want to run the server?") == 0) {
			server = new Server(this);
			runServer = true;
		}
		
		client = new Client(this, "localhost");
	}
	
	/**
	 * Initializes various internal values utilized by the engine.
	 * <p>
	 * This method runs directly after the {@code preInit} method and
	 * directly before the {@code postInit} method.
	 * </p>
	 * 
	 * @param internalProperties The internal properties to initialize
	 * @param gameType The type of game the engine is running
	 * @param projectionMatrix The projection matrix for the engine
	 */
	private void init(List<Property> internalProperties, int gameType, ModifiableObjectContainer<Matrix4f> projectionMatrix) {
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.gameType", "acl_internal_gameType", gameType, true, true, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.musicMute", "acl_internal_musicMute", false, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.soundMute", "acl_internal_soundMute", false, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.musicVolume", "acl_internal_musicVolume", 0f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.soundVolume", "acl_internal_soundVolume", 0f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.cycleIO", "acl_internal_cycleIO", true, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, propertyFPS = new Property(this, "acl.prop.fps", "acl_internal_fps", 0, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.showFPS", "acl_internal_showFPS", false, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.entityDuration", "acl_internal_entityDuration", true, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.entityCollision2D", "acl_internal_entityCollision2D", gameType == 0, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.entityCollision3D", "acl_internal_entityCollision3D", gameType == 1, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.shaderFogDensity", "acl_internal_shaderFogDensity", 0.007f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.shaderFogGradient", "acl_internal_shaderFogGradient", 1.5f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.clearColor", "acl_internal_clearColor", new Vector3f(0.5f, 0.5f, 0.5f), true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, propertySecondsPerFrame = new Property(this, "acl.prop.secondsPerFrame", "acl_internal_secondsPerFrame", 0f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, propertyTargetFPS = new Property(this, "acl.prop.targetFPS", "acl_internal_targetFPS", 60, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.skyboxFogFadeUpperLimit", "acl_internal_skyboxFogFadeUpperLimit", 30.0f, true, false, Optional.of((engine, value) -> {
			SkyboxShader shader = (SkyboxShader) engine.getRegistry().getShader("internal:skybox");
			shader.start();
			shader.loadUpperLimit((float) value);
			shader.stop();
		}), OperationLocation.RUN_ON_SET)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.skyboxFogFadeLowerLimit", "acl_internal_skyboxFogFadeLowerLimit", 0f, true, false, Optional.of((engine, value) -> {
			SkyboxShader shader = (SkyboxShader) engine.getRegistry().getShader("internal:skybox");
			shader.start();
			shader.loadLowerLimit((float) value);
			shader.stop();
		}), OperationLocation.RUN_ON_SET)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.skyboxRotationSpeed", "acl_internal_skyboxRotationSpeed", 1f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.celShadingLevels", "acl_internal_celShadingLevels", 0, true, false, Optional.of((engine, value) -> {
			Shader shader = (StaticShader) engine.getRegistry().getShader("internal:static");
			shader.start();
			((StaticShader) shader).loadCelShadingLevels((int) value);
			shader.stop();
			shader = (TerrainShader) engine.getRegistry().getShader("internal:terrain");
			shader.start();
			((TerrainShader) shader).loadCelShadingLevels((int) value);
			shader.stop();
			shader = (SkyboxShader) engine.getRegistry().getShader("internal:skybox");
			shader.start();
			((SkyboxShader) shader).loadCelShadingLevels((int) value);
			shader.stop();
		}), OperationLocation.RUN_ON_SET)));
		registry.register(Utils.addAndReturn(internalProperties, new Property(this, "acl.prop.clipPlane", "acl_internal_clipPlane", new Vector4f(0, 0, 0, 0), true, false, Optional.of((engine, value) -> {
			Vector4f clipPlane = (Vector4f) value;
			
			if (clipPlane.x == -1 && clipPlane.y == -1 && clipPlane.z == -1 && clipPlane.w == -1)
				GL11.glDisable(GL11.GL_CLIP_PLANE0);
			else {
				GL11.glEnable(GL11.GL_CLIP_PLANE0);
				Shader shader = (StaticShader) engine.getRegistry().getShader("internal:static");
				shader.start();
				((StaticShader) shader).loadClipPlane((Vector4f) value);
				shader.stop();
				shader = (TerrainShader) engine.getRegistry().getShader("internal:terrain");
				shader.start();
				((TerrainShader) shader).loadClipPlane((Vector4f) value);
				shader.stop();
			}
		}), OperationLocation.RUN_ON_SET)));
		
		registry.register(new KeyInput(this));
		registry.register(new MouseInput(this));
		
		try {
			if (gameType == 0) {
				Display.setDisplayMode(new DisplayMode((int) registry.getProperty("internal:screenWidth").get(), (int) registry.getProperty("internal:screenHeight").get()));
				Display.setTitle((String) registry.getProperty("internal:title").get());
				Display.setResizable(false);
				Display.setVSyncEnabled(true);
				Display.create();
				
				glEnable(GL_TEXTURE_2D);
				glShadeModel(GL_SMOOTH);
				glDisable(GL_DEPTH_TEST);
				glDisable(GL_LIGHTING);

				glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				glClearDepth(1);

				glEnable(GL_BLEND);
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

				glViewport(0, 0, (int) registry.getProperty("internal:screenWidth").get(), (int) registry.getProperty("internal:screenHeight").get());
				glMatrixMode(GL_MODELVIEW);

				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();
				glOrtho(0, (int) registry.getProperty("internal:screenWidth").get(), (int) registry.getProperty("internal:screenHeight").get(), 0, 1, -1);
				glMatrixMode(GL_MODELVIEW);
				
				Util.checkGLError();
			} else if (gameType == 1) {
				ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
				
				Display.setDisplayMode(new DisplayMode((int) registry.getProperty("internal:screenWidth").get(), (int) registry.getProperty("internal:screenHeight").get()));
				Display.create(new PixelFormat().withDepthBits(24), attribs);
				Display.setTitle((String) registry.getProperty("internal:title").get());
				Display.setResizable(false);
				
				glEnable(GL_DEPTH_TEST);
				Utils.Dim3.enableCulling();
				Vector3f clearColor = (Vector3f) registry.getProperty("internal:clearColor").get();
				glClearColor(clearColor.x, clearColor.y, clearColor.z, 1.0f);
				glViewport(0, 0, (int) registry.getProperty("internal:screenWidth").get(), (int) registry.getProperty("internal:screenHeight").get());
				
				projectionMatrix.value = Utils.Dim3.createProjectionMatrix(this);
				
				StaticShader staticShader = new StaticShader(this, "acl.shader.static", "acl_internal_static");
				staticShader.start();
				staticShader.loadProjectionMatrix(projectionMatrix.value);
				staticShader.stop();
				registry.register(staticShader);
				TerrainShader terrainShader = new TerrainShader(this, "acl.shader.terrain", "acl_internal_terrain");
				terrainShader.start();
				terrainShader.loadProjectionMatrix(projectionMatrix.value);
				terrainShader.connectTextureUnits();
				terrainShader.stop();
				registry.register(terrainShader);
				registry.register(new GUIShader(this, "acl.shader.gui", "acl_internal_gui"));
				SkyboxShader skyboxShader = new SkyboxShader(this, "acl.shader.skybox", "acl_internal_skybox");
				skyboxShader.start();
				skyboxShader.loadLimits((float) registry.getProperty("internal:skyboxFogFadeUpperLimit").get(), (float) registry.getProperty("internal:skyboxFogFadeLowerLimit").get());
				skyboxShader.loadProjectionMatrix(projectionMatrix.value);
				skyboxShader.stop();
				registry.register(skyboxShader);
				WaterShader waterShader = new WaterShader(this, "acl.shader.water", "acl_internal_water");
				waterShader.start();
				waterShader.loadProjectionMatrix(projectionMatrix.value);
				waterShader.connectTextureUnits();
				waterShader.stop();
				registry.register(waterShader);
				
				wHandler = new WaterFrameBufferHandler(this);
				
				Util.checkGLError();
			} else
				throw new IllegalArgumentException("Invalid gameType \"" + gameType + "\".");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		registry.register(new StoredFont("acl.font.default", "acl_internal_default", new Font("Arial", 0, 24)));
		
		eHandler.init();
	}
	
	/**
	 * Initializes various internal values utilized by the engine.
	 * <p>
	 * Even though this method runs directly after the {@code init}
	 * method, it is not called in the constructor but rather the
	 * {@code run} method. This allows for any values that may need
	 * to be initialized before the primary game loop but after the
	 * primary initializations.
	 * </p>
	 */
	private void postInit() {
		
	}
	
	/**
	 * Starts the primary game loop.
	 * <p>
	 * This method controls the updating and rendering of every aspect of the engine.
	 * Additionally, the frames per second counter is controlled by this method and can be shown and hidden via the {@code showFPS} property.
	 * </p><p>
	 * Note: This method will continue to run as long as the {@code running} property is true and the user has not requested to close the window of the program.
	 * </p>
	 */
	public void run() {
		postInit();
		
		Property cycleIO = registry.getProperty("internal:cycleIO");
		Property showFPS = registry.getProperty("internal:showFPS");
		Property gameType = registry.getProperty("internal:gameType");
		pHandler.setGameRunning(true);
		
		if (runServer)
			server.start();
		client.start();
		
		while (pHandler.isGameRunning() && !Display.isCloseRequested()) {
			if ((int) gameType.get() == 0)
				glClear(GL_COLOR_BUFFER_BIT);
			else
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			aHandler.onUpdate();
			
			if ((boolean) cycleIO.get())
				ioHandler.onUpdate();
			
			sHandler.onUpdate();
			sHandler.onRender(graphics);
			
			if ((boolean) showFPS.get() && ((int) gameType.get()) == 0) {				
				graphics.setFont(registry.getFont("internal:default"));
				Color prev = graphics.getColor();
				graphics.setColor(Color.yellow);
				graphics.drawString((int) registry.getProperty("internal:screenWidth").get() - 50, 10, String.valueOf((int) propertyFPS.get()));
				graphics.setColor(prev);
			}
			
			Display.sync((int) propertyTargetFPS.get());
			Display.update();
			updateFPS();
		}
		
		finalizeAndExit();
	}
	
	/** 
	 * Destroys the display and terminates the program.
	 * Additionally, this method will attempt to call the {@code onExit} method of the current state as specified by the {@code StateHandler}.
	 * If the current state is null, {@code onExit} will not be called.
	 * <p>
	 * Caution is advised concerning this method: any code after it is called will not run, as this method calls the {@code System.exit} function.
	 * </p><p>
	 * Note: This method is intended for internal use and should not be called by the user.
	 * </p>
	 */
	private void finalizeAndExit() {
		if (sHandler.getCurrentState() != null)
			sHandler.getCurrentState().onExit();
		if ((int) registry.getProperty("internal:gameType").get() == 1) {
			registry.getAllShaders().forEach(c -> c.cleanUp());
			modelLoader.clear();
			wHandler.cleanUp();
		}
		Display.destroy();
		System.exit(0);
	}
	
	/**
	 * Prepares the engine for closing the program.
	 * <p>
	 * Note that this method does not actually terminate the program;
	 * it only sets the {@code running} property to {@code false} which will terminate the game loop upon its next cycle.
	 * Any code within the game loop after this method is called may also be called, as the {@code run} method must complete its current cycle before checking game loop conditions.
	 * </p><p>
	 * If a quicker termination is required, the {@link #forceExit()} method may be used.
	 * </p>
	 */
	public void exit() {
		pHandler.setGameRunning(false);
	}
	
	/**
	 * Destroys the display and immediately terminates the program.
	 * <p>
	 * The use of this method is not advised for normal terminations of the program as it does not perform proper checks for all necessary devices.
	 * For terminations under normal conditions, the {@link #exit()} method may be used instead.
	 * </p>
	 */
	public void forceExit() {
		Display.destroy();
		System.exit(1);
	}
	
	/** Updates the internal frames per second counter and saves it to the {@code fps} property. */
	private void updateFPS() {
		long time = getTime();
		long result = time - lastFPS;
		propertySecondsPerFrame.set(result != 0 ? result / 1000f : 0f);
		propertyFPS.set((int) (1f / (float) propertySecondsPerFrame.get()));
		lastFPS = time;
	}
	
	/** 
	 * Gets the {@link Registry} instance as maintained by the engine.
	 * @return An instance of the {@code Registry} class
	 */
	public Registry getRegistry() {
		return registry;
	}
	
	/** 
	 * Gets the {@link Graphics} instance as maintained by the engine.
	 * @return An instance of the {@code Graphics} class
	 */
	public Graphics getGraphics() {
		return graphics;
	}
	
	/** 
	 * Gets the {@link IOHandler} instance as maintained by the engine.
	 * @return An instance of the {@code IOHandler} class
	 */
	public IOHandler getIOHandler() {
		return ioHandler;
	}
	
	/** 
	 * Gets the {@link AudioHandler} instance as maintained by the engine.
	 * @return An instance of the {@code AudioHandler} class
	 */
	public AudioHandler getAudioHandler() {
		return aHandler;
	}
	
	/** 
	 * Gets the {@link PropertyHandler} instance as maintained by the engine.
	 * @return An instance of the {@code PropertyHandler} class
	 */
	public PropertyHandler getPropertyHandler() {
		return pHandler;
	}
	
	/** 
	 * Gets the {@link EntityHandler} instance as maintained by the engine.
	 * @return An instance of the {@code EntityHandler} class
	 */
	public EntityHandler getEntityHandler() {
		return eHandler;
	}
	
	/** 
	 * Gets the {@link StateHandler} instance as maintained by the engine.
	 * @return An instance of the {@code StateHandler} class
	 */
	public StateHandler getStateHandler() {
		return sHandler;
	}
	
	/** 
	 * Gets the {@link TerrainHandler} instance as maintained by the engine.
	 * @return An instance of the {@code TerrainHandler} class
	 */
	public TerrainHandler getTerrainHandler() {
		return tHandler;
	}
	
	/** 
	 * Gets the {@link Random} instance as maintained by the engine.
	 * @return An instance of the {@code Random} class as supplied via {@link ThreadLocalRandom}
	 */
	public Random getRand() {
		return rand;
	}
	
	/** 
	 * Gets the {@link ModelLoader} instance as maintained by the engine.
	 * @return An instance of the {@code ModelLoader} class
	 */
	public ModelLoader getModelLoader() {
		return modelLoader;
	}
	
	/** 
	 * Gets the {@link Camera} instance as maintained by the engine.
	 * @return An instance of the {@code Camera} class
	 */
	public Camera getCamera() {
		return camera;
	}
	
	/**
	 * Sets the {@code camera} to the specified instance of {@link Camera}.
	 * <p>
	 * This method is intended for internal use only and should not be called by the user.
	 * </p>
	 * @param camera The instance of {@code Camera} to set
	 */
	protected void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	/**
	 * Gets the {@link WaterFrameBufferHandler} instance as maintained by the engine.
	 * @return An instance of the {@code WaterFrameBufferHandler} class
	 */
	public WaterFrameBufferHandler getWaterFrameBufferHandler() {
		return wHandler;
	}
	
	/** 
	 * Gets the current time of the program, in milliseconds.
	 * @return The time of the program, in milliseconds
	 */
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public Client getClient() {
		return client;
	}
	
}
