/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import de.kjEngine.graphics.Descriptor;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.shader.DescriptorSetSource;

/**
 * @author konst
 *
 */
public class VulkanDescriptorSet extends DescriptorSet {

	static class InternDescriptor implements Descriptor {

		Descriptor[] descriptors;
		boolean[] change;
		Type type;

		InternDescriptor(Type type, int count) {
			this.type = type;
			descriptors = new Descriptor[count];
			change = new boolean[count];
		}

		@Override
		public void dispose() {
			for (int i = 0; i < descriptors.length; i++) {
				if (descriptors[i] != null) {
					descriptors[i].dispose();
				}
			}
		}

		@Override
		public Type getType() {
			return type;
		}
	}

	InternDescriptor[] descriptors;
	boolean change = false;

	long set, pool;

	public VulkanDescriptorSet(DescriptorSetSource source) {
		super(source);

		descriptors = new InternDescriptor[source.getDescriptors().size()];

		Map<Integer, Integer> poolSizes = new HashMap<>();
		int typeCount = 0;

		for (int i = 0; i < descriptors.length; i++) {
			int type = getDescriptorType(source.getDescriptors().get(i).getType());
			if (!poolSizes.containsKey(type)) {
				poolSizes.put(type, 0);
				typeCount++;
			}
			poolSizes.put(type, poolSizes.get(type) + 1);
		}

		VkDescriptorPoolSize.Buffer sizes = VkDescriptorPoolSize.calloc(typeCount);
		int i = 0;
		for (int type : poolSizes.keySet()) {
			VkDescriptorPoolSize size = sizes.get(i++);
			size.set(type, poolSizes.get(type));
		}

		VkDescriptorPoolCreateInfo poolCreateInfo = VkDescriptorPoolCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO).pNext(NULL).flags(0);
		poolCreateInfo.pPoolSizes(sizes);
		poolCreateInfo.maxSets(1);

		LongBuffer pDescriptorPool = memAllocLong(1);
		err(vkCreateDescriptorPool(Graphics.getVkContext().getDevice(), poolCreateInfo, null, pDescriptorPool));
		pool = pDescriptorPool.get(0);

		sizes.free();
		poolCreateInfo.free();
		memFree(pDescriptorPool);

