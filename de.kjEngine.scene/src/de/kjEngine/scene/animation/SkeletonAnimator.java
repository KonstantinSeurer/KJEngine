/**
 * 
 */
package de.kjEngine.scene.animation;

import java.util.HashMap;
import java.util.Map;

import de.kjEngine.math.Transform;
import de.kjEngine.scene.animation.Skeleton.Joint;

/**
 * @author konst
 *
 */
public class SkeletonAnimator {
	
	private Map<String, TransformAnimator> jointAnimators = new HashMap<>();
	private Skeleton target;

	public SkeletonAnimator(Skeleton target) {
		this.target = target;
	}
	
	public SkeletonAnimator start(Map<String, TransformAnimation> jointAnimations, boolean loop) {
		for (String name : jointAnimations.keySet()) {
			TransformAnimator animator = jointAnimators.get(name);
			if (animator == null) {
				animator = new TransformAnimator();
				jointAnimators.put(name, animator);
			}
			animator.start(jointAnimations.get(name), loop);
		}
		return this;
	}
	
	public SkeletonAnimator update(float delta) {
		for (String name : jointAnimators.keySet()) {
			Transform transform = jointAnimators.get(name).update(delta);
			Joint joint = target.getJoint(name);
			if (joint != null) {
				joint.getTransform().set(transform);
			}
		}
		return this;
	}
}
