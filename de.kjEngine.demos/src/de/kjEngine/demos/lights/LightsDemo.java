/**
 * 
 */
package de.kjEngine.demos.lights;

import java.util.List;
import java.util.Set;

import de.kjEngine.component.Component;
import de.kjEngine.core.Engine;
import de.kjEngine.core.Engine.Settings.Window.RefreshMode;
import de.kjEngine.core.StateAdapter;
import de.kjEngine.graphics.Color;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.math.Real;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.random.Rand;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneComponent;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.camera.FirstPersonCameraController;
import de.kjEngine.scene.camera.PerspectiveCameraComponent;
import de.kjEngine.scene.light.SphereLightComponent;
import de.kjEngine.scene.light.SpotLightComponent;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.ui.Window;

/**
 * @author konst
 *
 */
public class LightsDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", LightsDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = RefreshMode.UNLIMITED;
		settings.stateHandlers.add(new StateAdapter() {

			@Override
			public void init() {
				Scene s = new Scene();
				s.camera = new PerspectiveCameraComponent();
				s.add(new Entity(true, Vec3.create(0f, 2f, -5f)).add(s.camera).add(new FirstPersonCameraController()));

				Model plane = new Model(Model.getDefaultPlane());
				plane.setMaterial(new PbrMaterial(Color.GRAY_03).setRoughness(0.3f));
				s.add(new Entity(false, Vec3.create(), Vec3.scale(200f)).add(new ModelComponent(plane)));

				Rand.setSeed(1945L);
				
				// Model sphere = new Model(SphereBuilder.create(10, 1f), PbrMaterial.getNullMaterial());

				for (int i = 0; i < 20; i++) {
					Vec3 color = Vec3.create(Rand.value(), Rand.value(), Rand.value()).normalise().mul(2f);

					Vec3 pos = Vec3.create(Rand.value(-2f, 2f), Rand.value(1f, 3f), Rand.value(-2f, 2f));
					float radius = Rand.value(0.1f, 0.5f);

					PbrMaterial mat = new PbrMaterial(Color.BLACK).setEmission(color);

					Entity light = new Entity(true, pos, Vec3.scale(radius));
					light.add(new ModelComponent(new Model(Model.getDefaultSphere()).setMaterial(mat)));
					light.add(new SphereLightComponent(color, radius));
					light.add(new SceneComponent<Entity, SceneComponent<Entity, ?>>(Component.EARLY) {

						float speed = Rand.value(0.5f, 1f);
						float amount = Rand.value(0.1f, 0.5f);
						float t;

						@Override
						public void update(float delta) {
							parent.transform.position.y = pos.y + Real.sin(t * speed) * amount;
							t += delta;
						}
					});
					s.add(light);
				}

				s.add(new Entity(false, Vec3.create(-10f, 2f, -10f)).add(new SpotLightComponent(Vec3.create(500f, 500f, 500f), Vec3.create(1f, -0.5f, 1f).normalise(), 1f, 1f)));

				List<Model> lamp_model = Model.loadModel(RL.create("jar://app/de/kjEngine/demos/lights/lamp.obj"), false);
				lamp_model.get(0).setMaterial(new PbrMaterial(Color.BLACK).setRoughness(0f));
				lamp_model.get(1).setMaterial(new PbrMaterial(Color.BLACK).setEmission(Vec3.create(10f, 10f, 10f)));
				lamp_model.get(2).setMaterial(new PbrMaterial(Color.ALUMINIUM).setRoughness(0f).setMetalness(1f));
				Entity lamp = new Entity(false, Vec3.create(-10f, 0f, -10f));
				lamp.add(new ModelComponent(lamp_model.get(0)));
				lamp.add(new ModelComponent(lamp_model.get(1)));
				lamp.add(new ModelComponent(lamp_model.get(2)));
				lamp.transform.rotation.rotateY(-Real.HALF_PI * 0.5f);
				s.add(lamp);

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public void initRenderer(Set<ID> target) {
				boolean raytracing = false;
				String rl = raytracing ? "jar://app/de/kjEngine/demos/lights/rayTracingPipeline.ppl" : "jar://app/de/kjEngine/demos/lights/rasterizationPipeline.ppl";
				Pipeline pipeline = Pipeline.create(RL.create(rl), Window.getWidth(), Window.getHeight());
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");

				pipeline.getRequiredRenderImplementations(target);
			}
		});
		Engine.start(settings, LightsDemo.class.getModule());
	}
}
