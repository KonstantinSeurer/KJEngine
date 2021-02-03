package de.kjEngine.scene.camera;

import de.kjEngine.math.Vec2;
import de.kjEngine.scene.SceneComponent;
import de.kjEngine.scene.TransformComponent;
import de.kjEngine.ui.Window;
import de.kjEngine.ui.event.EventAdapter;
import de.kjEngine.ui.event.MouseMoveEvent;

public class FirstPersonCameraController extends SceneComponent<TransformComponent<?, ?>, FirstPersonCameraController> {
	
	public float hSpeed = 4f, rSpeed = 4f, ySpeed = hSpeed;
	
	private Vec2 rotation = Vec2.create();

	public FirstPersonCameraController() {
		super(EARLY);
		
		Window.addEventListener(new EventAdapter() {

			@Override
			public void mouseMoved(MouseMoveEvent e) {
				if (Window.isMousePressed(0)) {
					rotation.x += rSpeed * (e.y - e.prevY) / Window.getHeight();
					rotation.y -= rSpeed * (e.x - e.prevX) / Window.getWidth();
					e.handle();
				}
			}
		});
	}

	@Override
	public void update(float delta) {
		if(Window.isKeyPressed(Window.KEY_W)) move(hSpeed * delta, 0f);
		if(Window.isKeyPressed(Window.KEY_S)) move(-hSpeed * delta, 0f);
		
		if(Window.isKeyPressed(Window.KEY_A)) {
			rotation.y -= Math.PI * 0.5;
			move(hSpeed * delta, 0f);
			rotation.y += Math.PI * 0.5;
		}
		if(Window.isKeyPressed(Window.KEY_D)) {
			rotation.y += Math.PI * 0.5;
			move(hSpeed * delta, 0f);
			rotation.y -= Math.PI * 0.5;
		}
		
		if(Window.isKeyPressed(Window.KEY_SPACE)) move(0f, ySpeed * delta);
		if(Window.isKeyPressed(Window.KEY_LSHIFT)) move(0f, -ySpeed * delta);
		
		if(Window.isKeyPressed(Window.KEY_UP)) rotation.x += rSpeed * delta;
		if(Window.isKeyPressed(Window.KEY_DOWN)) rotation.x -= rSpeed * delta;
		if(Window.isKeyPressed(Window.KEY_LEFT)) rotation.y -= rSpeed * delta;
		if(Window.isKeyPressed(Window.KEY_RIGHT)) rotation.y += rSpeed * delta;
		
		parent.transform.rotation.setIdentity();
		parent.transform.rotation.rotateX(rotation.x);
		parent.transform.rotation.rotateY(rotation.y);
	}

	private void move(float hs, float ys) {
		parent.transform.position.y += ys;
		parent.transform.position.x += hs * Math.sin(rotation.y);
		parent.transform.position.z += hs * Math.cos(rotation.y);
	}

	public Vec2 getRotation() {
		return rotation;
	}
}
