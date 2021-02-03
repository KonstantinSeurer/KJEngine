/**
 * 
 */
package de.kjEngine.scene.animation;

import de.kjEngine.scene.SceneComponent;
import de.kjEngine.scene.TransformComponent;

/**
 * @author konst
 *
 */
public class TransformAnimatorComponent extends SceneComponent<TransformComponent<?, TransformComponent<?, ?>>, TransformAnimatorComponent> {

	private TransformAnimator animator = new TransformAnimator();

	public TransformAnimatorComponent() {
		super(EARLY);
	}

	@Override
	public void update(float delta) {
		if (animator.isRunning()) {
			parent.transform.set(animator.update(delta));
		}
	}

	/**
	 * @return the animator
	 */
	public TransformAnimator getAnimator() {
		return animator;
	}
}
