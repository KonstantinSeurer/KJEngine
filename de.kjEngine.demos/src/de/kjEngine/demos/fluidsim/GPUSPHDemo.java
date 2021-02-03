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
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.SceneGraphItem.GPUImplementation.ID;
import de.kjEngine.scene.physics.sph.GPUSPH;
import de.kjEngine.scene.physics.sph.SPHParticle;
import de.kjEngine.scene.physics.sph.SPHSettings;
import de.kjEngine.scene.renderer.sph.ForwardSphDebugRenderer;
import de.kjEngine.ui.Window.RefreshMode;

/**
 * @author konst
 *
 */
public class GPUSPHDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", GPUSPHDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = RefreshMode.UNLIMITED;
		settings.window.width = 1600;
		settings.window.height = 800;
		settings.application = new ApplicationEventAdapter() {

			GPUSPH sph;

			@Override
			public void init() {
				Scene s = new Scene();

				s.getCamera().transform.position.set(2.5f, 1f, -3f);

				SPHSettings settings = new SPHSettings();
				settings.viscosity = 10f;
				settings.gassConstant = 1f;
				sph = new GPUSPH(settings);

				for (int x = 0; x < 30; x++) {
					for (int y = 0; y < 30; y++) {
						for (int z = 0; z < 30; z++) {
							Vec3 pos = Vec3.create(x, y, z).mul(settings.particleRadius * 0.9f).add(2f, 0f, 2f);
							float r = 0.001f;
							pos.add(Rand.value(-r, r), Rand.value(-r, r), Rand.value(-r, r));
							sph.addParticle(new SPHParticle(pos, Vec3.create()));
						}
					}
				}
				
//				int width = 20;
//				int height = 30;
//				for (int x = 0; x < width; x++) {
//					for (int y = 0; y < height; y++) {
//						float rnd = 0.01f;
//						Vec3 pos = Vec3.create(x + Rand.value(-rnd, rnd), y + Rand.value(-rnd, rnd), 0f).mul(settings.particleRadius * 0.2f);
//						// pos.y *= 0.6;
//						SPHParticle p = new SPHParticle();
//						p.position = pos;
//						sph.addParticle(p);
//					}
//				}
//
//				for (int x = 0; x < width; x++) {
//					for (int y = 0; y < height; y++) {
//						float rnd = 0.01f;
//						Vec3 pos = Vec3.create(x + Rand.value(-rnd, rnd), y + Rand.value(-rnd, rnd), 0f).mul(settings.particleRadius);
//						pos.x = 5f - pos.x;
//						pos.y *= 0.6;
//						SPHParticle p = new SPHParticle();
//						p.position = pos;
//						sph.addParticle(p);
//					}
//				}

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public void update() {
				sph.update(Engine.getDelta(), 10);
			}

			@Override
			public void updateDescriptors() {
				sph.updateDescriptors();
			}

			@Override
			public void render() {
				ForwardSphDebugRenderer renderer = (ForwardSphDebugRenderer) RenderingContext.getPipeline().getStage(ForwardSphDebugRenderer.class);
				renderer.getRenderQueue().add(sph);
			}

			@Override
			public Set<ID> initRenderer() {
				Pipeline pipeline = Pipeline.create(RL.create("jar://app/de/kjEngine/demos/fluidsim/gpusphPipeline.ppl"));

				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");

				return pipeline.getRequiredGPUImplementations();
			}
		};
		Engine.start(settings);
	}
}
