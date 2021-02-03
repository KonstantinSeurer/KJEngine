/**
 * 
 */
package de.kjEngine.scene;

import java.util.ArrayList;
import java.util.Arrays;

import de.kjEngine.component.Container.RenderImplementation;
import de.kjEngine.graphics.ArrayAccessor;
import de.kjEngine.graphics.Descriptor.Type;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.BufferSource.Layout;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.VariableSource;
import de.kjEngine.math.Mat4;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class SceneTransformBuffer implements RenderImplementation<Scene> {

	public static final int MAX_ENTITY_COUNT = 100000;
	public static final BufferSource BUFFER_SOURCE = new BufferSource("data", Type.STORAGE_BUFFER, Layout.PACKED, Arrays.asList(new VariableSource("mat4[" + MAX_ENTITY_COUNT + "]", "transforms")));
	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("transforms", Arrays.asList(BUFFER_SOURCE));

	private ShaderBuffer ssbo;
	private DescriptorSet descriptorSet;

	private TransformComponent<?, ?>[] staticLayout = new TransformComponent[MAX_ENTITY_COUNT];
	private int maxStaticLayoutEntry = -1;
	private int lowestBubble = 0;

	private Mat4 globalTransformBuffer = new Mat4();

	public final Array<TransformComponent<?, ?>> staticEntityQueue = new Array<>();
	public final Array<TransformComponent<?, ?>> dynamicEntityQueue = new Array<>();

	private Array<ModelComponent> models = new Array<>();

	public SceneTransformBuffer() {
		ssbo = Graphics.createStorageBuffer(BUFFER_SOURCE, new ArrayList<>(), ShaderBuffer.FLAG_NONE);

		descriptorSet = Graphics.createDescriptorSet(DESCRIPTOR_SET_SOURCE);
		descriptorSet.set("data", ssbo);
		descriptorSet.update();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors(Scene scene) {
		ArrayAccessor accessor = ssbo.getAccessor().getArray("transforms");

		// add new static entities
		for (int i = 0; i < staticEntityQueue.length(); i++) {
			TransformComponent<?, ?> e = staticEntityQueue.get(i);

			models.clear(false);
			e.getAll(ModelComponent.class, models);

			for (int j = 0; j < models.length(); j++) {
				ModelComponent model = models.get(j);
				int baseIndex = model.transformBufferGPUImplementation.transformIndex;
				for (int k = 0; k < model.getTransforms().length; k++) {
					staticLayout[baseIndex + k] = e;
					accessor.set(baseIndex + k, Mat4.mul(e.transform.globalTransform, model.getTransforms()[k], globalTransformBuffer));
				}
			}
		}

		// store dynamic entities
		for (int i = 0; i < dynamicEntityQueue.length(); i++) {
			TransformComponent<?, ?> e = dynamicEntityQueue.get(i);

			models.clear(false);
			e.getAll(ModelComponent.class, models);

			for (int j = 0; j < models.length(); j++) {
				ModelComponent model = models.get(j);
				int baseIndex = model.transformBufferGPUImplementation.transformIndex;
				for (int k = 0; k < model.getTransforms().length; k++) {
					accessor.set(baseIndex + k, Mat4.mul(e.transform.globalTransform, model.getTransforms()[k], globalTransformBuffer));
				}
			}
		}

		// TODO: remove static entities

		staticEntityQueue.clear(true);
		dynamicEntityQueue.clear(true);

		ssbo.update();
	}

	@Override
	public void render(Scene scene) {
		for (int i = 0; i < staticEntityQueue.length(); i++) {
			TransformComponent<?, ?> e = staticEntityQueue.get(i);

			models.clear(false);
			e.getAll(ModelComponent.class, models);

			for (int modelIndex = 0; modelIndex < models.length(); modelIndex++) {
				ModelComponent model = models.get(modelIndex);
				int requiredSize = model.getTransforms().length;

				searchLoop: for (int j = lowestBubble; j < MAX_ENTITY_COUNT; j++) {
					if (staticLayout[j] == null) {
						for (int k = 1; k < requiredSize; k++) {
							if (staticLayout[j + k] != null) {
								continue searchLoop;
							}
						}
						model.transformBufferGPUImplementation.transformIndex = j;
						maxStaticLayoutEntry = Math.max(maxStaticLayoutEntry, j + requiredSize - 1);
						break;
					}
				}
			}
		}

		// store dynamic entities
		int index = maxStaticLayoutEntry + 1;
		for (int i = 0; i < dynamicEntityQueue.length(); i++) {
			TransformComponent<?, ?> e = dynamicEntityQueue.get(i);

			models.clear(false);
			e.getAll(ModelComponent.class, models);

			for (int j = 0; j < models.length(); j++) {
				models.get(j).transformBufferGPUImplementation.transformIndex = index;
				index += models.get(j).getTransforms().length;
			}
		}
	}

	/**
	 * @return the ssbo
	 */
	public ShaderBuffer getSsbo() {
		return ssbo;
	}

	/**
	 * @return the descriptorSet
	 */
	public DescriptorSet getDescriptorSet() {
		return descriptorSet;
	}
}
