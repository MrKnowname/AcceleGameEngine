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
	 * Returns a <tt>Rectangle</tt> with the specified bounds of the <tt>Entity</tt>.
	 * <p>
	 * Typical implementation of this method involves returning an instance of <tt>Rectangle</tt> using the position of the <tt>Entity</tt> and its dimensions.
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
	 * In the above example, <tt>width</tt> and <tt>height</tt> are arbitrary values specified by the user and are not internal variables.
	 * </p>
	 * <p>
	 * Note: This method is used in determining collisions between entities.
	 * </p>
	 * @return A new instance of <tt>Rectangle</tt> specifying the bounds of the <tt>Entity</tt>
	 */
	public abstract Rectangle getBounds();

	/**
	 * Runs upon this <tt>Entity</tt> colliding with another <tt>Entity</tt>.
	 * <p>
	 * The collision of two entities colliding with each other is determined via their <tt>getBounds</tt> methods
	 * and whether the <tt>intersects</tt> method of each <tt>Rectangle</tt> from <tt>getBounds</tt> returns <tt>true</tt>.
	 * </p>
	 * <p>
	 * Entity collision can be toggled via the <tt>entityCollision</tt> property, which is <tt>true</tt> by default.
	 * </p>
	 * @param e The <tt>Entity</tt> that collided with the current <tt>Entity</tt>
	 */
	public abstract void onCollision(Entity2D e);
	
	public Texture getTexture() {
		return texture;
	}
	
}
