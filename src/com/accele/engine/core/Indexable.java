package com.accele.engine.core;

/** 
 * This interface requires all implementing classes to contain a <tt>registryID</tt> and <tt>localizedID</tt> value which are used in the registration of objects within the {@link Registry} class.
 * <p>
 * In order for an object to be registered in the engine via the <tt>Registry</tt>, it must implement this interface and return proper values to the contained methods.
 * This interface enables the registration and retrieval of all objects in the <tt>Registry</tt> and is therefore essential to the function of the engine.
 * </p>
 * 
 * @author William Garland
 * @since 1.0.0
 */
public interface Indexable {

	/** 
	 * Gets the value of the registryID of the implementing object, which is required to be able to retrieve the object from the internal entry list of the <tt>Registry</tt>.
	 * @return The registryID of the implementing object.
	 */
	public String getRegistryID();
	
	/** 
	 * Gets the value of the localizedID of the implementing object, which is required to be able to retrieve the object from the filtered entry lists of the <tt>Registry</tt>.
	 * @return The localizedID of the implementing object.
	 */
	public String getLocalizedID();
	
}
