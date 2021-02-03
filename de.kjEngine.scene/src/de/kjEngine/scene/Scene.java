package de.kjEngine.scene;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.kjEngine.component.Container;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.ibl.SceneUE4LightProbeList;
import de.kjEngine.scene.light.DirectionalLightComponent;
import de.kjEngine.scene.light.PointLightComponent;
import de.kjEngine.scene.light.SceneLightBuffer;
import de.kjEngine.scene.light.ShadowMapComponentDepth;
import de.kjEngine.scene.light.SphereLightComponent;
import de.kjEngine.scene.light.SpotLightComponent;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.scene.ocean.SceneTiledOceanList;
import de.kjEngine.scene.ocean.TiledOceanComponent;
import de.kjEngine.scene.terrain.SceneTiledTerrainList;
import de.kjEngine.scene.terrain.TiledTerrainComponent;

public class Scene extends Container<Entity, Scene> {

	public static final ID TRANSFORM_BUFFER = new ID(Scene.class, "transform_buffer");
	public static final ID LIGHT_BUFFER = new ID(Scene.class, "light_list");
	public static final ID ENTITY_MAP = new ID(Scene.class, "entity_map");
	public static final ID UE4_LIGHT_PROBE_LIST = new ID(Scene.class, "ue4_light_probe_list");
	public static final ID OCEAN_LIST = new ID(Scene.class, "tiled_ocean_list");
	public static final ID TERRAIN_LIST = new ID(Scene.class, "tiled_terrain_list");

	static {
		Renderable.RenderImplementation.Registry.registerProvider(TRANSFORM_BUFFER, new Renderable.RenderImplementation.Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new SceneTransformBuffer();
			}
		});
		Set<ID> transformBufferDependencies = new HashSet<>();
		transformBufferDependencies.add(EntityTransformBuffer.ID);
		transformBufferDependencies.add(ModelComponent.TRANSFORM_BUFFER);
		Renderable.RenderImplementation.Registry.registerDependency(TRANSFORM_BUFFER, transformBufferDependencies);

		Renderable.RenderImplementation.Registry.registerProvider(LIGHT_BUFFER, new Renderable.RenderImplementation.Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new SceneLightBuffer();
			}
		});
		Set<ID> lightBufferDependencies = new HashSet<>();
		lightBufferDependencies.add(DirectionalLightComponent.LIGHT_BUFFER);
		lightBufferDependencies.add(PointLightComponent.LIGHT_BUFFER);
		lightBufferDependencies.add(SphereLightComponent.LIGHT_BUFFER);
		lightBufferDependencies.add(SpotLightComponent.LIGHT_BUFFER);
		lightBufferDependencies.add(ShadowMapComponentDepth.ID); // TODO: there probably is a better way
		Renderable.RenderImplementation.Registry.registerDependency(LIGHT_BUFFER, lightBufferDependencies);
		
		Renderable.RenderImplementation.Registry.registerProvider(ENTITY_MAP, new Renderable.RenderImplementation.Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new SceneEntityMap();
			}
		});
		Set<ID> entityMapDependencies = new HashSet<>();
		entityMapDependencies.add(Model.ENTITY_MAP);
		entityMapDependencies.add(EntityEntityMap.ID);
		Renderable.RenderImplementation.Registry.registerDependency(ENTITY_MAP, entityMapDependencies);
		
		Renderable.RenderImplementation.Registry.registerProvider(UE4_LIGHT_PROBE_LIST, new Renderable.RenderImplementation.Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new SceneUE4LightProbeList();
			}
		});
		
		Renderable.RenderImplementation.Registry.registerProvider(OCEAN_LIST, new Renderable.RenderImplementation.Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new SceneTiledOceanList();
			}
		});
		Set<ID> oceanListDependencies = new HashSet<>();
		oceanListDependencies.add(TiledOceanComponent.OCEAN_LIST);
		Renderable.RenderImplementation.Registry.registerDependency(OCEAN_LIST, oceanListDependencies);
		
		Renderable.RenderImplementation.Registry.registerProvider(TERRAIN_LIST, new Renderable.RenderImplementation.Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new SceneTiledTerrainList();
			}
		});
		Set<ID> terrainListDependencies = new HashSet<>();
		terrainListDependencies.add(TiledTerrainComponent.TERRAIN_LIST);
		Renderable.RenderImplementation.Registry.registerDependency(TERRAIN_LIST, terrainListDependencies);
	}

	public CameraComponent camera;

	public final Entity dynamicRoot;
	public final Entity staticRoot = new Entity(false);

	// for faster access
	public final SceneTransformBuffer transformBufferGPUImplementation;
	public final SceneEntityMap entityMapGPUImplementation;

	public Scene() {
		super(new Entity(true));
		
		dynamicRoot = root;
		dynamicRoot.name = "root";

		staticRoot.setContainer(this);
		staticRoot.name = "root";

		transformBufferGPUImplementation = getRenderImplementation(SceneTransformBuffer.class);
		entityMapGPUImplementation = getRenderImplementation(SceneEntityMap.class);
	}

	public Scene add(String name, Entity e) {
		e.name = name;
		add(e);
		return this;
	}

	public Scene add(Entity e) {
		if (e.isDynamic()) {
			dynamicRoot.add(e);
		} else {
			staticRoot.add(e);
		}
		return this;
	}

	public Scene remove(Entity e) {
		if (e.isDynamic()) {
			dynamicRoot.remove(e);
		} else {
			staticRoot.remove(e);
		}
		return this;
	}

	public Scene add(Collection<? extends Entity> l) {
		for (Entity e : l) {
			add(e);
		}
		return this;
	}
	
	public Scene remove(Collection<? extends Entity> l) {
		for (Entity e : l) {
			remove(e);
		}
		return this;
	}
}
