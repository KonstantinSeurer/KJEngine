/**
 * 
 */
package de.kjEngine.editor;

import de.kjEngine.ui.UIFactory;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.ParentSize;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.Size;
import de.kjEngine.ui.transform.TopRightPixelOffset;

/**
 * @author konst
 *
 */
public class ComponentProperties extends Properties {
	
	private StringProperty nameProp;
	private BooleanProperty activeProp;

	@SuppressWarnings("unchecked")
	public ComponentProperties(UIFactory f) {
		Offset defaultX = new PixelOffset();
		Size defaultWidth = new ParentSize(1f);
		
		nameProp = new StringProperty(defaultX, new TopRightPixelOffset(), defaultWidth, "name", f, (name) -> {
			Main.currentNode.setText(name);
			Main.currentComponent.name = name;
		});
		add(nameProp);
		activeProp = new BooleanProperty(defaultX, new TopRightPixelOffset(), defaultWidth, "active", f, (active) -> {
			Main.currentComponent.setActive(active);
		});
		add(activeProp);
	}

	@Override
	public void activate() {
		nameProp.setValue(Main.currentNode.getText());
		activeProp.setValue(Main.currentComponent.isActive());
	}
}
