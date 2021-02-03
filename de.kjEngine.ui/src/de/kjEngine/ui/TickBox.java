/**
 * 
 */
package de.kjEngine.ui;

import de.kjEngine.ui.event.ActionListener;
import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.model.ModelComponent;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.Size;

/**
 * @author konst
 *
 */
public class TickBox extends UI {
	
	private boolean ticked;
	private ModelComponent tick;

	public TickBox(Offset x, Offset y, Size width, Size height, float rotation, Offset rotationPivotX, Offset rotationPivotY, Model defaultMode, Model hoverModel, Model pressModel, Model tickModel, ActionListener<TickBox> listener) {
		super(x, y, width, height, rotation, rotationPivotX, rotationPivotY);
		
		add(new ButtonComponent(defaultMode, hoverModel, pressModel) {
			
			@Override
			protected void press() {
				setTicked(!ticked);
				listener.run(TickBox.this);
			}
		});
		
		tick = new ModelComponent(tickModel);
		tick.setActive(false);
		add(tick);
	}

	public TickBox(Offset x, Offset y, Size width, Size height, Model defaultMode, Model hoverModel, Model pressModel, Model tickModel, ActionListener<TickBox> listener) {
		this(x, y, width, height, 0f, new PixelOffset(), new PixelOffset(), defaultMode, hoverModel, pressModel, tickModel, listener);
	}

	public boolean isTicked() {
		return ticked;
	}

	public void setTicked(boolean ticked) {
		this.ticked = ticked;
		tick.setActive(ticked);
	}
}
