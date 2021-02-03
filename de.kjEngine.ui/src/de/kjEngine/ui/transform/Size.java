/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public abstract class Size {
	
	public float value;

	public Size() {
	}
	
	public Size(float value) {
		this.value = value;
	}
	
	public abstract float getPixelWidth(UI ui, UI parent);
	
	public abstract float getPixelHeight(UI ui, UI parent);
}
