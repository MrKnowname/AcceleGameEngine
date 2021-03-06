package com.accele.engine.gfx;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex2i;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.Rectangle;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import com.accele.engine.core.Engine;
import com.accele.engine.entity.Entity3D;
import com.accele.engine.gfx.gui.GUI;
import com.accele.engine.gfx.shader.GUIShader;
import com.accele.engine.gfx.shader.Shader2D;
import com.accele.engine.gfx.shader.StaticShader;
import com.accele.engine.gfx.shader.TerrainShader;
import com.accele.engine.gfx.shader.WaterShader;
import com.accele.engine.gfx.skybox.Skybox;
import com.accele.engine.model.RawModel;
import com.accele.engine.model.TexturedModel;
import com.accele.engine.terrain.Terrain;
import com.accele.engine.util.Utils;

public final class Graphics {
	
	private static final float SIZE = 500f;
	
	private static final float[] VERTICES = {        
		    -SIZE,  SIZE, -SIZE,
		    -SIZE, -SIZE, -SIZE,
		     SIZE, -SIZE, -SIZE,
		     SIZE, -SIZE, -SIZE,
		     SIZE,  SIZE, -SIZE,
		    -SIZE,  SIZE, -SIZE,

		    -SIZE, -SIZE,  SIZE,
		    -SIZE, -SIZE, -SIZE,
		    -SIZE,  SIZE, -SIZE,
		    -SIZE,  SIZE, -SIZE,
		    -SIZE,  SIZE,  SIZE,
		    -SIZE, -SIZE,  SIZE,

		     SIZE, -SIZE, -SIZE,
		     SIZE, -SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE, -SIZE,
		     SIZE, -SIZE, -SIZE,

		    -SIZE, -SIZE,  SIZE,
		    -SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE, -SIZE,  SIZE,
		    -SIZE, -SIZE,  SIZE,

		    -SIZE,  SIZE, -SIZE,
		     SIZE,  SIZE, -SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		    -SIZE,  SIZE,  SIZE,
		    -SIZE,  SIZE, -SIZE,

		    -SIZE, -SIZE, -SIZE,
		    -SIZE, -SIZE,  SIZE,
		     SIZE, -SIZE, -SIZE,
		     SIZE, -SIZE, -SIZE,
		    -SIZE, -SIZE,  SIZE,
		     SIZE, -SIZE,  SIZE
	};
	
	private Engine engine;
	private StoredFont font;
	private Color color;
	private RawModel guiRect;
	private RawModel rect;
	private RawModel cube;
	private Matrix4f projectionMatrix;
	
	public Graphics(Engine engine, Matrix4f projectionMatrix) {
		this.engine = engine;
		this.font = engine.getRegistry().getFont("internal:default");
		this.color = Color.white;
		this.projectionMatrix = projectionMatrix;
		engine.getRegistry().register(guiRect = engine.getModelLoader().loadModel("acl.model.guiRect", "acl_internal_guiRect", new float[] { -1, 1, -1, -1, 1, 1, 1, -1 }, 2));
		engine.getRegistry().register(rect = engine.getModelLoader().loadModel("acl.model.rect", "acl_internal_rect", new float[] { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 }, 2));
		engine.getRegistry().register(cube = engine.getModelLoader().loadModel("acl.model.cube", "acl_internal_cube", VERTICES, 3));
	}
	
	public void drawImage(Texture tex, int x, int y, int width, int height, float rotation) {
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glPushMatrix();
		glTranslatef(x + width / 2, y + height / 2, 0);
		glRotatef(rotation, 0, 0, 1);
		glTranslatef(-(x + width / 2), -(y + height / 2), 0);
		drawImage(tex, x, y, width, height);
		glPopMatrix();
	}
	
	public void drawImage(Texture tex, int x, int y, int width, int height, float rotX, float rotY, float rotZ) {
		GL11.glMatrixMode(GL_MODELVIEW);
	    GL11.glLoadIdentity();
	    GL11.glPushMatrix();
	    GL11.glTranslatef(x + width / 2, y + height / 2, 0.0F);
	    GL11.glRotatef(rotX, 1.0F, 0.0F, 0.0F);
	    GL11.glRotatef(rotY, 0.0F, 1.0F, 0.0F);
	    GL11.glRotatef(rotZ, 0.0F, 0.0F, 1.0F);
	    GL11.glTranslatef(-(x + width / 2), -(y + height / 2), 0.0F);
	    drawImage(tex, x, y, width, height);
	    GL11.glPopMatrix();
	}
	
