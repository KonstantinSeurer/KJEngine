/**
 * 
 */
package de.kjEngine.editor;

import de.kjEngine.ui.TextField;
import de.kjEngine.ui.UIFactory;
import de.kjEngine.ui.event.ActionListener;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.Size;

/**
 * @author konst
 *
 */
public class StringProperty extends Property {

	public StringProperty(Offset x, Offset y, Size width, String name, UIFactory factory, ActionListener<String> change) {
		super(x, y, width, DEFAULT_HEIGHT, name, factory);

		float offset = getNameWidth();
		add(factory.textField(offset + "px", "", offset + "ptmpx", "1pt", "", (textField) -> change.run(textField.getText())));
	}
	
	public void setValue(String text) {
		get(TextField.class).setText(text);
	}
}
