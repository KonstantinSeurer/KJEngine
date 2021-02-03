/**
 * 
 */
package de.kjEngine.graphics;

import java.util.List;

import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.InterfaceBlockSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.StructSource;

/**
 * @author konst
 *
 */
public abstract class GraphicsContext {

	public GraphicsContext() {
	}

	public abstract void init(long window);
	
	/**
	 * @return true if the screenBuffer has been resized
	 */
	public abstract boolean pollImage();

	public abstract FrameBuffer getScreenBuffer();

	public abstract void swapBuffers();

	public abstract Graphics.API getApi();

	public abstract void dispose();

	public abstract void flush();

	public abstract Graphics.API.Version getVersion();

	public abstract DeviceVendor getDeviceVendor();

	public abstract GraphicsPipeline createGraphicsPipeline(PipelineSource source);

	public abstract ComputePipeline createComputePipeline(PipelineSource source);

	public abstract RayTracingPipeline createRayTracingPipeline(PipelineSource source);

	public abstract VertexBuffer createVertexBuffer(float[] data);

	public abstract IndexBuffer createIndexBuffer(int[] data);

	public abstract VertexArray createVertexArray(VertexArrayElement[] layout);

	public abstract CommandBuffer createCommandBuffer(int flags);

	public abstract ShaderBuffer createUniformBuffer(BufferSource source, List<StructSource> definedStructs, int flags);

	public abstract ShaderBuffer createStorageBuffer(BufferSource source, List<StructSource> definedStructs, int flags);

	public abstract Texture1D createTexture1D(int width, int levels, Texture1DDataProvider data, TextureFormat format, SamplingMode samplingMode, WrappingMode wrappingMode);

	public abstract Texture2D createTexture2D(Texture2DData data);

	public abstract Texture3D createTexture3D(Texture3DData data);

	public abstract TextureCube createTextureCube(int width, int height, byte[][] faces, TextureFormat format);

	public abstract DescriptorSet createDescriptorSet(DescriptorSetSource source);

	public abstract FrameBuffer createFramebuffer(int width, int height, InterfaceBlockSource source);

	public abstract Query createQuery(Query.Type type);

	public abstract NumSamplesPassedQuery createNumSamplesPassedQuery();

	public abstract AnySamplesPassedQuery createAnySamplesPassedQuery();
	
	public abstract TopLevelAccelerationStructure createTopLevelAccelerationStructure(int entryCount);
	
	public abstract BottomLevelAccelerationStructure createBottomLevelAccelerationStructure(ShaderBuffer source);

	public abstract void finish();
	
	public abstract DeviceCapabilities getCapabilities();
}
