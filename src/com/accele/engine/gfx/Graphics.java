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
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import com.accele.engine.core.Engine;
import com.accele.engine.entity.Entity3D;
import com.accele.engine.gfx.shader.StaticShader;
import com.accele.engine.gfx.shader.TerrainShader;
import com.accele.engine.model.RawModel;
import com.accele.engine.model.TexturedModel;
import com.accele.engine.terrain.Terrain;
import com.accele.engine.util.Utils;

public final class Graphics {

	private Engine engine;
	private StoredFont font;
	private Color color;
	
	public Graphics(Engine engine) {
		this.engine = engine;
		this.font = engine.getRegistry().getFont("internal:default");
		this.color = Color.white;
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
		
		tex.getImage().bind();
		
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
		tex.getImage().bind();
		
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
		tex.getImage().bind();
		
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
		tex.getImage().bind();
		
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
	
	// ================================================== 3D Methods ================================================== //
	
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getTextureID());
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
		
		shader.loadSkyColor((Vector3f) engine.getRegistry().getProperty("internal:clearColor").get());
		shader.loadFogDensity((float) engine.getRegistry().getProperty("internal:shaderFogDensity").get());
		shader.loadFogGradient((float) engine.getRegistry().getProperty("internal:shaderFogGradient").get());
		shader.loadFakeLighting(texture.useFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getModel().getTexture().getImage().getTextureID());
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
		if (texture.hasTransparency())
			Utils.Dim3.disableCulling();
		
		shader.loadSkyColor((Vector3f) engine.getRegistry().getProperty("internal:clearColor").get());
		shader.loadFogDensity((float) engine.getRegistry().getProperty("internal:shaderFogDensity").get());
		shader.loadFogGradient((float) engine.getRegistry().getProperty("internal:shaderFogGradient").get());
		shader.loadFakeLighting(texture.useFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getImage().getTextureID());
		
		for (Entity3D e : entities) {
			shader.loadTransformationMatrix(Utils.Dim3.createTransformationMatrix(e));
			GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);			
		}
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		Utils.Dim3.enableCulling();
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getModel().getBlendMap().getImage().getTextureID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
}
