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
 * In order to establish a new game instance, an instance of this class must be created followed by calling the <tt>run</tt> method.
 * The calling of the constructor initializes the engine and all associated objects, and as such,
 * no objects contained within the <tt>Engine</tt> instance or any methods related to the LWJGL framework may be called prior to this.
 * Additionally, the screen width and height as well as the window title may be set within the <tt>Engine</tt> constructor.
 * By default, the screen dimensions are 640x480 and the title is blank.
 * </p>
 * <p>
 * Upon calling the <tt>run</tt> method, the primary game loop will begin and will not terminate unless the <tt>exit</tt> or <tt>forceExit</tt> method is called.
 * As such, any initializations that the user may need to complete must be performed after the engine initialization and before the call to <tt>run</tt>.
 * This may include, but is not limited to, any calls to the <tt>Registry</tt>, any non-engine related initializations, or changes to internal or user-defined properties.
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
	
	private int recordedFPS;
	private long lastFPS;
	private int fps;
	
	/** 
	 * Constructs a new <tt>Engine</tt> with the specified <tt>screenWidth</tt>, <tt>screenHeight</tt>, <tt>title</tt>, and <tt>gameType</tt>.
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
	 * Constructs a new <tt>Engine</tt> with the specified <tt>screenWidth</tt> and <tt>screenHeight</tt>.
	 * <p>
	 * The <tt>title</tt> by default is left blank and the <tt>gameType</tt> is 0.
	 * </p>
	 * @param screenWidth The width of the screen
	 * @param screenHeight The height of the screen
	 */
	public Engine(int screenWidth, int screenHeight) {
		this(screenWidth, screenHeight, "", 0);
	}
	
	/** 
	 * Constructs a new <tt>Engine</tt> with the specified <tt>screenWidth</tt>, <tt>screenHeight</tt>, and <tt>gameType</tt>.
	 * <p>
	 * The <tt>title</tt> by default is left blank.
	 * </p>
	 * @param screenWidth The width of the screen
	 * @param screenHeight The height of the screen
	 * @param gameType The dimensions that the game will be. 0 = 2D, 1 = 3D
	 */
	public Engine(int screenWidth, int screenHeight, int gameType) {
		this(screenWidth, screenHeight, "", gameType);
	}
	
	/** 
	 * Constructs a new <tt>Engine</tt> with the specified <tt>title</tt>.
	 * <p>
	 * The <tt>screenWidth</tt>, <tt>screenHeight</tt>, and <tt>gameType</tt> by default are 640, 480, and 0, respectively.
	 * </p>
	 * @param title The title of the screen
	 */
	public Engine(String title) {
		this(640, 480, title, 0);
	}
	
	/** 
	 * Constructs a new <tt>Engine</tt> with the specified <tt>title</tt> and <tt>gameType</tt>.
	 * <p>
	 * The <tt>screenWidth</tt> and <tt>screenHeight</tt> by default are 640 and 480, respectively.
	 * </p>
	 * @param title The title of the screen
	 * @param gameType The dimensions that the game will be. 0 = 2D, 1 = 3D
	 */
	public Engine(String title, int gameType) {
		this(640, 480, title, gameType);
	}
	
	/** 
	 * Constructs a new <tt>Engine</tt>.
	 * <p>
	 * The <tt>screenWidth</tt>, <tt>screenHeight</tt>, and <tt>title</tt> by default are 640, 480, and blank, respectively.
	 * </p>
	 * @param gameType The dimensions that the game will be. 0 = 2D, 1 = 3D
	 */
	public Engine(int gameType) {
		this(640, 480, "", gameType);
	}
	
	/** 
	 * Constructs a new <tt>Engine</tt>.
	 * <p>
	 * The <tt>screenWidth</tt>, <tt>screenHeight</tt>, <tt>title</tt>, and <tt>gameType</tt> by default are 640, 480, blank, and 0, respectively.
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
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.fps", "acl_internal_fps", 0, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.showFPS", "acl_internal_showFPS", false, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.entityDuration", "acl_internal_entityDuration", true, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.entityCollision2D", "acl_internal_entityCollision2D", gameType == 0, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.entityCollision3D", "acl_internal_entityCollision3D", gameType == 1, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.shaderFogDensity", "acl_internal_shaderFogDensity", 0.007f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.shaderFogGradient", "acl_internal_shaderFogGradient", 1.5f, true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		registry.register(Utils.addProperty(internalProperties, new Property(this, "acl.prop.clearColor", "acl_internal_clearColor", new Vector3f(0.5f, 0.5f, 0.5f), true, false, Optional.empty(), OperationLocation.DO_NOT_RUN)));
		
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
				Display.setVSyncEnabled(true);
				
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
	 * Additionally, the frames per second counter is controlled by this method and can be shown and hidden via the <tt>showFPS</tt> property.
	 * </p><p>
	 * Note: This method will continue to run as long as the <tt>running</tt> property is true and the user has not requested to close the window of the program.
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
				updateFPS();
				
				graphics.setFont(registry.getFont("internal:default"));
				Color prev = graphics.getColor();
				graphics.setColor(Color.yellow);
				graphics.drawString((int) registry.getProperty("internal:screenWidth").get() - 50, 10, String.valueOf(recordedFPS));
				graphics.setColor(prev);
			}
			
			Display.update();
		}
		
		finalizeAndExit();
	}
	
	/** 
	 * Destroys the display and terminates the program.
	 * Additionally, this method will attempt to call the <tt>onExit</tt> method of the current state as specified by the <tt>StateHandler</tt>.
	 * If the current state is null, <tt>onExit</tt> will not be called.
	 * <p>
	 * Caution is advised concerning this method: any code after it is called will not run, as this method calls the <tt>System.exit</tt> function.
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
	 * it only sets the <tt>running</tt> property to <tt>false</tt> which will terminate the game loop upon its next cycle.
	 * Any code within the game loop after this method is called may also be called, as the <tt>run</tt> method must complete its current cycle before checking game loop conditions.
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
	
	/** Updates the internal frames per second counter and saves it to the <tt>fps</tt> property. */
	private void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			lastFPS = getTime();
			recordedFPS = fps;
			fps = 0;
		}
		fps++;
		registry.getProperty("internal:fps").set(recordedFPS);
	}
	
	/** 
	 * Gets the {@link Registry} instance as maintained by the engine.
	 * @return An instance of the <tt>Registry</tt> class
	 */
	public Registry getRegistry() {
		return registry;
	}
	
	/** 
	 * Gets the {@link Graphics} instance as maintained by the engine.
	 * @return An instance of the <tt>Graphics</tt> class
	 */
	public Graphics getGraphics() {
		return graphics;
	}
	
	/** 
	 * Gets the {@link IOHandler} instance as maintained by the engine.
	 * @return An instance of the <tt>IOHandler</tt> class
	 */
	public IOHandler getIOHandler() {
		return ioHandler;
	}
	
	/** 
	 * Gets the {@link AudioHandler} instance as maintained by the engine.
	 * @return An instance of the <tt>AudioHandler</tt> class
	 */
	public AudioHandler getAudioHandler() {
		return aHandler;
	}
	
	/** 
	 * Gets the {@link PropertyHandler} instance as maintained by the engine.
	 * @return An instance of the <tt>PropertyHandler</tt> class
	 */
	public PropertyHandler getPropertyHandler() {
		return pHandler;
	}
	
	/** 
	 * Gets the {@link EntityHandler} instance as maintained by the engine.
	 * @return An instance of the <tt>EntityHandler</tt> class
	 */
	public EntityHandler getEntityHandler() {
		return eHandler;
	}
	
	/** 
	 * Gets the {@link StateHandler} instance as maintained by the engine.
	 * @return An instance of the <tt>StateHandler</tt> class
	 */
	public StateHandler getStateHandler() {
		return sHandler;
	}
	
	/** 
	 * Gets the {@link TerrainHandler} instance as maintained by the engine.
	 * @return An instance of the <tt>TerrainHandler</tt> class
	 */
	public TerrainHandler getTerrainHandler() {
		return tHandler;
	}
	
	/** 
	 * Gets the {@link Random} instance as maintained by the engine.
	 * @return An instance of the <tt>Random</tt> class as supplied via {@link ThreadLocalRandom}
	 */
	public Random getRand() {
		return rand;
	}
	
	/** 
	 * Gets the {@link ModelLoader} instance as maintained by the engine.
	 * @return An instance of the <tt>ModelLoader</tt> class
	 */
	public ModelLoader getModelLoader() {
		return modelLoader;
	}
	
	/** 
	 * Gets the {@link Camera} instance as maintained by the engine.
	 * @return An instance of the <tt>Camera</tt> class
	 */
	public Camera getCamera() {
		return camera;
	}
	
	/**
	 * Sets the <tt>camera</tt> to the specified instance of {@link Camera}.
	 * This method is intended for internal use only and should not be called by the user.
	 * @param camera The instance of <tt>Camera</tt> to set
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