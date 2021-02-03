/**
 * 
 */
package de.kjEngine.demos.ssr;

import org.json.JSONObject;

import de.kjEngine.core.Engine;
import de.kjEngine.core.StateAdapter;
import de.kjEngine.graphics.Color;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.material.OpaquePbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.scene.renderer.SSRRenderer;
import de.kjEngine.ui.Window;

/**
 * @author konst
 *
 */
public class SSRDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", SSRDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = de.kjEngine.ui.RefreshMode.UNLIMITED;
		settings.window.width = 1600;
		settings.window.height = 900;
		settings.stateHandlers.add(new StateAdapter() {

			@Override
			public void init() {
				Scene s = new Scene();
				s.getSunLight().getColor().set(1f, 1f, 1f);
				s.getSunLight().getDirection().set(1f, -0.7f, 0.5f).normalise();

				s.getCamera().getTransform().getPosition().set(0f, 1f, -2f);

				s.addEntity(new Entity(Vec3.create(), new ModelComponent(new Model(Model.CUBE).setMaterial(new OpaquePbrMaterial(Color.GOLD).setMetalness(1f).setRoughness(0.1f)))));
				s.addEntity(new Entity(Vec3.create(), Vec3.scale(2f), new ModelComponent(new Model(Model.PLANE).setMaterial(new OpaquePbrMaterial(Color.IRON).setMetalness(1f).setRoughness(0.1f)))));

				Pipeline pipeline = Pipeline.create(new JSONObject(ResourceManager.loadTextResource(RL.create("jar://app/de/kjEngine/demos/ssr/pipeline.ppl"), false)));
				
				((SSRRenderer) pipeline.getStage(3)).setSource(pipeline.getStage(4).getOutput().get("result"));
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}
		});
		Engine.start(settings);
	}
}
