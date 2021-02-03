/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public class TopRightParentOffset extends Offset {

	public TopRightParentOffset() {
	}

	public TopRightParentOffset(float value) {
		super(value);
	}

	@Override
	public float getPixelOffsetX(UI ui, UI parent) {
		return parent.getPixelWidth() - ui.getPixelWidth() - value * parent.getPixelWidth();
	}

	@Override
	public float getPixelOffsetY(UI ui, UI parent) {
		return parent.getPixelHeight() - ui.getPixelHeight() - value * parent.getPixelHeight();
	}
}
