/**
 * 
 */
package de.kjEngine.demos.mars;

import java.util.Set;

import de.kjEngine.core.Engine;
import de.kjEngine.core.StateAdapter;
import de.kjEngine.graphics.Color;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.WrappingMode;
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
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.camera.FirstPersonCameraController;
import de.kjEngine.scene.camera.PerspectiveCameraComponent;
import de.kjEngine.scene.light.DirectionalLightComponent;
import de.kjEngine.scene.light.DirectionalLightShadowMapCascadeComponent;
import de.kjEngine.scene.material.Material;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.scene.model.procedual.RockBuilder;
import de.kjEngine.scene.model.procedual.RockBuilder.RadiusFunction;
import de.kjEngine.scene.terrain.ElevationFunction;
import de.kjEngine.scene.terrain.TiledTerrainComponent;
import de.kjEngine.ui.Window;
import de.kjEngine.util.Sampler;
import de.kjEngine.util.Time;
import de.kjEngine.util.Timer;

/**
 * @author konst
 *
 */
public class MarsDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", MarsDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = de.kjEngine.core.Engine.Settings.Window.RefreshMode.UNLIMITED;
		settings.stateHandlers.add(new StateAdapter() {

			Sampler sampler;

			@Override
			public void init() {
				Timer.start();

				Scene s = new Scene();
				s.camera = new PerspectiveCameraComponent();
				s.add(new Entity(true, Vec3.create(7.852727f, -5.8720174f, 0.6428261f)).add(s.camera).add(new FirstPersonCameraController()));

				final DirectionalLightComponent sun = new DirectionalLightComponent(Vec3.scale(3f), Vec3.create(1f, -0.3f, 0f).normalise());
				sun.add(new DirectionalLightShadowMapCascadeComponent(2048, 0.3f, 1f, 2f));
				sun.add(new DirectionalLightShadowMapCascadeComponent(2048, 0.1f, 0.3f, 4f));
				sun.add(new DirectionalLightShadowMapCascadeComponent(2048, 0.02f, 0.1f, 5f));
				sun.add(new DirectionalLightShadowMapCascadeComponent(4096, 0f, 0.02f, 10f));
				s.add(new Entity(true).add(sun));

				Color surfaceColor = new Color(0.5f, 0.25f, 0.125f, 1f);

				TiledTerrainComponent terrain = new TiledTerrainComponent(new ElevationFunction() {

					@Override
					public float getElevation(float x, float z) {
						 return Rand.noise("simplex", x * 0.05f, z * 0.05f) + Rand.noise("simplex", x * 0.005f, z * 0.005f) * 10f;
					}
				}, 100, 100, 100f, 100f, new PbrMaterial(surfaceColor)
						.setNormal(Graphics.loadTexture(RL.create("jar://app/de/kjEngine/demos/mars/dirt_normal_map.jpg"), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, false)), 50f);
				s.add(new Entity(true, Vec3.create(-50f, 0f, -5f)).add(terrain));

				Model[] largeRocks = new Model[5];

				Material rockMaterial = new PbrMaterial(surfaceColor);

				for (int i = 0; i < largeRocks.length; i++) {
					float seed = i * 38.27f;
					largeRocks[i] = new Model(RockBuilder.create(32, new RadiusFunction() {

						@Override
						public float getRadius(Vec3 p) {
							return Rand.noise("veronoi", p.x, p.y, p.z + seed) * 0.5f + 0.5f;
						}
					}), rockMaterial);
				}

				Model smallRock = new Model(RockBuilder.create(8, new RadiusFunction() {

					@Override
					public float getRadius(Vec3 p) {
						return Rand.noise("veronoi", p.x, p.y, p.z + 38f) * 0.1f + 0.1f;
					}
				}), rockMaterial);

				Model verrySmallRock = new Model(RockBuilder.create(4, new RadiusFunction() {

					@Override
					public float getRadius(Vec3 p) {
						return Rand.noise("veronoi", p.x, p.y, p.z + 47f) * 0.03f + 0.03f;
					}
				}), rockMaterial);

				Rand.setSeed(123);

				for (int i = 0; i < 100; i++) {
					float x = terrain.getParent().transform.getGlobalPosition().x + Rand.value() * terrain.width;
					float z = terrain.getParent().transform.getGlobalPosition().z + Rand.value() * terrain.length;
					Entity e = new Entity(false, Vec3.create(x, terrain.getGlobalElevation(x, z), z), Vec3.scale(1f));
					e.add(new ModelComponent(largeRocks[(int) Rand.value(0, largeRocks.length)]));
					s.add(e);

					for (int j = 0; j < 10; j++) {
						float xo = Rand.value(-1f, 1f);
						float zo = Rand.value(-1f, 1f);
						float d = Real.pow(Rand.value() * 2f, 2f) * 2f;
						xo *= d;
						zo *= d;
						Entity e2 = new Entity(false, Vec3.create(x + xo, terrain.getGlobalElevation(x + xo, z + zo), z + zo), Vec3.scale(1f));
						e2.add(new ModelComponent(smallRock));
						s.add(e2);
					}

					for (int j = 0; j < 400; j++) {
						float xo = Rand.value(-1f, 1f);
						float zo = Rand.value(-1f, 1f);
						float d = Real.pow(Rand.value() * 2f, 2f) * 5f;
						xo *= d;
						zo *= d;
						Entity e2 = new Entity(false, Vec3.create(x + xo, terrain.getGlobalElevation(x + xo, z + zo), z + zo), Vec3.scale(1f));
						e2.add(new ModelComponent(verrySmallRock));
						s.add(e2);
					}
				}
				
				s.add(new Entity(true, Vec3.create(0f, -3f, 0f)).add(new ModelComponent(Model.getDefaultSphere())));

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");

				Timer.printPassed();

				sampler = new Sampler(new Timer(Time.secondsToNanos(0.01f)), Thread.currentThread());
			}

			@Override
			public void initRenderer(Set<ID> target) {
				Pipeline pipeline = Pipeline.create(RL.create("jar://app/de/kjEngine/demos/mars/pipeline.ppl"), Window.getWidth(), Window.getHeight());
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");

				pipeline.getRequiredRenderImplementations(target);
			}

			@Override
			public void dispose() {
				sampler.stop();
				sampler.print(50);
			}
		});
		Engine.start(settings, MarsDemo.class.getModule());
	}
}
