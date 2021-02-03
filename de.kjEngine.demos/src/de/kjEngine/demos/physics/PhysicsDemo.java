/**
 * 
 */
package de.kjEngine.demos.physics;

import java.util.Set;

import de.kjEngine.core.Engine;
import de.kjEngine.core.StateAdapter;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.Texture2DDataProvider;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;
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
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.scene.physics.PhysicsComponent;
import de.kjEngine.scene.physics.PhysicsSimulation;
import de.kjEngine.scene.physics.RigidBody;
import de.kjEngine.scene.physics.collission.PlaneCollider;
import de.kjEngine.scene.physics.collission.SphereCollider;
import de.kjEngine.scene.physics.solver.ImpulseSolver;
import de.kjEngine.scene.physics.solver.PositionSolver;
import de.kjEngine.ui.Window;

/**
 * @author konst
 *
 */
public class PhysicsDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", PhysicsDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = de.kjEngine.core.Engine.Settings.Window.RefreshMode.UNLIMITED;
		settings.window.width = 1000;
		settings.window.height = 600;
		settings.stateHandlers.add(new StateAdapter() {

			@Override
			public void init() {
				Scene s = new Scene();
				s.camera = new PerspectiveCameraComponent();
				s.add(new Entity(true, Vec3.create(0f, 5f, -10f)).add(s.camera).add(new FirstPersonCameraController()));

				final DirectionalLightComponent sun = new DirectionalLightComponent(Vec3.scale(3f), Vec3.create(1f, -1f, 0f).normalise());
				sun.add(new DirectionalLightShadowMapCascadeComponent(2048, 0.02f, 0.1f, 5f));
				sun.add(new DirectionalLightShadowMapCascadeComponent(4096, 0f, 0.02f, 10f));
				s.add(new Entity(true).add(sun));

				PbrMaterial sphereMaterial = new PbrMaterial(Graphics.createTexture2D(new Texture2DData(4, 2, 1, new Texture2DDataProvider() {

					@Override
					public void get(int x, int y, Vec4 target) {
						target.set(0.1f, 0.1f, 0.1f, 1f);
						if ((x + y) % 2 == 1) {
							target.set(1f, 1f, 1f);
						}
					}
				}, TextureFormat.RGBA8, SamplingMode.NEAREST, WrappingMode.REPEAT)));

//				Entity sphere = new Entity(true, Vec3.create(-1f, 2f, 0f));
//				sphere.add(new ModelComponent(new Model(Model.getDefaultSphere()).setMaterial(sphereMaterial)));
//				sphere.add(new PhysicsComponent(new RigidBody(true, 1f, 0.5f, new SphereCollider(1f))));
//				((RigidBody) sphere.get(PhysicsComponent.class).object).accelerate(Vec3.create(1f, 0f, 0f));
//				s.add(sphere);

				for (int x = 0; x < 5; x++) {
					for (int y = 0; y < 10; y++) {
						for (int z = 0; z < 5; z++) {
							float randRange = 0.05f;
							float radius = 0.4f;
							Vec3 pos = Vec3.create(x, y + 1f, z).add(Rand.value(randRange, randRange), Rand.value(randRange, randRange), Rand.value(randRange, randRange));
							Entity sphere = new Entity(true, pos, Vec3.scale(radius));
							sphere.add(new ModelComponent(new Model(Model.getDefaultSphere()).setMaterial(sphereMaterial)));
							sphere.add(new PhysicsComponent(new RigidBody(true, 1f, 0.5f, new SphereCollider(radius))));
							s.add(sphere);
						}
					}
				}

				Entity plane = new Entity(false, Vec3.create(0f, 0f, 0f), Vec3.scale(100f));
				plane.add(new ModelComponent(Model.getDefaultPlane()));
				plane.add(new PhysicsComponent(new RigidBody(false, 0f, 0.5f, new PlaneCollider())));
				s.add(plane);

				PhysicsSimulation sim = new PhysicsSimulation();
				sim.solvers.add(new PositionSolver());
				sim.solvers.add(new ImpulseSolver());
				s.implementations.add(sim);

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public void initRenderer(Set<ID> target) {
				Pipeline pipeline = Pipeline.create(RL.create("jar://app/de/kjEngine/demos/physics/pipeline.ppl"), Window.getWidth(), Window.getHeight());
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");

				pipeline.getRequiredRenderImplementations(target);
			}
		});
		Engine.updateSubSteps = 1;
		Engine.start(settings, PhysicsDemo.class.getModule());
	}
}
