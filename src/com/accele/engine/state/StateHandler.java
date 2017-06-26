package com.accele.engine.state;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Renderable;
import com.accele.engine.core.Tickable;
import com.accele.engine.gfx.Graphics;

public final class StateHandler implements Tickable, Renderable {
	
	private Engine engine;
	private State currentState;
	
	public StateHandler(Engine engine) {
		this.engine = engine;
		this.currentState = null;
	}
	
	@Override
	public void onUpdate() {
		if (currentState != null)
			currentState.onUpdate();
	}
	
	@Override
	public void onRender(Graphics g) {
		if (currentState != null)
			currentState.onRender(g);
	}
	
	public State getCurrentState() {
		return currentState;
	}
	
	public void setCurrentState(String newState, boolean exitOld, boolean initNew) {
		try {
			if (exitOld && currentState != null)
				currentState.onExit();
			currentState = engine.getRegistry().getState(newState);
			if (initNew)
				currentState.onInit();
		} catch (Exception e) {
			System.err.println("StateHandlerError: Invalid state \"" + newState + "\".");
		}
	}
	
}
