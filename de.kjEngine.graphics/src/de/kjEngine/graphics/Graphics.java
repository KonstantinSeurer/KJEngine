/**
 * 
 */
package de.kjEngine.graphics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.ivelate.JavaHDR.HDREncoder;
import com.github.ivelate.JavaHDR.HDRImage;

import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.InterfaceBlockSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderType;
import de.kjEngine.graphics.shader.StructSource;
import de.kjEngine.graphics.vulkan.VulkanContext;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceManager;
import de.kjEngine.io.ResourceNotFoundException;
import de.kjEngine.io.UnknownProtocolException;
import de.kjEngine.math.Vec4;

/**
 * @author konst
 *
 */
public class Graphics {
	
	private static Map<RL, Texture2D> textures = new HashMap<>();
	
	public static Texture2D loadTexture(RL rl, SamplingMode samplingMode, WrappingMode wrappingMode, boolean cached) {
		if (cached) {
			Texture2D texture = textures.get(rl);
			if (texture == null) {
				texture = loadTexture(rl, samplingMode, wrappingMode, false);
				textures.put(rl, texture);
			}
			return texture;
		} else {
			if (rl.getType().equals("hdr")) {
				try {
					HDRImage image = HDREncoder.readHDR(rl.openInputStream(), true);
					return createTexture2D(new Texture2DData(image.getWidth(), image.getHeight(), 1, new Texture2DDataProvider() {

						@Override
						public void get(int x, int y, Vec4 target) {
							y = image.getHeight() - y - 1;
							target.x = image.getPixelValue(x, y, 0);
							target.y = image.getPixelValue(x, y, 1);
							target.z = image.getPixelValue(x, y, 2);
							target.w = 1f;
						}
					}, TextureFormat.RGB16F, samplingMode, wrappingMode));
				} catch (UnknownProtocolException e) {
					e.printStackTrace();
				} catch (ResourceNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			return createTexture2D(Texture2DData.create(ResourceManager.loadImage(rl, false), samplingMode, wrappingMode));
		}
	}

	public static enum API {
		OPENGL, VULKAN;

		public static class Version {
			public int major, minor, patch;

			public Version() {
			}

			public Version(int major, int minor, int patch) {
				this.major = major;
				this.minor = minor;
				this.patch = patch;
			}

			@Override
			public String toString() {
				return major + "." + minor + "." + patch;
			}
		}
	}

	private static GraphicsContext context;

	public static void init(GraphicsContext context) {
		if (Graphics.context != null) {
			return;
		}
		Graphics.context = context;
	}

	public static GraphicsContext getContext() {
		return context;
	}

	public static VulkanContext getVkContext() {
		return (VulkanContext) context;
	}

	public static API getApi() {
		return context.getApi();
	}

	public static GraphicsPipeline createGraphicsPipeline(PipelineSource source) {
		return context.createGraphicsPipeline(source);
	}

	public static ComputePipeline createComputePipeline(PipelineSource source) {
		return context.createComputePipeline(source);
	}

	public static VertexBuffer createVertexBuffer(float[] data) {
		return context.createVertexBuffer(data);
	}

	public static IndexBuffer createIndexBuffer(int[] data) {
		return context.createIndexBuffer(data);
	}

	public static VertexArray createVertexArray(VertexArrayElement[] layout) {
		return context.createVertexArray(layout);
	}

	public static CommandBuffer createCommandBuffer(int flags) {
		return context.createCommandBuffer(flags);
	}

	public static ShaderBuffer createUniformBuffer(BufferSource source, List<StructSource> definedStructs, int flags) {
		return context.createUniformBuffer(source, definedStructs, flags);
	}
	
	public static ShaderBuffer createUniformBuffer(PipelineSource pipeline, String set, String name, int flags) {
		return context.createUniformBuffer(pipeline.getDescriptorSet(set).getBuffer(name), pipeline.getStructs(), flags);
	}

	public static ShaderBuffer createStorageBuffer(BufferSource source, List<StructSource> definedStructs, int flags) {
		return context.createStorageBuffer(source, definedStructs, flags);
	}

	public static Texture2D createTexture2D(Texture2DData data) {
		return context.createTexture2D(data);
	}

	private static final float ONE_D_255 = 1f / 255f;

	public static Texture2D createTexture2D(int color) {
		int r = (color >> 24) & 0xff;
		int g = (color >> 16) & 0xff;
		int b = (color >> 8) & 0xff;
		int a = (color) & 0xff;

		return createTexture2D(r * ONE_D_255, g * ONE_D_255, b * ONE_D_255, a * ONE_D_255);
	}

	public static Texture2D createTexture2D(Vec4 color) {
		return createTexture2D(color.x, color.y, color.z, color.w);
	}

	public static Texture2D createTexture2D(float r, float g, float b, float a) {
		return createTexture2D(new Texture2DData(1, 1, 1, new Texture2DDataProvider() {

			@Override
			public void get(int x, int y, Vec4 target) {
				target.set(r, g, b, a);
			}
		}, TextureFormat.RGBA8, SamplingMode.NEAREST, WrappingMode.CLAMP));
	}
	
	public static Texture2D createTexture2D(float gray) {
		return createTexture2D(gray, gray, gray, 1f);
	}

	public static Texture1D createTexture1D(int width, int levels, Texture1DDataProvider data, TextureFormat format, SamplingMode samplingMode, WrappingMode wrappingMode) {
		return context.createTexture1D(width, levels, data, format, samplingMode, wrappingMode);
	}

	public static TextureCube createTextureCube(int width, int height, byte[][] faces, TextureFormat format) {
		return context.createTextureCube(width, height, faces, format);
	}

	public static TextureCube createTextureCube(String[] paths) {
		if (paths.length < 6) {
			return null;
		}

		byte[][] faces = null;
		int width = 0, height = 0;

		for (int i = 0; i < 6; i++) {
			try {
				InputStream in;
				if (paths[i].startsWith("/")) {
					in = Graphics.class.getResourceAsStream(paths[i]);
				} else {
					in = new FileInputStream(new File(paths[i]));
				}
				if (in == null) {
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return createTextureCube(width, height, faces, TextureFormat.RGBA8);
	}

	public static Texture3D createTexture3D(Texture3DData data) {
		return context.createTexture3D(data);
	}

	public static Texture3D createTexture3D(int width, int height, int length, int levels, Texture3DDataProvider data, SamplingMode samplingMode, TextureFormat format, WrappingMode wrappingMode) {
		return createTexture3D(new Texture3DData(width, height, length, levels, data, format, samplingMode, wrappingMode));
	}

	public static DescriptorSet createDescriptorSet(DescriptorSetSource source) {
		return context.createDescriptorSet(source);
	}

	public static FrameBuffer createFramebuffer(int width, int height, InterfaceBlockSource source) {
		return context.createFramebuffer(width, height, source);
	}

	public static Query createQuery(Query.Type type) {
		return context.createQuery(type);
	}

	public static NumSamplesPassedQuery createNumSamplesPassedQuery() {
		return context.createNumSamplesPassedQuery();
	}

	public static AnySamplesPassedQuery createAnySamplesPassedQuery() {
		return context.createAnySamplesPassedQuery();
	}
	
	public static TopLevelAccelerationStructure createTopLevelAccelerationStructure(int entryCount) {
		return context.createTopLevelAccelerationStructure(entryCount);
	}
	
	public static BottomLevelAccelerationStructure createBottomLevelAccelerationStructure(ShaderBuffer source) {
		return context.createBottomLevelAccelerationStructure(source);
	}
	
	public static RayTracingPipeline createRayTracingPipeline(PipelineSource source) {
		return context.createRayTracingPipeline(source);
	}

	public static FrameBuffer createFramebuffer(int width, int height, PipelineSource pipeline) {
		return createFramebuffer(width, height, pipeline.getShader(ShaderType.FRAGMENT).getOutput());
	}
}
