package tmp.engineTester;

import org.lwjgl.opengl.Display;

import tmp.models.RawModel;
import tmp.models.TexturedModel;
import tmp.renderEngine.DisplayManager;
import tmp.renderEngine.Loader;
import tmp.renderEngine.Renderer;
import tmp.shaders.StaticShader;
import tmp.textures.ModelTexture;

public class MainGameLoop {

	public static int count = 0;
	
	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", "C:/Users/MrKnowname/Desktop/BesiegeTest/lib/native/");
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		StaticShader shader = new StaticShader();

		float[] vertices = { 
				-0.5f, 0.5f, 0, // V0
				-0.5f, -0.5f, 0, // V1
				0.5f, -0.5f, 0, // V2
				0.5f, 0.5f, 0 // V3
		};

		int[] indices = { 
				0, 1, 3, // Top left triangle (V0,V1,V3)
				3, 1, 2 // Bottom right triangle (V3,V1,V2)
		};
		
		float[] textureCoords = {
			0, 0, 0, 1, 1, 1, 1, 0	
		};

		RawModel model = loader.loadToVAO(vertices, indices, textureCoords);
		ModelTexture texture = new ModelTexture(loader.loadTexture("res/test.png"));
		TexturedModel texturedModel = new TexturedModel(model, texture);

		while (!Display.isCloseRequested()) {
			// game logic
			renderer.prepare();
			shader.start();
			renderer.render(texturedModel);
			shader.stop();
			DisplayManager.updateDisplay();
		}

		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
