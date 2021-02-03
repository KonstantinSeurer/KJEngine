/**
 * 
 */
package de.kjEngine.scene.camera;

import de.kjEngine.ui.Window;

/**
 * @author konst
 *
 */
public class PerspectiveCameraComponent extends CameraComponent {
	
	public PerspectiveCameraComponent() {
		super(new PerspectiveFrustum((float) Math.toRadians(70.0), Window.getAspect(), 0.2f, 300f));
	}

	@Override
	public PerspectiveFrustum getFrustum() {
		return (PerspectiveFrustum) super.getFrustum();
	}
}
