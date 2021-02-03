/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;

import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.DescriptorSource;

/**
 * @author konst
 *
 */
public class DescriptorSetLayoutCache {

	private static Map<DescriptorSetSource, Long> layouts = new HashMap<>();
	
	public static long getLayout(DescriptorSetSource source) {
		if (!layouts.containsKey(source)) {
			VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.calloc(source.getDescriptors().size());
			for (int i = 0; i < source.getDescriptors().size(); i++) {
				DescriptorSource descriptor = source.getDescriptors().get(i);
				VkDescriptorSetLayoutBinding binding = bindings.get(i);
				binding.binding(i);
				binding.descriptorType(getDescriptorType(descriptor.getType()));
				binding.descriptorCount(descriptor.getArrayLength());
				binding.stageFlags(VK_SHADER_STAGE_ALL);
				binding.pImmutableSamplers(null);
			}

			VkDescriptorSetLayoutCreateInfo layoutCreateInfo = VkDescriptorSetLayoutCreateInfo.calloc()
					.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO).pNext(NULL).flags(0);
			layoutCreateInfo.pBindings(bindings);

			LongBuffer pSetLayout = memAllocLong(1);
			err(vkCreateDescriptorSetLayout(Graphics.getVkContext().getDevice(), layoutCreateInfo, null, pSetLayout));
			layouts.put(source, pSetLayout.get(0));
			
			bindings.free();
			layoutCreateInfo.free();
			memFree(pSetLayout);
		}
		return layouts.get(source);
	}
}
