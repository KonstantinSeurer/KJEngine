/**
 * 
 */
package de.kjEngine.scene.camera;

/**
 * @author konst
 *
 */
public class OrthographicCameraComponent extends CameraComponent {
	
	public OrthographicCameraComponent() {
		super(new OrthographicFrustum());
	}

	@Override
	public OrthographicFrustum getFrustum() {
		return (OrthographicFrustum) super.getFrustum();
	}
}
