/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public class CenterOffset extends Offset {

	public CenterOffset() {
		super();
	}

	public CenterOffset(float value) {
		super(value);
	}

	@Override
	public float getPixelOffsetX(UI ui, UI parent) {
		if (parent == null) {
			return 0f;
		}
		return parent.getPixelWidth() * 0.5f - ui.getPixelWidth() * 0.5f;
	}

	@Override
	public float getPixelOffsetY(UI ui, UI parent) {
		if (parent == null) {
			return 0f;
		}
		return parent.getPixelHeight() * 0.5f - ui.getPixelHeight() * 0.5f;
	}
}
