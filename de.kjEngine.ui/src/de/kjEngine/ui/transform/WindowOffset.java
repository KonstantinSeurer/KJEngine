/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;
import de.kjEngine.ui.Window;

/**
 * @author konst
 *
 */
public class WindowOffset extends Offset {

	public WindowOffset() {
	}

	public WindowOffset(float value) {
		super(value);
	}

	@Override
	public float getPixelOffsetX(UI ui, UI parent) {
		return value * Window.getWidth();
	}

	@Override
	public float getPixelOffsetY(UI ui, UI parent) {
		return value * Window.getHeight();
	}
}
