package com.accele.engine.gfx;

import org.lwjgl.util.vector.Vector3f;

import com.accele.engine.core.Engine;
import com.accele.engine.io.KeyControllable;
import com.accele.engine.io.KeyInput;
import com.accele.engine.io.MouseControllable;
import com.accele.engine.io.MouseInput;

public class DefaultUserControlledCamera extends Camera implements KeyControllable, MouseControllable {

	private Engine engine;
	private float speed;
	private boolean trackMouse;
	
	public DefaultUserControlledCamera(Engine engine, Vector3f pos) {
		super(pos);
		this.engine = engine;
		speed = 0.5f;
		trackMouse = true;
	}
	
	@Override
	public void onUpdate() {
		if (trackMouse) {
			yaw = -((int) engine.getRegistry().getProperty("internal:screenWidth").get() - MouseInput.getX() / 2);
			pitch = ((int) engine.getRegistry().getProperty("internal:screenHeight").get() / 2) - MouseInput.getY();
			
			if (pitch >= 90)
				pitch = 90;
			else if (pitch <= -90)
				pitch = -90;
		}
	}

	@Override
	public void onButtonPress(int key) {
		
	}

	@Override
	public void onButtonRelease(int key) {
		
	}

	@Override
	public void onKeyPress(int key) {
		if (key == KeyInput.KEY_LSHIFT)
			trackMouse = false;
	}

	@Override
	public void onKeyRelease(int key) {
		if (key == KeyInput.KEY_LSHIFT)
			trackMouse = true;
	}
	
	@Override
	public void onKeyHold(int key) {
		/*if (key == KeyInput.KEY_W) {
			pos.z += -(float) Math.cos(Math.toRadians(yaw)) * speed;
			pos.x += (float) Math.sin(Math.toRadians(yaw)) * speed;
		}
		
		if (key == KeyInput.KEY_A) {
			pos.z -= (float) Math.cos(Math.toRadians(yaw)) * speed;
			pos.x -= (float) Math.sin(Math.toRadians(yaw)) * speed;
		}
		
		if (key == KeyInput.KEY_S) {
			pos.z -= -(float) Math.cos(Math.toRadians(yaw)) * speed;
			pos.x -= (float) Math.sin(Math.toRadians(yaw)) * speed;
		}
		
		if (key == KeyInput.KEY_D) {
			pos.z += (float) Math.cos(Math.toRadians(yaw)) * speed;
			pos.x += (float) Math.sin(Math.toRadians(yaw)) * speed;
		}*/
		
		if (key == KeyInput.KEY_Z)
			pos.y += speed;
		
		if (key == KeyInput.KEY_X)
			pos.y -= speed;
	}

}
