package com.accele.engine.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.Rectangle;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.accele.engine.core.Engine;
import com.accele.engine.entity.Entity2D;
import com.accele.engine.entity.Entity3D;
import com.accele.engine.gfx.Camera;
import com.accele.engine.model.ModelLoader;
import com.accele.engine.model.RawModel;
import com.accele.engine.property.Property;
import com.accele.engine.sfx.internal.SFXException;
import com.accele.engine.sfx.internal.SoundStore;
import com.accele.engine.terrain.Terrain;
import com.accele.engine.util.internal.Vertex;

public final class Utils {
	
	public static final Loader DEFAULT_TEXTURE_LOADER = new Loader() {
		@Override
		public Object load(String location) {
			try {
				Texture texture = TextureLoader.getTexture("PNG", new FileInputStream(new File(location)));
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
				return texture;
			} catch (IOException e) {
				System.err.println("ResourceLoaderError: " + e.getMessage());
				return null;
			}
		}
	};
	
	public static final Loader DEFAULT_IMAGE_LOADER = new Loader() {
		@Override
		public Object load(String location) {
			try {
				BufferedImage image = ImageIO.read(new File(location));
				return image;
			} catch (IOException e) {
				System.err.println("ResourceLoaderError: " + e.getMessage());
				return null;
			}
		}
	};
	
	public static final Loader DEFAULT_SFX_LOADER = new Loader() {
		@Override
		public Object load(String location) {
			SoundStore.get().init();
			try {
				File f = new File(location);
				
				if (!f.exists() || !f.isFile())
					throw new SFXException("Failed to load sfx " + location);
				
				if (location.toLowerCase().endsWith(".ogg"))
					return SoundStore.get().getOgg(f);
				else if (location.toLowerCase().endsWith(".wav"))
					return SoundStore.get().getWAV(f);
				else if ((location.toLowerCase().endsWith(".xm")) || (location.toLowerCase().endsWith(".mod")))
					return SoundStore.get().getMOD(f);
				else if ((location.toLowerCase().endsWith(".aif")) || (location.toLowerCase().endsWith(".aiff")))
					return SoundStore.get().getAIF(f);
				else
					throw new SFXException("Only .xm, .mod, .ogg, and .aif/f are supported.");
			} catch (Throwable e) {
				System.err.println("ResourceLoaderError: " + e.getMessage());
				return null;
			}
		}
	};
	
	public static final Loader DEFAULT_MODEL_LOADER = new Loader() {

		@Override
		public Object load(String location) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(new File(location)));
				String line = "";
				List<Vertex> vertices = new ArrayList<>();
				List<Vector2f> textures = new ArrayList<>();
				List<Vector3f> normals = new ArrayList<>();
				List<Integer> indices = new ArrayList<>();
				
