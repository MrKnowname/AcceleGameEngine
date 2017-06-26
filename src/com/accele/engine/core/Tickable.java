package com.accele.engine.core;

/** 
 * This interface is required for any object that needs to consistently update information.
 * <p>
 * For all implementing classes, the <tt>onUpdate</tt> method will run once per object every cycle, as set by the primary game loop, unless called externally by the user.
 * </p>
 * 
 * @author William Garland
 * @since 1.0.0
 */
public interface Tickable {

	/**
	 * Allows implementing classes to update information in a cyclic manner.
	 * <p>
	 * This method runs at least once per cycle via the primary game loop, unless called externally by the user.
	 * </p>
	 */
	public void onUpdate();
	
}
