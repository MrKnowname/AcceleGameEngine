package com.accele.engine.entity;

import org.lwjgl.util.Rectangle;
import org.lwjgl.util.vector.Vector2f;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.Texture;

public abstract class Entity2D extends Entity {

	protected Texture texture;
	
	public Entity2D(Engine engine, String registryID, String localizedID, Vector2f pos, Texture texture) {
		super(engine, registryID, localizedID, pos);
		this.texture = texture;
	}
	
	/**
	 * Returns a {@code Rectangle} with the specified bounds of the {@code Entity}.
	 * <p>
	 * Typical implementation of this method involves returning an instance of {@code Rectangle} using the position of the {@code Entity} and its dimensions.
	 * For example:
	 * </p>
	 * <blockquote>
	 * <pre>
	 * {@literal @Override}
	 * public Rectangle getBounds() {
	 *     return new Rectangle((int) pos.x, (int) pos.y, width, height);
	 * }
	 * </pre>
	 * </blockquote>
	 * <p>
	 * In the above example, {@code width} and {@code height} are arbitrary values specified by the user and are not internal variables.
	 * </p>
	 * <p>
	 * Note: This method is used in determining collisions between entities.
	 * </p>
	 * @return A new instance of {@code Rectangle} specifying the bounds of the {@code Entity}
	 */
	public abstract Rectangle getBounds();

	/**
	 * Runs upon this {@code Entity} colliding with another {@code Entity}.
	 * <p>
	 * The collision of two entities colliding with each other is determined via their {@code getBounds} methods
	 * and whether the {@code intersects} method of each {@code Rectangle} from {@code getBounds} returns {@code true}.
	 * </p>
	 * <p>
	 * Entity collision can be toggled via the {@code entityCollision} property, which is {@code true} by default.
	 * </p>
	 * @param e The {@code Entity} that collided with the current {@code Entity}
	 */
	public abstract void onCollision(Entity2D e);
	
	public Texture getTexture() {
		return texture;
	}
	
}