				while (true) {
					line = reader.readLine();
					if (line.startsWith("v ")) {
						String[] currentLine = line.split(" ");
						Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
								(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
						Vertex newVertex = new Vertex(vertices.size(), vertex);
						vertices.add(newVertex);

					} else if (line.startsWith("vt ")) {
						String[] currentLine = line.split(" ");
						Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
								(float) Float.valueOf(currentLine[2]));
						textures.add(texture);
					} else if (line.startsWith("vn ")) {
						String[] currentLine = line.split(" ");
						Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
								(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
						normals.add(normal);
					} else if (line.startsWith("f ")) {
						break;
					}
				}
				
				while (line != null && line.startsWith("f ")) {
	                String[] currentLine = line.split(" ");
	                String[] vertex1 = currentLine[1].split("/");
	                String[] vertex2 = currentLine[2].split("/");
	                String[] vertex3 = currentLine[3].split("/");
	                Vertex.processVertex(vertex1, vertices, indices);
	                Vertex.processVertex(vertex2, vertices, indices);
	                Vertex.processVertex(vertex3, vertices, indices);
	                line = reader.readLine();
	            }
								
				reader.close();
				
				removeUnusedVertices(vertices);
				
				float[] verticesArray = new float[vertices.size() * 3];
				int[] indicesArray = new int[indices.size()];
				
				float[] texturesArray = new float[vertices.size() * 2];
				float[] normalsArray = new float[vertices.size() * 3];

				for (int i = 0; i < vertices.size(); i++) {
					Vertex currentVertex = vertices.get(i);
					Vector3f position = currentVertex.getPosition();
					Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
					Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
					verticesArray[i * 3] = position.x;
					verticesArray[i * 3 + 1] = position.y;
					verticesArray[i * 3 + 2] = position.z;
					texturesArray[i * 2] = textureCoord.x;
					texturesArray[i * 2 + 1] = 1 - textureCoord.y;
					normalsArray[i * 3] = normalVector.x;
					normalsArray[i * 3 + 1] = normalVector.y;
					normalsArray[i * 3 + 2] = normalVector.z;
				}

				for (int i = 0; i < indicesArray.length; i++)
					indicesArray[i] = indices.get(i);

				return new Object[] { verticesArray, indicesArray, texturesArray, normalsArray };
			} catch (IOException e) {
				System.err.println("ResourceLoaderError: " + e.getMessage());
				return null;
			}
		}
		
	};
	
	private static void removeUnusedVertices(List<Vertex> vertices) {
		for (Vertex vertex : vertices) {
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}
	
	private Utils() {}
	
	public static int[] toIntArray(List<Integer> list) {
		int[] result = new int[list.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = list.get(i);
		
		return result;
	}
	
	public static <T extends Property> Property addProperty(List<T> properties, T property) {
		properties.add(property);
		return property;
	}
	
	public static float clamp(float var, float min, float max) {
		return (var >= min ? var : min) <= max ? var : max;
	}
	
	public static boolean inRange(float var, float min, float max) {
		return (var >= min && var <= max);
	}
	
	public static class Dim2 {
		
		public static boolean withinBounds(Rectangle boundee, Rectangle bounder, boolean boundeeHasOffset) {
			if (boundeeHasOffset) {
				return (boundee.getX() >= bounder.getX()) && (boundee.getY() >= bounder.getY()) && (boundee.getX() + boundee.getWidth() <= bounder.getWidth())
						&& (boundee.getY() + boundee.getHeight() <= bounder.getHeight());
			}
			return (boundee.getX() >= bounder.getX()) && (boundee.getY() >= bounder.getY()) && (boundee.getX() <= bounder.getWidth())
					&& (boundee.getY() <= bounder.getHeight());
		}

		public static int boundCheck(Rectangle boundee, Rectangle bounder, boolean boundeeHasOffset) {
			if (boundee.getX() < bounder.getX()) {
				return 0;
			}
			if (boundee.getY() < bounder.getY()) {
				return 1;
			}
			if (boundeeHasOffset) {
				if (boundee.getX() + boundee.getWidth() > bounder.getWidth()) {
					return 2;
				}
				if (boundee.getY() + boundee.getHeight() > bounder.getHeight()) {
					return 3;
				}
			} else {
				if (boundee.getX() > bounder.getWidth()) {
					return 2;
				}
				if (boundee.getY() > bounder.getHeight()) {
					return 3;
				}
			}
			return -1;
		}

		public static Rectangle fixBounds(Rectangle boundee, Rectangle bounder, boolean boundeeHasOffset) {
			Rectangle result = new Rectangle(boundee);
			if (boundee.getX() < bounder.getX()) {
				result.setX(bounder.getX());
			}
			if (boundee.getY() < bounder.getY()) {
				result.setY(bounder.getY());
			}
			if (boundeeHasOffset) {
				if (boundee.getX() + boundee.getWidth() > bounder.getWidth()) {
					result.setX(bounder.getWidth());
				}
				if (boundee.getY() + boundee.getHeight() > bounder.getHeight()) {
					result.setY(bounder.getHeight());
				}
			} else {
				if (boundee.getX() > bounder.getWidth()) {
					result.setX(bounder.getWidth());
				}
				if (boundee.getY() > bounder.getHeight()) {
					result.setY(bounder.getHeight());
				}
			}
			return result;
		}
		
		public static Vector2f fixVector(Vector2f var, Rectangle requiredBounds, int xOffset, int yOffset) {
			var.x = clamp(var.x + xOffset, requiredBounds.getX(), requiredBounds.getX() + requiredBounds.getWidth()) - xOffset;
			var.y = clamp(var.y + yOffset, requiredBounds.getY(), requiredBounds.getY() + requiredBounds.getHeight()) - yOffset;
			
			return var;
		}
		
		public static int entityCollidesWithEntity(Entity2D a, Entity2D b) {
			Rectangle ra = a.getBounds();
			Rectangle rb = b.getBounds();
			
			if (ra.getX() + ra.getWidth() == rb.getX())
				return 0;
			else if (ra.getY() + ra.getHeight() == rb.getY())
				return 1;
			else if (ra.getX() == rb.getX() + rb.getWidth())
				return 2;
			else if (ra.getY() == rb.getY() + rb.getHeight())
				return 3;
			else
				return -1;
		}
		
	}
	
	public static class Dim3 {
		
		public static final float GFX_FOV = 70;
		public static final float GFX_NEAR_PLANE = 0.1f;
		public static final float GFX_FAR_PLANE = 1000;
		public static final float MAX_TERRAIN_PIXEL_COLOR = 0xFFFFFF;
		public static final float MAX_TERRAIN_HEIGHT = 40f;
		
		public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
			Matrix4f matrix = new Matrix4f();
			matrix.setIdentity();
			Matrix4f.translate(translation, matrix, matrix);
			Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
			Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
			Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
			Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
			
			return matrix;
		}
		
		public static Matrix4f createTransformationMatrix(Entity3D entity) {
			return createTransformationMatrix((Vector3f) entity.getPos(), entity.getXRot(), entity.getYRot(), entity.getZRot(), entity.getScale());
		}
		
		public static Matrix4f createTransformationMatrix(Terrain terrain) {
			return createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		}
		
		public static Matrix4f createProjectionMatrix(Engine engine) {
			return createProjectionMatrix(engine, GFX_FOV, GFX_NEAR_PLANE, GFX_FAR_PLANE);
		}
		
		public static Matrix4f createProjectionMatrix(Engine engine, float fov, float nearPlane, float farPlane) {
			float aspectRatio = (float) engine.getRegistry().getProperty("internal:aspectRatio").get();
			float yScale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))) * aspectRatio);
			float xScale = yScale / aspectRatio;
			float frustumLength = farPlane - nearPlane;
			
			Matrix4f projectionMatrix = new Matrix4f();
			projectionMatrix.m00 = xScale;
			projectionMatrix.m11 = yScale;
			projectionMatrix.m22 = -((farPlane + nearPlane) / frustumLength);
			projectionMatrix.m23 = -1;
			projectionMatrix.m32 = -((2 * nearPlane * farPlane) / frustumLength);
			projectionMatrix.m33 = 0;
			
			return projectionMatrix;
		}
		
		public static Matrix4f createViewMatrix(Camera camera) {
			Matrix4f matrix = new Matrix4f();
			matrix.setIdentity();
			Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), matrix, matrix);
			Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), matrix, matrix);
			Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), matrix, matrix);
			Vector3f pos = camera.getPos();
			Vector3f npos = new Vector3f(-pos.x, -pos.y, -pos.z);
			Matrix4f.translate(npos, matrix, matrix);
			
			return matrix;
		}
		
		public static RawModel generateTerrainModel(String registryID, String localizedID, ModelLoader loader, Resource heightMap, float size) {
			return generateTerrainModel(registryID, localizedID, loader, heightMap, MAX_TERRAIN_PIXEL_COLOR, MAX_TERRAIN_HEIGHT, size);
		}
		
		public static RawModel generateTerrainModel(String registryID, String localizedID, ModelLoader loader, Resource heightMap, float maxPixelColor, float maxHeight, float size) {
			heightMap.load();
			
			int vertexCount = ((BufferedImage) heightMap.getValue()).getHeight();
			
			int count = vertexCount * vertexCount;
			float[] vertices = new float[count * 3];
			float[] normals = new float[count * 3];
			float[] textureCoords = new float[count * 2];
			int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];
			int vertexPointer = 0;
			for (int i = 0; i < vertexCount; i++) {
				for (int j = 0; j < vertexCount; j++) {
					vertices[vertexPointer * 3] = (float) j / ((float) vertexCount - 1) * size;
					vertices[vertexPointer * 3 + 1] = getTerrainHeight(j, i, (BufferedImage) heightMap.getValue(), maxPixelColor, maxHeight);
					vertices[vertexPointer * 3 + 2] = (float) i / ((float) vertexCount - 1) * size;
					Vector3f normal = calculateNormal(j, i, (BufferedImage) heightMap.getValue(), maxPixelColor, maxHeight);
					normals[vertexPointer * 3] = normal.x;
					normals[vertexPointer * 3 + 1] = normal.y;
					normals[vertexPointer * 3 + 2] = normal.z;
					textureCoords[vertexPointer * 2] = (float) j / ((float) vertexCount - 1);
					textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) vertexCount - 1);
					vertexPointer++;
				}
			}
			int pointer = 0;
			for (int gz = 0; gz < vertexCount - 1; gz++) {
				for (int gx = 0; gx < vertexCount - 1; gx++) {
					int topLeft = (gz * vertexCount) + gx;
					int topRight = topLeft + 1;
					int bottomLeft = ((gz + 1) * vertexCount) + gx;
					int bottomRight = bottomLeft + 1;
					indices[pointer++] = topLeft;
					indices[pointer++] = bottomLeft;
					indices[pointer++] = topRight;
					indices[pointer++] = topRight;
					indices[pointer++] = bottomLeft;
					indices[pointer++] = bottomRight;
				}
			}

			return loader.loadModel(registryID, localizedID, vertices, indices, textureCoords, normals);
		}
		
		private static Vector3f calculateNormal(int x, int z, BufferedImage image, float maxPixelColor, float maxHeight) {
			float lHeight = getTerrainHeight(x - 1, z, image, maxPixelColor, maxHeight);
			float rHeight = getTerrainHeight(x - 1, z, image, maxPixelColor, maxHeight);
			float dHeight = getTerrainHeight(x - 1, z, image, maxPixelColor, maxHeight);
			float uHeight = getTerrainHeight(x - 1, z, image, maxPixelColor, maxHeight);
			return (Vector3f) new Vector3f(lHeight - rHeight, 2f, dHeight - uHeight).normalise();
		}
		
		private static float getTerrainHeight(int x, int z, BufferedImage image, float maxPixelColor, float maxHeight) {
			if (!inRange(x, 0, image.getHeight()) || !inRange(z, 0, image.getHeight()))
				return 0;
			return ((image.getRGB(x, z) + (maxPixelColor / 2f)) / (maxPixelColor / 2f)) * maxHeight;
		}
		
		public static RawModel generateFlatTerrainModel(String registryID, String localizedID, ModelLoader loader, float size, int vertexCount) {
			int count = vertexCount * vertexCount;
			float[] vertices = new float[count * 3];
			float[] normals = new float[count * 3];
			float[] textureCoords = new float[count * 2];
			int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];
			int vertexPointer = 0;
			for (int i = 0; i < vertexCount; i++) {
				for (int j = 0; j < vertexCount; j++) {
					vertices[vertexPointer * 3] = (float) j / ((float) vertexCount - 1) * size;
					vertices[vertexPointer * 3 + 1] = 0;
					vertices[vertexPointer * 3 + 2] = (float) i / ((float) vertexCount - 1) * size;
					normals[vertexPointer * 3] = 0;
					normals[vertexPointer * 3 + 1] = 1;
					normals[vertexPointer * 3 + 2] = 0;
					textureCoords[vertexPointer * 2] = (float) j / ((float) vertexCount - 1);
					textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) vertexCount - 1);
					vertexPointer++;
				}
			}
			int pointer = 0;
			for (int gz = 0; gz < vertexCount - 1; gz++) {
				for (int gx = 0; gx < vertexCount - 1; gx++) {
					int topLeft = (gz * vertexCount) + gx;
					int topRight = topLeft + 1;
					int bottomLeft = ((gz + 1) * vertexCount) + gx;
					int bottomRight = bottomLeft + 1;
					indices[pointer++] = topLeft;
					indices[pointer++] = bottomLeft;
					indices[pointer++] = topRight;
					indices[pointer++] = topRight;
					indices[pointer++] = bottomLeft;
					indices[pointer++] = bottomRight;
				}
			}

			return loader.loadModel(registryID, localizedID, vertices, indices, textureCoords, normals);
		}
		
		public static void enableCulling() {
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
		}
		
		public static void disableCulling() {
			GL11.glDisable(GL11.GL_CULL_FACE);
		}
		
	}
	
}
