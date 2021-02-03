/**
 * 
 */
package de.kjEngine.ui;

import de.kjEngine.math.Vec2;
import de.kjEngine.ui.event.MouseButtonEvent;
import de.kjEngine.ui.event.MouseMoveEvent;
import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.model.ModelComponent;

/**
 * @author konst
 *
 */
public abstract class ButtonComponent extends UIComponent<UI, ButtonComponent> {
	
	public static enum State {
		DEFAULT, HOVERING, PRESSED
	}

	public Model defaultModel, hoverModel, pressModel;
	private ModelComponent modelComponent;
	private State state;

	public ButtonComponent(Model defaultModel, Model hoverModel, Model pressModel) {
		super(EARLY);
		this.defaultModel = defaultModel;
		this.hoverModel = hoverModel;
		this.pressModel = pressModel;
		modelComponent = new ModelComponent(defaultModel);
	}

	@Override
	public void init() {
		super.init();
		parent.add(modelComponent);
	}

	@Override
	public void mousePressed(MouseButtonEvent e) {
		if (e.isHandled()) {
			return;
		}
		if (parent.intersects(Vec2.create(e.x, e.y))) {
			setState(State.PRESSED);
			press();
			e.handle();
		}
	}

	@Override
	public void mouseReleased(MouseButtonEvent e) {
		if (!e.isHandled() && parent.intersects(Vec2.create(e.x, e.y))) {
			setState(State.HOVERING);
			e.handle();
		} else {
			setState(State.DEFAULT);
		}
	}

	@Override
	public void mouseMoved(MouseMoveEvent e) {
		if (e.isHandled() && state == State.HOVERING) {
			setState(State.DEFAULT);
		}
		if (parent.intersects(Vec2.create(e.x, e.y))) {
			if (!e.isHandled()) {
				e.handle();
				if (state == State.DEFAULT) {
					setState(State.HOVERING);
				}
			}
		} else if (!isPressed()) {
			setState(State.DEFAULT);
		}
	}

	public void setState(State state) {
		if (this.state == state) {
			return;
		}
		this.state = state;
		switch (state) {
		case DEFAULT:
			modelComponent.model = defaultModel;
			break;
		case HOVERING:
			modelComponent.model = hoverModel;
			break;
		case PRESSED:
			modelComponent.model = pressModel;
			break;
		}
	}

	public State getState() {
		return state;
	}

	public boolean isPressed() {
		return state == State.PRESSED;
	}
	
	protected abstract void press();
}
