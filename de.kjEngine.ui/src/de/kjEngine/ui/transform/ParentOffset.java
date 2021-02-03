/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public class ParentOffset extends Offset {

	public ParentOffset() {
	}

	public ParentOffset(float value) {
		super(value);
	}

	@Override
	public float getPixelOffsetX(UI ui, UI parent) {
		return value * parent.getPixelWidth();
	}

	@Override
	public float getPixelOffsetY(UI ui, UI parent) {
		return value * parent.getPixelHeight();
	}
}
