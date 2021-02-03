/**
 * 
 */
package de.kjEngine.scene.renderer.deferred;

import java.util.HashSet;

import de.kjEngine.graphics.CommandBuffer;
import de.kjEngine.graphics.FrameBuffer;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.shader.InterfaceBlockSource;
import de.kjEngine.graphics.shader.VaryingVariableSource;
import de.kjEngine.renderer.RenderList;
import de.kjEngine.renderer.Stage;

/**
 * @author konst
 *
 */
public abstract class DeferredPbrRenderer extends Stage {

	public static final InterfaceBlockSource FRAGMENT_SHADER_OUTPUT = new InterfaceBlockSource();
	static {
		FRAGMENT_SHADER_OUTPUT.getVariables().add(new VaryingVariableSource("vec3", "albedo", new HashSet<>()));
		FRAGMENT_SHADER_OUTPUT.getVariables().add(new VaryingVariableSource("vec3", "normal", new HashSet<>()));
		FRAGMENT_SHADER_OUTPUT.getVariables().add(new VaryingVariableSource("vec3", "subsurface", new HashSet<>()));
		FRAGMENT_SHADER_OUTPUT.getVariables().add(new VaryingVariableSource("vec3", "emission", new HashSet<>()));
		FRAGMENT_SHADER_OUTPUT.getVariables().add(new VaryingVariableSource("float", "roughness", new HashSet<>()));
		FRAGMENT_SHADER_OUTPUT.getVariables().add(new VaryingVariableSource("float", "metalness", new HashSet<>()));
		FRAGMENT_SHADER_OUTPUT.getVariables().add(new VaryingVariableSource("float", "ao", new HashSet<>()));
		FRAGMENT_SHADER_OUTPUT.setBoolean("blend", false);
		FRAGMENT_SHADER_OUTPUT.setBoolean("depth", true);
	}

	protected FrameBuffer frameBuffer;
	private Texture2D albedo, roughness, metalness, emission, subsurface, normal, depth, ao;

	public DeferredPbrRenderer(InputProvider inputProvider) {
		super(inputProvider);
	}

	@Override
	public void dispose() {
		frameBuffer.dispose();
	}

	@Override
	protected void renderImplementation(RenderList renderList, CommandBuffer cb) {
		if (albedo == null || roughness == null || metalness == null || emission == null || subsurface == null || normal == null || depth == null) {
			cb.bindFrameBuffer(frameBuffer);
			cb.clearFrameBuffer(frameBuffer);
			cb.unbindFrameBuffer(frameBuffer);
		}
		if (albedo != null) {
			cb.copyTexture2D(albedo, frameBuffer.getColorAttachment("albedo"));
		}
		if (roughness != null) {
			cb.copyTexture2D(roughness, frameBuffer.getColorAttachment("roughness"));
		}
		if (metalness != null) {
			cb.copyTexture2D(metalness, frameBuffer.getColorAttachment("metalness"));
		}
		if (emission != null) {
			cb.copyTexture2D(emission, frameBuffer.getColorAttachment("emission"));
		}
		if (subsurface != null) {
			cb.copyTexture2D(subsurface, frameBuffer.getColorAttachment("subsurface"));
		}
		if (normal != null) {
			cb.copyTexture2D(normal, frameBuffer.getColorAttachment("normal"));
		}
		if (ao != null) {
			cb.copyTexture2D(ao, frameBuffer.getColorAttachment("ao"));
		}
		if (depth != null) {
			cb.copyTexture2D(depth, frameBuffer.getDepthAttachment());
		}
	}

	@Override
	protected void linkImplementation() {
		albedo = input.get("albedo");
		roughness = input.get("roughness");
		metalness = input.get("metalness");
		emission = input.get("emission");
		subsurface = input.get("subsurface");
		normal = input.get("normal");
		depth = input.get("depth");
		ao = input.get("ao");
		
		addFrameBufferToOutput(frameBuffer);
	}

	@Override
	protected void resizeImplementation(int width, int height) {
		if (frameBuffer != null) {
			frameBuffer.dispose();
		}
		frameBuffer = Graphics.createFramebuffer(width, height, FRAGMENT_SHADER_OUTPUT);
	}
}
