package com.accele.engine.gfx;

import com.accele.engine.util.Resource;

public class ModelTexture extends Texture {

	private float shineDamper;
	private float reflectivity;
	private boolean hasTransparency;
	private boolean useFakeLighting;
	
	public ModelTexture(String registryID, String localizedID, Resource image, float shineDamper, float reflectivity, boolean hasTransparency, boolean useFakeLighting) {
		super(registryID, localizedID, image);
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
		this.hasTransparency = hasTransparency;
		this.useFakeLighting = useFakeLighting;
	}
	
	public ModelTexture(String registryID, String localizedID, Resource image, boolean hasTransparency, boolean useFakeLighting) {
		this(registryID, localizedID, image, 1, 0, hasTransparency, useFakeLighting);
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean hasTransparency() {
		return hasTransparency;
	}

	public void setTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	public boolean useFakeLighting() {
		return useFakeLighting;
	}

	public void setFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

}
