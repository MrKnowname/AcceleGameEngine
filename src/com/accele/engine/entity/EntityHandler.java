package com.accele.engine.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Renderable;
import com.accele.engine.core.Tickable;
import com.accele.engine.gfx.Graphics;
import com.accele.engine.gfx.Light;
import com.accele.engine.gfx.shader.StaticShader;
import com.accele.engine.model.TexturedModel;
import com.accele.engine.property.Property;
import com.accele.engine.util.Batch;

/**
 * This class controls the updating and rendering of all instances of the {@link Entity} class.
 * <p>
 * In addition to calling the {@code onUpdate} and {@code onRender} methods of each {@code Entity}, the {@code EntityHandler} controls the collision detection between entities.
 * This functionality can be toggled via the {@code entityCollision2D} and {@code entityCollision3D} properties, which are {@code true} by default depending on the {@code gameType}.
 * </p>
 * @author William Garland
 * @since 2.0.0
 */
public final class EntityHandler implements Tickable, Renderable {

	private Engine engine;
	private List<Entity> entities;
	private Map<TexturedModel, List<Entity3D>> batchEntities;
	private Property entityCollision2D;
	private StaticShader shader;
	private Light staticShaderLight;
	
	/**
	 * Creates a new instance of {@code EntityHandler}.
	 * <p>
	 * This constructor should only be called by the {@link Engine} and should not be called by the user.
	 * </p>
	 * @param engine The instance of the {@code Engine}
	 */
	public EntityHandler(Engine engine) {
		this.engine = engine;
		this.entities = new ArrayList<>();
		this.batchEntities = new HashMap<>();
	}
	
	/**
	 * Initializes attributes required by this instance of {@link EntityHandler}.
	 * <p>
	 * This method should only be called by the {@link Engine} and should not be called by the user.
	 * </p>
	 */
	public void init() {
		this.entityCollision2D = engine.getRegistry().getProperty("internal:entityCollision2D");
		this.shader = (StaticShader) engine.getRegistry().getShader("internal:static");
	}
	
	@Override
	public void onUpdate() {
		for (Entity e : entities) {
			e.internalUpdate();
			e.onUpdate();
			
			if ((boolean) entityCollision2D.get() && e instanceof Entity2D) {
				for (Entity other : entities) {
					if (e instanceof Entity2D && ((Entity2D) e).getBounds() != null && ((Entity2D) other).getBounds() != null && ((Entity2D) e).getBounds().intersects(((Entity2D) other).getBounds())) {
						((Entity2D) e).onCollision((Entity2D) other);
						((Entity2D) other).onCollision((Entity2D) e);
					}
				}
			}
		}
		
		batchEntities.forEach((a, b) -> b.forEach(c -> c.onUpdate()));
	}
	
	@Override
	public void onRender(Graphics g) {
		entities.forEach(a -> a.onRender(g));
		batchEntities.forEach((a, b) -> {
			shader.start();
			if (staticShaderLight != null)
				shader.loadLight(staticShaderLight);
			shader.loadViewMatrix(engine.getCamera());
			g.drawEntitiesUsingModel(b.toArray(new Entity3D[b.size()]), a, shader);
			shader.stop();
		});
	}
	
	/**
	 * Adds the specified {@link Entity} to the list of entities.
	 * @param e The {@code Entity} to add
	 */
	public void addEntity(Entity e) {
		if (e instanceof Entity3D && e.getClass().isAnnotationPresent(Batch.class))
			addBatchEntity((Entity3D) e);
		else
			entities.add(e);
	}
	
	/**
	 * Adds the specified {@link Entity3D} to the list of batch entities.
	 * @param e The {@code Entity3D} to add
	 */
	public void addBatchEntity(Entity3D e) {
		if (batchEntities.containsKey(e.model)) {
			batchEntities.get(e.model).add(e);
		} else {
			List<Entity3D> entities = new ArrayList<>();
			entities.add(e);
			batchEntities.put(e.model, entities);
		}
	}
	
	/**
	 * Removes the first occurrence of the specified {@link Entity} from the list of entities.
	 * @param e The {@code Entity} to remove
	 */
	public void removeEntity(Entity e) {
		if (e instanceof Entity3D && e.getClass().isAnnotationPresent(Batch.class)) {
			batchEntities.get(((Entity3D) e).model).remove((Entity3D) e);
		} else
			entities.remove(e);
	}
	
	/**
	 * Retrieves the first occurrence of the {@link Entity} with the specified {@code localizedID}.
	 * @param localizedID The id of the {@code Entity} to find
	 * @return The first occurrence of the {@code Entity} with the specified {@code localizedID}
	 */
	public Entity getFirstEntityByID(String localizedID) {
		return entities.stream().filter(p -> p.localizedID.equals(localizedID)).findFirst().orElseGet(() -> null);
	}
	
	/**
	 * Retrieves all occurrences of the {@link Entity} with the specified {@code localizedID}.
	 * @param localizedID The id of the {@code Entity} to find
	 * @return All occurrences of the {@code Entity} with the specified {@code localizedID} as an array of {@code Entity}
	 */
	public Entity[] getAllEntitiesByID(String localizedID) {
		List<Entity> tmp = entities.stream().filter(p -> p.localizedID.equals(localizedID)).collect(Collectors.toList());
		return tmp.toArray(new Entity[tmp.size()]);
	}
	
	/**
	 * Retrieves all entities from the list of entities.
	 * @return All entities as an array of {@link Entity}
	 */
	public Entity[] getEntities() {
		return entities.toArray(new Entity[entities.size()]);
	}
	
	/**
	 * Adds the specified array of {@link Entity} to the map of batch entities to be rendered using the specified {@link TexturedModel}.
	 * <p>
	 * Note that if an entry using the specified {@code TexturedModel} is already present,
	 * the list for the model will be overwritten with the specified {@code entities} array.
	 * </p>
	 * @param model The {@code TexturedModel} to use
	 * @param entities The array of {@code Entity} to add
	 */
	public void addBatch(TexturedModel model, Entity3D[] entities) {
		this.batchEntities.put(model, Arrays.asList(entities));
	}
	
	public Light getStaticShaderLight() {
		return staticShaderLight;
	}
	
	public void setStaticShaderLight(Light staticShaderLight) {
		this.staticShaderLight = staticShaderLight;
	}
	
}
