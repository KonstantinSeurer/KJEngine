/**
 * 
 */
package de.kjEngine.graphics.vulkan;

/**
 * @author konst
 *
 */
public interface VulkanPipeline {

	public long getPipeline();
	public long getLayout();
	public int getBindPoint();
	public VulkanUniforms getUniforms();
}
