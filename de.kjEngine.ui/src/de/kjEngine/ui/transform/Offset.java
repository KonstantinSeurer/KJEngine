/**
 * 
 */
package de.kjEngine.ui.transform;

import de.kjEngine.ui.UI;

/**
 * @author konst
 *
 */
public abstract class Offset {
	
	public float value;

	public Offset() {
	}
	
	public Offset(float value) {
		this.value = value;
	}
	
	public abstract float getPixelOffsetX(UI ui, UI parent);
	
	public abstract float getPixelOffsetY(UI ui, UI parent);
}
