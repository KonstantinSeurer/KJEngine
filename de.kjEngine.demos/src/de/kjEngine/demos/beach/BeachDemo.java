/**
 * 
 */
package de.kjEngine.demos.beach;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.component.Component;
import de.kjEngine.core.Engine;
import de.kjEngine.core.Engine.Settings.Window.RefreshMode;
import de.kjEngine.core.StateAdapter;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.math.Real;
import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.random.Rand;
import de.kjEngine.renderer.ColorInput;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneComponent;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.atmosphere.PbrAtmosphereComponent;
import de.kjEngine.scene.camera.FirstPersonCameraController;
import de.kjEngine.scene.camera.PerspectiveCameraComponent;
import de.kjEngine.scene.light.DirectionalLightComponent;
import de.kjEngine.scene.light.DirectionalLightShadowMapCascadeComponent;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.ocean.PhillipsOceanHeightMap;
import de.kjEngine.scene.ocean.TiledOceanComponent;
import de.kjEngine.scene.terrain.TiledTerrainComponent;
import de.kjEngine.ui.Window;

/**
 * @author konst
 *
 */
public class BeachDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", BeachDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = RefreshMode.UNLIMITED;
		settings.window.resizable = true;

		settings.stateHandlers.add(new StateAdapter() {

			float t = -0.05f;
			ColorInput ambientLight;

			@Override
			public void init() {
				Scene s = new Scene();
				PerspectiveCameraComponent cam = new PerspectiveCameraComponent();
				cam.getFrustum().setFar(200f);
				s.camera = cam;
				FirstPersonCameraController controller = new FirstPersonCameraController();
				controller.getRotation().y += Real.HALF_PI;
				s.add(new Entity(true, Vec3.create(37f, -3f, 100f)).add(s.camera).add(controller));

				DirectionalLightComponent sun = new DirectionalLightComponent(Vec3.scale(3f), Vec3.create(-1f, -0.2f, -1f).normalise());
				sun.add(new SceneComponent<DirectionalLightComponent, SceneComponent<DirectionalLightComponent, ?>>(Component.EARLY) {

					@Override
					public void update(float delta) {
						parent.direction.set(Real.cos(t * Real.TWO_PI + Real.PI), Real.sin(t * Real.TWO_PI + Real.PI), 0f);
					}
				});
				sun.add(new DirectionalLightShadowMapCascadeComponent(2048, 0.3f, 1f, 2f));
				sun.add(new DirectionalLightShadowMapCascadeComponent(2048, 0.1f, 0.3f, 4f));
				sun.add(new DirectionalLightShadowMapCascadeComponent(4096, 0.02f, 0.1f, 5f));
				sun.add(new DirectionalLightShadowMapCascadeComponent(4096, 0f, 0.02f, 10f));
				s.add(new Entity(true).add(sun));

				Rand.setSeed(0);

				PbrMaterial ground = new PbrMaterial();
				ground.setAlbedo(Graphics.loadTexture(RL.create("jar://app/de/kjEngine/demos/beach/aerial_beach_01_diff_4k.jpg"), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, false));
				ground.setRoughness(Graphics.loadTexture(RL.create("jar://app/de/kjEngine/demos/beach/aerial_beach_01_rough_4k.jpg"), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, false));
				ground.setNormal(Graphics.loadTexture(RL.create("jar://app/de/kjEngine/demos/beach/aerial_beach_01_nor_4k.jpg"), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, false));
				ground.setDisplacement(Graphics.loadTexture(RL.create("jar://app/de/kjEngine/demos/beach/aerial_beach_01_disp_4k.jpg"), SamplingMode.LINEAR_LINEAR, WrappingMode.REPEAT, false));

				TiledTerrainComponent terrain = new TiledTerrainComponent((x, z) -> {
					float h = Rand.noise("simplex", x * 0.05f, z * 0.05f) + Rand.noise("simplex", x * 0.005f, z * 0.005f) * 10f;
					return h * (1f - x * 0.009f) - Real.pow(x * 0.01f, 4f) * 4f - x * 0.02f;
				}, 130, 100, 100f, 200f, ground, 80f);
				s.add(new Entity(true, Vec3.create(-50f, -2f, 0f)).add(terrain));

				Entity ocean = new Entity(true);
				ocean.transform.position.set(-50f, -5f, 0f);
				PhillipsOceanHeightMap heightMap = new PhillipsOceanHeightMap(256, 1000, Vec2.create(-20f, 0f), 2f, 1000f);
				ocean.add(new TiledOceanComponent(heightMap, 10, 40, 100f, 200f, 5f, 5f));
				s.add(ocean);

				float atmosphereRadius = 10000000f;
				float atmosphereHeight = 100000f;
				float baseDensity = 0.00001f;
				float densityFalloff = 3f;
				s.add(new Entity(true).add(new PbrAtmosphereComponent(atmosphereRadius, atmosphereRadius + atmosphereHeight, baseDensity, densityFalloff))
						.add(new SceneComponent<Entity, SceneComponent<Entity, ?>>(Component.EARLY) {

							@Override
							public void update(float delta) {
								parent.transform.position.set(s.camera.getParent().transform.getGlobalPosition());
								parent.transform.position.y = -atmosphereRadius - 100f;
							}
						}));

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public void update(float delta) {
				t += delta * 0.005f;

				float fractT = Real.fract(t);

				Vec3 sunRiseSkyColor = Vec3.interpolate(Vec3.create(0.2f, 0.1f, 0.01f), Vec3.create(0.06f, 0.1f, 0.3f).mul(0.5f), 0.5f);

				if (fractT > 0.5f) {
					Vec3 skyColor = Vec3.interpolate(sunRiseSkyColor, Vec3.create(), sunRiseSkyColor, fractT * 2f - 1f, Vec3.create());
					ambientLight.setColor(skyColor.x, skyColor.y, skyColor.z, 1f);
				} else {
					Vec3 skyColor = Vec3.interpolate(sunRiseSkyColor, Vec3.create(0.06f, 0.1f, 0.3f), sunRiseSkyColor, fractT * 2f, Vec3.create());
					ambientLight.setColor(skyColor.x, skyColor.y, skyColor.z, 1f);
				}
			}

			@Override
			public void initRenderer(Set<ID> target) {
				Pipeline pipeline = Pipeline.create(new JSONObject(ResourceManager.loadTextResource(RL.create("jar://app/de/kjEngine/demos/beach/pipeline.ppl"), false)), Window.getWidth(),
						Window.getHeight());
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");

				ambientLight = pipeline.getStage(ColorInput.class);

				pipeline.getRequiredRenderImplementations(target);
			}
		});
		Engine.start(settings, BeachDemo.class.getModule());
	}
}
