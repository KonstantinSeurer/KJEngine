/**
 * 
 */
package de.kjEngine.graphics;

/**
 * @author konst
 *
 */
public class DeviceCapabilities {

	public int maxTexture1DSize;
	public int maxTexture2DSize;
	public int maxTexture3DSize;
	public int maxTextureLayerCount;
	
	public int maxUniformBufferSize;
	public int maxStorageBufferSize;
	
	public int maxBoundDescriptorSetCount;
	public int maxBoundTextureCount;
	public int maxBoundUniformBufferCount;
	public int maxBoundStorageBufferCount;
	public int maxBoundImageCount;
	public int maxDescriptorCount;
	
	public int maxDescriptorSetTextureCount;
	public int maxDescriptorSetUniformBufferCount;
	public int maxDescriptorSetStorageBufferCount;
	public int maxDescriptorSetImageCount;
	
	public int maxVertexShaderInputCount;
	public int maxVertexShaderOutputCount;
	
	public int maxTesselationControlShaderInputCount;
	public int maxTesselationControlShaderOutputCount;
	
	public int maxTesselationEvaluationShaderInputCount;
	public int maxTesselationEvaluationShaderOutputCount;
	
	public int maxGeometryShaderInputCount;
	public int maxGeometryShaderOutputCount;
	
	public int maxFragmentShaderInputCount;
	public int maxFragmentShaderOutputCount;
	
	public int maxTesselationPatchSize;
	
	public int maxGeometryOutputVertexCount;
	
	public int maxComputeWorkGroupCountX;
	public int maxComputeWorkGroupCountY;
	public int maxComputeWorkGroupCountZ;
	
	public int maxComputeWorkGroupSizeX;
	public int maxComputeWorkGroupSizeY;
	public int maxComputeWorkGroupSizeZ;
	
	public int maxFramebufferWidth;
	public int maxFramebufferHeight;
	public int maxColorAttachmentCount;
	
	public boolean tesselationShader;
	public boolean geometryShader;
	
	public boolean depthClamp;
	
