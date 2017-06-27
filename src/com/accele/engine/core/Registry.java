package com.accele.engine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.accele.engine.entity.Entity;
import com.accele.engine.entity.EntityHandler;
import com.accele.engine.gfx.Camera;
import com.accele.engine.gfx.Light;
import com.accele.engine.gfx.StoredFont;
import com.accele.engine.gfx.Texture;
import com.accele.engine.gfx.shader.Shader;
import com.accele.engine.io.Input;
import com.accele.engine.io.KeyControllable;
import com.accele.engine.io.KeyInput;
import com.accele.engine.io.MouseControllable;
import com.accele.engine.io.MouseInput;
import com.accele.engine.model.Model;
import com.accele.engine.property.Property;
import com.accele.engine.sfx.Music;
import com.accele.engine.sfx.SFX;
import com.accele.engine.state.State;
import com.accele.engine.terrain.Terrain;
import com.accele.engine.terrain.TerrainHandler;

/**
 * This class acts as a storage medium for all objects essential to the engine.
 * <p>
 * In order to utilize a specific object, it must be registered in an instance of this class via the {@code register} or {@code registerAll} method.
 * All objects can be accessed from the central entry list via the {@code get} method or from the specialized list that it is in.
 * Objects that may be registered include any instances of {@link Input}, {@link SFX}, {@link State}, {@link Property}, {@link Texture}, or {@link StoredFont}.
 * </p>
 * <p>
 * No two objects may have the same {@code registryID} or {@code localizedID}.
 * Also note that any and all objects created by the engine itself will always have a {@code registryID} of the pattern "acl.type.name" and a {@code localizedID} of the pattern "acl_internal_name".
 * For example:
 * </p>
 * <blockquote>
 * The property {@code screenWidth} has a {@code registryID} of "acl.prop.screenWidth" and a {@code localizedID} of "acl_internal_screenWidth".
 * <p>
 * The default font has a {@code registryID} of "acl.font.default" and a {@code localizedID} of "acl_internal_default".
 * </p>
 * </blockquote>
 * <p>
 * For ease of use, when using the {@code localizedID} to access objects, all engine-created objects may be accessed without the use of "acl_internal_name" such that "name" may be used instead.
 * This can however lead to certain cases of ambiguity.
 * If a user-defined object is registered having the same {@code localizedID} as an engine-defined object (without the initial "acl_internal_"), 
 * the engine will prioritize the user-defined object over the engine-defined one.
 * If such is the case, the engine-defined object can be accessed via the "force-internal" tag, which is written as "internal:name".
 * </p>
 * 
 * @author William Garland
 * @since 1.0.0
 */
public final class Registry {

	private Engine engine;
	private Map<String, Indexable> entries;
	private Map<String, Input> inputs;
	private Map<String, SFX> sfx;
	private Map<String, State> states;
	private Map<String, Property> properties;
	private Map<String, Texture> textures;
	private Map<String, StoredFont> fonts;
	private Map<String, Shader> shaders;
	private Map<String, Model> models;
	private Map<String, Light> lights;
	
	/**
	 * Creates a new {@code Registry} instance.
	 * <p>
	 * This should only be called by the {@link Engine} class and should not be called by the user.
	 * </p>
	 */
	protected Registry(Engine engine) {
		this.engine = engine;
		this.entries = new HashMap<>();
		this.inputs = new HashMap<>();
		this.sfx = new HashMap<>();
		this.states = new HashMap<>();
		this.properties = new HashMap<>();
		this.textures = new HashMap<>();
		this.fonts = new HashMap<>();
		this.shaders = new HashMap<>();
		this.models = new HashMap<>();
		this.lights = new HashMap<>();
	}
	
	/**
	 * Registers the given {@code entry} to the {@code entries} map.
	 * <p>
	 * This method is intended for internal use only and should never be directly called by the user.
	 * </p>
	 * @return {@code true} if it was successful at adding the {@code entry} and {@code false} if it was not
	 */
	private boolean register(Indexable entry) {
		if (entries.containsKey(entry.getRegistryID())) {
			System.err.println("RegistryError: Duplicate entry \"" + entry.getRegistryID() + "\".");
			return false;
		} else {
			entries.put(entry.getRegistryID(), entry);
			return true;
		}
	}
	
