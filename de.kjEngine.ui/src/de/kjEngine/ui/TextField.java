/**
 * 
 */
package de.kjEngine.ui;

import de.kjEngine.component.Component;
import de.kjEngine.math.Real;
import de.kjEngine.math.Vec2;
import de.kjEngine.ui.event.ActionListener;
import de.kjEngine.ui.event.KeyEvent;
import de.kjEngine.ui.event.MouseButtonEvent;
import de.kjEngine.ui.font.FontType;
import de.kjEngine.ui.font.TextComponent;
import de.kjEngine.ui.model.Material;
import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.model.ModelComponent;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.ParentSize;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.Size;

/**
 * @author konst
 *
 */
public class TextField extends UI {

	private boolean editable;
	private TextComponent textComp;
	private int curserPosition;
	public final UI curserUI;
	private float t;

	public TextField(Offset x, Offset y, Size width, Size height, float rotation, Offset rotationPivotX, Offset rotationPivotY, String text, FontType font, float fontSize, Model buttonDefault,
			Model buttonHover, Model buttonPress, Material curserMaterial, Size curserWidth, ActionListener<TextField> listener) {
		super(x, y, width, height, rotation, rotationPivotX, rotationPivotY);

		add(new ButtonComponent(buttonDefault, buttonHover, buttonPress) {

			@Override
			protected void press() {
				boolean prevEditable = editable;
				editable = true;
				if (prevEditable != editable) {
					t = 0f;
					curserPosition = getText().length();
					updateCurserPosition();
				}
			}

			@Override
			public void mousePressed(MouseButtonEvent e) {
				if (e.isHandled() || !parent.intersects(Vec2.create(e.x, e.y))) {
					setState(State.DEFAULT);
					editable = false;
				} else {
					super.mousePressed(e);
				}
			}

			@Override
			public void mouseReleased(MouseButtonEvent e) {
			}
		});

		textComp = new TextComponent(text, font, fontSize, false, false, true);
		add(textComp);

		curserUI = new UI(new PixelOffset(), new PixelOffset(), curserWidth, new ParentSize(1f));
		curserUI.add(new ModelComponent(new Model(Model.getRectangle(), curserMaterial)));
		curserUI.setActive(false);
		add(curserUI);

		add(new UIComponent<>(Component.EARLY) {

			@Override
			public void keyPressed(KeyEvent e) {
				if (editable) {
					t = 0f;
					if (e.key == Window.KEY_BACK) {
						if (curserPosition > 0) {
							String currentText = textComp.getText();
							String newText = currentText.substring(0, curserPosition - 1);
							if (curserPosition < currentText.length()) {
								newText += currentText.substring(curserPosition);
							}
							textComp.setText(newText);
							curserPosition--;
							updateCurserPosition();
							listener.run(TextField.this);
						}
					} else if (e.key == Window.KEY_LEFT) {
						if (curserPosition > 0) {
							curserPosition--;
							updateCurserPosition();
						}
					} else if (e.key == Window.KEY_RIGHT) {
						String currentText = textComp.getText();
						if (curserPosition < currentText.length()) {
							curserPosition++;
							updateCurserPosition();
						}
					} else if (e.character != 0) {
						String currentText = textComp.getText();
						String newText = "";
						if (curserPosition > 0) {
							newText = currentText.substring(0, curserPosition);
						}
						newText += e.character;
						if (curserPosition < currentText.length()) {
							newText += currentText.substring(curserPosition);
						}
						textComp.setText(newText);
						curserPosition++;
						updateCurserPosition();
						listener.run(TextField.this);
					}
					e.handle();
				}
			}

			@Override
			protected void update(float delta) {
				curserUI.setActive(editable && Real.fract(t) < 0.5f);
				t += delta;
			}
		});
	}

	private void updateCurserPosition() {
		curserUI.x.value = textComp.getTextWidth(0, curserPosition);
	}

	public String getText() {
		return textComp.getText();
	}
	
	public void setText(String text) {
		textComp.setText(text);
	}

	public TextField(Offset x, Offset y, Size width, Size height, String text, FontType font, float fontSize, Model buttonDefault, Model buttonHover, Model buttonPress, Material curserMaterial,
			Size curserWidth, ActionListener<TextField> listener) {
		this(x, y, width, height, 0f, new PixelOffset(), new PixelOffset(), text, font, fontSize, buttonDefault, buttonHover, buttonPress, curserMaterial, curserWidth, listener);
	}
}
