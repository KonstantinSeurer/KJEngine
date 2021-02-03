/**
 * 
 */
package de.kjEngine.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kjEngine.renderer.Renderable;

/**
 * @author konst
 *
 */
public class UISceneManager {
	
	public static Map<String, UIScene> scenes = new HashMap<>();
	private static UIScene activeScene;
	
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

	public static void addScene(String name, UIScene scene) {
		scenes.put(name, scene);
	}

	public static void removeScene(String name) {
		scenes.remove(name);
	}

	public static UIScene getScene(String name) {
		return scenes.get(name);
	}

	public static void setScene(String name) {
		if (activeScene != null) {
			Window.removeEventListener(activeScene);
		}
		activeScene = scenes.get(name);
		if (activeScene != null) {
			Window.addEventListener(activeScene);
		}
	}

	public static UIScene getActiveScene() {
		return activeScene;
	}
}