	/**
	 * Registers the given instance of {@link Input} to the appropriate maps.
	 * @param <T> A subclass of {@code Input}
	 * @param entry The entry to be added
	 */
	public <T extends Input> void register(T entry) {
		if (register((Indexable) entry)) {
			inputs.put(entry.getLocalizedID(), entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link SFX} to the appropriate maps.
	 * @param <T> A subclass of {@code SFX}
	 * @param entry The entry to be added
	 */
	public <T extends SFX> void register(T entry) {
		if (register((Indexable) entry)) {
			sfx.put(entry.getLocalizedID(), entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link State} to the appropriate maps.
	 * @param <T> A subclass of {@code State}
	 * @param entry The entry to be added
	 */
	public <T extends State> void register(T entry) {
		if (register((Indexable) entry)) {
			states.put(entry.getLocalizedID(), entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link Property} to the appropriate maps.
	 * @param <T> An implementation of {@code Property}
	 * @param entry The entry to be added
	 */
	public <T extends Property> void register(T entry) {
		if (register((Indexable) entry)) {
			properties.put(entry.getLocalizedID(), entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link Texture} to the appropriate maps.
	 * @param <T> An implementation of {@code Texture}
	 * @param entry The entry to be added
	 */
	public <T extends Texture> void register(T entry) {
		if (register((Indexable) entry)) {
			textures.put(entry.getLocalizedID(), entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link StoredFont} to the appropriate maps.
	 * @param <T> An implementation of {@code StoredFont}
	 * @param entry The entry to be added
	 */
	public <T extends StoredFont> void register(T entry) {
		if (register((Indexable) entry)) {
			fonts.put(entry.getLocalizedID(), entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link KeyControllable}.
	 * <p>
	 * This instance of {@code register} does not actually register the entry to any particular map defined in the {@code Registry} class,
	 * rather it acts as a convenience method to add the entry to the controller list in {@link KeyInput}.
	 * </p>
	 * @param <T> An implementation of {@code KeyControllable}
	 * @param entry The entry to be added
	 */
	public <T extends KeyControllable> void register(T entry) {
		((KeyInput) inputs.get("acl_internal_keyboard")).addController(entry);
	}
	
	/**
	 * Registers the given instance of {@link MouseControllable}.
	 * <p>
	 * This instance of {@code register} does not actually register the entry to any particular map defined in the {@code Registry} class,
	 * rather it acts as a convenience method to add the entry to the controller list in {@link MouseInput}.
	 * </p>
	 * @param <T> An implementation of {@code MouseControllable}
	 * @param entry The entry to be added
	 */
	public <T extends MouseControllable> void register(T entry) {
		((MouseInput) inputs.get("acl_internal_mouse")).addController(entry);
	}
	
	/**
	 * Registers the given instance of {@link Entity}.
	 * <p>
	 * This instance of {@code register} does not actually register the entry to any particular map defined in the {@code Registry} class,
	 * rather it acts as a convenience method to add the entry to the entity list in {@link EntityHandler}.
	 * </p>
	 * @param <T> A subclass of {@code Entity}
	 * @param entry The entry to be added
	 */
	public <T extends Entity> void register(T entry) {
		engine.getEntityHandler().addEntity(entry);
	}
	
	/**
	 * Registers the given instance of {@link Camera}.
	 * <p>
	 * This instance of {@code register} does not actually register the entry to any particular map defined in the {@code Registry} class,
	 * rather it acts as a convenience method to set the value of the instance of {@code Camera} maintained by the engine.
	 * </p>
	 * @param <T> An implementation of {@code Camera}
	 * @param entry The entry to be added
	 */
	public <T extends Camera> void register(T entry) {
		engine.setCamera(entry);
	}
	
	/**
	 * Registers the given instance of {@link Shader} to the appropriate maps.
	 * @param <T> An implementation of {@code Shader}
	 * @param entry The entry to be added
	 */
	public <T extends Shader> void register(T entry) {
		if (register((Indexable) entry)) {
			shaders.put(entry.getLocalizedID(), entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link Terrain}.
	 * <p>
	 * This instance of {@code register} does not actually register the entry to any particular map defined in the {@code Registry} class,
	 * rather it acts as a convenience method to add the entry to the terrain list in {@link TerrainHandler}.
	 * </p>
	 * @param <T> A subclass of {@code Terrain}
	 * @param entry The entry to be added
	 */
	public <T extends Terrain> void register(T entry) {
		engine.getTerrainHandler().addTerrain(entry);
	}
	
	/**
	 * Registers the given instance of {@link Model} to the appropriate maps.
	 * @param <T> An implementation of {@code Model}
	 * @param entry The entry to be added
	 */
	public <T extends Model> void register(T entry) {
		if (register((Indexable) entry)) {
			models.put(entry.getLocalizedID(), entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link Light} to the appropriate maps.
	 * @param <T> An implementation of {@code Light}
	 * @param entry The entry to be added
	 */
	public <T extends Light> void register(T entry) {
		if (register((Indexable) entry)) {
			lights.put(entry.getLocalizedID(), entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link Input} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> A subclass of {@code Input}
	 * @param entry The entry to be added
	 */
	public <T extends Input> void registerAll(T entry) {
		if (register((Indexable) entry)) {
			inputs.put(entry.getLocalizedID(), entry);
			if (entry instanceof KeyControllable)
				((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
			if (entry instanceof MouseControllable)
				((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link SFX} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> A subclass of {@code SFX}
	 * @param entry The entry to be added
	 */
	public <T extends SFX> void registerAll(T entry) {
		if (register((Indexable) entry)) {
			sfx.put(entry.getLocalizedID(), entry);
			if (entry instanceof KeyControllable)
				((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
			if (entry instanceof MouseControllable)
				((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link State} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> A subclass of {@code State}
	 * @param entry The entry to be added
	 */
	public <T extends State> void registerAll(T entry) {
		if (register((Indexable) entry)) {
			states.put(entry.getLocalizedID(), entry);
			if (entry instanceof KeyControllable)
				((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
			if (entry instanceof MouseControllable)
				((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link Property} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> An implementation of {@code Property}
	 * @param entry The entry to be added
	 */
	public <T extends Property> void registerAll(T entry) {
		if (register((Indexable) entry)) {
			properties.put(entry.getLocalizedID(), entry);
			if (entry instanceof KeyControllable)
				((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
			if (entry instanceof MouseControllable)
				((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link Texture} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> An implementation of {@code Texture}
	 * @param entry The entry to be added
	 */
	public <T extends Texture> void registerAll(T entry) {
		if (register((Indexable) entry)) {
			textures.put(entry.getLocalizedID(), entry);
			if (entry instanceof KeyControllable)
				((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
			if (entry instanceof MouseControllable)
				((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link StoredFont} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> An implementation of {@code StoredFont}
	 * @param entry The entry to be added
	 */
	public <T extends StoredFont> void registerAll(T entry) {
		if (register((Indexable) entry)) {
			fonts.put(entry.getLocalizedID(), entry);
			if (entry instanceof KeyControllable)
				((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
			if (entry instanceof MouseControllable)
				((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link Entity} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> A subclass of {@code Entity}
	 * @param entry The entry to be added
	 */
	public <T extends Entity> void registerAll(T entry) {
		engine.getEntityHandler().addEntity(entry);
		if (entry instanceof KeyControllable)
			((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
		if (entry instanceof MouseControllable)
			((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
	}
	
	/**
	 * Registers the given instance of {@link Camera} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> A subclass of {@code Camera}
	 * @param entry The entry to be added
	 */
	public <T extends Camera> void registerAll(T entry) {
		if (register((Indexable) entry)) {
			engine.setCamera(entry);
			if (entry instanceof KeyControllable)
				((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
			if (entry instanceof MouseControllable)
				((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link Shader} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> An implementation of {@code Shader}
	 * @param entry The entry to be added
	 */
	public <T extends Shader> void registerAll(T entry) {
		if (register((Indexable) entry)) {
			shaders.put(entry.getLocalizedID(), entry);
			if (entry instanceof KeyControllable)
				((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
			if (entry instanceof MouseControllable)
				((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link Terrain} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> A subclass of {@code Terrain}
	 * @param entry The entry to be added
	 */
	public <T extends Terrain> void registerAll(T entry) {
		engine.getTerrainHandler().addTerrain(entry);
		if (entry instanceof KeyControllable)
			((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
		if (entry instanceof MouseControllable)
			((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
	}
	
	/**
	 * Registers the given instance of {@link Model} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> A subclass of {@code Model}
	 * @param entry The entry to be added
	 */
	public <T extends Model> void registerAll(T entry) {
		if (register((Indexable) entry)) {
			models.put(entry.getLocalizedID(), entry);
			if (entry instanceof KeyControllable)
				((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
			if (entry instanceof MouseControllable)
				((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
		}
	}
	
	/**
	 * Registers the given instance of {@link Light} to the appropriate maps.
	 * <p>
	 * This method acts as a convenience method to replace the need to register a single object multiple times, 
	 * specifically when the given object is an implementation of {@link KeyControllable} and/or {@link MouseControllable}
	 * in addition to an implementation of a particular superclass.
	 * </p>
	 * @param <T> A subclass of {@code Light}
	 * @param entry The entry to be added
	 */
	public <T extends Light> void registerAll(T entry) {
		if (register((Indexable) entry)) {
			lights.put(entry.getLocalizedID(), entry);
			if (entry instanceof KeyControllable)
				((KeyInput) inputs.get("acl_internal_keyboard")).addController((KeyControllable) entry);
			if (entry instanceof MouseControllable)
				((MouseInput) inputs.get("acl_internal_mouse")).addController((MouseControllable) entry);
		}
	}
	
	/** 
	 * Retrieves the implementation of {@link Indexable} that has the given {@code registryID}.
	 * <p>
	 * This method utilizes a map containing all registered entries and is not tied to a particular class; 
	 * it includes any class which implements the {@code Indexable} interface.
	 * </p>
	 * @param registryID The id of the desired entry
	 * @return An implementation of {@code Indexable}
	 */
	public Indexable get(String registryID) {
		return registryID.startsWith("internal:") ? entries.get("acl_internal_" + registryID.replaceFirst("internal\\:", "")) : (entries.get(registryID) != null ? entries.get(registryID) : entries.get("acl_internal_" + registryID));
	}
	
	/** 
	 * Retrieves the implementation of {@link Input} that has the given {@code localizedID}.
	 * @param localizedID The id of the desired entry
	 * @return An instance of {@code Input}
	 */
	public Input getInput(String localizedID) {
		return localizedID.startsWith("internal:") ? inputs.get("acl_internal_" + localizedID.replaceFirst("internal\\:", "")) : (inputs.get(localizedID) != null ? inputs.get(localizedID) : inputs.get("acl_internal_" + localizedID));
	}
	
	/** 
	 * Retrieves the implementation of {@link SFX} that has the given {@code localizedID}.
	 * @param localizedID The id of the desired entry
	 * @return An instance of {@code SFX}
	 */
	public SFX getSFX(String localizedID) {
		return localizedID.startsWith("internal:") ? sfx.get("acl_internal_" + localizedID.replaceFirst("internal\\:", "")) : (sfx.get(localizedID) != null ? sfx.get(localizedID) : sfx.get("acl_internal_" + localizedID));
	}
	
	/** 
	 * Retrieves the implementation of {@link State} that has the given {@code localizedID}.
	 * @param localizedID The id of the desired entry
	 * @return An instance of {@code State}
	 */
	public State getState(String localizedID) {
		return localizedID.startsWith("internal:") ? states.get("acl_internal_" + localizedID.replaceFirst("internal\\:", "")) : (states.get(localizedID) != null ? states.get(localizedID) : states.get("acl_internal_" + localizedID));
	}
	
	/** 
	 * Retrieves the implementation of {@link Property} that has the given {@code localizedID}.
	 * @param localizedID The id of the desired entry
	 * @return An instance of {@code Property}
	 */
	public Property getProperty(String localizedID) {
		return localizedID.startsWith("internal:") ? properties.get("acl_internal_" + localizedID.replaceFirst("internal\\:", "")) : (properties.get(localizedID) != null ? properties.get(localizedID) : properties.get("acl_internal_" + localizedID));
	}
	
	/** 
	 * Retrieves the implementation of {@link Texture} that has the given {@code localizedID}.
	 * @param localizedID The id of the desired entry
	 * @return An instance of {@code Texture}
	 */
	public Texture getTexture(String localizedID) {
		return localizedID.startsWith("internal:") ? textures.get("acl_internal_" + localizedID.replaceFirst("internal\\:", "")) : (textures.get(localizedID) != null ? textures.get(localizedID) : textures.get("acl_internal_" + localizedID));
	}
	
	/** 
	 * Retrieves the implementation of {@link StoredFont} that has the given {@code localizedID}.
	 * @param localizedID The id of the desired entry
	 * @return An instance of {@code StoredFont}
	 */
	public StoredFont getFont(String localizedID) {
		return localizedID.startsWith("internal:") ? fonts.get("acl_internal_" + localizedID.replaceFirst("internal\\:", "")) : (fonts.get(localizedID) != null ? fonts.get(localizedID) : fonts.get("acl_internal_" + localizedID));
	}
	
	/** 
	 * Retrieves the implementation of {@link Shader} that has the given {@code localizedID}.
	 * @param localizedID The id of the desired entry
	 * @return An instance of {@code Shader}
	 */
	public Shader getShader(String localizedID) {
		return localizedID.startsWith("internal:") ? shaders.get("acl_internal_" + localizedID.replaceFirst("internal\\:", "")) : (shaders.get(localizedID) != null ? shaders.get(localizedID) : shaders.get("acl_internal_" + localizedID));
	}
	
	/** 
	 * Retrieves the implementation of {@link Model} that has the given {@code localizedID}.
	 * @param localizedID The id of the desired entry
	 * @return An instance of {@code Model}
	 */
	public Model getModel(String localizedID) {
		return localizedID.startsWith("internal:") ? models.get("acl_internal_" + localizedID.replaceFirst("internal\\:", "")) : (models.get(localizedID) != null ? models.get(localizedID) : models.get("acl_internal_" + localizedID));
	}
	
	/** 
	 * Retrieves the implementation of {@link Light} that has the given {@code localizedID}.
	 * @param localizedID The id of the desired entry
	 * @return An instance of {@code Light}
	 */
	public Light getLight(String localizedID) {
		return localizedID.startsWith("internal:") ? lights.get("acl_internal_" + localizedID.replaceFirst("internal\\:", "")) : (lights.get(localizedID) != null ? lights.get(localizedID) : lights.get("acl_internal_" + localizedID));
	}
	
	/**
	 * Gets a list of all registered entries and returns them as an {@link ArrayList} of {@link Indexable}.
	 * @return An {@code ArrayList} of all entries
	 */
	public List<Indexable> getAll() {
		return new ArrayList<>(entries.values());
	}
	
	/**
	 * Gets a list of all registered instances of {@link Input} and returns them as an {@link ArrayList}.
	 * @return An {@code ArrayList} of all instances of {@code Input}
	 */
	public List<Input> getAllInputs() {
		return new ArrayList<>(inputs.values());
	}
	
	/**
	 * Gets a list of all registered instances of {@link SFX} and returns them as an {@link ArrayList}.
	 * @return An {@code ArrayList} of all instances of {@code SFX}
	 */
	public List<SFX> getAllSFX() {
		return new ArrayList<>(sfx.values());
	}
	
	/**
	 * Gets a list of all registered instances of {@link State} and returns them as an {@link ArrayList}.
	 * @return An {@code ArrayList} of all instances of {@code State}
	 */
	public List<State> getAllStates() {
		return new ArrayList<>(states.values());
	}
	
	/**
	 * Gets a list of all registered instances of {@link Property} and returns them as an {@link ArrayList}.
	 * @return An {@code ArrayList} of all instances of {@code Property}
	 */
	public List<Property> getAllProperties() {
		return new ArrayList<>(properties.values());
	}
	
	/**
	 * Gets a list of all registered instances of {@link Texture} and returns them as an {@link ArrayList}.
	 * @return An {@code ArrayList} of all instances of {@code Texture}
	 */
	public List<Texture> getAllTextures() {
		return new ArrayList<>(textures.values());
	}
	
	/**
	 * Gets a list of all registered instances of {@link StoredFont} and returns them as an {@link ArrayList}.
	 * @return An {@code ArrayList} of all instances of {@code StoredFont}
	 */
	public List<StoredFont> getAllFonts() {
		return new ArrayList<>(fonts.values());
	}
	
	/**
	 * Gets a list of all registered instances of {@link Shader} and returns them as an {@link ArrayList}.
	 * @return An {@code ArrayList} of all instances of {@code Shader}
	 */
	public List<Shader> getAllShaders() {
		return new ArrayList<>(shaders.values());
	}
	
	/**
	 * Gets a list of all registered instances of {@link Model} and returns them as an {@link ArrayList}.
	 * @return An {@code ArrayList} of all instances of {@code Model}
	 */
	public List<Model> getAllModels() {
		return new ArrayList<>(models.values());
	}
	
	/**
	 * Gets a list of all registered instances of {@link Light} and returns them as an {@link ArrayList}.
	 * @return An {@code ArrayList} of all instances of {@code Light}
	 */
	public List<Light> getAllLights() {
		return new ArrayList<>(lights.values());
	}
	
	/**
	 * Gets a list of {@code registryID}s of all registered entries and returns them as a {@link List}.
	 * @return A {@code List} of {@code registryID}s of all entries
	 */
	public List<String> getAllRegistryIDs() {
		return entries.values().stream().map(f -> f.getRegistryID()).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code localizedID}s of all registered entries and returns them as a {@link List}.
	 * <p>
	 * Note: Any engine-defined entries will be returned without the "acl_internal_" prefix.
	 * </p>
	 * @return A {@code List} of {@code localizedID}s of all entries
	 */
	public List<String> getAllLocalizedIDs() {
		return entries.values().stream().map(f -> {
			if (f.getLocalizedID().startsWith("acl_internal_"))
				return f.getLocalizedID().replaceFirst("acl_internal_", "");
			else
				return f.getLocalizedID();
		}).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code registryID}s of all registered instances of {@link Input} and returns them as a {@link List}.
	 * @return A {@code List} of {@code registryID}s of all instances of {@code Input}
	 */
	public List<String> getAllInputRegistryIDs() {
		return inputs.values().stream().map(f -> f.getRegistryID()).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code localizedID}s of all registered instances of {@link Input} and returns them as a {@link List}.
	 * <p>
	 * Note: Any engine-defined entries will be returned without the "acl_internal_" prefix.
	 * </p>
	 * @return A {@code List} of {@code localizedID}s of all instances of {@code Input}
	 */
	public List<String> getAllInputLocalizedIDs() {
		return inputs.values().stream().map(f -> {
			if (f.getLocalizedID().startsWith("acl_internal_"))
				return f.getLocalizedID().replaceFirst("acl_internal_", "");
			else
				return f.getLocalizedID();
		}).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code registryID}s of all registered instances of {@link SFX} and returns them as a {@link List}.
	 * @return A {@code List} of {@code registryID}s of all instances of {@code SFX}
	 */
	public List<String> getAllSFXRegistryIDs() {
		return sfx.values().stream().map(f -> f.getRegistryID()).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code localizedID}s of all registered instances of {@link SFX} and returns them as a {@link List}.
	 * <p>
	 * Note: Any engine-defined entries will be returned without the "acl_internal_" prefix.
	 * </p>
	 * @return A {@code List} of {@code localizedID}s of all instances of {@code SFX}
	 */
	public List<String> getAllSFXLocalizedIDs() {
		return sfx.values().stream().map(f -> {
			if (f.getLocalizedID().startsWith("acl_internal_"))
				return f.getLocalizedID().replaceFirst("acl_internal_", "");
			else
				return f.getLocalizedID();
		}).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code registryID}s of all registered instances of {@link State} and returns them as a {@link List}.
	 * @return A {@code List} of {@code registryID}s of all instances of {@code State}
	 */
	public List<String> getAllStateRegistryIDs() {
		return states.values().stream().map(f -> f.getRegistryID()).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code localizedID}s of all registered instances of {@link State} and returns them as a {@link List}.
	 * <p>
	 * Note: Any engine-defined entries will be returned without the "acl_internal_" prefix.
	 * </p>
	 * @return A {@code List} of {@code localizedID}s of all instances of {@code State}
	 */
	public List<String> getAllStateLocalizedIDs() {
		return states.values().stream().map(f -> {
			if (f.getLocalizedID().startsWith("acl_internal_"))
				return f.getLocalizedID().replaceFirst("acl_internal_", "");
			else
				return f.getLocalizedID();
		}).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code registryID}s of all registered instances of {@link Property} and returns them as a {@link List}.
	 * @return A {@code List} of {@code registryID}s of all instances of {@code Property}
	 */
	public List<String> getAllPropertyRegistryIDs() {
		return properties.values().stream().map(f -> f.getRegistryID()).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code localizedID}s of all registered instances of {@link Property} and returns them as a {@link List}.
	 * <p>
	 * Note: Any engine-defined entries will be returned without the "acl_internal_" prefix.
	 * </p>
	 * @return A {@code List} of {@code localizedID}s of all instances of {@code Property}
	 */
	public List<String> getAllPropertyLocalizedIDs() {
		return properties.values().stream().map(f -> {
			if (f.getLocalizedID().startsWith("acl_internal_"))
				return f.getLocalizedID().replaceFirst("acl_internal_", "");
			else
				return f.getLocalizedID();
		}).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code registryID}s of all registered instances of {@link Texture} and returns them as a {@link List}.
	 * @return A {@code List} of {@code registryID}s of all instances of {@code Texture}
	 */
	public List<String> getAllTextureRegistryIDs() {
		return textures.values().stream().map(f -> f.getRegistryID()).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code localizedID}s of all registered instances of {@link Texture} and returns them as a {@link List}.
	 * <p>
	 * Note: Any engine-defined entries will be returned without the "acl_internal_" prefix.
	 * </p>
	 * @return A {@code List} of {@code localizedID}s of all instances of {@code Texture}
	 */
	public List<String> getAllTextureLocalizedIDs() {
		return textures.values().stream().map(f -> {
			if (f.getLocalizedID().startsWith("acl_internal_"))
				return f.getLocalizedID().replaceFirst("acl_internal_", "");
			else
				return f.getLocalizedID();
		}).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code registryID}s of all registered instances of {@link StoredFont} and returns them as a {@link List}.
	 * @return A {@code List} of {@code registryID}s of all instances of {@code StoredFont}
	 */
	public List<String> getAllFontRegistryIDs() {
		return fonts.values().stream().map(f -> f.getRegistryID()).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code localizedID}s of all registered instances of {@link StoredFont} and returns them as a {@link List}.
	 * <p>
	 * Note: Any engine-defined entries will be returned without the "acl_internal_" prefix.
	 * </p>
	 * @return A {@code List} of {@code localizedID}s of all instances of {@code StoredFont}
	 */
	public List<String> getAllFontLocalizedIDs() {
		return fonts.values().stream().map(f -> {
			if (f.getLocalizedID().startsWith("acl_internal_"))
				return f.getLocalizedID().replaceFirst("acl_internal_", "");
			else
				return f.getLocalizedID();
		}).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code registryID}s of all registered instances of {@link Shader} and returns them as a {@link List}.
	 * @return A {@code List} of {@code registryID}s of all instances of {@code Shader}
	 */
	public List<String> getAllShaderRegistryIDs() {
		return shaders.values().stream().map(f -> f.getRegistryID()).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code localizedID}s of all registered instances of {@link Shader} and returns them as a {@link List}.
	 * <p>
	 * Note: Any engine-defined entries will be returned without the "acl_internal_" prefix.
	 * </p>
	 * @return A {@code List} of {@code localizedID}s of all instances of {@code Shader}
	 */
	public List<String> getAllShaderLocalizedIDs() {
		return shaders.values().stream().map(f -> {
			if (f.getLocalizedID().startsWith("acl_internal_"))
				return f.getLocalizedID().replaceFirst("acl_internal_", "");
			else
				return f.getLocalizedID();
		}).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code registryID}s of all registered instances of {@link Model} and returns them as a {@link List}.
	 * @return A {@code List} of {@code registryID}s of all instances of {@code Model}
	 */
	public List<String> getAllModelRegistryIDs() {
		return models.values().stream().map(f -> f.getRegistryID()).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code localizedID}s of all registered instances of {@link Model} and returns them as a {@link List}.
	 * <p>
	 * Note: Any engine-defined entries will be returned without the "acl_internal_" prefix.
	 * </p>
	 * @return A {@code List} of {@code localizedID}s of all instances of {@code Model}
	 */
	public List<String> getAllModelLocalizedIDs() {
		return models.values().stream().map(f -> {
			if (f.getLocalizedID().startsWith("acl_internal_"))
				return f.getLocalizedID().replaceFirst("acl_internal_", "");
			else
				return f.getLocalizedID();
		}).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code registryID}s of all registered instances of {@link Light} and returns them as a {@link List}.
	 * @return A {@code List} of {@code registryID}s of all instances of {@code Light}
	 */
	public List<String> getAllLightRegistryIDs() {
		return lights.values().stream().map(f -> f.getRegistryID()).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of {@code localizedID}s of all registered instances of {@link Light} and returns them as a {@link List}.
	 * <p>
	 * Note: Any engine-defined entries will be returned without the "acl_internal_" prefix.
	 * </p>
	 * @return A {@code List} of {@code localizedID}s of all instances of {@code Light}
	 */
	public List<String> getAllLightLocalizedIDs() {
		return lights.values().stream().map(f -> {
			if (f.getLocalizedID().startsWith("acl_internal_"))
				return f.getLocalizedID().replaceFirst("acl_internal_", "");
			else
				return f.getLocalizedID();
		}).collect(Collectors.toList());
	}
	
	/**
	 * Removes the first occurrence of the given {@link Entity} from the list of all entities as well as any controller lists that particular entity may have occupied.
	 * @param e The {@code Entity} to remove
	 */
	public void removeEntity(Entity e) {
		engine.getEntityHandler().removeEntity(e);
		
		if (e instanceof KeyControllable)
			((KeyInput) inputs.get("acl_internal_keyboard")).removeController((KeyControllable) e);
		if (e instanceof MouseControllable)
			((MouseInput) inputs.get("acl_internal_mouse")).removeController((MouseControllable) e);
	}
	
	/**
	 * Removes the first occurrence of the given {@link Terrain} from the list of all terrains as well as any controller lists that particular terrain may have occupied.
	 * @param e The {@code Terrain} to remove
	 */
	public void removeTerrain(Terrain t) {
		engine.getTerrainHandler().removeTerrain(t);
		
		if (t instanceof KeyControllable)
			((KeyInput) inputs.get("acl_internal_keyboard")).removeController((KeyControllable) t);
		if (t instanceof MouseControllable)
			((MouseInput) inputs.get("acl_internal_mouse")).removeController((MouseControllable) t);
	}
	
	/**
	 * Gets a list of all instances of {@link Music} from the list of all instances of {@link SFX} and returns them as a {@link List}.
	 * @return A {@code List} of all instances of {@code Music}
	 */
	public List<Music> getAllMusic() {
		return sfx.values().stream().filter(p -> p instanceof Music).map(f -> (Music) f).collect(Collectors.toList());
	}
	
}
