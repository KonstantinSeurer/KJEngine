/**
 * 
 */
package de.kjEngine.demos.shadingsandbox;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.core.ApplicationEventAdapter;
import de.kjEngine.core.Engine;
import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.renderer.Filter;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.RenderingContext;
import de.kjEngine.renderer.Stage;
import de.kjEngine.renderer.Stage.InputProvider;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneManager;
import de.kjEngine.scene.SceneGraphItem.GPUImplementation.ID;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.camera.CameraComponentUBO;
import de.kjEngine.ui.Window.RefreshMode;

/**
 * @author konst
 *
 */
public class ShadingSandbox {
	
	private static class Shader extends Filter {

		public Shader(InputProvider inputProvider, int width, int height) {
			super(inputProvider, width, height);
			
			try {
				PipelineSource source = PipelineSource.parse(RL.create("jar://app/de/kjEngine/demos/shadingsandbox/shader.shader"));
				source.getDescriptorSets().add(CameraComponentUBO.DESCRIPTOR_SET_SOURCE);
				init(source);
			} catch (ShaderCompilationException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void bindDescriptors(Scene scene, CommandBuffer cb) {
			cb.bindDescriptorSet(((CameraComponentUBO) scene.getCamera().getRenderImplementation(CameraComponent.UBO)).getDescriptorSet(), "camera");
		}

		@Override
		protected void prepare(Scene scene) {
		}

		@Override
		protected void linkImplementation(InputProvider input) {
			output.put("result", frameBuffer.getColorAttachment("color"));
		}

		@Override
		protected void updateDescriptors(Scene scene) {
		}

		@Override
		public void getRequiredRenderImplementations(Set<ID> target) {
			target.add(CameraComponent.UBO);
		}
	}
	
	static {
		Pipeline.registerStageProvider("shadingSandbox", new Pipeline.FilterProvider() {
			
			@Override
			protected Stage create(InputProvider input, JSONObject obj, int width, int height) {
				return new Shader(input, width, height);
			}
		});
	}

	public static void main() {
		main(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("app", ShadingSandbox.class);
		Engine.Settings settings = new Engine.Settings();
		settings.window.refreshMode = RefreshMode.UNLIMITED;
		settings.application = new ApplicationEventAdapter() {

			@Override
			public void init() {
				Scene s = new Scene();
				s.getCamera().transform.getPosition().set(0f, 2f, -2f);

				SceneManager.addScene("scene", s);
				SceneManager.setScene("scene");
			}

			@Override
			public Set<ID> initRenderer() {
				Pipeline pipeline = Pipeline.create(new JSONObject(ResourceManager.loadTextResource(RL.create("jar://app/de/kjEngine/demos/shadingsandbox/pipeline.ppl"), false)));
				RenderingContext.addPipeline("pipeline", pipeline);
				RenderingContext.setPipeline("pipeline");

				return pipeline.getRequiredGPUImplementations();
			}

			@Override
			public void dispose() {
			}
		};
		Engine.start(settings);
	}
}
