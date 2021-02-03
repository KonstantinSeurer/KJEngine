/**
 * 
 */
package de.kjEngine.demos.charactermovement;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.core.ApplicationEventAdapter;
import de.kjEngine.core.Engine;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.Texture2DDataProvider;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.math.Real;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.scene.Component;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.SceneGraphItem.GPUImplementation.ID;
import de.kjEngine.scene.animation.Skeleton;
import de.kjEngine.scene.animation.SkeletonAnimator;
import de.kjEngine.scene.camera.PerspectiveCameraComponent;
import de.kjEngine.scene.io.ColladaFile;
import de.kjEngine.scene.light.DirectionalLightComponent;
import de.kjEngine.scene.light.DirectionalLightShadowMapComponent;
import de.kjEngine.scene.light.ShadowMap;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.ui.Window;
import de.kjEngine.ui.event.EventAdapter;
import de.kjEngine.ui.event.MouseEvent;
import de.kjEngine.ui.event.MouseWheelEvent;

/**
 * @author konst
 *
 */
public class CharacterMovementDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", CharacterMovementDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = de.kjEngine.ui.RefreshMode.UNLIMITED;
		settings.window.width = 1600;
		settings.window.height = 900;
		settings.application = new ApplicationEventAdapter() {

			float camRotY;

			@Override
			public void init() {
				Scene s = new Scene();

				final DirectionalLightComponent sun = new DirectionalLightComponent(true, Vec3.scale(3f), Vec3.create(-1f, -1f, -1f).normalise());
				sun.addComponent(new DirectionalLightShadowMapComponent(new ShadowMap(8192), 50f));
				sun.addComponent(new Component() {

					@Override
					public void update() {
						parent.transform.position.set(s.getCamera().transform.getGlobalPosition());
					}
				});
				s.addEntity(sun);

				ColladaFile modelFile = new ColladaFile(RL.create("jar://app/de/kjEngine/demos/charactermovement/model.dae"));
				Model model = modelFile.getModels().get(0).getModel();
				PbrMaterial material = new PbrMaterial(ResourceManager.loadTexture(RL.create("jar://app/de/kjEngine/demos/charactermovement/model_diffuse.png"), false));
				model.setMaterial(material);
				Skeleton skeleton = modelFile.getModels().get(0).getSkeleton();

				SkeletonAnimator animator = new SkeletonAnimator(skeleton);
				animator.start(modelFile.getAnimations(), true);
				Entity character = new Entity(true, Vec3.create(), Vec3.scale(0.1f), new ModelComponent(model)).addComponent(new Component() {

					float orientation;
					float targetOrientation;
					Vec3 velocity = Vec3.create();

					@Override
					public void update() {
						super.update();

						animator.animate();
						skeleton.update();
						skeleton.apply(parent.getModel());

						velocity.set(0f, 0f, 0f);

						if (Window.isKeyPressed(Window.KEY_W)) {
							velocity.add(Real.sin(camRotY), 0f, Real.cos(camRotY));
						}
						if (Window.isKeyPressed(Window.KEY_S)) {
							velocity.sub(Real.sin(camRotY), 0f, Real.cos(camRotY));
						}

						if (Window.isKeyPressed(Window.KEY_D)) {
							velocity.add(Real.sin(camRotY + Real.HALF_PI), 0f, Real.cos(camRotY + Real.HALF_PI));
						}
						if (Window.isKeyPressed(Window.KEY_A)) {
							velocity.sub(Real.sin(camRotY + Real.HALF_PI), 0f, Real.cos(camRotY + Real.HALF_PI));
						}

						float speed = velocity.length();
						if (speed > 0.0001f) {
							targetOrientation = Real.atan(velocity.z / speed, velocity.x / speed);
						}
						orientation = Real.interpolateRotationAngle(orientation, targetOrientation, delta * 10f);

						parent.transform.rotation.setIdentity().rotateY(Real.HALF_PI - orientation);

						parent.transform.position.add(velocity, delta);
					}
				});
				s.addEntity(character);

				PerspectiveCameraComponent cam = new PerspectiveCameraComponent(true);
				cam.addComponent(new Component() {

					float rotX;
					float zoom = 3f;

					@Override
					public void init() {
						super.init();
						Window.addEventListener(new EventAdapter() {

							@Override
							public boolean mouseWheelMoved(MouseWheelEvent e) {
								zoom -= e.getWheelD() * 0.25f;
								return false;
							}

							@Override
							public boolean mouseMoved(MouseEvent e) {
								float sensitivity = 5f / Window.getWidth();
								if (Window.isMousePressed(0)) {
									camRotY += e.getDx() * sensitivity;
									rotX += e.getDy() * sensitivity;
								}
								return false;
							}
						});
					}

					@Override
					public void update() {
						super.update();

						parent.transform.position.set(character.transform.position);
						parent.transform.rotation.setIdentity();

						parent.transform.rotation.rotateX(rotX);
						parent.transform.rotation.rotateY(camRotY);

						parent.transform.position.add(parent.transform.localTransform.getZ().normalise().negate().mul(zoom));
						parent.transform.position.y += 0.5f;
					}
				});
				s.addEntity(cam);
				s.setCamera(cam);

				Model ground = new Model(Model.getDefaultPlane());
				ground.setMaterial(new PbrMaterial(Graphics.createTexture2D(new Texture2DData(64, 64, 1, new Texture2DDataProvider() {

					@Override
					public void get(int x, int y, Vec4 target) {
						target.w = 1f;
						if ((x + y) % 2 == 0) {
							target.set(1f, 1f, 1f);
						} else {
							target.set(0.5f, 0.5f, 0.5f);
						}
					}
				}, TextureFormat.RGBA8, SamplingMode.NEAREST, WrappingMode.REPEAT))));
				s.addEntity(new Entity(false, Vec3.create(), Vec3.create(32f, 1f, 32f), new ModelComponent(ground)));

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public Set<ID> initRenderer() {
				Pipeline pipeline = Pipeline.create(new JSONObject(ResourceManager.loadTextResource(RL.create("jar://app/de/kjEngine/demos/charactermovement/pipeline.ppl"), false)));
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");
				return pipeline.getRequiredGPUImplementations();
			}
		};
		Engine.start(settings);
	}
}
