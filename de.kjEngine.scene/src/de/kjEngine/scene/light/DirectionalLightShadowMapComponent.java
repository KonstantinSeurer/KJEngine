/**
 * 
 */
package de.kjEngine.scene.light;

import de.kjEngine.math.Mat3;
import de.kjEngine.math.Vec3;
import de.kjEngine.scene.camera.OrthographicFrustum;

/**
 * @author konst
 *
 */
public class DirectionalLightShadowMapComponent extends ShadowMapComponent<DirectionalLightComponent> {

	private float size;

	public DirectionalLightShadowMapComponent(int resolution, float size) {
		super(new OrthographicFrustum(), resolution);
		setSize(size);
	}

	@Override
	public void update(float delta) {
		Mat3 lightDirection = new Mat3();

		Vec3 dir = Vec3.create(parent.direction);
		dir.normalise();

		lightDirection.setZ(dir);

		if (dir.y > 0.99f || dir.y < -0.99f) {
			lightDirection.setX(Vec3.cross(dir, Vec3.Z, null).normalise());
		} else {
			lightDirection.setX(Vec3.cross(dir, Vec3.Y, null).normalise());
		}

		lightDirection.setY(Vec3.cross(dir, lightDirection.getX(), null).normalise());
		
		transform.rotation.setFromMatrix(lightDirection);
		
		super.update(delta);
	}

	/**
	 * @return the size
	 */
	public float getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(float size) {
		this.size = size;
		((OrthographicFrustum) camera.getFrustum()).setSize(size, size, size);
	}
}
