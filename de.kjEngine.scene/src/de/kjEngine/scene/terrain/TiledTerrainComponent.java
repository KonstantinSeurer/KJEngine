/**
 * 
 */
package de.kjEngine.scene.terrain;

import java.util.ArrayList;

import de.kjEngine.graphics.Descriptor.Type;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.Texture2DDataProvider;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.BufferSource.Layout;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.TextureSource;
import de.kjEngine.math.Mat4;
import de.kjEngine.math.Vec3;
import de.kjEngine.math.Vec4;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Renderable.RenderImplementation.Provider;
import de.kjEngine.renderer.Renderable.RenderImplementation.Registry;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.SceneComponent;
import de.kjEngine.scene.material.Material;

/**
 * @author konst
 *
 */
public class TiledTerrainComponent extends SceneComponent<Entity, TiledTerrainComponent> {
	
	public static final ID TERRAIN_LIST = new ID(TiledTerrainComponent.class, "terrain_list");
	
	static {
		Registry.registerProvider(TERRAIN_LIST, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new TiledTerrainComponentTerrainList();
			}
		});
	}
	
	public static final BufferSource SETTINGS_UBO_SOURCE = new BufferSource("settings", Type.UNIFORM_BUFFER, Layout.STANDARD);
	static {
		SETTINGS_UBO_SOURCE.addMember("int", "tileCountX");
		SETTINGS_UBO_SOURCE.addMember("int", "tileCountZ");
		SETTINGS_UBO_SOURCE.addMember("float", "textureCoordScale");
		SETTINGS_UBO_SOURCE.addMember("mat4", "transform");
	}
	
	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("terrain");
	static {
		DESCRIPTOR_SET_SOURCE.addDescriptor(new TextureSource("heightMap", 2));
		DESCRIPTOR_SET_SOURCE.addDescriptor(SETTINGS_UBO_SOURCE);
	}

	private ElevationFunction elevationFunction;
	public final int tileCountX, tileCountZ;
	public final float width, length;
	public final Material material;
	public final float textureCoordScale;
	public final DescriptorSet descriptorSet;
	public final Texture2D heightMap;
	public final ShaderBuffer settings;

	public TiledTerrainComponent(ElevationFunction elevationFunction, int resolution, int tileCountX, int tileCountZ, float width, float length, Material material, float textureCoordScale) {
		super(LATE);
		
		this.elevationFunction = elevationFunction;
		this.tileCountX = tileCountX;
		this.tileCountZ = tileCountZ;
		this.width = width;
		this.length = length;
		this.material = material;
		this.textureCoordScale = textureCoordScale;
		
		final float xScale = width / (resolution - 1);
		final float yScale = length / (resolution - 1);
		heightMap = Graphics.createTexture2D(new Texture2DData(resolution, resolution, 1, new Texture2DDataProvider() {
			
			@Override
			public void get(int x, int y, Vec4 target) {
				target.x = elevationFunction.getElevation(x * xScale, y * yScale);
			}
		}, TextureFormat.R16F, SamplingMode.LINEAR, WrappingMode.CLAMP));
		
		settings = Graphics.createUniformBuffer(SETTINGS_UBO_SOURCE, new ArrayList<>(), ShaderBuffer.FLAG_NONE);
		settings.getAccessor().seti("tileCountX", tileCountX);
		settings.getAccessor().seti("tileCountZ", tileCountZ);
		settings.getAccessor().set("textureCoordScale", textureCoordScale);
		settings.update();
		
		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
		descriptorSet.set("heightMap", heightMap);
		descriptorSet.set("settings", settings);
		descriptorSet.update();
	}
	
	private final Mat4 transformBuffer =  new Mat4();

	@Override
	public void updateDescriptors() {
		super.updateDescriptors();
		
		transformBuffer.set(parent.transform.globalTransform);
		transformBuffer.scale(width, 1f, length);
		settings.getAccessor().set("transform", transformBuffer);
		settings.update();
	}

	public float getGlobalElevation(float x, float z) {
		transformBuffer.set(parent.transform.globalTransform);
		transformBuffer.invert();
		Vec3 pos = Mat4.transform(transformBuffer, Vec4.create(x, 0f, z, 1f), null);
		return elevationFunction.getElevation(pos.x, pos.z);
	}

	public void setElevationFunction(ElevationFunction elevationFunction) {
		this.elevationFunction = elevationFunction;
		
		final float xScale = width / (heightMap.width - 1);
		final float yScale = length / (heightMap.height - 1);
		heightMap.setData(new Texture2DDataProvider() {
			
			@Override
			public void get(int x, int y, Vec4 target) {
				target.x = elevationFunction.getElevation(x * xScale, y * yScale);
			}
		});
	}

	public ElevationFunction getElevationFunction() {
		return elevationFunction;
	}
}
