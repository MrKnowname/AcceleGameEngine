package com.accele.engine.core;

import com.accele.engine.gfx.Graphics;

/** 
 * This interface is required for any object that needs to render to the screen.
 * <p>
 * While the instance of {@link Engine} contains a {@code getGraphics} method, it cannot be used to render to the screen, rather it is used for setting and retrieving data such as color and font.
 * The instance of the {@link Graphics} class that can render to the screen is passed through this interface's method only.
 * </p>
 * 
 * @author William Garland
 * @since 1.0.0
 */
public interface Renderable {

	/**
	 * Allows implementing classes to render to the screen using the provided {@link Graphics} instance.
	 * @param g The instance of {@code Graphics} to be used for rendering
	 */
	public void onRender(Graphics g);
	
}
