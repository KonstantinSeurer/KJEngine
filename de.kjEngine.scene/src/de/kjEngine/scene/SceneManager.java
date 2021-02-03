package de.kjEngine.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kjEngine.renderer.Renderable;

public class SceneManager {

	public static Map<String, Scene> scenes = new HashMap<>();
	private static Scene activeScene;
	
	public static void updateDescriptors() {
		if (activeScene != null) {
			activeScene.updateDescriptors();
		}
	}

	public static void update(float delta) {
		if (activeScene != null) {
			activeScene.update(delta);
		}
	}

	public static void render(List<Renderable<?>> renderList) {
		if (activeScene != null) {
			activeScene.render();
			renderList.add(activeScene);
		}
	}

	public static void addScene(String name, Scene scene) {
		scenes.put(name, scene);
	}

	public static void removeScene(String name) {
		scenes.remove(name);
	}

	public static Scene getScene(String name) {
		return scenes.get(name);
	}

	public static void setScene(String name) {
		activeScene = scenes.get(name);
	}

	public static Scene getActiveScene() {
		return activeScene;
	}
}
