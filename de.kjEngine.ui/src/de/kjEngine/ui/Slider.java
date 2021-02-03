/**
 * 
 */
package de.kjEngine.ui;

import de.kjEngine.math.Real;
import de.kjEngine.math.Vec2;
import de.kjEngine.ui.event.MouseMoveEvent;
import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.transform.CenterOffset;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.ParentOffset;
import de.kjEngine.ui.transform.Size;

/**
 * @author konst
 *
 */
public abstract class Slider extends UI {

	public static enum Orientation {
		HORIZONTAL, VERTICAL
	}

	public final ButtonComponent button;
	public final UI buttonUI;
	public final Orientation orientation;
	private float value;

	public Slider(Offset x, Offset y, Size width, Size height, float rotation, Offset rotationPivotX, Offset rotationPivotY, Model defaultModel, Model hoverModel, Model pressModel,
			Orientation orientation, Size buttonWidth, Size buttonHeight) {
		super(x, y, width, height, rotation, rotationPivotX, rotationPivotY);
		this.orientation = orientation;
		Offset buttonX, buttonY;
		if (orientation == Orientation.HORIZONTAL) {
			buttonX = new ParentOffset();
			buttonY = new CenterOffset();
		} else {
			buttonX = new CenterOffset();
			buttonY = new ParentOffset();
		}
		buttonUI = new UI(buttonX, buttonY, buttonWidth, buttonHeight);
		button = new ButtonComponent(defaultModel, hoverModel, pressModel) {

			@Override
			protected void press() {
			}

			@Override
			public void mouseMoved(MouseMoveEvent e) {
				super.mouseMoved(e);
				if (isPressed()) {
					e.handle();
					Vec2 d = Vec2.sub(toLocal(Vec2.create(e.x, e.y)), toLocal(Vec2.create(e.prevX, e.prevY)), null);
					if (orientation == Orientation.HORIZONTAL) {
						float max = 1f - buttonUI.getPixelWidth() / getPixelWidth();
						buttonUI.x.value = Real.clamp(buttonUI.x.value + d.x, 0f, max);
						value = buttonUI.x.value / max;
						change();
					} else {
						float max = 1f - buttonUI.getPixelHeight() / getPixelHeight();
						buttonUI.y.value = Real.clamp(buttonUI.y.value + d.y, 0f, max);
						value = buttonUI.y.value / max;
						change();
					}
				}
			}
		};
		buttonUI.add(button);
		add(buttonUI);
	}

	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		value = Real.clamp(value, 0f, 1f);
		this.value = value;
		if (orientation == Orientation.HORIZONTAL) {
			float max = 1f - buttonUI.getPixelWidth() / getPixelWidth();
			buttonUI.x.value = value * max;;
			change();
		} else {
			float max = 1f - buttonUI.getPixelHeight() / getPixelHeight();
			buttonUI.y.value = value * max;
			change();
		}
	}
	
	protected abstract void change();
}
