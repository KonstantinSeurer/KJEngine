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
public class WindowSize extends Size {

	public WindowSize() {
	}

	public WindowSize(float value) {
		super(value);
	}

	@Override
	public float getPixelWidth(UI ui, UI parent) {
		return value * Window.getWidth();
	}

	@Override
	public float getPixelHeight(UI ui, UI parent) {
		return value * Window.getHeight();
	}
}
