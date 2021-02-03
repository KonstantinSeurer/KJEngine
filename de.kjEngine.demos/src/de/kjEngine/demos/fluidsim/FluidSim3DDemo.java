/**
 * 
 */
package de.kjEngine.demos.fluidsim;

import java.util.Set;

import de.kjEngine.core.ApplicationEventAdapter;
import de.kjEngine.core.Engine;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.random.Rand;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.SceneGraphItem.GPUImplementation.ID;
import de.kjEngine.scene.camera.PerspectiveCameraComponent;
import de.kjEngine.scene.light.DirectionalLightComponent;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.scene.physics.sph.CPUSPH;
import de.kjEngine.scene.physics.sph.SPHParticle;
import de.kjEngine.scene.physics.sph.SPHSettings;
import de.kjEngine.ui.Window.RefreshMode;
import de.kjEngine.util.Timer;

/**
 * @author konst
 *
 */
public class FluidSim3DDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", FluidSim3DDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = RefreshMode.UNLIMITED;
		settings.window.width = 1600;
		settings.window.height = 800;
		settings.application = new ApplicationEventAdapter() {

			CPUSPH sim;

			@Override
			public void init() {
				Scene s = new Scene();

				s.getCamera().transform.position.set(2.5f, 0.5f, -3f);
				((PerspectiveCameraComponent) s.getCamera()).getFrustum().setNear(0.1f).setFar(10f);

				s.addEntity(new DirectionalLightComponent(false, Vec3.create(2f, 2f, 2f), Vec3.create(0f, -1f, 0f)));

				SPHSettings settings = new SPHSettings();

				sim = new CPUSPH(settings);

				int width = 20;
				int height = 30;
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						float rnd = 0.01f;
						Vec3 pos = Vec3.create(x + Rand.value(-rnd, rnd), y + Rand.value(-rnd, rnd), 0f).mul(settings.particleRadius);
						pos.y *= 0.6;
						Entity e = new Entity(true, pos, Vec3.scale(settings.particleRadius * 0.5f), new ModelComponent(Model.getDefaultSphere()));
						s.addEntity(e);
						SPHParticle p = new SPHParticle();
						p.position = e.transform.position;
						sim.addParticle(p);
					}
				}

				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						float rnd = 0.01f;
						Vec3 pos = Vec3.create(x + Rand.value(-rnd, rnd), y + Rand.value(-rnd, rnd), 0f).mul(settings.particleRadius);
						pos.x = 5f - pos.x;
						pos.y *= 0.6;
						Entity e = new Entity(true, pos, Vec3.scale(settings.particleRadius * 0.5f), new ModelComponent(Model.getDefaultSphere()));
						s.addEntity(e);
						SPHParticle p = new SPHParticle();
						p.position = e.transform.position;
						sim.addParticle(p);
					}
				}

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public void update() {
				Timer.start();
				sim.update(Engine.getDelta() * 4f, 10);
				Timer.printPassed();
			}

			@Override
			public Set<ID> initRenderer() {
				Pipeline pipeline = Pipeline.create(RL.create("jar://app/de/kjEngine/demos/fluidsim/pipeline.ppl"));

				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");

				return pipeline.getRequiredGPUImplementations();
			}
		};
		Engine.start(settings);
	}
}
