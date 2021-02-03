/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import de.kjEngine.graphics.Graphics;
import de.kjEngine.util.Disposable;

/**
 * @author konst
 *
 */
public class VulkanSemaphoreChain implements Disposable {

	private List<Long> semaphorePool = new ArrayList<>();
	private List<Long> semaphoreChain = new ArrayList<>();

	private VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO).pNext(NULL).flags(0);
	private LongBuffer pSemaphore = memAllocLong(1);

	public VulkanSemaphoreChain() {
	}

	public long push() {
		long semaphore;
		if (semaphorePool.isEmpty()) {
			err(vkCreateSemaphore(Graphics.getVkContext().getDevice(), semaphoreCreateInfo, null, pSemaphore));
			semaphore = pSemaphore.get(0);
		} else {
			semaphore = semaphorePool.remove(0);
		}
		semaphoreChain.add(semaphore);
		return semaphore;
	}

	public long peek() {
		if (semaphoreChain.isEmpty()) {
			return VK_NULL_HANDLE;
		}
		return semaphoreChain.get(semaphoreChain.size() - 1);
	}

	public void clear() {
		semaphorePool.addAll(semaphoreChain);
		semaphoreChain.clear();
	}

	@Override
	public void dispose() {
		semaphoreCreateInfo.free();
		memFree(pSemaphore);

		clear();

		VkDevice device = Graphics.getVkContext().getDevice();

		for (long semaphore : semaphorePool) {
			vkDestroySemaphore(device, semaphore, null);
		}
	}
}
