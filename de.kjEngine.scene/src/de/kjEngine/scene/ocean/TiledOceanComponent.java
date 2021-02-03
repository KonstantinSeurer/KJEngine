/**
 * 
 */
package de.kjEngine.scene.ocean;

import java.util.ArrayList;

import de.kjEngine.graphics.Descriptor.Type;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.BufferSource.Layout;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.TextureSource;
import de.kjEngine.math.Mat4;
import de.kjEngine.renderer.Renderable;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Renderable.RenderImplementation.Provider;
import de.kjEngine.renderer.Renderable.RenderImplementation.Registry;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.SceneComponent;

/**
 * @author konst
 *
 */
public class TiledOceanComponent extends SceneComponent<Entity, TiledOceanComponent> {

	public static final ID OCEAN_LIST = new ID(TiledOceanComponent.class, "forward_renderer");
	static {
		Registry.registerProvider(OCEAN_LIST, new Provider() {

			@Override
			public Renderable.RenderImplementation create() {
				return new TiledOceanComponentOceanList();
			}
		});
	}

	public static final BufferSource SETTINGS_UBO_SOURCE = new BufferSource("settings", Type.UNIFORM_BUFFER, Layout.STANDARD, new ArrayList<>());
	static {
		SETTINGS_UBO_SOURCE.addMember("int", "tileCountX");
		SETTINGS_UBO_SOURCE.addMember("int", "tileCountZ");
		SETTINGS_UBO_SOURCE.addMember("float", "textureCoordScaleX");
		SETTINGS_UBO_SOURCE.addMember("float", "textureCoordScaleZ");
		SETTINGS_UBO_SOURCE.addMember("mat4", "transform");
	}

	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("ocean", new ArrayList<>());
	static {
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(SETTINGS_UBO_SOURCE);
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("dx", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("dy", 2));
		DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("dz", 2));
	}

	public final OceanHeightMap heightMap;
	public final int tileCountX, tileCountZ;
	public final float width, length;
	public final float heightMapTileWidth, heightMapTileLength;

	public final ShaderBuffer settingsUbo;
	public final DescriptorSet descriptorSet;

	public TiledOceanComponent(OceanHeightMap heightMap, int tileCountX, int tileCountZ, float width, float length, float heightMapTileWidth, float heightMapTileLength) {
		super(LATE);
		
		this.heightMap = heightMap;
		this.tileCountX = tileCountX;
		this.tileCountZ = tileCountZ;
		this.width = width;
		this.length = length;
		this.heightMapTileWidth = heightMapTileWidth;
		this.heightMapTileLength = heightMapTileLength;

		settingsUbo = Graphics.createUniformBuffer(SETTINGS_UBO_SOURCE, new ArrayList<>(), ShaderBuffer.FLAG_NONE);
		settingsUbo.getAccessor().seti("tileCountX", tileCountX);
		settingsUbo.getAccessor().seti("tileCountZ", tileCountZ);
		settingsUbo.getAccessor().set("textureCoordScaleX", width / heightMapTileWidth);
		settingsUbo.getAccessor().set("textureCoordScaleZ", length / heightMapTileLength);
		settingsUbo.update();

		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
		descriptorSet.set("settings", settingsUbo);
		descriptorSet.set("dx", heightMap.getDx());
		descriptorSet.set("dy", heightMap.getDy());
		descriptorSet.set("dz", heightMap.getDz());
		descriptorSet.update();
	}

	private final Mat4 transformBuffer = new Mat4();

	@Override
	public void updateDescriptors() {
		super.updateDescriptors();

		transformBuffer.set(parent.transform.globalTransform);
		transformBuffer.scale(width, 1f, length);
		settingsUbo.getAccessor().set("transform", transformBuffer);
		settingsUbo.update();
	}
}
