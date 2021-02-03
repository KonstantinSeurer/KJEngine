/**
 * 
 */
package de.kjEngine.editor;

import de.kjEngine.math.Vec3;
import de.kjEngine.ui.UI;
import de.kjEngine.ui.UIFactory;
import de.kjEngine.ui.event.ActionListener;
import de.kjEngine.ui.transform.AddOffset;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.Size;
import de.kjEngine.ui.transform.UIOffset;

/**
 * @author konst
 *
 */
public class Vec3Property extends Property {
	
	private FloatProperty xProp, yProp, zProp;
	private Vec3 value = Vec3.create();

	public Vec3Property(Offset x, Offset y, Size width, String name, UIFactory factory, ActionListener<Vec3> change) {
		super(x, y, width, DEFAULT_HEIGHT, name, factory);
		
		float nameWidth = getNameWidth();
		
		Size floatWidth = new Size() {
			
			@Override
			public float getPixelWidth(UI ui, UI parent) {
				return (parent.getPixelWidth() - nameWidth) / 3f;
			}
			
			@Override
			public float getPixelHeight(UI ui, UI parent) {
				return 0;
			}
		};
		Offset nameOffset = new PixelOffset(nameWidth);
		
		xProp = new FloatProperty(nameOffset, new PixelOffset(), floatWidth, "x", factory, (val) -> {
			value.x = val;
			change.run(value);
		});
		add(xProp);
		
		yProp = new FloatProperty(new AddOffset(nameOffset, new UIOffset(1f)), new PixelOffset(), floatWidth, "y", factory, (val) -> {
			value.y = val;
			change.run(value);
		});
		add(yProp);
		
		zProp = new FloatProperty(new AddOffset(nameOffset, new UIOffset(2f)), new PixelOffset(), floatWidth, "z", factory, (val) -> {
			value.z = val;
			change.run(value);
		});
		add(zProp);
	}
	
	public void setValue(Vec3 value) {
		this.value.set(value);
		xProp.setValue(value.x);
		yProp.setValue(value.y);
		zProp.setValue(value.z);
	}
}
