/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public class ParentMinusPixelSize extends Size {

	public ParentMinusPixelSize() {
	}

	public ParentMinusPixelSize(float value) {
		super(value);
	}

	@Override
	public float getPixelWidth(UI ui, UI parent) {
		return parent.getPixelWidth() - value;
	}

	@Override
	public float getPixelHeight(UI ui, UI parent) {
		return parent.getPixelHeight() - value;
	}
}
