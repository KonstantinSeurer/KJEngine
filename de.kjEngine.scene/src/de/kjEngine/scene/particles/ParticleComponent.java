/**
 * 
 */
package de.kjEngine.scene.particles;

import de.kjEngine.math.Quat;
import de.kjEngine.scene.SceneComponent;
import de.kjEngine.scene.TransformComponent;

/**
 * @author konst
 *
 */
public class ParticleComponent extends SceneComponent<TransformComponent<?, TransformComponent<?, ?>>, ParticleComponent> {

	private float duration;
	private float rotation;

	public ParticleComponent(float duration) {
		super(EARLY);
		this.duration = duration;
	}

	public ParticleComponent(float rotation, float duration) {
		super(EARLY);
		this.rotation = rotation;
		this.duration = duration;
	}

	@Override
	public void update(float delta) {
		if (duration < 0f) {
			parent.getParent().remove(parent);
		}

		if (container.camera == null) {
			return;
		}

		parent.transform.rotation.setIdentity();
		parent.transform.rotation.rotateZ(rotation);
		Quat.mul(parent.transform.rotation, container.camera.getParent().transform.rotation, parent.transform.rotation);

		duration -= delta;
	}

	/**
	 * @return the rotation
	 */
	public float getRotation() {
		return rotation;
	}

	/**
	 * @param rotation the rotation to set
	 */
	public ParticleComponent setRotation(float rotation) {
		this.rotation = rotation;
		return this;
	}
}
