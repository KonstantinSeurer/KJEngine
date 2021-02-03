/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public class PixelOffset extends Offset {

	public PixelOffset() {
	}

	public PixelOffset(float value) {
		super(value);
	}

	@Override
	public float getPixelOffsetX(UI ui, UI parent) {
		return value;
	}

	@Override
	public float getPixelOffsetY(UI ui, UI parent) {
		return value;
	}
}
