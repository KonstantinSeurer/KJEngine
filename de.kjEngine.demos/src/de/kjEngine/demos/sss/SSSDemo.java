/**
 * 
 */
package de.kjEngine.demos.sss;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.core.ApplicationEventAdapter;
import de.kjEngine.core.Engine;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.scene.Component;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.SceneGraphItem.GPUImplementation.ID;
import de.kjEngine.scene.light.DirectionalLightComponent;
import de.kjEngine.scene.light.DirectionalLightShadowMapComponent;
import de.kjEngine.scene.light.ShadowMap;
import de.kjEngine.scene.material.OpaquePbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.ui.Window.RefreshMode;

/**
 * @author konst
 *
 */
public class SSSDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", SSSDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = RefreshMode.UNLIMITED;
		settings.application = new ApplicationEventAdapter() {

			@Override
			public void init() {
				Scene s = new Scene();
				s.getCamera().transform.getPosition().set(0f, 0f, -5f);

				final DirectionalLightComponent sun = new DirectionalLightComponent(true, Vec3.scale(3f), Vec3.create(1f, -1f, -1f).normalise());
				sun.addComponent(new DirectionalLightShadowMapComponent(new ShadowMap(2048), 20f));
				sun.addComponent(new Component() {

					@Override
					public void update() {
						parent.transform.getPosition().set(s.getCamera().transform.getGlobalPosition());
					}
				});
				s.addEntity(sun);
				
				OpaquePbrMaterial testMaterial = new OpaquePbrMaterial();
				testMaterial.setSubsurface(Vec3.create(1f, 0f, 0f));
				Model testModel = new Model(Model.getDefaultSphere());
				testModel.setMaterial(testMaterial);
				s.addEntity(new Entity(false, Vec3.create(), new ModelComponent(testModel)));
				s.addEntity(new Entity(false, Vec3.create(0f, -1f, 0f), Vec3.scale(5f), new ModelComponent(Model.getDefaultPlane())));

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public Set<ID> initRenderer() {
				Pipeline pipeline = Pipeline.create(new JSONObject(ResourceManager.loadTextResource(RL.create("jar://app/de/kjEngine/demos/sss/pipeline.ppl"), false)));
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");

				return pipeline.getRequiredGPUImplementations();
			}

			@Override
			public void dispose() {
			}
		};
		Engine.start(settings);
	}
}
