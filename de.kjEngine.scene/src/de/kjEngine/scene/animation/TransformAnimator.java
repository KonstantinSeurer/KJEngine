/**
 * 
 */
package de.kjEngine.scene.animation;

import de.kjEngine.math.Quat;
import de.kjEngine.math.Transform;
import de.kjEngine.math.Vec3;

/**
 * @author konst
 *
 */
public class TransformAnimator {

	private Transform transform = new Transform();
	private TransformAnimation currentAnimation;
	private int currentFrame;
	private float currentFrameProgress;
	private boolean looping;
	private boolean running;

	public TransformAnimator() {
	}

	public void start(TransformAnimation animation, boolean loop) {
		currentAnimation = animation;
		currentFrame = 0;
		currentFrameProgress = 0f;
		looping = loop;
		running = true;
	}

	public Transform update(float delta) {
		if (!running) {
			return transform;
		}

		float currentFrameLength = currentAnimation.frameLengths[currentFrame];

		if (currentFrameProgress > currentFrameLength) {
			currentFrameProgress -= currentFrameLength;
			currentFrame++;
		}

		int nextFrame = currentFrame + 1;

		if (nextFrame == currentAnimation.transforms.length) {
			if (looping) {
				currentFrame = 0;
				nextFrame = 1;
			} else {
				nextFrame = currentFrame;
				stop();
			}
		}

		float progress = currentFrameProgress / currentFrameLength;

		Vec3.interpolate(currentAnimation.transforms[currentFrame].position, currentAnimation.transforms[nextFrame].position, progress, transform.position);
		Vec3.interpolate(currentAnimation.transforms[currentFrame].scale, currentAnimation.transforms[nextFrame].scale, progress, transform.scale);

		Quat.interpolate(currentAnimation.transforms[currentFrame].rotation, currentAnimation.transforms[nextFrame].rotation, progress, transform.rotation);
		
		currentFrameProgress += delta;
		
		return transform;
	}

	public void stop() {
		running = false;
	}

	/**
	 * @return the looping
	 */
	public boolean isLooping() {
		return looping;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @return the transform
	 */
	public Transform getTransform() {
		return transform;
	}
}
