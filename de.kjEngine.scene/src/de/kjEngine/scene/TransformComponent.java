/**
 * 
 */
package de.kjEngine.scene;

import de.kjEngine.math.Transform;
import de.kjEngine.math.Vec3;

/**
 * @author konst
 *
 */
public class TransformComponent<ParentType extends SceneComponent<?, ?>, ComponentType extends TransformComponent<ParentType, ?>> extends SceneComponent<ParentType, ComponentType> {
	
	public Transform transform = new Transform();
	
	public EntityTransformBuffer transformBufferGPUImplementation;

	public TransformComponent(boolean dynamic) {
		this(dynamic, Vec3.create());
	}

	public TransformComponent(boolean dynamic, Vec3 position) {
		this(dynamic, position, Vec3.scale(1f));
	}

	public TransformComponent(boolean dynamic, Vec3 position, Vec3 scale) {
		super(dynamic ? LATE : 0);
		transform.position.set(position);
		transform.scale.set(scale);
		transformBufferGPUImplementation = getRenderImplementation(EntityTransformBuffer.class);
	}

	@Override
	public void update(float delta) {
		transform.update();
	}

	@Override
	public void init() {
		super.init();
		transform.update();
	}

	public ComponentType add(TransformComponent<? super ComponentType, ?> c) {
		c.transform.parent = transform;
		return super.add(c);
	}
}
