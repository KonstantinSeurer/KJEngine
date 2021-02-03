/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public class AddOffset extends Offset {
	
	private final Offset a, b;

	public AddOffset(Offset a, Offset b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public float getPixelOffsetX(UI ui, UI parent) {
		return a.getPixelOffsetX(ui, parent) + b.getPixelOffsetX(ui, parent);
	}

	@Override
	public float getPixelOffsetY(UI ui, UI parent) {
		return a.getPixelOffsetY(ui, parent) + b.getPixelOffsetY(ui, parent);
	}
}
