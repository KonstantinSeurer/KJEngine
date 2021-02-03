/**
 * 
 */
package de.kjEngine.ui;

import de.kjEngine.component.Component;
import de.kjEngine.ui.event.EventListener;
import de.kjEngine.ui.event.KeyEvent;
import de.kjEngine.ui.event.MouseButtonEvent;
import de.kjEngine.ui.event.MouseMoveEvent;
import de.kjEngine.ui.event.MouseWheelEvent;

/**
 * @author konst
 *
 */
public class UIComponent<ParentType extends UIComponent<?, ?>, ComponentType extends UIComponent<ParentType, ?>> extends Component<ParentType, ComponentType, UIScene> implements EventListener {

	public UIComponent(int flags) {
		super(flags);
	}

	@Override
	public void mousePressed(MouseButtonEvent e) {
	}

	@Override
	public void mouseReleased(MouseButtonEvent e) {
	}

	@Override
	public void mouseMoved(MouseMoveEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
