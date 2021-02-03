/**
 * 
 */
package de.kjEngine.editor;

import de.kjEngine.ui.TickBox;
import de.kjEngine.ui.UI;
import de.kjEngine.ui.UIFactory;
import de.kjEngine.ui.event.ActionListener;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.ParentSize;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.Size;

/**
 * @author konst
 *
 */
public class BooleanProperty extends Property {

	public BooleanProperty(Offset x, Offset y, Size width, String name, UIFactory factory, ActionListener<Boolean> change) {
		super(x, y, width, DEFAULT_HEIGHT, name, factory);
		
		add(factory.tickBox(new PixelOffset(getNameWidth()), new PixelOffset(), new Size() {
			
			@Override
			public float getPixelWidth(UI ui, UI parent) {
				return ui.getPixelHeight();
			}
			
			@Override
			public float getPixelHeight(UI ui, UI parent) {
				return 0;
			}
		}, new ParentSize(1f), (box) -> change.run(box.isTicked())));
	}
	
	public void setValue(boolean value) {
		get(TickBox.class).setTicked(value);
	}
}
