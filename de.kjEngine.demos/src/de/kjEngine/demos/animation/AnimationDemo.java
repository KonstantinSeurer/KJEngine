/**
 * 
 */
package de.kjEngine.demos.animation;

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
import de.kjEngine.scene.animation.Skeleton;
import de.kjEngine.scene.animation.SkeletonAnimator;
import de.kjEngine.scene.ibl.UE4LightProbeComponent;
import de.kjEngine.scene.io.ColladaFile;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.ui.Window;

/**
 * @author konst
 *
 */
public class AnimationDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", AnimationDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = de.kjEngine.ui.RefreshMode.UNLIMITED;
		settings.window.width = 1600;
		settings.window.height = 900;
		settings.application = new ApplicationEventAdapter() {

			@Override
			public void init() {
				Scene s = new Scene();
				s.getCamera().transform.position.z = -2f;

				s.addEntity(new Entity(false, Vec3.create(0f, 2f, 0f), Vec3.create(7f, 5f, 40f)).addComponent(new UE4LightProbeComponent(16, 128, 0.1f, 100f)));

				ColladaFile modelFile = new ColladaFile(RL.create("jar://app/de/kjEngine/demos/animation/model.dae"));
				Model model = modelFile.getModels().get(0).getModel();
				PbrMaterial material = new PbrMaterial(ResourceManager.loadTexture(RL.create("jar://app/de/kjEngine/demos/animation/model_diffuse.png"), false));
				model.setMaterial(material);
				Skeleton skeleton = modelFile.getModels().get(0).getSkeleton();

				for (int i = 0; i < 4; i++) {
					for (int x = 0; x < 20; x++) {
						for (int z = 0; z < 20; z++) {
							Skeleton skeletonInstance = new Skeleton(skeleton);

							SkeletonAnimator animator = new SkeletonAnimator(skeletonInstance);
							animator.start(modelFile.getAnimations(), true);
							s.addEntity(new Entity(true, Vec3.create(x * 0.5f - 5f, 0f, z * 0.5f - 5f + i * 15f), Vec3.scale(0.1f), new ModelComponent(model)).addComponent(new Component() {

								@Override
								public void update() {
									animator.animate();
									skeletonInstance.update();
									skeletonInstance.apply(parent.getModel());

									parent.transform.position.z += delta * 1.5f;

									if (parent.transform.position.z > 30f) {
										parent.transform.position.z -= 60f;
									}
								}
							}));
						}
					}
				}

				s.addEntity(new Entity(false, Vec3.create(0f, -0.2f, 0f), Vec3.create(6f, 0.2f, 30f), new ModelComponent(Model.getDefaultCube())));

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public Set<ID> initRenderer() {
				Pipeline pipeline = Pipeline.create(new JSONObject(ResourceManager.loadTextResource(RL.create("jar://app/de/kjEngine/demos/animation/pipeline.ppl"), false)));
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");
				return pipeline.getRequiredGPUImplementations();
			}
		};
		Engine.start(settings);
	}
}
