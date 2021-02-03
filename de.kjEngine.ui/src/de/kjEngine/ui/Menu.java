/**
 * 
 */
package de.kjEngine.ui;

import de.kjEngine.math.Vec2;
import de.kjEngine.ui.event.ActionListener;
import de.kjEngine.ui.event.MouseButtonEvent;
import de.kjEngine.ui.font.FontType;
import de.kjEngine.ui.font.TextComponent;
import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.ParentOffset;
import de.kjEngine.ui.transform.ParentSize;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.Size;

/**
 * @author konst
 *
 */
public class Menu extends UI {

	public final UI optionsUI;
	private boolean open;
	public final String[] options;

	public Menu(Offset x, Offset y, Size width, Size height, float rotation, Offset rotationPivotX, Offset rotationPivotY, String name, FontType font, float fontSize, Model defaultModel,
			Model hoverModel, Model pressModel, ActionListener<String> listener, String... options) {
		super(x, y, width, height, rotation, rotationPivotX, rotationPivotY);
		this.options = options;
		add(new ButtonComponent(defaultModel, hoverModel, pressModel) {

			@Override
			protected void press() {
				setOpen(!open);
			}
		});
		add(new TextComponent(name, font, fontSize, false, true, true));
		optionsUI = new UI(new PixelOffset(), new PixelOffset(), new ParentSize(1f), new ParentSize(1f));
		for (int i = 0; i < options.length; i++) {
			String option = options[i];
			UI ui = new UI(new PixelOffset(), new ParentOffset(-1f - i), new ParentSize(1f), new ParentSize(1f));
			ui.add(new ButtonComponent(defaultModel, hoverModel, pressModel) {

				@Override
				protected void press() {
					listener.run(option);
					setOpen(false);
				}
			});
			ui.add(new TextComponent(option, font, fontSize, false, false, true));
			optionsUI.add(ui);
		}
	}

	@Override
	public void mousePressed(MouseButtonEvent e) {
		super.mousePressed(e);
		Vec2 local = toLocal(Vec2.create(e.x, e.y));
		if (local.x < 0f || local.x > 1f || local.y > 1f || local.y < -options.length) {
			setOpen(false);
		}
	}

	public void setOpen(boolean open) {
		if (this.open == open) {
			return;
		}
		this.open = open;
		if (open) {
			add(optionsUI);
		} else {
			remove(optionsUI);
		}
	}
}
