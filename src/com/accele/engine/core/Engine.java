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

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import com.accele.engine.entity.EntityHandler;
import com.accele.engine.gfx.Camera;
import com.accele.engine.gfx.Graphics;
import com.accele.engine.gfx.StoredFont;
import com.accele.engine.gfx.shader.StaticShader;
import com.accele.engine.gfx.shader.TerrainShader;
import com.accele.engine.io.IOHandler;
import com.accele.engine.io.KeyInput;
import com.accele.engine.io.MouseInput;
import com.accele.engine.model.ModelLoader;
import com.accele.engine.property.OperationLocation;
import com.accele.engine.property.Property;
import com.accele.engine.property.PropertyHandler;
import com.accele.engine.sfx.AudioHandler;
import com.accele.engine.state.StateHandler;
import com.accele.engine.terrain.TerrainHandler;
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
		registry = new Registry(this);
		ioHandler = new IOHandler(this);
		aHandler = new AudioHandler(this);
		eHandler = new EntityHandler(this);
		sHandler = new StateHandler(this);
		tHandler = new TerrainHandler();
		rand = ThreadLocalRandom.current();
		modelLoader = new ModelLoader();
		
		List<Property> internalProperties = new ArrayList<>();
		
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.version", "acl_internal_version", "4.1.2", true, true, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.aspectRatio", "acl_internal_aspectRatio", (float) screenWidth / (float) screenHeight, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.screenWidth", "acl_internal_screenWidth", screenWidth, true, false, Optional.of((engine, value) -> {
			engine.getRegistry().getProperty("aspectRatio").set(((Integer) value).floatValue() / ((Integer) engine.getRegistry().getProperty("screenHeight").get()).floatValue());
		}), OperationLocation.RUN_ON_SET)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.screenHeight", "acl_internal_screenHeight", screenHeight, true, false, Optional.of((engine, value) -> {
			engine.getRegistry().getProperty("aspectRatio").set(((Integer) engine.getRegistry().getProperty("screenWidth").get()).floatValue() / ((Integer) value).floatValue());
		}), OperationLocation.RUN_ON_SET)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.title", "acl_internal_title", title, true, false, Optional.of((engine, value) -> {
			Display.setTitle((String) value);
		}), OperationLocation.RUN_ON_SET)));
		
		init(internalProperties, gameType);
		
		graphics = new Graphics(this);
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
	
	/** Initializes various internal values utilized by the engine. */
	private void init(List<Property> internalProperties, int gameType) {
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.gameType", "acl_internal_gameType", gameType, true, true, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.musicMute", "acl_internal_musicMute", false, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.soundMute", "acl_internal_soundMute", false, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.musicVolume", "acl_internal_musicVolume", 0f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.soundVolume", "acl_internal_soundVolume", 0f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.cycleIO", "acl_internal_cycleIO", true, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, propertyFPS = new Property(this, "acl.prop.fps", "acl_internal_fps", 0, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.showFPS", "acl_internal_showFPS", false, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.entityDuration", "acl_internal_entityDuration", true, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.entityCollision2D", "acl_internal_entityCollision2D", gameType == 0, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.entityCollision3D", "acl_internal_entityCollision3D", gameType == 1, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.shaderFogDensity", "acl_internal_shaderFogDensity", 0.007f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.shaderFogGradient", "acl_internal_shaderFogGradient", 1.5f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.clearColor", "acl_internal_clearColor", new Vector3f(0.5f, 0.5f, 0.5f), true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, propertySecondsPerFrame = new Property(this, "acl.prop.secondsPerFrame", "acl_internal_secondsPerFrame", 0f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, propertyTargetFPS = new Property(this, "acl.prop.targetFPS", "acl_internal_targetFPS", 60, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		
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
				Display.create(new PixelFormat(), attribs);
				Display.setTitle((String) registry.getProperty("internal:title").get());
				Display.setResizable(false);
				//Display.setVSyncEnabled(true);
				
				glEnable(GL_DEPTH_TEST);
				Utils.Dim3.enableCulling();
				Vector3f clearColor = (Vector3f) registry.getProperty("internal:clearColor").get();
				glClearColor(clearColor.x, clearColor.y, clearColor.z, 1.0f);
				glViewport(0, 0, (int) registry.getProperty("internal:screenWidth").get(), (int) registry.getProperty("internal:screenHeight").get());
			} else
				throw new IllegalArgumentException("Invalid gameType \"" + gameType + "\".");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		StaticShader staticShader = new StaticShader("acl.shader.static", "acl_internal_static");
		staticShader.start();
		staticShader.loadProjectionMatrix(Utils.Dim3.createProjectionMatrix(this));
		staticShader.stop();
		registry.register(staticShader);
		TerrainShader terrainShader = new TerrainShader("acl.shader.terrain", "acl_internal_terrain");
		terrainShader.start();
		terrainShader.loadProjectionMatrix(Utils.Dim3.createProjectionMatrix(this));
		terrainShader.connectTextureUnits();
		terrainShader.stop();
		registry.register(terrainShader);
		
		
		registry.register(new StoredFont("acl.font.default", "acl_internal_default", new Font("Arial", 0, 24)));
		
		eHandler.init();
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
		Property cycleIO = registry.getProperty("internal:cycleIO");
		Property showFPS = registry.getProperty("internal:showFPS");
		Property gameType = registry.getProperty("internal:gameType");
		pHandler.setGameRunning(true);
		
		while (pHandler.isGameRunning() && !Display.isCloseRequested()) {
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
		registry.getAllShaders().forEach(c -> c.cleanUp());
		modelLoader.clear();
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
		propertySecondsPerFrame.set((time - lastFPS) / 1000f);
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
	 * Gets the current time of the program, in milliseconds.
	 * @return The time of the program, in milliseconds
	 */
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
}
