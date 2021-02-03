/**
 * 
 */
package de.kjEngine.scene.renderer;

import java.util.Set;

import org.json.JSONObject;

import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.renderer.Pipeline;
import de.kjEngine.renderer.Pipeline.PrepassProvider;
import de.kjEngine.renderer.Pipeline.Provider;
import de.kjEngine.renderer.PrepassStage;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Stage;
import de.kjEngine.renderer.Stage.InputProvider;
import de.kjEngine.scene.renderer.EquirectangularEnvironmentMapRenderer;
import de.kjEngine.scene.renderer.PbrAtmosphereOpticalDepthLUTRenderer;
import de.kjEngine.scene.renderer.PbrAtmosphereRenderer;
import de.kjEngine.scene.renderer.SSAOFilter;
import de.kjEngine.scene.renderer.SimpleAtmosphereRenderer;
import de.kjEngine.scene.renderer.VolumeRenderer;
import de.kjEngine.scene.renderer.deferred.DeferredPbrDecalRenderer;
import de.kjEngine.scene.renderer.deferred.DeferredPbrModelRenderer;
import de.kjEngine.scene.renderer.deferred.DeferredPbrShader;
import de.kjEngine.scene.renderer.deferred.DeferredPbrTiledTerrainRenderer;
import de.kjEngine.scene.renderer.ocean.ForwardTiledOceanRenderer;
import de.kjEngine.scene.renderer.ocean.PhillipsOceanHeightMapRenderer;
import de.kjEngine.scene.renderer.raytracing.PbrRayTracer;
import de.kjEngine.scene.renderer.shadows.ShadowDepthMapOutput;
import de.kjEngine.scene.renderer.shadows.ShadowDepthMapPbrModelRenderer;
import de.kjEngine.scene.renderer.shadows.ShadowDepthMapTiledOceanRenderer;
import de.kjEngine.scene.renderer.shadows.ShadowDepthMapTiledTerrainRenderer;
import de.kjEngine.scene.renderer.shadows.ShadowMapRenderer;
import de.kjEngine.scene.renderer.shadows.ShadowMapRenderer.PipelineProvider;
import de.kjEngine.scene.renderer.sph.ForwardSphDebugRenderer;

/**
 * @author konst
 *
 */
public class ModuleInitializer {

	public static void init() {
		JarProtocolImplementation.registerClassLoader("scene.renderer", ModuleInitializer.class);

		Pipeline.registerPrepassStageProvider("shadowMapRenderer", new PrepassProvider() {

			@Override
			public PrepassStage create(JSONObject obj) {
				if (obj.has("pipeline")) {
					JSONObject pipeline = obj.getJSONObject("pipeline");
					return new ShadowMapRenderer(new PipelineProvider() {

						@Override
						public void getRequiredRenderImplementations(Set<ID> target) {
							Pipeline pipeline = get(1); // TODO: There needs to be a better solution
							pipeline.getRequiredRenderImplementations(target);
							pipeline.dispose();
						}

						@Override
						public Pipeline get(int resolution) {
							return Pipeline.create(pipeline, resolution, resolution);
						}
					});
				}
				return null;
			}
		});
		Pipeline.registerStageProvider("shadowDepthMapPbrModelRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new ShadowDepthMapPbrModelRenderer(input);
			}
		});
		Pipeline.registerStageProvider("shadowDepthMapOutput", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new ShadowDepthMapOutput(input);
			}
		});
		Pipeline.registerStageProvider("shadowDepthMapTiledOceanRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new ShadowDepthMapTiledOceanRenderer(input);
			}
		});
		Pipeline.registerStageProvider("shadowDepthMapTiledTerrainRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new ShadowDepthMapTiledTerrainRenderer(input);
			}
		});

		Pipeline.registerStageProvider("ssaoFilter", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new SSAOFilter(input);
			}
		});
		Pipeline.registerStageProvider("volumetricCloudRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new VolumeRenderer(input);
			}
		});
		Pipeline.registerStageProvider("simpleAtmosphereRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new SimpleAtmosphereRenderer(input);
			}
		});
		Pipeline.registerStageProvider("pbrAtmosphereRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new PbrAtmosphereRenderer(input);
			}
		});
		Pipeline.registerPrepassStageProvider("pbrAtmosphereOpticalDepthLUTRenderer", new PrepassProvider() {

			@Override
			public PrepassStage create(JSONObject obj) {
				return new PbrAtmosphereOpticalDepthLUTRenderer();
			}
		});
		Pipeline.registerStageProvider("equirectangularEnvironmentMapRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new EquirectangularEnvironmentMapRenderer(input);
			}
		});
//		Pipeline.registerStageProvider("ue4LightProbeRenderer", new Provider() {
//
//			@Override
//			public Stage create(InputProvider input, JSONObject obj) {
//				return new UE4LightProbeRenderer(input);
//			}
//		});
//		Pipeline.registerPrepassStageProvider("ue4LightProbeUpdater", new PrepassProvider() {
//
//			@Override
//			public PrepassStage create(JSONObject obj) {
//				return new UE4LightProbeUpdater();
//			}
//		});
		Pipeline.registerStageProvider("deferredPbrModelRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new DeferredPbrModelRenderer(input);
			}
		});
		Pipeline.registerStageProvider("deferredPbrDecalRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new DeferredPbrDecalRenderer(input);
			}
		});
		Pipeline.registerStageProvider("deferredPbrTiledTerrainRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new DeferredPbrTiledTerrainRenderer(input);
			}
		});
		Pipeline.registerStageProvider("forwardTiledOceanRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new ForwardTiledOceanRenderer(input);
			}
		});
		Pipeline.registerPrepassStageProvider("phillipsOceanHeightMapRenderer", new PrepassProvider() {

			@Override
			public PrepassStage create(JSONObject obj) {
				return new PhillipsOceanHeightMapRenderer();
			}
		});
		Pipeline.registerStageProvider("forwardSphDebugRenderer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new ForwardSphDebugRenderer(input);
			}
		});
		Pipeline.registerStageProvider("deferredPbrShader", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new DeferredPbrShader(input);
			}
		});
		Pipeline.registerStageProvider("pbrRayTracer", new Provider() {

			@Override
			public Stage create(InputProvider input, JSONObject obj) {
				return new PbrRayTracer(input);
			}
		});
	}
}
