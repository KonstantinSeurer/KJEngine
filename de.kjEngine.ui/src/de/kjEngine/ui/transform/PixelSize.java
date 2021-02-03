/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public class PixelSize extends Size {

	public PixelSize() {
	}

	public PixelSize(float value) {
		super(value);
	}

	@Override
	public float getPixelWidth(UI ui, UI parent) {
		return value;
	}

	@Override
	public float getPixelHeight(UI ui, UI parent) {
		return value;
	}
}
