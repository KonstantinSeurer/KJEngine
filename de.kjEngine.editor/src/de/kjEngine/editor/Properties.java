/**
 * 
 */
package de.kjEngine.editor;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.component.Component;
import de.kjEngine.ui.UI;
import de.kjEngine.ui.UIComponent;
import de.kjEngine.ui.transform.ParentSize;
import de.kjEngine.ui.transform.PixelOffset;

/**
 * @author konst
 *
 */
public abstract class Properties extends UI {
	
	private List<Property> properties = new ArrayList<>();

	public Properties() {
		super(new PixelOffset(), new PixelOffset(), new ParentSize(1f), new ParentSize(1f));
		
		add(new UIComponent<>(Component.EARLY) {

			@Override
			protected void update(float delta) {
				float offset = 0f;
				for (Property ui : properties) {
					ui.y.value = offset;
					offset += ui.getPixelHeight() + 1f;
				}
			}
		});
	}
	
	public void add(Property property) {
		super.add(property);
		properties.add(property);
	}
	
	public abstract void activate();
}
