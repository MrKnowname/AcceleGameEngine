package com.accele.engine.io;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.accele.engine.core.Engine;

public final class MouseInput extends Input {
	
	public static final int BUTTON_LEFT = 0;
	public static final int BUTTON_RIGHT = 1;
	
	private List<MouseControllable> controllers;
	
	public MouseInput(Engine engine) {
		super(engine, "acl.io.mouse", "acl_internal_mouse");
		controllers = new ArrayList<>();
	}
	
	@Override
	public void onUpdate() {
		while (Mouse.next()) {
			int key = Mouse.getEventButton();
			if (Mouse.getEventButtonState())
				controllers.forEach(controller -> controller.onButtonPress(key));
			else
				controllers.forEach(controller -> controller.onButtonRelease(key));
		}
	}
	
	public void addController(MouseControllable controller) {
		controllers.add(controller);
	}
	
	public void removeController(MouseControllable controller) {
		controllers.remove(controller);
	}
	
	public static int getX() {
		return Mouse.getX();
	}
	
	public static int getY() {
		return Mouse.getY();
	}

}
