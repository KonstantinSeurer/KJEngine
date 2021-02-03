/**
 * 
 */
package de.kjEngine.ui.model;

import de.kjEngine.ui.UI;
import de.kjEngine.ui.UIComponent;

/**
 * @author konst
 *
 */
public class ModelComponent extends UIComponent<UI, ModelComponent> {

	public Model model;

	public ModelComponent() {
		super(LATE);
	}

	public ModelComponent(Model model) {
		super(LATE);
		this.model = model;
	}
}
