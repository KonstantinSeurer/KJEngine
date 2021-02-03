/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public class TopRightPixelOffset extends Offset {

	public TopRightPixelOffset() {
	}

	public TopRightPixelOffset(float value) {
		super(value);
	}

	@Override
	public float getPixelOffsetX(UI ui, UI parent) {
		return parent.getPixelWidth() - ui.getPixelWidth() - value;
	}

	@Override
	public float getPixelOffsetY(UI ui, UI parent) {
		return parent.getPixelHeight() - ui.getPixelHeight() - value;
	}
}
