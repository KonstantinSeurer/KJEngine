/**
 * 
 */
package de.kjEngine.editor;

import de.kjEngine.math.Vec2;
import de.kjEngine.ui.TextField;
import de.kjEngine.ui.UI;
import de.kjEngine.ui.UIComponent;
import de.kjEngine.ui.UIFactory;
import de.kjEngine.ui.event.ActionListener;
import de.kjEngine.ui.event.MouseWheelEvent;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.Size;

/**
 * @author konst
 *
 */
public class FloatProperty extends StringProperty {

	public FloatProperty(Offset x, Offset y, Size width, String name, UIFactory factory, ActionListener<Float> change) {
		super(x, y, width, name, factory, (text) -> {
			try {
				change.run(Float.valueOf(text));
			} catch (NumberFormatException e) {
			}
		});
		
		TextField textField = get(TextField.class);
		textField.add(new UIComponent<UI, UIComponent<UI, ?>>(0) {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (!e.isHandled() && parent.intersects(Vec2.create(e.x, e.y))) {
					e.handle();
					try {
						float value = Float.valueOf(textField.getText());
						value += e.movement;
						setValue(value);
						change.run(value);
					} catch (NumberFormatException ex) {
					}
				}
			}
		});
	}

	public void setValue(float value) {
		setValue(String.valueOf(value));
	}
}
