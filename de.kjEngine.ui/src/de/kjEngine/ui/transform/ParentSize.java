/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public class ParentSize extends Size {

	public ParentSize() {
	}

	public ParentSize(float value) {
		super(value);
	}

	@Override
	public float getPixelWidth(UI ui, UI parent) {
		return value * parent.getPixelWidth();
	}

	@Override
	public float getPixelHeight(UI ui, UI parent) {
		return value * parent.getPixelHeight();
	}
}
