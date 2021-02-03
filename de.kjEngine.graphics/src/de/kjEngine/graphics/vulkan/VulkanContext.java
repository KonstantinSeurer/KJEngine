/**
 * 
 */
package de.kjEngine.graphics.vulkan;

import static de.kjEngine.graphics.vulkan.VulkanUtil.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.EXTDebugReport.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashSet;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.EXTDescriptorIndexing;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkAttachmentDescription;
import org.lwjgl.vulkan.VkAttachmentReference;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkComponentMapping;
import org.lwjgl.vulkan.VkDebugReportCallbackCreateInfoEXT;
import org.lwjgl.vulkan.VkDebugReportCallbackEXT;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceLimits;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkRenderPassCreateInfo;
import org.lwjgl.vulkan.VkSubpassDescription;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

import de.kjEngine.graphics.AnySamplesPassedQuery;
import de.kjEngine.graphics.BottomLevelAccelerationStructure;
import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.ComputePipeline;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.DeviceCapabilities;
import de.kjEngine.graphics.DeviceVendor;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsContext;
import de.kjEngine.graphics.GraphicsPipeline;
import de.kjEngine.graphics.IndexBuffer;
import de.kjEngine.graphics.NumSamplesPassedQuery;
import de.kjEngine.graphics.Query;
import de.kjEngine.graphics.RayTracingPipeline;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.Texture1D;
import de.kjEngine.graphics.Texture1DDataProvider;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.Texture3D;
import de.kjEngine.graphics.Texture3DData;
import de.kjEngine.graphics.TextureCube;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.TopLevelAccelerationStructure;
import de.kjEngine.graphics.VertexArray;
import de.kjEngine.graphics.VertexArrayElement;
import de.kjEngine.graphics.VertexBuffer;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.graphics.Graphics.API.Version;
import de.kjEngine.graphics.Query.Type;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.InterfaceBlockSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.PrimitiveType;
import de.kjEngine.graphics.shader.StructSource;
import de.kjEngine.graphics.shader.VaryingVariableSource;
import de.kjEngine.math.Real;

/**
 * @author konst
 *
 */
public class VulkanContext extends GraphicsContext {

	private static final long UINT64_MAX = 0xFFFFFFFFFFFFFFFFL;

	private static final boolean debug = true;

	private VkInstance instance;
	private long debugCallback;
	private VkPhysicalDevice physicalDevice;
	private int graphicsQueueFamilyIndex = -1;
	private int presentQueueFamilyIndex = -1;
	private VkDevice device;
	private PointerBuffer ppEnabledLayerNames;
	private PointerBuffer ppEnabledExtensionNames;
	private VkQueue graphicsQueue;
	private long surface;
	private VkQueue presentQueue;
	private Swapchain swapchain;
	private long renderpass;

	private VulkanSemaphoreChain semaphoreChain = new VulkanSemaphoreChain();

	private int currentImage;

	private long dynamicCommandPool;

	private static final ByteBuffer[] LAYERS = { memUTF8("VK_LAYER_LUNARG_standard_validation") };

	private boolean supportsRayTracing;

	public static class Swapchain {
		public long swapchain;
		public long[] images;
		public VkSurfaceFormatKHR format;
		public VkExtent2D extent;
		public long[] imageViews;
		public long[] framebuffers;

		public long depthImage;
		public long depthImageView;
		public long depthImageMemory;
	}

	private FrameBuffer screenbuffer;

	private long window;

	private DeviceCapabilities capabilities = new DeviceCapabilities();

	public VulkanContext() {
	}

	@Override
	public void init(long window) {
		if (!glfwVulkanSupported()) {
			throw new AssertionError("GLFW failed to find the Vulkan loader");
		}
		this.window = window;
		PointerBuffer requiredExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
		createInstance(requiredExtensions);
		setupDebugging();
		createSurface();
		createPhysicalDevice();
		createDevice();
		createGraphicsQueue();
		createPresentQueue();
		createSwapchain();
		getSwapchainImages();
		createSwapchainImageViews();
		createRenderPass();
		createFramebuffers();
		createScreenbuffer();
		createDynamicCommandPool();
	}

	private void recreateSwapchain() {
		err(vkDeviceWaitIdle(device));

		disposeSwapchain();

		createSwapchain();
		getSwapchainImages();
		createSwapchainImageViews();
		createRenderPass();
		createFramebuffers();
		createScreenbuffer();
	}

	private void disposeSwapchain() {
		vkDestroyRenderPass(device, renderpass, null);
		for (long framebuffer : swapchain.framebuffers) {
			vkDestroyFramebuffer(device, framebuffer, null);
		}
		for (int i = 0; i < swapchain.imageViews.length; i++) {
			vkDestroyImageView(device, swapchain.imageViews[i], null);
		}
		KHRSwapchain.vkDestroySwapchainKHR(device, swapchain.swapchain, null);
	}

