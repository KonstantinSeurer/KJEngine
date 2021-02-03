/**
 * 
 */
package de.kjEngine.ui.font;

import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.model.ModelComponent;

/**
 * @author konst
 *
 */
public class TextComponent extends ModelComponent {

	private String text;
	private FontType font;
	private float fontSize;
	private boolean wrap, centerX, centerY;
	private boolean change;

	private float width, height;

	private Model disposeModel;

	public TextComponent(String text, FontType font, float fontSize, boolean wrap, boolean centerX, boolean centerY) {
		this.text = text;
		this.font = font;
		this.fontSize = fontSize;
		this.wrap = wrap;
		this.centerX = centerX;
		this.centerY = centerY;
		change = true;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		change = true;
	}

	public FontType getFont() {
		return font;
	}

	public void setFont(FontType font) {
		this.font = font;
		change = true;
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
		change = true;
	}

	public boolean isWrap() {
		return wrap;
	}

	public void setWrap(boolean wrap) {
		this.wrap = wrap;
		change = true;
	}

	public boolean isCenterX() {
		return centerX;
	}

	public void setCenterX(boolean centerX) {
		this.centerX = centerX;
		change = true;
	}

	public boolean isCenterY() {
		return centerY;
	}

	public void setCenterY(boolean centerY) {
		this.centerY = centerY;
		change = true;
	}

	public float getTextWidth(int line) {
		int beginIndex = 0;
		for (int i = 0; i < line; i++) {
			beginIndex = text.indexOf('\n', beginIndex) + 1;
		}
		char[] characters = text.toCharArray();
		float fontSize = this.fontSize / font.data.getMaxCharacterHeight();
		float width = 0f;
		for (int i = beginIndex; i < characters.length; i++) {
			char c = characters[i];
			if (c == '\n') {
				break;
			}
			if (c == ' ') {
				width += font.data.getSpaceWidth() * fontSize;
			} else {
				width += font.data.getCharacter(c).getxAdvance() * fontSize;
			}
		}
		return width;
	}
	
	public float getTextWidth(int line, int maxX) {
		int beginIndex = 0;
		for (int i = 0; i < line; i++) {
			beginIndex = text.indexOf('\n', beginIndex) + 1;
		}
		char[] characters = text.toCharArray();
		float fontSize = this.fontSize / font.data.getMaxCharacterHeight();
		float width = 0f;
		int x = 0;
		for (int i = beginIndex; i < characters.length; i++) {
			if (x == maxX) {
				break;
			}
			char c = characters[i];
			if (c == '\n') {
				break;
			}
			if (c == ' ') {
				width += font.data.getSpaceWidth() * fontSize;
			} else {
				width += font.data.getCharacter(c).getxAdvance() * fontSize;
			}
			x++;
		}
		return width;
	}

	@Override
	protected void update(float delta) {
		super.update(delta);
		float newWidth = parent.getPixelWidth();
		float newHeight = parent.getPixelHeight();
		if (newWidth != width || newHeight != height) {
			change = true;
			width = newWidth;
			height = newHeight;
		}
	}

	@Override
	public void updateDescriptors() {
		super.updateDescriptors();
		if (disposeModel != null) {
			disposeModel.dispose();
			disposeModel = null;
		}
		if (change) {
			change = false;
			disposeModel = model;
			model = font.text(text, fontSize, wrap, centerX, centerX, width, height);
		}
	}
}
