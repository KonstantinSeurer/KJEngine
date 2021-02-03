package de.kjEngine.graphics;

import de.kjEngine.math.Vec4;

public class Color {

	public static final Color NONE = new Color(Vec4.create());
	public static final Color BLACK = new Color(Vec4.create(0f, 0f, 0f, 1f));
	public static final Color WHITE = new Color(Vec4.create(1f, 1f, 1f, 1f));
	public static final Color RED = new Color(Vec4.create(1f, 0f, 0f, 1f));
	public static final Color GREEN = new Color(Vec4.create(0f, 1f, 0f, 1f));
	public static final Color BLUE = new Color(Vec4.create(0f, 0f, 1f, 1f));
	public static final Color YELLOW = new Color(Vec4.create(1f, 1f, 0f, 1f));
	public static final Color PURPLE = new Color(Vec4.create(1f, 0f, 1f, 1f));

	public static final Color GRAY_09 = new Color(Vec4.create(0.9f, 0.9f, 0.9f, 1f));
	public static final Color GRAY_08 = new Color(Vec4.create(0.8f, 0.8f, 0.8f, 1f));
	public static final Color GRAY_07 = new Color(Vec4.create(0.7f, 0.7f, 0.7f, 1f));
	public static final Color GRAY_06 = new Color(Vec4.create(0.6f, 0.6f, 0.6f, 1f));
	public static final Color GRAY_05 = new Color(Vec4.create(0.5f, 0.5f, 0.5f, 1f));
	public static final Color GRAY_04 = new Color(Vec4.create(0.4f, 0.4f, 0.4f, 1f));
	public static final Color GRAY_03 = new Color(Vec4.create(0.3f, 0.3f, 0.3f, 1f));
	public static final Color GRAY_025 = new Color(Vec4.create(0.25f, 0.25f, 0.25f, 1f));
	public static final Color GRAY_02 = new Color(Vec4.create(0.2f, 0.2f, 0.2f, 1f));
	public static final Color GRAY_015 = new Color(Vec4.create(0.15f, 0.15f, 0.15f, 1f));
	public static final Color GRAY_01 = new Color(Vec4.create(0.1f, 0.1f, 0.1f, 1f));
	
	public static final Color ORANGE = new Color(Vec4.create(1f, 0.5f, 0f, 1f));
	public static final Color LIGHT_BLUE = new Color(Vec4.create(0.2f, 0.6f, 1f, 1f));

	public static final Color IRON = new Color(0.56f, 0.57f, 0.58f, 1f);
	public static final Color COPPER = new Color(0.95f, 0.64f, 0.54f, 1f);
	public static final Color GOLD = new Color(1.0f, 0.71f, 0.29f, 1f);
	public static final Color ALUMINIUM = new Color(0.91f, 0.92f, 0.92f, 1f);
	public static final Color SILVER = new Color(0.95f, 0.93f, 0.88f, 1f);

	private Vec4 color;
	private Texture2D texture;

	public Color(Vec4 color) {
		setColor(color);
	}

	public Color(float r, float g, float b, float a) {
		setColor(Vec4.create(r, g, b, a));
	}

	public Color(int r, int g, int b, int a) {
		setColor((Vec4) Vec4.create(r, g, b, a).mul(1f / 255f));
	}

	public Color(int col) {
		int r = (col >> 24) & 0xFF;
		int g = (col >> 16) & 0xFF;
		int b = (col >> 8) & 0xFF;
		int a = (col) & 0xFF;
		setColor(Vec4.create(r / 255f, g / 255f, b / 255f, a / 255f));
	}

	public Vec4 getColor() {
		return color;
	}

	public void setColor(Vec4 color) {
		this.color = color;
		texture = Graphics.createTexture2D(color);
	}

	public Texture2D getTexture() {
		return texture;
	}
}
