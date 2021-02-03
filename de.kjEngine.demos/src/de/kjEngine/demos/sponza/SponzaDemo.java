/**
 * 
 */
package de.kjEngine.demos.sponza;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.core.ApplicationEventAdapter;
import de.kjEngine.core.Engine;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.math.Real;
import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.scene.Component;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.SceneGraphItem.GPUImplementation.ID;
import de.kjEngine.scene.io.OBJFileLoader;
import de.kjEngine.scene.light.DirectionalLightComponent;
import de.kjEngine.scene.light.DirectionalLightShadowMapComponent;
import de.kjEngine.scene.light.ShadowMap;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.ui.Window;

/**
 * @author konst
 *
 */
public class SponzaDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", SponzaDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = de.kjEngine.ui.RefreshMode.UNLIMITED;
		settings.window.width = 1600;
		settings.window.height = 900;
		settings.application = new ApplicationEventAdapter() {

			@Override
			public void init() {
				Scene s = new Scene();

				final DirectionalLightComponent sun = new DirectionalLightComponent(true, Vec3.scale(5f), Vec3.create(0f, -1f, 0f).normalise());
				sun.addComponent(new DirectionalLightShadowMapComponent(new ShadowMap(2048), 50f));
				sun.addComponent(new Component() {

					@Override
					public void update() {
						parent.transform.position.set(s.getCamera().transform.getGlobalPosition());
					}
				});
				s.addEntity(sun);
				
				Model[] sponza = Model.combine(OBJFileLoader.loadOBJ(RL.create("jar://app/de/kjEngine/demos/sponza/sponza.obj")));
				ModelComponent[] sponzaInstance = new ModelComponent[sponza.length];
				for (int i = 0; i < sponza.length; i++) {
					sponzaInstance[i] = new ModelComponent(sponza[i]);
				}
				Entity sponzaEntity = new Entity(false, Vec3.create(), Vec3.scale(0.01f), sponzaInstance);
				sponzaEntity.transform.rotation.rotateY(Real.HALF_PI);
				s.addEntity(sponzaEntity);
				
				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public Set<ID> initRenderer() {
				Pipeline pipeline = Pipeline.create(new JSONObject(ResourceManager.loadTextResource(RL.create("jar://app/de/kjEngine/demos/sponza/pipeline.ppl"), false)));
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");
				return pipeline.getRequiredGPUImplementations();
			}
		};
		Engine.start(settings);
	}
}
