/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public class UIOffset extends Offset {
	
	public UIOffset() {
	}
	
	public UIOffset(float value) {
		super(value);
	}

	@Override
	public float getPixelOffsetX(UI ui, UI parent) {
		return ui.getPixelWidth() * value;
	}

	@Override
	public float getPixelOffsetY(UI ui, UI parent) {
		return ui.getPixelHeight() * value;
	}
}
