/**
 * 
 */
package de.kjEngine.demos.ocean;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.audio.AudioContext;
import de.kjEngine.audio.openal.OpenALContext;
import de.kjEngine.awt.Gui;
import de.kjEngine.core.ApplicationEventAdapter;
import de.kjEngine.core.Engine;
import de.kjEngine.core.ImplementationProvider;
import de.kjEngine.graphics.GraphicsContext;
import de.kjEngine.graphics.vulkan.VulkanContext;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.SceneGraphItem.GPUImplementation.ID;
import de.kjEngine.scene.light.DirectionalLightComponent;
import de.kjEngine.scene.ocean.TiledOceanComponent;
import de.kjEngine.ui.Window;
import de.kjEngine.scene.ocean.PhillipsOceanHeightMap;

/**
 * @author konst
 *
 */
public class OceanDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", OceanDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = de.kjEngine.ui.RefreshMode.UNLIMITED;
		settings.window.width = 16 * 100;
		settings.window.height = 9 * 100;
		settings.implementationProvider = new ImplementationProvider() {

			@Override
			public GraphicsContext createGraphicsContext() {
				return new VulkanContext();
			}

			@Override
			public AudioContext createAudioContext() {
				return new OpenALContext();
			}
		};
		settings.application = new ApplicationEventAdapter() {

			@Override
			public void init() {
				Scene s = new Scene();
				s.addEntity(new DirectionalLightComponent(false, Vec3.create(3f, 3f, 3f), Vec3.create(0f, -1f, 0f)));
				s.getCamera().transform.position.set(0f, 3f, 0f);

				Entity ocean = new Entity(true);
				ocean.transform.scale.set(5f, 5f, 5f);
				PhillipsOceanHeightMap heightMap = new PhillipsOceanHeightMap(256, 1000, Vec2.create(20f, 20f), 20f);
				ocean.addComponent(new TiledOceanComponent(heightMap, 10, 10));
				s.addEntity(ocean);

				float aspect = Window.getAspect();
				Gui.addGui(Window.getRootPanel(), 0f, 0f, 0.5f / aspect, 0.5f, heightMap.getDx());
				Gui.addGui(Window.getRootPanel(), 0.5f / aspect, 0f, 0.5f / aspect, 0.5f, heightMap.getDy());
				Gui.addGui(Window.getRootPanel(), 1f / aspect, 0f, 0.5f / aspect, 0.5f, heightMap.getDz());
				
				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public Set<ID> initRenderer() {
				Pipeline pipeline = Pipeline.create(new JSONObject(ResourceManager.loadTextResource(RL.create("jar://app/de/kjEngine/demos/ocean/pipeline.ppl"), false)));
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");
				return pipeline.getRequiredGPUImplementations();
			}
		};
		Engine.start(settings);
	}
}