	private void createDynamicCommandPool() {
		VkCommandPoolCreateInfo commandPoolCreateInfo = VkCommandPoolCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO).pNext(NULL);
		commandPoolCreateInfo.queueFamilyIndex(graphicsQueueFamilyIndex);
		commandPoolCreateInfo.flags(VK_COMMAND_POOL_CREATE_TRANSIENT_BIT | VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);

		LongBuffer pCommandpool = memAllocLong(1);
		err(vkCreateCommandPool(device, commandPoolCreateInfo, null, pCommandpool));
		dynamicCommandPool = pCommandpool.get(0);

		memFree(pCommandpool);
		commandPoolCreateInfo.free();
	}

	private void createScreenbuffer() {
		IntBuffer pWidth = memAllocInt(1), pHeight = memAllocInt(1);
		glfwGetWindowSize(window, pWidth, pHeight);

		VkClearValue clearColor = VkClearValue.calloc();
		clearColor.color().float32(0, 0f);
		clearColor.color().float32(1, 0f);
		clearColor.color().float32(2, 0f);
		clearColor.color().float32(3, 1f);

		InterfaceBlockSource fboSource = new InterfaceBlockSource();
		fboSource.getVariables().add(new VaryingVariableSource("vec4", "out_color", new HashSet<>()));

		int width = pWidth.get(0), height = pHeight.get(0);

		screenbuffer = new FrameBuffer(width, height, fboSource) {

			@Override
			public void dispose() {
			}

			@Override
			public Texture2D getColorAttachment(String attachment) {
				return new VulkanTexture2D(width, height, 1, TextureFormat.RGBA8, SamplingMode.LINEAR, WrappingMode.CLAMP, swapchain.images[currentImage], VK_NULL_HANDLE,
						new long[] { swapchain.imageViews[currentImage] }, swapchain.imageViews[currentImage], VK_NULL_HANDLE, swapchain.format.format(), VK_IMAGE_ASPECT_COLOR_BIT);
			}

			@Override
			public Texture2D getDepthAttachment() {
				return null;
			}

			@Override
			public byte[] getPixels(String attachment) {
				return null;
			}
		};
		memFree(pWidth);
		memFree(pHeight);
	}

	private void createRenderPass() {
		VkAttachmentDescription colorAttachment = VkAttachmentDescription.calloc().flags(0);
		colorAttachment.format(swapchain.format.format());
		colorAttachment.samples(VK_SAMPLE_COUNT_1_BIT);
		colorAttachment.loadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
		colorAttachment.storeOp(VK_ATTACHMENT_STORE_OP_STORE);
		colorAttachment.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
		colorAttachment.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
		colorAttachment.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
		colorAttachment.finalLayout(KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

		VkAttachmentReference colorAttachmentReference = VkAttachmentReference.calloc();
		colorAttachmentReference.attachment(0);
		colorAttachmentReference.layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

		VkSubpassDescription subpass = VkSubpassDescription.calloc().flags(0);
		subpass.pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
		subpass.colorAttachmentCount(1);
		VkAttachmentReference.Buffer attachmentRefs = VkAttachmentReference.calloc(1).put(0, colorAttachmentReference);
		subpass.pColorAttachments(attachmentRefs);
		subpass.pInputAttachments(null);
		subpass.pPreserveAttachments(null);
		subpass.pDepthStencilAttachment(null);

		VkRenderPassCreateInfo renderPassCreateInfo = VkRenderPassCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO).pNext(NULL).flags(0);
		VkAttachmentDescription.Buffer attachments = VkAttachmentDescription.calloc(1).put(0, colorAttachment);
		renderPassCreateInfo.pAttachments(attachments);

		VkSubpassDescription.Buffer subpasses = VkSubpassDescription.calloc(1).put(0, subpass);
		renderPassCreateInfo.pSubpasses(subpasses);
		renderPassCreateInfo.pDependencies(null);

		LongBuffer pRenderpass = memAllocLong(1);
		err(vkCreateRenderPass(device, renderPassCreateInfo, null, pRenderpass));
		renderpass = pRenderpass.get(0);

		subpass.free();
		colorAttachment.free();
		colorAttachmentReference.free();
		memFree(pRenderpass);
		attachments.free();
		subpasses.free();
		attachmentRefs.free();
	}

	private void createFramebuffers() {
		swapchain.framebuffers = new long[swapchain.images.length];
		for (int i = 0; i < swapchain.images.length; i++) {
			VkFramebufferCreateInfo framebufferCreateInfo = VkFramebufferCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO).pNext(NULL).flags(0);
			framebufferCreateInfo.renderPass(renderpass);
			LongBuffer pAttachments = (LongBuffer) memAllocLong(1).put(0, swapchain.imageViews[i]);
			framebufferCreateInfo.pAttachments(pAttachments);
			framebufferCreateInfo.width(swapchain.extent.width());
			framebufferCreateInfo.height(swapchain.extent.height());
			framebufferCreateInfo.layers(1);

			LongBuffer pFramebuffer = memAllocLong(1);
			err(vkCreateFramebuffer(device, framebufferCreateInfo, null, pFramebuffer));
			swapchain.framebuffers[i] = pFramebuffer.get(0);

			memFree(pFramebuffer);
			memFree(pAttachments);
			framebufferCreateInfo.free();
		}
	}

	private void createSwapchainImageViews() {
		swapchain.imageViews = new long[swapchain.images.length];

		for (int i = 0; i < swapchain.images.length; i++) {
			VkComponentMapping comps = VkComponentMapping.malloc();
			comps.r(VK_COMPONENT_SWIZZLE_IDENTITY);
			comps.g(VK_COMPONENT_SWIZZLE_IDENTITY);
			comps.b(VK_COMPONENT_SWIZZLE_IDENTITY);
			comps.a(VK_COMPONENT_SWIZZLE_IDENTITY);

			VkImageSubresourceRange subresourceRange = VkImageSubresourceRange.malloc();
			subresourceRange.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
			subresourceRange.baseMipLevel(0).levelCount(1);
			subresourceRange.baseArrayLayer(0).layerCount(1);

			VkImageViewCreateInfo imageViewCreateInfo = VkImageViewCreateInfo.malloc().sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO).pNext(NULL).flags(0).image(swapchain.images[i])
					.viewType(VK_IMAGE_VIEW_TYPE_2D).format(swapchain.format.format()).components(comps).subresourceRange(subresourceRange);

			LongBuffer pImageView = memAllocLong(1);
			vkCreateImageView(device, imageViewCreateInfo, null, pImageView);
			swapchain.imageViews[i] = pImageView.get(0);

			memFree(pImageView);
			imageViewCreateInfo.free();
			comps.free();
			subresourceRange.free();
		}
	}

	private void getSwapchainImages() {
		IntBuffer pImageCount = memAllocInt(1);
		KHRSwapchain.vkGetSwapchainImagesKHR(device, swapchain.swapchain, pImageCount, null);

		LongBuffer pImages = memAllocLong(pImageCount.get(0));
		KHRSwapchain.vkGetSwapchainImagesKHR(device, swapchain.swapchain, pImageCount, pImages);

		swapchain.images = new long[pImageCount.get(0)];
		for (int i = 0; i < swapchain.images.length; i++) {
			swapchain.images[i] = pImages.get(i);
		}

		memFree(pImageCount);
		memFree(pImages);
	}

	private void createSwapchain() {
		swapchain = new Swapchain();

		VkSurfaceCapabilitiesKHR surfaceCapabilities = VkSurfaceCapabilitiesKHR.malloc();
		err(KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, surfaceCapabilities));

		IntBuffer pFormatCount = memAllocInt(1);
		err(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, null));

		if (pFormatCount.get(0) == 0) {
			System.err.println("No surface formats found!");
			System.exit(1);
		}

		VkSurfaceFormatKHR.Buffer pFormats = VkSurfaceFormatKHR.malloc(pFormatCount.get(0));
		err(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, pFormats));

		IntBuffer pPresentModeCount = memAllocInt(1);
		err(KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, null));

		if (pPresentModeCount.get(0) == 0) {
			System.err.println("No present modes found!");
			System.exit(1);
		}

		IntBuffer pPresentModes = memAllocInt(pPresentModeCount.get(0));
		err(KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, pPresentModes));

		VkSurfaceFormatKHR surfaceFormat = null;
		int presentMode = -1;
		VkExtent2D extent = VkExtent2D.malloc();

		for (int i = 0; i < pFormatCount.get(0); i++) {
			VkSurfaceFormatKHR format = pFormats.get(i);
			if (format.format() == VK_FORMAT_B8G8R8A8_UNORM && format.colorSpace() == KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR) {
				surfaceFormat = format;
				break;
			}
		}
		if (surfaceFormat == null) {
			System.err.println("Could not find the best surface format!");
			System.out.println("format: UNDEFINED");
			surfaceFormat = pFormats.get(0);
		}

		for (int i = 0; i < pPresentModeCount.get(0); i++) {
			int mode = pPresentModes.get(i);
			if (mode == KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR) {
				presentMode = mode;
			}
		}
		if (presentMode == -1) {
			presentMode = KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR;
		}

		IntBuffer pWidth = memAllocInt(1);
		IntBuffer pHeight = memAllocInt(1);
		GLFW.glfwGetWindowSize(window, pWidth, pHeight);
		extent.width(Real.clamp(pWidth.get(0), surfaceCapabilities.minImageExtent().width(), surfaceCapabilities.maxImageExtent().width()));
		extent.height(Real.clamp(pHeight.get(0), surfaceCapabilities.minImageExtent().height(), surfaceCapabilities.maxImageExtent().height()));

		int imageCount = surfaceCapabilities.minImageCount() + 1;
		if (imageCount > surfaceCapabilities.maxImageCount()) {
			imageCount = surfaceCapabilities.maxImageCount();
		}

		VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.malloc();

		if (graphicsQueueFamilyIndex != presentQueueFamilyIndex) {
			IntBuffer pQueueFamilyIndices = memAllocInt(2);
			pQueueFamilyIndices.put(0, graphicsQueueFamilyIndex);
			pQueueFamilyIndices.put(1, presentQueueFamilyIndex);
			swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT).pQueueFamilyIndices(pQueueFamilyIndices);
			memFree(pQueueFamilyIndices);
		} else {
			swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
		}

		int preTransform;
		if ((surfaceCapabilities.supportedTransforms() & VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR) != 0) {
			preTransform = VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
		} else {
			preTransform = surfaceCapabilities.currentTransform();
		}

		swapchainCreateInfo.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR).pNext(NULL).flags(0).surface(surface).minImageCount(imageCount).imageFormat(surfaceFormat.format())
				.imageColorSpace(surfaceFormat.colorSpace()).imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT).preTransform(preTransform).imageArrayLayers(1)
				.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE).pQueueFamilyIndices(null).presentMode(presentMode).oldSwapchain(VK_NULL_HANDLE).clipped(true)
				.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
		swapchainCreateInfo.imageExtent().width(extent.width()).height(extent.height());

		LongBuffer pSwapchain = memAllocLong(1);
		err(KHRSwapchain.vkCreateSwapchainKHR(device, swapchainCreateInfo, null, pSwapchain));
		swapchain.swapchain = pSwapchain.get(0);

		swapchain.format = surfaceFormat;
		swapchain.extent = extent;

		memFree(pSwapchain);
		swapchainCreateInfo.free();
		memFree(pWidth);
		memFree(pHeight);
		memFree(pPresentModeCount);
		surfaceCapabilities.free();
		memFree(pFormatCount);
	}

	private void createPresentQueue() {
		PointerBuffer pQueue = memAllocPointer(1);
		vkGetDeviceQueue(device, presentQueueFamilyIndex, 0, pQueue);
		presentQueue = new VkQueue(pQueue.get(0), device);
		memFree(pQueue);
	}

	private void createGraphicsQueue() {
		PointerBuffer pQueue = memAllocPointer(1);
		vkGetDeviceQueue(device, graphicsQueueFamilyIndex, 0, pQueue);
		graphicsQueue = new VkQueue(pQueue.get(0), device);
		memFree(pQueue);
	}

	private void createSurface() {
		LongBuffer pSurface = memAllocLong(1);
		err(GLFWVulkan.glfwCreateWindowSurface(instance, window, null, pSurface));
		surface = pSurface.get(0);
		memFree(pSurface);
	}

	private void createDevice() {
		FloatBuffer pQueuePriorities = memAllocFloat(1);
		pQueuePriorities.put(1f);
		pQueuePriorities.flip();

		int queueCount = 1;

		VkDeviceQueueCreateInfo graphicsQueueCreateInfo = VkDeviceQueueCreateInfo.malloc().sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO).flags(0).pNext(NULL)
				.queueFamilyIndex(graphicsQueueFamilyIndex).pQueuePriorities(pQueuePriorities);

		VkDeviceQueueCreateInfo presentQueueCreateInfo = null;

		if (presentQueueFamilyIndex != graphicsQueueFamilyIndex) {
			presentQueueCreateInfo = VkDeviceQueueCreateInfo.malloc().sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO).flags(0).pNext(NULL).queueFamilyIndex(presentQueueFamilyIndex)
					.pQueuePriorities(pQueuePriorities);
			queueCount++;
		}

		VkDeviceQueueCreateInfo.Buffer queueCreateInfos = VkDeviceQueueCreateInfo.malloc(queueCount);
		queueCreateInfos.put(0, graphicsQueueCreateInfo);
		if (queueCount > 1) {
			queueCreateInfos.put(1, presentQueueCreateInfo);
		}

		VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.calloc();
		features.depthClamp(true);
		features.tessellationShader(true);
		
		String[] extensionStrings = new String[] { VK_KHR_SWAPCHAIN_EXTENSION_NAME, EXTDescriptorIndexing.VK_EXT_DESCRIPTOR_INDEXING_EXTENSION_NAME };

		PointerBuffer extensions = memAllocPointer(extensionStrings.length);
		for (int i = 0; i < extensionStrings.length; i++) {
			extensions.put(i, memUTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME));
		}

		PointerBuffer ppEnabledLayerNames = memAllocPointer(LAYERS.length);
		for (int i = 0; debug && i < LAYERS.length; i++)
			ppEnabledLayerNames.put(i, LAYERS[i]);

		VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.malloc().sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO).pNext(NULL).flags(0).pQueueCreateInfos(queueCreateInfos)
				.ppEnabledExtensionNames(extensions).ppEnabledLayerNames(ppEnabledLayerNames).pEnabledFeatures(features);

		PointerBuffer pDevice = memAllocPointer(1);
		err(vkCreateDevice(physicalDevice, deviceCreateInfo, null, pDevice));
		device = new VkDevice(pDevice.get(0), physicalDevice, deviceCreateInfo);

		queueCreateInfos.free();
		deviceCreateInfo.free();
		features.free();
		graphicsQueueCreateInfo.free();
		memFree(pQueuePriorities);
		memFree(ppEnabledLayerNames);
		for (int i = 0; i < extensionStrings.length; i++) {
			nmemFree(extensions.get(i));
		}
		memFree(extensions);
	}

	private void setupDebugging() {
		if (debug) {
			final VkDebugReportCallbackEXT debugCallback = new VkDebugReportCallbackEXT() {
				public int invoke(int flags, int objectType, long object, long location, int messageCode, long pLayerPrefix, long pMessage, long pUserData) {
					System.err.println("ERROR OCCURED: " + VkDebugReportCallbackEXT.getString(pMessage));
					return 0;
				}
			};
			VkDebugReportCallbackCreateInfoEXT dbgCreateInfo = VkDebugReportCallbackCreateInfoEXT.calloc().sType(VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT).pNext(NULL)
					.pfnCallback(debugCallback).pUserData(NULL).flags(VK_DEBUG_REPORT_ERROR_BIT_EXT | VK_DEBUG_REPORT_WARNING_BIT_EXT);
			LongBuffer pCallback = memAllocLong(1);
			err(vkCreateDebugReportCallbackEXT(instance, dbgCreateInfo, null, pCallback));
			this.debugCallback = pCallback.get(0);
			memFree(pCallback);
			dbgCreateInfo.free();
		}
	}

	private void createPhysicalDevice() {
		IntBuffer pCount = memAllocInt(1);

		err(vkEnumeratePhysicalDevices(instance, pCount, null));

		int count = pCount.get(0);
		if (count == 0) {
			System.err.println("No physical devices found!");
			System.exit(1);
		}

		PointerBuffer pPhysicalDevices = memAllocPointer(count);
		err(vkEnumeratePhysicalDevices(instance, pCount, pPhysicalDevices));

		for (int i = 0; i < count; i++) {
			long address = pPhysicalDevices.get(i);
			VkPhysicalDevice pd = new VkPhysicalDevice(address, instance);

			VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.malloc();
			vkGetPhysicalDeviceProperties(pd, properties);

			VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.malloc();
			vkGetPhysicalDeviceFeatures(pd, features);

			if (isDeviceSuitable(properties, features)) {
				physicalDevice = pd;

				initCapabilities(properties, features);

				vkEnumerateDeviceExtensionProperties(pd, (ByteBuffer) null, pCount, null);
				VkExtensionProperties.Buffer pProperties = VkExtensionProperties.calloc(pCount.get(0));
				vkEnumerateDeviceExtensionProperties(pd, (ByteBuffer) null, pCount, pProperties);
				for (int j = 0; j < pCount.get(0); j++) {
					String name = pProperties.get(j).extensionNameString();
					if ("KHR_ray_tracing".equals(name)) {
						supportsRayTracing = true;
						break;
					}
				}
				pProperties.free();
				break;
			}
		}

		memFree(pCount);

		if (physicalDevice == null) {
			System.err.println("No physical device is suitable!");
			System.exit(1);
		} else {
			getQueueFamilies(physicalDevice);
			if (graphicsQueueFamilyIndex == -1) {
				System.err.println("No graphics queue family has been found!");
				System.exit(1);
			}
		}
	}

	private void initCapabilities(VkPhysicalDeviceProperties properties, VkPhysicalDeviceFeatures features) {
		VkPhysicalDeviceLimits limits = properties.limits();

		capabilities.maxTexture1DSize = limits.maxImageDimension1D();
		capabilities.maxTexture2DSize = limits.maxImageDimension2D();
		capabilities.maxTexture3DSize = limits.maxImageDimension3D();
		capabilities.maxTextureLayerCount = limits.maxImageArrayLayers();

		capabilities.maxUniformBufferSize = limits.maxUniformBufferRange();
		capabilities.maxStorageBufferSize = limits.maxStorageBufferRange();

		capabilities.maxBoundDescriptorSetCount = limits.maxBoundDescriptorSets();
		capabilities.maxBoundTextureCount = limits.maxPerStageDescriptorSampledImages();
		capabilities.maxBoundUniformBufferCount = limits.maxPerStageDescriptorUniformBuffers();
		capabilities.maxBoundStorageBufferCount = limits.maxPerStageDescriptorStorageBuffers();
		capabilities.maxBoundImageCount = limits.maxPerStageDescriptorStorageImages();
		capabilities.maxDescriptorCount = limits.maxPerStageResources();

		capabilities.maxDescriptorSetTextureCount = limits.maxDescriptorSetSampledImages();
		capabilities.maxDescriptorSetUniformBufferCount = limits.maxDescriptorSetUniformBuffers();
		capabilities.maxDescriptorSetStorageBufferCount = limits.maxDescriptorSetStorageBuffers();
		capabilities.maxDescriptorSetImageCount = limits.maxDescriptorSetStorageImages();

		capabilities.maxVertexShaderInputCount = limits.maxVertexInputAttributes();
		capabilities.maxVertexShaderOutputCount = limits.maxVertexOutputComponents();

		capabilities.maxTesselationControlShaderInputCount = limits.maxTessellationControlPerVertexInputComponents();
		capabilities.maxTesselationControlShaderOutputCount = limits.maxTessellationControlPerVertexOutputComponents();

		capabilities.maxTesselationEvaluationShaderInputCount = limits.maxTessellationEvaluationInputComponents();
		capabilities.maxTesselationEvaluationShaderOutputCount = limits.maxTessellationEvaluationOutputComponents();

		capabilities.maxGeometryShaderInputCount = limits.maxGeometryInputComponents();
		capabilities.maxGeometryShaderOutputCount = limits.maxGeometryOutputComponents();

		capabilities.maxFragmentShaderInputCount = limits.maxFragmentInputComponents();
		capabilities.maxFragmentShaderOutputCount = limits.maxFragmentOutputAttachments();

		capabilities.maxTesselationPatchSize = limits.maxTessellationPatchSize();

		capabilities.maxGeometryOutputVertexCount = limits.maxGeometryOutputVertices();

		capabilities.maxComputeWorkGroupCountX = limits.maxComputeWorkGroupCount(0);
		capabilities.maxComputeWorkGroupCountY = limits.maxComputeWorkGroupCount(1);
		capabilities.maxComputeWorkGroupCountZ = limits.maxComputeWorkGroupCount(2);

		capabilities.maxComputeWorkGroupSizeX = limits.maxComputeWorkGroupSize(0);
		capabilities.maxComputeWorkGroupSizeY = limits.maxComputeWorkGroupSize(1);
		capabilities.maxComputeWorkGroupSizeZ = limits.maxComputeWorkGroupSize(2);

		capabilities.maxFramebufferWidth = limits.maxFramebufferWidth();
		capabilities.maxFramebufferHeight = limits.maxFramebufferHeight();
		capabilities.maxColorAttachmentCount = limits.maxColorAttachments();

		capabilities.tesselationShader = features.tessellationShader();
		capabilities.geometryShader = features.geometryShader();

		capabilities.depthClamp = features.depthClamp();
	}

	private void getQueueFamilies(VkPhysicalDevice pd) {
		IntBuffer pCount = memAllocInt(1);
		vkGetPhysicalDeviceQueueFamilyProperties(pd, pCount, null);

		VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(pCount.get(0));
		vkGetPhysicalDeviceQueueFamilyProperties(pd, pCount, queueFamilies);

		for (int i = 0; i < pCount.get(0); i++) {
			VkQueueFamilyProperties properties = queueFamilies.get(i);

			if (properties.queueCount() > 0) {
				if ((properties.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
					graphicsQueueFamilyIndex = i;
				}

				IntBuffer pSupported = memAllocInt(1);
				err(KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(pd, i, surface, pSupported));
				if (pSupported.get(0) != 0) {
					presentQueueFamilyIndex = i;
				}
				memFree(pSupported);
			}
		}

		memFree(pCount);
		queueFamilies.free();
	}

	private boolean isDeviceSuitable(VkPhysicalDeviceProperties properties, VkPhysicalDeviceFeatures features) {
		boolean b = true;
		b &= properties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU;
		b &= features.geometryShader();
		b &= features.tessellationShader();
		b &= features.samplerAnisotropy();

		features.free();
		properties.free();

		return b;
	}

	private void createInstance(PointerBuffer requiredExtensions) {
		VkApplicationInfo appInfo = VkApplicationInfo.calloc().sType(VK_STRUCTURE_TYPE_APPLICATION_INFO).pApplicationName(memUTF8("Vulkan App")).pEngineName(memUTF8(""))
				.apiVersion(VK_MAKE_VERSION(1, 0, 2));

		ByteBuffer VK_EXT_DEBUG_REPORT_EXTENSION = memUTF8(VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
		ppEnabledExtensionNames = memAllocPointer(requiredExtensions.limit() + 1);
		ppEnabledExtensionNames.put(requiredExtensions).put(VK_EXT_DEBUG_REPORT_EXTENSION).flip();

		ppEnabledLayerNames = memAllocPointer(LAYERS.length);
		for (int i = 0; debug && i < LAYERS.length; i++)
			ppEnabledLayerNames.put(LAYERS[i]);
		ppEnabledLayerNames.flip();

		VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.calloc().sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO).pNext(NULL).pApplicationInfo(appInfo)
				.ppEnabledExtensionNames(ppEnabledExtensionNames).ppEnabledLayerNames(ppEnabledLayerNames);
		PointerBuffer pInstance = memAllocPointer(1);

		err(vkCreateInstance(pCreateInfo, null, pInstance));
		long instance = pInstance.get(0);
		memFree(pInstance);

		this.instance = new VkInstance(instance, pCreateInfo);

		pCreateInfo.free();
		memFree(VK_EXT_DEBUG_REPORT_EXTENSION);
		memFree(appInfo.pApplicationName());
		memFree(appInfo.pEngineName());
		appInfo.free();
	}

	@Override
	public boolean pollImage() {
		long signal = semaphoreChain.push();
		
		boolean resized = false;

		IntBuffer pIndex = memAllocInt(1);
		int result = KHRSwapchain.vkAcquireNextImageKHR(device, swapchain.swapchain, UINT64_MAX, signal, VK_NULL_HANDLE, pIndex);
		if (result == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR || result == VK_SUBOPTIMAL_KHR) {
			recreateSwapchain();
			err(KHRSwapchain.vkAcquireNextImageKHR(device, swapchain.swapchain, UINT64_MAX, signal, VK_NULL_HANDLE, pIndex));
			
			resized = true;
		} else if (result != VK_SUCCESS) {
			err(result);
		}
		currentImage = pIndex.get(0);
		memFree(pIndex);
		
		return resized;
	}

	@Override
	public void swapBuffers() {
		VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc().sType(KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR).pNext(NULL);

		LongBuffer pWait = memAllocLong(1).put(0, semaphoreChain.peek());
		presentInfo.pWaitSemaphores(pWait);

		LongBuffer pSwapchain = memAllocLong(1).put(0, swapchain.swapchain);
		presentInfo.pSwapchains(pSwapchain);
		presentInfo.swapchainCount(1);

		IntBuffer pIndex = memAllocInt(1).put(0, currentImage);
		presentInfo.pImageIndices(pIndex);

		KHRSwapchain.vkQueuePresentKHR(presentQueue, presentInfo);

		memFree(pIndex);
		memFree(pSwapchain);
		memFree(pWait);
		presentInfo.free();

		vkDeviceWaitIdle(device);

		semaphoreChain.clear();
	}

	@Override
	public Graphics.API getApi() {
		return Graphics.API.VULKAN;
	}

	@Override
	public void dispose() {
		vkDeviceWaitIdle(device);

		semaphoreChain.dispose();

		vkDestroyCommandPool(Graphics.getVkContext().getDevice(), dynamicCommandPool, null);
		disposeSwapchain();
		KHRSurface.vkDestroySurfaceKHR(instance, surface, null);
		vkDestroyDevice(device, null);
		vkDestroyDebugReportCallbackEXT(instance, debugCallback, null);
		vkDestroyInstance(instance, null);
	}

	public static boolean isDebug() {
		return debug;
	}

	public VkInstance getInstance() {
		return instance;
	}

	public VkPhysicalDevice getPhysicalDevice() {
		return physicalDevice;
	}

	public int getGraphicsQueueFamilyIndex() {
		return graphicsQueueFamilyIndex;
	}

	public int getPresentQueueFamilyIndex() {
		return presentQueueFamilyIndex;
	}

	public VkDevice getDevice() {
		return device;
	}

	public VkQueue getGraphicsQueue() {
		return graphicsQueue;
	}

	public long getSurface() {
		return surface;
	}

	public VkQueue getPresentQueue() {
		return presentQueue;
	}

	public Swapchain getSwapchain() {
		return swapchain;
	}

	public long getRenderpass() {
		return renderpass;
	}

	public int getCurrentImage() {
		return currentImage;
	}

	@Override
	public FrameBuffer getScreenBuffer() {
		return screenbuffer;
	}

	public long getDynamicCommandPool() {
		return dynamicCommandPool;
	}

	@Override
	public void flush() {
	}

	@Override
	public Version getVersion() {
		return new Version();
	}

	@Override
	public DeviceVendor getDeviceVendor() {
		return DeviceVendor.NVIDIA;
	}

	@Override
	public GraphicsPipeline createGraphicsPipeline(PipelineSource source) {
		return new VulkanGraphicsPipeline(source);
	}

	@Override
	public ComputePipeline createComputePipeline(PipelineSource source) {
		return new VulkanComputePipeline(source);
	}

	@Override
	public VertexBuffer createVertexBuffer(float[] data) {
		return new VulkanVertexBuffer(data);
	}

	@Override
	public IndexBuffer createIndexBuffer(int[] data) {
		return new VulkanIndexBuffer(data);
	}

	@Override
	public VertexArray createVertexArray(VertexArrayElement[] layout) {
		return new VulkanVertexArray();
	}

	@Override
	public CommandBuffer createCommandBuffer(int flags) {
		return new VulkanCommandBuffer(true);
	}

	@Override
	public ShaderBuffer createUniformBuffer(BufferSource source, List<StructSource> definedStructs, int flags) {
		return new VulkanUniformBuffer(source, definedStructs, flags);
	}

	@Override
	public ShaderBuffer createStorageBuffer(BufferSource source, List<StructSource> definedStructs, int flags) {
		return new VulkanStorageBuffer(source, definedStructs, flags);
	}

	@Override
	public Texture2D createTexture2D(Texture2DData data) {
		return new VulkanTexture2D(data);
	}

	@Override
	public Texture1D createTexture1D(int width, int levels, Texture1DDataProvider data, TextureFormat format, SamplingMode samplingMode, WrappingMode wrappingMode) {
		return null;
	}

	@Override
	public TextureCube createTextureCube(int width, int height, byte[][] faces, TextureFormat format) {
		return null;
	}

	@Override
	public DescriptorSet createDescriptorSet(DescriptorSetSource source) {
		return new VulkanDescriptorSet(source);
	}

	@Override
	public FrameBuffer createFramebuffer(int width, int height, InterfaceBlockSource source) {
		return new VulkanFrameBuffer(width, height, source);
	}

	@Override
	public Query createQuery(Type type) {
		return new VulkanQuery(type);
	}

	@Override
	public NumSamplesPassedQuery createNumSamplesPassedQuery() {
		return new VulkanQuery(Query.Type.NUM_SAMPLES_PASSED);
	}

	@Override
	public AnySamplesPassedQuery createAnySamplesPassedQuery() {
		return new VulkanQuery(Query.Type.ANY_SAMPLES_PASSED);
	}

	@Override
	public Texture3D createTexture3D(Texture3DData data) {
		return null;
	}

	/**
	 * @return the semaphoreChain
	 */
	public VulkanSemaphoreChain getSemaphoreChain() {
		return semaphoreChain;
	}

	@Override
	public void finish() {
		vkDeviceWaitIdle(device);
	}

	@Override
	public RayTracingPipeline createRayTracingPipeline(PipelineSource source) {
		if (supportsRayTracing) {

		}
		return new EmulatedVulkanRayTracingPipeline(source);
	}

	@Override
	public TopLevelAccelerationStructure createTopLevelAccelerationStructure(int entryCount) {
		if (supportsRayTracing) {

		}
		return new EmulatedVulkanTopLevelAccelerationStructure(entryCount);
	}

	@Override
	public BottomLevelAccelerationStructure createBottomLevelAccelerationStructure(ShaderBuffer source) {
		if (supportsRayTracing) {

		}
		return new EmulatedVulkanBottomLevelAccelerationStructure(null, null, PrimitiveType.TRIANGLE_LIST);
	}

	@Override
	public DeviceCapabilities getCapabilities() {
		return capabilities;
	}

	public long getCurrentFramebuffer() {
		return swapchain.framebuffers[currentImage];
	}

	public boolean supportsRayTracing() {
		return supportsRayTracing;
	}
}
