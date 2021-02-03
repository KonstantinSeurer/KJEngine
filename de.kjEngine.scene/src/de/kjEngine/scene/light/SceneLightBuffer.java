/**
 * 
 */
package de.kjEngine.scene.light;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.component.Container.RenderImplementation;
import de.kjEngine.graphics.BufferAccessor;
import de.kjEngine.graphics.Descriptor.Type;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.StructAccessor;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.BufferSource.Layout;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.StructSource;
import de.kjEngine.graphics.shader.TextureSource;
import de.kjEngine.graphics.shader.VariableSource;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;

/**
 * @author konst
 *
 */
public class SceneLightBuffer implements RenderImplementation<Scene> {

	public static final int MAX_POINT_LIGHT_COUNT = 32;
	public static final int MAX_SPHERE_LIGHT_COUNT = 32;
	public static final int MAX_SPOT_LIGHT_COUNT = 32;
	public static final int MAX_DIRECTIONAL_LIGHT_COUNT = 32;
	public static final int MAX_SHADOW_MAP_COUNT = 4;

	public static final BufferSource SSBO_SOURCE = new BufferSource("lights", Type.STORAGE_BUFFER, Layout.PACKED, new ArrayList<>());
	static {
		SSBO_SOURCE.getMembers().add(new VariableSource("PointLight[" + MAX_POINT_LIGHT_COUNT + "]", "pointLights"));
		SSBO_SOURCE.getMembers().add(new VariableSource("SphereLight[" + MAX_SPHERE_LIGHT_COUNT + "]", "sphereLights"));
		SSBO_SOURCE.getMembers().add(new VariableSource("SpotLight[" + MAX_SPOT_LIGHT_COUNT + "]", "spotLights"));
		SSBO_SOURCE.getMembers().add(new VariableSource("DirectionalLight[" + MAX_DIRECTIONAL_LIGHT_COUNT + "]", "directionalLights"));
		SSBO_SOURCE.getMembers().add(new VariableSource("mat4[" + MAX_SHADOW_MAP_COUNT + "]", "shadowMapMatrices"));
		SSBO_SOURCE.getMembers().add(new VariableSource("int", "pointLightCount"));
		SSBO_SOURCE.getMembers().add(new VariableSource("int", "sphereLightCount"));
		SSBO_SOURCE.getMembers().add(new VariableSource("int", "spotLightCount"));
		SSBO_SOURCE.getMembers().add(new VariableSource("int", "directionalLightCount"));
	}

	public static final StructSource POINT_LIGHT_SOURCE = new StructSource("PointLight", new ArrayList<>());
	static {
		POINT_LIGHT_SOURCE.getMembers().add(new VariableSource("vec3", "position"));
		POINT_LIGHT_SOURCE.getMembers().add(new VariableSource("vec3", "color"));
	}

	public static final StructSource SPHERE_LIGHT_SOURCE = new StructSource("SphereLight", new ArrayList<>());
	static {
		SPHERE_LIGHT_SOURCE.getMembers().add(new VariableSource("vec3", "position"));
		SPHERE_LIGHT_SOURCE.getMembers().add(new VariableSource("float", "radius"));
		SPHERE_LIGHT_SOURCE.getMembers().add(new VariableSource("vec3", "color"));
	}

	public static final StructSource SPOT_LIGHT_SOURCE = new StructSource("SpotLight", new ArrayList<>());
	static {
		SPOT_LIGHT_SOURCE.getMembers().add(new VariableSource("vec3", "position"));
		SPOT_LIGHT_SOURCE.getMembers().add(new VariableSource("vec3", "direction"));
		SPOT_LIGHT_SOURCE.getMembers().add(new VariableSource("float", "angle"));
		SPOT_LIGHT_SOURCE.getMembers().add(new VariableSource("float", "falloff"));
		SPOT_LIGHT_SOURCE.getMembers().add(new VariableSource("vec3", "color"));
	}