		VkDescriptorSetAllocateInfo allocateInfo = VkDescriptorSetAllocateInfo.calloc().sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO).pNext(NULL);
		allocateInfo.descriptorPool(pool);
		LongBuffer pSetLayouts = memAllocLong(1).put(0, DescriptorSetLayoutCache.getLayout(source));
		allocateInfo.pSetLayouts(pSetLayouts);

		LongBuffer pDescriptorSets = memAllocLong(1);
		err(vkAllocateDescriptorSets(Graphics.getVkContext().getDevice(), allocateInfo, pDescriptorSets));
		set = pDescriptorSets.get(0);

		memFree(pDescriptorSets);
		memFree(pSetLayouts);
		allocateInfo.free();
	}
	
	public void update() {
		if (change) {
			List<VkWriteDescriptorSet> writes = new ArrayList<>();

			for (int i = 0; i < descriptors.length; i++) {
				InternDescriptor desc = descriptors[i];
				if (desc == null) {
					continue;
				}
				for (int j = 0; j < desc.descriptors.length; j++) {
					Descriptor d = desc.descriptors[j];
					if (d == null || !desc.change[j]) {
						continue;
					}
					desc.change[j] = false;
					switch (d.getType()) {
					case STORAGE_BUFFER: {
						VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.calloc(1);
						bufferInfo.get(0).buffer(((VulkanBuffer) d).getBuffer());
						bufferInfo.get(0).offset(0);
						bufferInfo.get(0).range(((ShaderBuffer) d).getAccessor().getSize());

						VkWriteDescriptorSet write = VkWriteDescriptorSet.calloc().sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET).pNext(NULL);
						write.dstSet(set);
						write.dstBinding(i);
						write.dstArrayElement(j);
						write.descriptorType(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER);
						write.pBufferInfo(bufferInfo);
						write.pImageInfo(null);
						write.pTexelBufferView(null);
						writes.add(write);
					}
						break;
					case TEXTURE_1D:
						break;
					case TEXTURE_2D: {
						VkDescriptorImageInfo.Buffer imageInfo = VkDescriptorImageInfo.calloc(1);
						imageInfo.imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
						imageInfo.imageView(((VulkanTexture2D) d).samplerView);
						imageInfo.sampler(((VulkanTexture2D) d).sampler);

						VkWriteDescriptorSet write = VkWriteDescriptorSet.calloc().sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET).pNext(NULL);
						write.dstSet(set);
						write.dstBinding(i);
						write.dstArrayElement(j);
						write.descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
						write.pBufferInfo(null);
						write.pImageInfo(imageInfo);
						write.pTexelBufferView(null);
						writes.add(write);
					}
						break;
					case TEXTURE_CUBE:
						break;
					case UNIFORM_BUFFER: {
						VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.calloc(1);
						bufferInfo.get(0).buffer(((VulkanBuffer) d).getBuffer());
						bufferInfo.get(0).offset(0);
						bufferInfo.get(0).range(((ShaderBuffer) d).getAccessor().getSize());

						VkWriteDescriptorSet write = VkWriteDescriptorSet.calloc().sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET).pNext(NULL);
						write.dstSet(set);
						write.dstBinding(i);
						write.dstArrayElement(j);
						write.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
						write.pBufferInfo(bufferInfo);
						write.pImageInfo(null);
						write.pTexelBufferView(null);
						writes.add(write);
					}
						break;
					case TEXTURE_3D:
						break;
					case IMAGE_2D: {
						VulkanImage2D image = (VulkanImage2D) d;

						VkDescriptorImageInfo.Buffer imageInfo = VkDescriptorImageInfo.calloc(1);
						imageInfo.imageLayout(VK_IMAGE_LAYOUT_GENERAL);
						imageInfo.imageView(image.texture.imageViews[image.level]);
						imageInfo.sampler(image.texture.sampler);

						VkWriteDescriptorSet write = VkWriteDescriptorSet.calloc().sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET).pNext(NULL);
						write.dstSet(set);
						write.dstBinding(i);
						write.dstArrayElement(j);
						write.descriptorType(VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
						write.pBufferInfo(null);
						write.pImageInfo(imageInfo);
						write.pTexelBufferView(null);
						writes.add(write);
					}
						break;
					case IMAGE_3D:
						break;
					case ACCELERATION_STRUCTURE:
						break;
					}
				}
			}

			VkWriteDescriptorSet.Buffer pDescriptorWrites = VkWriteDescriptorSet.calloc(writes.size());
			for (int i = 0; i < writes.size(); i++) {
				pDescriptorWrites.put(i, writes.get(i));
			}

			vkUpdateDescriptorSets(Graphics.getVkContext().getDevice(), pDescriptorWrites, null);

			pDescriptorWrites.free();

			change = false;
		}
	}

	@Override
	public void dispose() {
		vkDestroyDescriptorPool(Graphics.getVkContext().getDevice(), pool, null);
	}

	@Override
	public Descriptor get(String name) {
		int binding = -1;
		for (int i = 0; i < source.getDescriptors().size(); i++) {
			if (source.getDescriptors().get(i).getName().equals(name)) {
				binding = i;
			}
		}

		if (binding != -1) {
			return descriptors[binding];
		}

		return null;
	}

	@Override
	public void set(String name, int index, Descriptor d) {
		int binding = -1;
		for (int i = 0; i < source.getDescriptors().size(); i++) {
			if (source.getDescriptors().get(i).getName().equals(name)) {
				binding = i;
			}
		}

		if (binding != -1) {
			if (descriptors[binding] == null) {
				descriptors[binding] = new InternDescriptor(source.getDescriptors().get(binding).getType(), source.getDescriptors().get(binding).getArrayLength());
			}
			descriptors[binding].descriptors[index] = d;
			descriptors[binding].change[index] = true;
			change = true;
		}
	}
}
