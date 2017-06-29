package com.accele.engine.io;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Tickable;
import com.accele.engine.util.Utils;

public class MousePicker implements Tickable {

	private Engine engine;
	private Vector3f currentRay;
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	
	public MousePicker(Engine engine) {
		this.engine = engine;
		this.currentRay = new Vector3f(0, 0, 0);
		this.projectionMatrix = engine.getGraphics().getProjectionMatrix();
		this.viewMatrix = Utils.Dim3.createViewMatrix(engine.getCamera());
	}

	@Override
	public void onUpdate() {
		viewMatrix = Utils.Dim3.createViewMatrix(engine.getCamera());
		currentRay = calculateRay();
	}
	
	public Vector3f getCurrentRay() {
		return currentRay;
	}
	
	private Vector3f calculateRay() {
		float mx = MouseInput.getX();
		float my = MouseInput.getY();
		Vector2f ndc = getNormalizedDeviceCoordinates(mx, my);
		Vector4f clipCoords = new Vector4f(ndc.x, ndc.y, -1f, 1f);
		Vector4f eyeCoords = toEyeCoordinates(clipCoords);
		Vector3f worldRay = toWorldCoordinates(eyeCoords);
		return worldRay;
	}
	
	private Vector3f toWorldCoordinates(Vector4f eyeCoords) {
		Matrix4f inverse = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(inverse, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}
	
	private Vector4f toEyeCoordinates(Vector4f clipCoords) {
		Matrix4f inverse = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(inverse, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}
	
	private Vector2f getNormalizedDeviceCoordinates(float mx, float my) {
		float x = (2 * mx) / (int) engine.getRegistry().getProperty("internal:screenWidth").get() - 1;
		float y = (2 * my) / (int) engine.getRegistry().getProperty("internal:screenHeight").get() - 1;
		return new Vector2f(x, y);
	}

}