	public DeviceCapabilities() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (depthClamp ? 1231 : 1237);
		result = prime * result + (geometryShader ? 1231 : 1237);
		result = prime * result + maxBoundDescriptorSetCount;
		result = prime * result + maxBoundImageCount;
		result = prime * result + maxBoundStorageBufferCount;
		result = prime * result + maxBoundTextureCount;
		result = prime * result + maxBoundUniformBufferCount;
		result = prime * result + maxColorAttachmentCount;
		result = prime * result + maxComputeWorkGroupCountX;
		result = prime * result + maxComputeWorkGroupCountY;
		result = prime * result + maxComputeWorkGroupCountZ;
		result = prime * result + maxComputeWorkGroupSizeX;
		result = prime * result + maxComputeWorkGroupSizeY;
		result = prime * result + maxComputeWorkGroupSizeZ;
		result = prime * result + maxDescriptorCount;
		result = prime * result + maxDescriptorSetImageCount;
		result = prime * result + maxDescriptorSetStorageBufferCount;
		result = prime * result + maxDescriptorSetTextureCount;
		result = prime * result + maxDescriptorSetUniformBufferCount;
		result = prime * result + maxFragmentShaderInputCount;
		result = prime * result + maxFragmentShaderOutputCount;
		result = prime * result + maxFramebufferHeight;
		result = prime * result + maxFramebufferWidth;
		result = prime * result + maxGeometryOutputVertexCount;
		result = prime * result + maxGeometryShaderInputCount;
		result = prime * result + maxGeometryShaderOutputCount;
		result = prime * result + maxStorageBufferSize;
		result = prime * result + maxTesselationControlShaderInputCount;
		result = prime * result + maxTesselationControlShaderOutputCount;
		result = prime * result + maxTesselationEvaluationShaderInputCount;
		result = prime * result + maxTesselationEvaluationShaderOutputCount;
		result = prime * result + maxTesselationPatchSize;
		result = prime * result + maxTexture1DSize;
		result = prime * result + maxTexture2DSize;
		result = prime * result + maxTexture3DSize;
		result = prime * result + maxTextureLayerCount;
		result = prime * result + maxUniformBufferSize;
		result = prime * result + maxVertexShaderInputCount;
		result = prime * result + maxVertexShaderOutputCount;
		result = prime * result + (tesselationShader ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceCapabilities other = (DeviceCapabilities) obj;
		if (depthClamp != other.depthClamp)
			return false;
		if (geometryShader != other.geometryShader)
			return false;
		if (maxBoundDescriptorSetCount != other.maxBoundDescriptorSetCount)
			return false;
		if (maxBoundImageCount != other.maxBoundImageCount)
			return false;
		if (maxBoundStorageBufferCount != other.maxBoundStorageBufferCount)
			return false;
		if (maxBoundTextureCount != other.maxBoundTextureCount)
			return false;
		if (maxBoundUniformBufferCount != other.maxBoundUniformBufferCount)
			return false;
		if (maxColorAttachmentCount != other.maxColorAttachmentCount)
			return false;
		if (maxComputeWorkGroupCountX != other.maxComputeWorkGroupCountX)
			return false;
		if (maxComputeWorkGroupCountY != other.maxComputeWorkGroupCountY)
			return false;
		if (maxComputeWorkGroupCountZ != other.maxComputeWorkGroupCountZ)
			return false;
		if (maxComputeWorkGroupSizeX != other.maxComputeWorkGroupSizeX)
			return false;
		if (maxComputeWorkGroupSizeY != other.maxComputeWorkGroupSizeY)
			return false;
		if (maxComputeWorkGroupSizeZ != other.maxComputeWorkGroupSizeZ)
			return false;
		if (maxDescriptorCount != other.maxDescriptorCount)
			return false;
		if (maxDescriptorSetImageCount != other.maxDescriptorSetImageCount)
			return false;
		if (maxDescriptorSetStorageBufferCount != other.maxDescriptorSetStorageBufferCount)
			return false;
		if (maxDescriptorSetTextureCount != other.maxDescriptorSetTextureCount)
			return false;
		if (maxDescriptorSetUniformBufferCount != other.maxDescriptorSetUniformBufferCount)
			return false;
		if (maxFragmentShaderInputCount != other.maxFragmentShaderInputCount)
			return false;
		if (maxFragmentShaderOutputCount != other.maxFragmentShaderOutputCount)
			return false;
		if (maxFramebufferHeight != other.maxFramebufferHeight)
			return false;
		if (maxFramebufferWidth != other.maxFramebufferWidth)
			return false;
		if (maxGeometryOutputVertexCount != other.maxGeometryOutputVertexCount)
			return false;
		if (maxGeometryShaderInputCount != other.maxGeometryShaderInputCount)
			return false;
		if (maxGeometryShaderOutputCount != other.maxGeometryShaderOutputCount)
			return false;
		if (maxStorageBufferSize != other.maxStorageBufferSize)
			return false;
		if (maxTesselationControlShaderInputCount != other.maxTesselationControlShaderInputCount)
			return false;
		if (maxTesselationControlShaderOutputCount != other.maxTesselationControlShaderOutputCount)
			return false;
		if (maxTesselationEvaluationShaderInputCount != other.maxTesselationEvaluationShaderInputCount)
			return false;
		if (maxTesselationEvaluationShaderOutputCount != other.maxTesselationEvaluationShaderOutputCount)
			return false;
		if (maxTesselationPatchSize != other.maxTesselationPatchSize)
			return false;
		if (maxTexture1DSize != other.maxTexture1DSize)
			return false;
		if (maxTexture2DSize != other.maxTexture2DSize)
			return false;
		if (maxTexture3DSize != other.maxTexture3DSize)
			return false;
		if (maxTextureLayerCount != other.maxTextureLayerCount)
			return false;
		if (maxUniformBufferSize != other.maxUniformBufferSize)
			return false;
		if (maxVertexShaderInputCount != other.maxVertexShaderInputCount)
			return false;
		if (maxVertexShaderOutputCount != other.maxVertexShaderOutputCount)
			return false;
		if (tesselationShader != other.tesselationShader)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeviceCapabilities [maxTexture1DSize=" + maxTexture1DSize + ", maxTexture2DSize=" + maxTexture2DSize + ", maxTexture3DSize=" + maxTexture3DSize + ", maxTextureLayerCount="
				+ maxTextureLayerCount + ", maxUniformBufferSize=" + maxUniformBufferSize + ", maxStorageBufferSize=" + maxStorageBufferSize + ", maxBoundDescriptorSetCount="
				+ maxBoundDescriptorSetCount + ", maxBoundTextureCount=" + maxBoundTextureCount + ", maxBoundUniformBufferCount=" + maxBoundUniformBufferCount + ", maxBoundStorageBufferCount="
				+ maxBoundStorageBufferCount + ", maxBoundImageCount=" + maxBoundImageCount + ", maxDescriptorCount=" + maxDescriptorCount + ", maxDescriptorSetTextureCount="
				+ maxDescriptorSetTextureCount + ", maxDescriptorSetUniformBufferCount=" + maxDescriptorSetUniformBufferCount + ", maxDescriptorSetStorageBufferCount="
				+ maxDescriptorSetStorageBufferCount + ", maxDescriptorSetImageCount=" + maxDescriptorSetImageCount + ", maxVertexShaderInputCount=" + maxVertexShaderInputCount
				+ ", maxVertexShaderOutputCount=" + maxVertexShaderOutputCount + ", maxTesselationControlShaderInputCount=" + maxTesselationControlShaderInputCount
				+ ", maxTesselationControlShaderOutputCount=" + maxTesselationControlShaderOutputCount + ", maxTesselationEvaluationShaderInputCount=" + maxTesselationEvaluationShaderInputCount
				+ ", maxTesselationEvaluationShaderOutputCount=" + maxTesselationEvaluationShaderOutputCount + ", maxGeometryShaderInputCount=" + maxGeometryShaderInputCount
				+ ", maxGeometryShaderOutputCount=" + maxGeometryShaderOutputCount + ", maxFragmentShaderInputCount=" + maxFragmentShaderInputCount + ", maxFragmentShaderOutputCount="
				+ maxFragmentShaderOutputCount + ", maxTesselationPatchSize=" + maxTesselationPatchSize + ", maxGeometryOutputVertexCount=" + maxGeometryOutputVertexCount
				+ ", maxComputeWorkGroupCountX=" + maxComputeWorkGroupCountX + ", maxComputeWorkGroupCountY=" + maxComputeWorkGroupCountY + ", maxComputeWorkGroupCountZ=" + maxComputeWorkGroupCountZ
				+ ", maxComputeWorkGroupSizeX=" + maxComputeWorkGroupSizeX + ", maxComputeWorkGroupSizeY=" + maxComputeWorkGroupSizeY + ", maxComputeWorkGroupSizeZ=" + maxComputeWorkGroupSizeZ
				+ ", maxFramebufferWidth=" + maxFramebufferWidth + ", maxFramebufferHeight=" + maxFramebufferHeight + ", maxColorAttachmentCount=" + maxColorAttachmentCount + ", tesselationShader="
				+ tesselationShader + ", geometryShader=" + geometryShader + ", depthClamp=" + depthClamp + "]";
	}
}
