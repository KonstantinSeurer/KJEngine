/**
 * 
 */
package de.kjEngine.demos.gltf;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.audio.AudioContext;
import de.kjEngine.audio.openal.OpenALContext;
import de.kjEngine.core.ApplicationEventAdapter;
import de.kjEngine.core.Engine;
import de.kjEngine.core.StateAdapter;
import de.kjEngine.core.ImplementationProvider;
import de.kjEngine.graphics.GraphicsContext;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.vulkan.VulkanContext;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.math.Vec3;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.SceneGraphItem.GPUImplementation.ID;
import de.kjEngine.scene.ibl.UE4LightProbeComponent;
import de.kjEngine.scene.io.GLTFLoader;
import de.kjEngine.scene.physics.PhysicsContext;
import de.kjEngine.scene.physics.cpu.CpuPhysicsContext;
import de.kjEngine.scene.renderer.SSRRenderer;
import de.kjEngine.ui.Window;

/**
 * @author konst
 *
 */
public class GLTFDemo {

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", GLTFDemo.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = de.kjEngine.ui.RefreshMode.UNLIMITED;
		settings.window.width = 1600;
		settings.window.height = 900;
		settings.application = new ApplicationEventAdapter() {
			@Override
			public void init() {
				Scene s = GLTFLoader.load(RL.create("jar://app/de/kjEngine/demos/gltf/scene.glb"));

				Texture2D environment = ResourceManager.loadTexture(RL.create("jar://app/de/kjEngine/demos/gltf/environment.hdr"), true);

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public Set<ID> initRenderer() {
				Pipeline pipeline = Pipeline.create(new JSONObject(ResourceManager.loadTextResource(RL.create("jar://app/de/kjEngine/demos/gltf/pipeline.ppl"), false)));
				((SSRRenderer) pipeline.getStage(5)).setSource(pipeline.getStage(6).getOutput().get("result"));
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");
				
				return pipeline.getRequiredGPUImplementations();
			}
		};
		Engine.start(settings);
	}
}
