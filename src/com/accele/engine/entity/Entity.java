package com.accele.engine.entity;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Indexable;
import com.accele.engine.core.Renderable;
import com.accele.engine.core.Tickable;
import com.accele.engine.property.Property;

/**
 * The <tt>Entity</tt> class represents the base class for any and all entities present in the game.
 * <p>
 * This class assumes the desire of the user to update and render the entity to the screen, as it implements the {@link Tickable} and {@link Renderable} interfaces.
 * Entities are not limited in their functionality nor are they limited in their design; this class was intended to be a base class for any type of entity, whether animate or inanimate.
 * Frequently used examples of the <tt>Entity</tt> class include, but are not limited to: static objects, enemies, players, non-playable characters, and text.
 * </p>
 * <p>
 * All instances of this class are managed via the {@link EntityHandler} class.
 * Multiple objects of the same id may be registered as the list of entities is not bound by their <tt>registryID</tt> or <tt>localizedID</tt>.
 * </p>
 * @author William Garland
 * @since 1.0.0
 */
public abstract class Entity implements Indexable, Tickable, Renderable {

	protected final Engine engine;
	protected final String registryID;
	protected final String localizedID;
	protected final List<Property> properties;
	protected Vector pos;
	protected int duration;
	
	/**
	 * Creates a new <tt>Entity</tt> with the given parameters.
	 * @param engine The instance of {@link Engine} utilized by the user
	 * @param registryID The <tt>registryID</tt> of the <tt>Entity</tt>
	 * @param localizedID The <tt>localizedID</tt> of the <tt>Entity</tt>
	 * @param pos The position of the <tt>Entity</tt> as a vector that uses floating-point values
	 */
	public Entity(Engine engine, String registryID, String localizedID, Vector pos) {
		this.engine = engine;
		this.registryID = registryID;
		this.localizedID = localizedID;
		this.properties = new ArrayList<>();
		this.pos = pos;
		this.duration = -1;
	}
	
	/**
	 * Updates internal values associated with this instance of <tt>Entity</tt>.
	 * <p>
	 * Note: This method is not intended for direct use by the user; it should only be called via the primary game loop.
	 * </p>
	 */
	public final void internalUpdate() {
		for (Property p : properties) {
			p.run();
		}
		
		if (duration != -1 && (boolean) engine.getRegistry().getProperty("internal:entityDuration").get()) {
			duration--;
			if (duration <= 0)
				engine.getEntityHandler().removeEntity(this);
		}
	}
	
	@Override
	public final String getRegistryID() {
		return registryID;
	}
	
	@Override
	public final String getLocalizedID() {
		return localizedID;
	}
	
	/**
	 * Adds the specified {@link Property} to the list of properties associated with the <tt>Entity</tt>.
	 * <p>
	 * Note: This method can be chained in order to add multiple properties within one statement.
	 * </p>
	 * @param property The <tt>Property</tt> to add
	 * @return The current<tt>Entity</tt>
	 */
	public final Entity withProperty(Property property) {
		properties.add(property);
		return this;
	}
	
	/**
	 * Gets the position of the <tt>Entity</tt>.
	 * @return An instance of <tt>Vector</tt> containing the position of the <tt>Entity</tt>
	 */
	public Vector getPos() {
		return pos;
	}
	
}
