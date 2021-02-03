/**
 * 
 */
package de.kjEngine.editor;

import de.kjEngine.ui.UI;
import de.kjEngine.ui.UIFactory;
import de.kjEngine.ui.font.TextComponent;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.PixelSize;
import de.kjEngine.ui.transform.Size;

/**
 * @author konst
 *
 */
public class Property extends UI {
	
	public static final Size DEFAULT_HEIGHT = new PixelSize(20f);

	public Property(Offset x, Offset y, Size width, Size height, String name, UIFactory factory) {
		super(x, y, width, height);
		
		add(new TextComponent(name + ":", factory.font, factory.fontSize, false, false, true));
	}
	
	public float getNameWidth() {
		return get(TextComponent.class).getTextWidth(0);
	}
}