	public static final StructSource DIRECTIONAL_LIGHT_SOURCE = new StructSource("DirectionalLight", new ArrayList<>());
	static {
		DIRECTIONAL_LIGHT_SOURCE.getMembers().add(new VariableSource("vec3", "direction"));
		DIRECTIONAL_LIGHT_SOURCE.getMembers().add(new VariableSource("vec3", "color"));
		DIRECTIONAL_LIGHT_SOURCE.getMembers().add(new VariableSource("int", "shadowMapIndex"));
		DIRECTIONAL_LIGHT_SOURCE.getMembers().add(new VariableSource("int", "shadowMapCount"));
		DIRECTIONAL_LIGHT_SOURCE.getMembers().add(new VariableSource("vec4", "dummy"));
	}

	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("lights", new ArrayList<>());
	static {
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(SSBO_SOURCE);
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("shadowMap0", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("shadowMap1", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("shadowMap2", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("shadowMap3", 2));
	}

	public static final PipelineSource LIBRARY_SOURCE = new PipelineSource();
	static {
		LIBRARY_SOURCE.getDescriptorSets().add(DESCRIPTOR_SET_SOURCE);

		LIBRARY_SOURCE.getStructs().add(POINT_LIGHT_SOURCE);
		LIBRARY_SOURCE.getStructs().add(SPHERE_LIGHT_SOURCE);
		LIBRARY_SOURCE.getStructs().add(SPOT_LIGHT_SOURCE);
		LIBRARY_SOURCE.getStructs().add(DIRECTIONAL_LIGHT_SOURCE);
	}

	private ShaderBuffer ssbo;
	private DescriptorSet descriptorSet;

	private List<PointLightComponent> positionalLights = new ArrayList<>();
	private List<SphereLightComponent> sphereLights = new ArrayList<>();
	private List<SpotLightComponent> spotLights = new ArrayList<>();
	private List<DirectionalLightComponent> directionalLights = new ArrayList<>();

	private List<PointLightComponent> staticPositionalLights = new ArrayList<>();
	private List<SphereLightComponent> staticSphereLights = new ArrayList<>();
	private List<SpotLightComponent> staticSpotLights = new ArrayList<>();
	private List<DirectionalLightComponent> staticDirectionalLights = new ArrayList<>();

	public SceneLightBuffer() {
		ssbo = Graphics.createStorageBuffer(SSBO_SOURCE, LIBRARY_SOURCE.getStructs(), ShaderBuffer.FLAG_NONE);
		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
		descriptorSet.set("lights", ssbo);
		descriptorSet.update();
	}

	@Override
	public void dispose() {
		ssbo.dispose();
		descriptorSet.dispose();
	}

	@Override
	public void updateDescriptors(Scene scene) {
		positionalLights.addAll(staticPositionalLights);
		sphereLights.addAll(staticSphereLights);
		spotLights.addAll(staticSpotLights);
		directionalLights.addAll(staticDirectionalLights);

		BufferAccessor accessor = ssbo.getAccessor();

		int shadowMapCount = 0;

		for (int i = 0; i < positionalLights.size() && i < MAX_POINT_LIGHT_COUNT; i++) {
			PointLightComponent light = positionalLights.get(i);
			StructAccessor a = accessor.getArray("pointLights").getStruct(i);

			a.set("position", ((Entity) light.getParent()).transform.getGlobalPosition());
			a.set("color", light.color);
		}

		for (int i = 0; i < sphereLights.size() && i < MAX_SPHERE_LIGHT_COUNT; i++) {
			SphereLightComponent light = sphereLights.get(i);
			StructAccessor a = accessor.getArray("sphereLights").getStruct(i);

			Vec3 position = ((Entity) light.getParent()).transform.getGlobalPosition();

			a.set("position", position);
			a.set("radius", light.radius);
			a.set("color", light.color);
		}

		for (int i = 0; i < spotLights.size() && i < MAX_SPOT_LIGHT_COUNT; i++) {
			SpotLightComponent light = spotLights.get(i);
			StructAccessor a = accessor.getArray("spotLights").getStruct(i);

			Vec3 position = ((Entity) light.getParent()).transform.getGlobalPosition();

			a.set("position", position);
			a.set("direction", light.direction);
			a.set("color", light.color);
			a.set("angle", light.angle);
			a.set("falloff", light.angularFalloff);
		}

		for (int i = 0; i < directionalLights.size() && i < MAX_DIRECTIONAL_LIGHT_COUNT; i++) {
			DirectionalLightComponent light = directionalLights.get(i);
			StructAccessor a = accessor.getArray("directionalLights").getStruct(i);

			a.set("direction", light.direction);
			a.set("color", light.color);

			@SuppressWarnings({ "rawtypes", "unchecked" })
			List<ShadowMapComponent> maps = light.getAll(ShadowMapComponent.class);
			
			if (!maps.isEmpty() && shadowMapCount < 4) {
				a.seti("shadowMapIndex", shadowMapCount);
				int count = Math.min(maps.size(), 4 - shadowMapCount);
				a.seti("shadowMapCount", count);

				for (int j = 0; j < count; j++) {
					accessor.getArray("shadowMapMatrices").set(shadowMapCount, maps.get(j).camera.getViewProjection());

					String mapName = "shadowMap" + shadowMapCount;
					descriptorSet.set(mapName, ((ShadowMapComponentDepth) maps.get(j).renderImplementationMap.get(ShadowMapComponentDepth.ID)).getDepth());

					shadowMapCount++;
				}
			} else {
				a.seti("shadowMapIndex", -1);
			}
		}

		accessor.seti("pointLightCount", positionalLights.size());
		accessor.seti("sphereLightCount", sphereLights.size());
		accessor.seti("spotLightCount", spotLights.size());
		accessor.seti("directionalLightCount", directionalLights.size());

		ssbo.update();
		descriptorSet.update();

		positionalLights.clear();
		sphereLights.clear();
		spotLights.clear();
		directionalLights.clear();
	}

	@Override
	public void render(Scene scene) {
	}

	/**
	 * @return the ssbo
	 */
	public ShaderBuffer getSsbo() {
		return ssbo;
	}

	/**
	 * @return the descriptorSet
	 */
	public DescriptorSet getDescriptorSet() {
		return descriptorSet;
	}

	/**
	 * @return the positionalLights
	 */
	public List<PointLightComponent> getPositionalLights() {
		return positionalLights;
	}

	/**
	 * @return the sphereLights
	 */
	public List<SphereLightComponent> getSphereLights() {
		return sphereLights;
	}

	/**
	 * @return the spotLights
	 */
	public List<SpotLightComponent> getSpotLights() {
		return spotLights;
	}

	/**
	 * @return the directionalLights
	 */
	public List<DirectionalLightComponent> getDirectionalLights() {
		return directionalLights;
	}

	/**
	 * @return the staticPositionalLights
	 */
	public List<PointLightComponent> getStaticPositionalLights() {
		return staticPositionalLights;
	}

	/**
	 * @return the staticSphereLights
	 */
	public List<SphereLightComponent> getStaticSphereLights() {
		return staticSphereLights;
	}

	/**
	 * @return the staticSpotLights
	 */
	public List<SpotLightComponent> getStaticSpotLights() {
		return staticSpotLights;
	}

	/**
	 * @return the staticDirectionalLights
	 */
	public List<DirectionalLightComponent> getStaticDirectionalLights() {
		return staticDirectionalLights;
	}
}