	public void drawImageInternal(Texture tex, int x, int y, int width, int height, float rotX, float rotY, float rotZ) {
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glPushMatrix();
		glTranslatef(x + width / 2, y + height / 2, 0);
		if (rotX != 0)
			glRotatef(rotX, 1, 0, 0);
		if (rotY != 0)
			glRotatef(rotY, 0, 1, 0);
		if (rotZ != 0)
			glRotatef(rotZ, 0, 0, 1);
		
		drawImage(tex, x, y, width, height);
		
		glPopMatrix();
	}
	
	public void drawImage(Texture tex, int x, int y, int width, int height) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		tex.getInternal().bind();
		
		glColor4f(1f, 1f, 1f, 1f);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2i(x, y);
		glTexCoord2f(1, 0);
		glVertex2i(x + width, y);
		glTexCoord2f(1, 1);
		glVertex2i(x + width, y + height);
		glTexCoord2f(0, 1);
		glVertex2i(x, y + height);
		glEnd();
	}
	
	public void drawCenteredImage(Texture tex, Rectangle rect, int width, int height) {
		tex.getInternal().bind();
		
		int centeredX = rect.getX() + ((rect.getWidth() - width) / 2);
		int centeredY = rect.getY() + ((rect.getHeight() - height) / 2);
		
		glColor4f(1f, 1f, 1f, 1f);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2i(centeredX, centeredY);
		glTexCoord2f(1, 0);
		glVertex2i(centeredX + width, centeredY);
		glTexCoord2f(1, 1);
		glVertex2i(centeredX + width, centeredY + height);
		glTexCoord2f(0, 1);
		glVertex2i(centeredX, centeredY + width);
		glEnd();
	}
	
	public void drawCenteredImageHorizontal(Texture tex, Rectangle rect, int width, int height) {
		tex.getInternal().bind();
		
		int centeredX = rect.getX() + ((rect.getWidth() - width) / 2);
		
		glColor4f(1f, 1f, 1f, 1f);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2i(centeredX, rect.getY());
		glTexCoord2f(1, 0);
		glVertex2i(centeredX + width, rect.getY());
		glTexCoord2f(1, 1);
		glVertex2i(centeredX + width, rect.getY() + height);
		glTexCoord2f(0, 1);
		glVertex2i(centeredX, rect.getY() + width);
		glEnd();
	}
	
	public void drawCenteredImageVertical(Texture tex, Rectangle rect, int width, int height) {
		tex.getInternal().bind();
		
		int centeredY = rect.getY() + ((rect.getHeight() - height) / 2);
		
		glColor4f(1f, 1f, 1f, 1f);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2i(rect.getX(), centeredY);
		glTexCoord2f(1, 0);
		glVertex2i(rect.getX() + width, centeredY);
		glTexCoord2f(1, 1);
		glVertex2i(rect.getX() + width, centeredY + height);
		glTexCoord2f(0, 1);
		glVertex2i(rect.getX(), centeredY + width);
		glEnd();
	}
	
	public void fillRect(int x, int y, int width, int height) {
		glDisable(GL_TEXTURE_2D);
		glColor4f(color.r, color.g, color.b, color.a);
		glBegin(GL_QUADS);
		
		glVertex2f(x, y);
		glVertex2f(x + width, y);
		glVertex2f(x + width, y + height);
		glVertex2f(x, y + height);
		
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}
	
	public void drawRect(int x, int y, int width, int height) {
		glDisable(GL_TEXTURE_2D);
		glColor4f(color.r, color.g, color.b, color.a);
		glBegin(GL_LINES);
		
		glVertex2f(x, y);
		glVertex2f(x + width, y);
		glVertex2f(x + width, y);
		glVertex2f(x + width, y + height);
		glVertex2f(x + width, y + height);
		glVertex2f(x, y + height);
		glVertex2f(x, y + height);
		glVertex2f(x, y);
		
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}
	
	public void drawRect(Rectangle rect) {
		drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}
	
	public void drawCenteredString(Rectangle rect, String data) {
		int x = (rect.getWidth() - font.getDerivedFont().getWidth(data)) / 2;
		int y = ((rect.getHeight() - font.getDerivedFont().getHeight(data)) / 2);
		x += rect.getX();
		y += rect.getY();
		drawString(x, y, data);
	}
	
	public void drawCenteredStringHorizontal(Rectangle rect, String data) {
		int x = (rect.getWidth() - font.getDerivedFont().getWidth(data)) / 2;
		x += rect.getX();
		drawString(x, rect.getY(), data);
	}
	
	public void drawCenteredStringVertical(Rectangle rect, String data) {
		int y = ((rect.getHeight() - font.getDerivedFont().getHeight(data)) / 2);
		y += rect.getY();
		drawString(rect.getX(), y, data);
	}
	
	public void drawString(int x, int y, String data) {
		font.getDerivedFont().drawString(x, y, data, color);
	}
	
	public StoredFont getFont() {
		return font;
	}
	
	public void setFont(StoredFont font) {
		this.font = font;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	// ================================================== New 2D Methods ================================================== //
	
	public void drawTexture(Vector2f pos, Vector2f rot, Vector2f scale, Texture texture, Shader2D shader) {
		shader.start();
		GL30.glBindVertexArray(guiRect.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
		shader.loadTransformationMatrix(Utils.Dim2.createTransformationMatrix(pos, rot, scale));
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, guiRect.getVertexCount());
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	// ================================================== 3D Methods ================================================== //
	
	public void drawGUIComponent(GUI gui, GUIShader shader) {
		shader.start();
		GL30.glBindVertexArray(guiRect.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture().getTextureID());
		shader.loadTransformationMatrix(Utils.Dim2.createTransformationMatrix(gui.getPos(), gui.getRotation(), gui.getScale()));
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, guiRect.getVertexCount());
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void drawModel(RawModel model) {
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	public void drawModel(TexturedModel model) {
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		ModelTexture texture = model.getTexture();
		if (texture.hasTransparency())
			Utils.Dim3.disableCulling();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		Utils.Dim3.enableCulling();
	}
	
	public void drawEntity(Entity3D entity, StaticShader shader) {
		GL30.glBindVertexArray(entity.getModel().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		shader.loadTransformationMatrix(Utils.Dim3.createTransformationMatrix(entity));
		
		ModelTexture texture = entity.getModel().getTexture();
		if (texture.hasTransparency())
			Utils.Dim3.disableCulling();
		
		shader.loadNumRows(texture.getNumRows());
		shader.loadOffset(new Vector2f(entity.getTextureXOffset(), entity.getTextureYOffset()));
		shader.loadSkyColor((Vector3f) engine.getRegistry().getProperty("internal:clearColor").get());
		shader.loadFogDensity((float) engine.getRegistry().getProperty("internal:shaderFogDensity").get());
		shader.loadFogGradient((float) engine.getRegistry().getProperty("internal:shaderFogGradient").get());
		shader.loadFakeLighting(texture.useFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getModel().getTexture().getTextureID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		Utils.Dim3.enableCulling();
	}
	
	public void drawEntitiesUsingModel(Entity3D[] entities, TexturedModel model, StaticShader shader) {
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		ModelTexture texture = model.getTexture();
		shader.loadNumRows(texture.getNumRows());
		if (texture.hasTransparency())
			Utils.Dim3.disableCulling();		
		shader.loadSkyColor((Vector3f) engine.getRegistry().getProperty("internal:clearColor").get());
		shader.loadFogDensity((float) engine.getRegistry().getProperty("internal:shaderFogDensity").get());
		shader.loadFogGradient((float) engine.getRegistry().getProperty("internal:shaderFogGradient").get());
		shader.loadFakeLighting(texture.useFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
		
		for (Entity3D e : entities) {
			shader.loadTransformationMatrix(Utils.Dim3.createTransformationMatrix(e));
			shader.loadOffset(new Vector2f(e.getTextureXOffset(), e.getTextureYOffset()));
			GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);			
		}
		
		Utils.Dim3.enableCulling();
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	public void drawTerrain(Terrain terrain, TerrainShader shader) {
		GL30.glBindVertexArray(terrain.getModel().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		shader.loadTransformationMatrix(Utils.Dim3.createTransformationMatrix(terrain));
		
		TerrainTexture texture = terrain.getModel().getTexture();
		
		shader.loadSkyColor((Vector3f) engine.getRegistry().getProperty("internal:clearColor").get());
		shader.loadFogDensity((float) engine.getRegistry().getProperty("internal:shaderFogDensity").get());
		shader.loadFogGradient((float) engine.getRegistry().getProperty("internal:shaderFogGradient").get());
		shader.loadShineVariables(1, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getBackground().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getR().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getG().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getB().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getModel().getBlendMap().getTextureID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	public void drawSkybox(Skybox skybox) {
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, skybox.getDayMapTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, skybox.getNightMapTextureID());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	public void drawWaterQuad(float x, float z, float height, float size, WaterShader shader) {
		shader.loadViewMatrix(engine.getCamera());
		GL30.glBindVertexArray(rect.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, engine.getWaterFrameBufferHandler().getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, engine.getWaterFrameBufferHandler().getRefractionTexture());
		shader.loadModelMatrix(Utils.Dim3.createTransformationMatrix(new Vector3f(x, height, z), 0, 0, 0, size));
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, rect.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
}
