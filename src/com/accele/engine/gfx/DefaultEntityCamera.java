package com.accele.engine.gfx;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.entity.Entity3D;
import com.accele.engine.io.MouseControllable;
import com.accele.engine.io.MouseInput;

public class DefaultEntityCamera extends Camera implements MouseControllable {

	private float distanceFromEntity;
	private float angleAroundEntity;
	private Entity3D entity;
	private boolean lDown;
	private boolean rDown;
	
	public DefaultEntityCamera(Entity3D entity) {
		super(new Vector3f(0, 0, 0));
		this.entity = entity;
	}

	@Override
	public void onUpdate() {
		calculateZoom();
		if (lDown) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
			if (pitch < 0)
				pitch = 0;
			else if (pitch > 90)
				pitch = 90;
		}
		if (rDown) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundEntity -= angleChange;
		}
		float horizontalDistance = getHorizontalDistance();
		float verticalDistance = getVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		yaw = 180 - (entity.getYRot() + angleAroundEntity);
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = entity.getYRot() + angleAroundEntity;
		float xOffset = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float zOffset = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		pos.x = ((Vector3f) entity.getPos()).x - xOffset;
		pos.y = ((Vector3f) entity.getPos()).y + verticalDistance;
		pos.z = ((Vector3f) entity.getPos()).z - zOffset;
	}
	
	private float getHorizontalDistance() {
		return Math.max((float) (distanceFromEntity * Math.cos(Math.toRadians(pitch))), 0);
	}
	
	private float getVerticalDistance() {
		return Math.max((float) (distanceFromEntity * Math.sin(Math.toRadians(pitch))), 0);
	}
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromEntity -= zoomLevel;
	}

	@Override
	public void onButtonPress(int key) {
		if (key == MouseInput.BUTTON_LEFT)
			lDown = true;
		if (key == MouseInput.BUTTON_RIGHT)
			rDown = true;
	}

	@Override
	public void onButtonRelease(int key) {
		if (key == MouseInput.BUTTON_LEFT)
			lDown = false;
		if (key == MouseInput.BUTTON_RIGHT)
			rDown = false;
	}

}
