/**
 * 
 */
package de.kjEngine.demos.spheres;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.core.ApplicationEventAdapter;
import de.kjEngine.core.Engine;
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
import de.kjEngine.scene.SceneGraphItem.GPUImplementation.ID;
import de.kjEngine.scene.ibl.UE4LightProbeComponent;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.ui.Window;

/**
 * @author konst
 *
 */
public class SpheresDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", SpheresDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = de.kjEngine.ui.RefreshMode.UNLIMITED;
		settings.window.width = 1600;
		settings.window.height = 900;
		settings.application = new ApplicationEventAdapter() {

			@Override
			public void init() {
				Scene s = new Scene();

				int diffuseResolution = 16, specularResolution = 128;

				int num = 10;

				for (int x = 0; x < num; x++) {
					Vec3 pos = Vec3.create(x, 0f, 5f);

					Model m = new Model(Model.getDefaultSphere());
					PbrMaterial mat = new PbrMaterial(Color.GRAY_05);
					mat.setRoughness(x / (float) (num - 1));
					m.setMaterial(mat);
					s.addEntity(new Entity(false, pos, Vec3.scale(0.4f), new ModelComponent(m)));
				}

				for (int x = 0; x < num; x++) {
					Vec3 pos = Vec3.create(x, 1f, 5f);

					Model m = new Model(Model.getDefaultSphere());
					PbrMaterial mat = new PbrMaterial(Color.GOLD);
					mat.setRoughness(x / (float) (num - 1));
					mat.setMetalness(1f);
					m.setMaterial(mat);
					s.addEntity(new Entity(false, pos, Vec3.scale(0.4f), new ModelComponent(m)));
				}
				
				for (int x = 0; x < num; x++) {
					Vec3 pos = Vec3.create(x, 2f, 5f);

					Model m = new Model(Model.getDefaultSphere());
					PbrMaterial mat = new PbrMaterial(Color.SILVER);
					mat.setRoughness(x / (float) (num - 1));
					mat.setMetalness(1f);
					m.setMaterial(mat);
					s.addEntity(new Entity(false, pos, Vec3.scale(0.4f), new ModelComponent(m)));
				}
				
				for (int x = 0; x < num; x++) {
					Vec3 pos = Vec3.create(x, 3f, 5f);

					Model m = new Model(Model.getDefaultSphere());
					PbrMaterial mat = new PbrMaterial(Color.COPPER);
					mat.setRoughness(x / (float) (num - 1));
					mat.setMetalness(1f);
					m.setMaterial(mat);
					s.addEntity(new Entity(false, pos, Vec3.scale(0.4f), new ModelComponent(m)));
				}
				
				s.addEntity(new Entity(false, Vec3.create(5f, 2f, 5f), Vec3.create(6f, 3f, 0.5f)).addComponent(new UE4LightProbeComponent(diffuseResolution, specularResolution, 0.1f, 10f)));

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public Set<ID> initRenderer() {
				Pipeline pipeline = Pipeline.create(new JSONObject(ResourceManager.loadTextResource(RL.create("jar://app/de/kjEngine/demos/spheres/pipeline.ppl"), false)));
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");
				
				return pipeline.getRequiredGPUImplementations();
			}
		};
		Engine.start(settings);
	}
}
