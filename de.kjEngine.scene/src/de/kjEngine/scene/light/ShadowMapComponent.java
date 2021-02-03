/**
 * 
 */
package de.kjEngine.scene.light;

import de.kjEngine.scene.TransformComponent;
import de.kjEngine.scene.camera.CameraComponent;
import de.kjEngine.scene.camera.Frustum;

/**
 * @author konst
 *
 */
public class ShadowMapComponent<ParentType extends LightComponent<?>> extends TransformComponent<ParentType, ShadowMapComponent<ParentType>> {

	public final CameraComponent camera;
	public final int resolution;

	public ShadowMapComponent(Frustum frustum, int resolution) {
		super(true);
		this.resolution = resolution;
		camera = new CameraComponent(frustum);
		add(camera);
	}

	@Override
	public void init() {
		super.init();
		if (parent.getParent() != null) {
			transform.parent = parent.getParent().transform;
		}
	}
}
