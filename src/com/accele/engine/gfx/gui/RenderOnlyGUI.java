package com.accele.engine.gfx.gui;

import org.lwjgl.util.vector.Vector2f;

import com.accele.engine.core.Engine;
import com.accele.engine.gfx.Graphics;
import com.accele.engine.gfx.Texture;
import com.accele.engine.gfx.shader.GUIShader;

public class RenderOnlyGUI extends GUI {
	
	public RenderOnlyGUI(Engine engine, String registryID, String localizedID, Texture texture, Vector2f pos, Vector2f rotation, Vector2f scale) {
		super(engine, registryID, localizedID, texture, pos, rotation, scale, engine.getRegistry().getShader("internal:gui"));
	}

	@Override
	public void onUpdate() {
		
	}

	@Override
	public void onRender(Graphics g) {
		g.drawGUIComponent(this, (GUIShader) shader);
	}

}
