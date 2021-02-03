/**
 * 
 */
package de.kjEngine.scene.camera;

import java.util.HashSet;
import java.util.Set;

import de.kjEngine.component.Container.RenderImplementation;
import de.kjEngine.graphics.ArrayAccessor;
import de.kjEngine.graphics.Descriptor.Type;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.BufferSource.Layout;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.math.Transform;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.SceneEntityMap;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class SceneTransformIndexBuffer implements RenderImplementation<Scene> {

	public static final ID ID = new ID(Scene.class, "transform_index_buffer");

	static {
		Registry.registerProvider(ID, new Provider() {

			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new SceneTransformIndexBuffer();
			}
		});
		Set<ID> dependencies = new HashSet<>();
		dependencies.add(Scene.ENTITY_MAP);
		dependencies.add(Scene.TRANSFORM_BUFFER);
		dependencies.add(CameraComponentTransformIndexBuffer.ID);
		Registry.registerDependency(ID, dependencies);
	}

	public static final BufferSource SSBO_SOURCE = new BufferSource("indices", Type.STORAGE_BUFFER, Layout.PACKED);
	static {
		SSBO_SOURCE.addMember("int[100000]", "indices");
	}

	public static final DescriptorSetSource DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("indices");
	static {
		DESCRIPTOR_SET_SOURCE.addDescriptor(SSBO_SOURCE);
	}

	public final Array<CameraComponentTransformIndexBuffer> buffers = new Array<>();

	public SceneTransformIndexBuffer() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void render(Scene scene) {
		int bufferCount = buffers.length();
		CameraComponentTransformIndexBuffer[] buffers = this.buffers.get(new CameraComponentTransformIndexBuffer[this.buffers.length()]);

		SceneEntityMap entityMap = scene.getRenderImplementation(SceneEntityMap.class);

		Frustum[] frustums = new Frustum[bufferCount];
		ArrayAccessor[] accessors = new ArrayAccessor[bufferCount];

		for (int bufferIndex = 0; bufferIndex < bufferCount; bufferIndex++) {
			CameraComponentTransformIndexBuffer b = buffers[bufferIndex];

			frustums[bufferIndex] = b.camera.getFrustum();

			b.descriptorSet.swap();

			b.indexSsbo.swap();
			accessors[bufferIndex] = b.indexSsbo.getCurrent().getAccessor().getArray("indices");
		}

		Array<ModelComponent> models = new Array<>();

		int[] baseIndices = new int[bufferCount];
		int[] startingBaseIndices = new int[bufferCount];

		for (int bufferIndex = 0; bufferIndex < bufferCount; bufferIndex++) {
			buffers[bufferIndex].batchSizes = new int[entityMap.models.length()];
		}

		for (int entryIndex = 0; entryIndex < entityMap.models.length(); entryIndex++) {
			Array<Entity> batch = entityMap.entities.get(entryIndex);
			Model m = entityMap.models.get(entryIndex);
			float maxD = m.mesh.maxD;

			for (int i = 0; i < bufferCount; i++) {
				startingBaseIndices[i] = baseIndices[i];
			}

			int batchSize = batch.length();
			for (int i = 0; i < batchSize; i++) {
				Entity e = batch.get(i);

				models.clear(false);
				e.getAllUnsafe(ModelComponent.class, models);

				for (int k = 0; k < models.length(); k++) {
					ModelComponent model = models.get(k);
					if (model.getModel() == m) {
						Transform transform = e.transform;
						float r = maxD * transform.scale.max();
						Vec3 pos = transform.getGlobalPosition();
						int transformIndex = model.transformBufferGPUImplementation.transformIndex;

						for (int bufferIndex = 0; bufferIndex < bufferCount; bufferIndex++) {
							if (frustums[bufferIndex].intersectsSphere(pos, r)) {
								accessors[bufferIndex].seti(baseIndices[bufferIndex]++, transformIndex);
							}
						}
					}
				}
			}

			for (int bufferIndex = 0; bufferIndex < bufferCount; bufferIndex++) {
				buffers[bufferIndex].batchSizes[entryIndex] = baseIndices[bufferIndex] - startingBaseIndices[bufferIndex];
			}
		}

		for (int bufferIndex = 0; bufferIndex < bufferCount; bufferIndex++) {
			buffers[bufferIndex].indexSsbo.getCurrent().update();
		}
	}

	@Override
	public void updateDescriptors(Scene scene) {
		this.buffers.clear(false);
	}

	public DescriptorSet getDescriptorSet(CameraComponent cam) {
		for (int i = 0; i < buffers.length(); i++) {
			CameraComponentTransformIndexBuffer buffer = buffers.get(i);
			if (buffer.camera == cam) {
				return buffer.descriptorSet.getCurrent();
			}
		}
		return null;
	}

	public int getBatchSize(CameraComponent cam, int modelIndex) {
		for (int i = 0; i < buffers.length(); i++) {
			CameraComponentTransformIndexBuffer buffer = buffers.get(i);
			if (buffer.camera == cam) {
				return buffer.batchSizes[modelIndex];
			}
		}
		return 0;
	}
}
