/**
 * 
 */
package de.kjEngine.scene.animation;

import de.kjEngine.scene.material.Material;
import de.kjEngine.util.Timer;

/**
 * @author konst
 *
 */
public class MaterialAnimator {
	
	private MaterialAnimation currentAnimation;
	private int currentFrame;
	private Timer timer = new Timer();
	private boolean looping;
	private boolean running;

	public MaterialAnimator() {
	}
	
	public void start(MaterialAnimation animation, boolean loop) {
		currentAnimation = animation;
		currentFrame = 0;
		timer.reset();
		timer.setDelay(animation.delay);
		looping = loop;
		running = true;
	}
	
	public Material update() {
		if (currentAnimation == null) {
			return null;
		}
		if (running) {
			if (timer.time()) {
				currentFrame++;
				if (currentFrame >= currentAnimation.materials.length) {
					if (looping) {
						currentFrame = 0;
					} else {
						currentFrame--;
						running = false;
					}
				}
			}
		}
		return currentAnimation.materials[currentFrame];
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
}
