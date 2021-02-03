/**
 * 
 */
package de.kjEngine.ui;

import java.util.HashMap;
import java.util.Map;

import de.kjEngine.graphics.Color;
import de.kjEngine.ui.Slider.Orientation;
import de.kjEngine.ui.Tree.Node;
import de.kjEngine.ui.event.ActionListener;
import de.kjEngine.ui.font.FontType;
import de.kjEngine.ui.font.TextComponent;
import de.kjEngine.ui.model.Material;
import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.model.ModelComponent;
import de.kjEngine.ui.model.StandartMaterial;
import de.kjEngine.ui.transform.CenterOffset;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.ParentMinusPixelSize;
import de.kjEngine.ui.transform.ParentOffset;
import de.kjEngine.ui.transform.ParentSize;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.PixelSize;
import de.kjEngine.ui.transform.Size;
import de.kjEngine.ui.transform.TopRightParentOffset;
import de.kjEngine.ui.transform.TopRightPixelOffset;
import de.kjEngine.ui.transform.UIOffset;
import de.kjEngine.ui.transform.WindowOffset;
import de.kjEngine.ui.transform.WindowSize;

/**
 * @author konst
 *
 */
public class UIFactory {

	public static class ButtonStyle {
		public Model standart, hover, press;

		public ButtonStyle() {
		}

		public ButtonStyle(Model standart, Model hover, Model press) {
			this.standart = standart;
			this.hover = hover;
			this.press = press;
		}
	}

	public static interface OffsetProvider {

		public Offset create(float value);
	}

	public static interface SizeProvider {

		public Size create(float value);
	}

	private static Map<String, OffsetProvider> offsetProviders = new HashMap<>();
	private static Map<String, SizeProvider> sizeProviders = new HashMap<>();

	public static void addOffsetProvider(String unit, OffsetProvider provider) {
		offsetProviders.put(unit, provider);
	}

	public static void addSizeProvider(String unit, SizeProvider provider) {
		sizeProviders.put(unit, provider);
	}

	static {
		addOffsetProvider("px", new OffsetProvider() {

			@Override
			public Offset create(float value) {
				return new PixelOffset(value);
			}
		});
		addSizeProvider("px", new SizeProvider() {

			@Override
			public Size create(float value) {
				return new PixelSize(value);
			}
		});
		addOffsetProvider("ct", new OffsetProvider() {

			@Override
			public Offset create(float value) {
				return new CenterOffset(value);
			}
		});
		addOffsetProvider("pt", new OffsetProvider() {

			@Override
			public Offset create(float value) {
				return new ParentOffset(value);
			}
		});
		addSizeProvider("pt", new SizeProvider() {

			@Override
			public Size create(float value) {
				return new ParentSize(value);
			}
		});
		addOffsetProvider("trpt", new OffsetProvider() {

			@Override
			public Offset create(float value) {
				return new TopRightParentOffset(value);
			}
		});
		addOffsetProvider("trpx", new OffsetProvider() {

			@Override
			public Offset create(float value) {
				return new TopRightPixelOffset(value);
			}
		});
		addOffsetProvider("win", new OffsetProvider() {

			@Override
			public Offset create(float value) {
				return new WindowOffset(value);
			}
		});
		addSizeProvider("win", new SizeProvider() {

			@Override
			public Size create(float value) {
				return new WindowSize(value);
			}
		});
		addSizeProvider("ptmpx", new SizeProvider() {

			@Override
			public Size create(float value) {
				return new ParentMinusPixelSize(value);
			}
		});
		addOffsetProvider("ui", new OffsetProvider() {

			@Override
			public Offset create(float value) {
				return new UIOffset(value);
			}
		});
	}

	public static UIFactory createDefault() {
		UIFactory factory = new UIFactory();

		factory.button = new ButtonStyle();
		factory.button.standart = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_05.getTexture()));
		factory.button.hover = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_06.getTexture()));
		factory.button.press = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_07.getTexture()));

		factory.sliderButton = new ButtonStyle();
		factory.sliderButton.standart = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_04.getTexture()));
		factory.sliderButton.hover = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_05.getTexture()));
		factory.sliderButton.press = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_06.getTexture()));

		factory.treeNode = new ButtonStyle();
		factory.treeNode.standart = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_03.getTexture()));
		factory.treeNode.hover = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_04.getTexture()));
		factory.treeNode.press = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_05.getTexture()));

		factory.splitPanelSplitter = new ButtonStyle();
		factory.splitPanelSplitter.standart = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_02.getTexture()));
		factory.splitPanelSplitter.hover = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_03.getTexture()));
		factory.splitPanelSplitter.press = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_04.getTexture()));

		factory.textFieldBackground = new ButtonStyle();
		factory.textFieldBackground.standart = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_01.getTexture()));
		factory.textFieldBackground.hover = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_015.getTexture()));
		factory.textFieldBackground.press = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_02.getTexture()));
		factory.textFieldCurserMaterial = new StandartMaterial(Color.WHITE.getTexture());
		factory.textFieldCurserWidth = new PixelSize(1f);

		factory.font = FontType.getArial();
		factory.fontSize = 15f;

		factory.splitPanelBackground = new StandartMaterial(Color.GRAY_03.getTexture());

		factory.sliderBackground = new StandartMaterial(Color.GRAY_025.getTexture());

		factory.tickBoxBackground = new ButtonStyle();
		factory.tickBoxBackground.standart = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_01.getTexture()));
		factory.tickBoxBackground.hover = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_015.getTexture()));
		factory.tickBoxBackground.press = new Model(Model.getRectangle(), new StandartMaterial(Color.GRAY_02.getTexture()));
		float tickBorder = 0.2f;
		factory.tickModel = new Model(new float[] { tickBorder, tickBorder, 1f - tickBorder, tickBorder, 1f - tickBorder, 1f - tickBorder, tickBorder, 1f - tickBorder },
				new float[] { 0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f }, new int[] { 0, 1, 2, 2, 3, 0 }, new StandartMaterial(Color.WHITE.getTexture()));

		return factory;
	}

	public ButtonStyle button, sliderButton, treeNode, splitPanelSplitter, textFieldBackground, tickBoxBackground;
	public FontType font;
	public float fontSize;
	public Material splitPanelBackground;
	public Material sliderBackground;
	public Material textFieldCurserMaterial;
	public Size textFieldCurserWidth;
	public Model tickModel;

	public UIFactory() {
	}

	public ButtonComponent buttonComponent(ActionListener<ButtonComponent> press) {
		return new ButtonComponent(button.standart, button.hover, button.press) {

			@Override
			protected void press() {
				press.run(this);
			}
		};
	}

	public static Offset offset(String offset) {
		if (offset == null || offset.isEmpty()) {
			return new PixelOffset();
		}

		int unitStart = 0;
		for (; unitStart < offset.length(); unitStart++) {
			char c = offset.charAt(unitStart);
			if (!Character.isDigit(c) && c != '.') {
				break;
			}
		}
		String unit = offset.substring(unitStart);
		String value = offset.substring(0, unitStart);
		float fValue = 0f;
		if (!value.isEmpty()) {
			fValue = Float.parseFloat(value);
		}
		return offsetProviders.get(unit).create(fValue);
	}

	public static Size size(String size) {
		int unitStart = 0;
		for (; unitStart < size.length(); unitStart++) {
			char c = size.charAt(unitStart);
			if (!Character.isDigit(c) && c != '.') {
				break;
			}
		}
		String unit = size.substring(unitStart);
		String value = size.substring(0, unitStart);
		float fValue = 0f;
		if (!value.isEmpty()) {
			fValue = Float.parseFloat(value);
		}
		return sizeProviders.get(unit).create(fValue);
	}

	public UI ui(String x, String y, String width, String height) {
		return new UI(offset(x), offset(y), size(width), size(height));
	}

	public UI button(Offset x, Offset y, Size width, Size height, ActionListener<ButtonComponent> press) {
		UI ui = new UI(x, y, width, height);
		ui.add(buttonComponent(press));
		return ui;
	}

	public UI button(String x, String y, String width, String height, ActionListener<ButtonComponent> press) {
		return button(offset(x), offset(y), size(width), size(height), press);
	}

	public UI button(Offset x, Offset y, Size width, Size height, String text, ActionListener<ButtonComponent> press) {
		UI ui = new UI(x, y, width, height);
		ui.add(buttonComponent(press));
		ui.add(new TextComponent(text, font, fontSize, false, true, true));
		return ui;
	}

	public UI button(String x, String y, String width, String height, String text, ActionListener<ButtonComponent> press) {
		return button(offset(x), offset(y), size(width), size(height), text, press);
	}

	public UI image(Offset x, Offset y, Size width, Size height, Material material) {
		UI ui = new UI(x, y, width, height);
		Model model = new Model(Model.getRectangle());
		model.material = material;
		ui.add(new ModelComponent(model));
		return ui;
	}

	public UI image(String x, String y, String width, String height, Material material) {
		return image(offset(x), offset(y), size(width), size(height), material);
	}

	public UI label(Offset x, Offset y, Size width, Size height, String text, boolean centerX, boolean centerY, boolean wrap) {
		UI ui = new UI(x, y, width, height);
		ui.add(new TextComponent(text, font, fontSize, wrap, centerX, centerY));
		return ui;
	}

	public UI label(String x, String y, String width, String height, String text, boolean centerX, boolean centerY, boolean wrap) {
		return label(offset(x), offset(y), size(width), size(height), text, centerX, centerY, wrap);
	}

	public UI label(Offset x, Offset y, Size width, Size height, Material material, String text, boolean centerX, boolean centerY, boolean wrap) {
		UI ui = new UI(x, y, width, height);
		Model background = new Model(Model.getRectangle());
		background.material = material;
		ui.add(new ModelComponent(background));
		ui.add(new TextComponent(text, font, fontSize, wrap, centerX, centerY));
		return ui;
	}

	public UI label(String x, String y, String width, String height, Material material, String text, boolean centerX, boolean centerY, boolean wrap) {
		return label(offset(x), offset(y), size(width), size(height), material, text, centerX, centerY, wrap);
	}

	public Slider slider(Offset x, Offset y, Size width, Size height, Orientation orientation, Size buttonWidth, Size buttonHeight, ActionListener<Slider> change) {
		Slider slider = new Slider(x, y, width, height, 0f, new PixelOffset(), new PixelOffset(), sliderButton.standart, sliderButton.hover, sliderButton.press, orientation, buttonWidth,
				buttonHeight) {

			@Override
			protected void change() {
				change.run(this);
			}
		};
		Model background = new Model(Model.getRectangle());
		background.material = sliderBackground;
		slider.add(new ModelComponent(background));
		return slider;
	}

	public Slider slider(String x, String y, String width, String height, Orientation orientation, String buttonWidth, String buttonHeight, ActionListener<Slider> change) {
		return slider(offset(x), offset(y), size(width), size(height), orientation, size(buttonWidth), size(buttonHeight), change);
	}

	public SplitPanel splitPanel(Offset x, Offset y, Size width, Size height, Orientation orientation, Size splitterSize) {
		SplitPanel panel = new SplitPanel(x, y, width, height, 0f, new PixelOffset(), new PixelOffset(), splitPanelSplitter.standart, splitPanelSplitter.hover, splitPanelSplitter.press, orientation,
				splitterSize);
		Model background = new Model(Model.getRectangle());
		background.material = splitPanelBackground;
		panel.add(new ModelComponent(background));
		return panel;
	}

	public SplitPanel splitPanel(String x, String y, String width, String height, Orientation orientation, String splitterSize) {
		return splitPanel(offset(x), offset(y), size(width), size(height), orientation, size(splitterSize));
	}

	public Menu menu(Offset x, Offset y, Size width, Size height, String name, ActionListener<String> optionListener, String... options) {
		return new Menu(x, y, width, height, 0f, new PixelOffset(), new PixelOffset(), name, font, fontSize, button.standart, button.hover, button.press, optionListener, options);
	}

	public Menu menu(String x, String y, String width, String height, String name, ActionListener<String> optionListener, String... options) {
		return menu(offset(x), offset(y), size(width), size(height), name, optionListener, options);
	}

	public Tree tree(Offset x, Offset y, Size width, Size height, Size nodeHeight, ActionListener<Node> openListener) {
		return new Tree(x, y, width, height, 0f, new PixelOffset(), new PixelOffset(), nodeHeight, treeNode.standart, treeNode.hover, treeNode.press, font, fontSize, openListener);
	}

	public Tree tree(String x, String y, String width, String height, String nodeHeight, ActionListener<Node> openListener) {
		return tree(offset(x), offset(y), size(width), size(height), size(nodeHeight), openListener);
	}

	public TextField textField(Offset x, Offset y, Size width, Size height, String text, ActionListener<TextField> listener) {
		return new TextField(x, y, width, height, text, font, fontSize, textFieldBackground.standart, textFieldBackground.hover, textFieldBackground.press, textFieldCurserMaterial,
				textFieldCurserWidth, listener);
	}

	public TextField textField(String x, String y, String width, String height, String text, ActionListener<TextField> listener) {
		return textField(offset(x), offset(y), size(width), size(height), text, listener);
	}

	public TickBox tickBox(Offset x, Offset y, Size width, Size height, ActionListener<TickBox> listener) {
		return new TickBox(x, y, width, height, tickBoxBackground.standart, tickBoxBackground.hover, tickBoxBackground.press, tickModel, listener);
	}
	
	public TickBox tickBox(String x, String y, String width, String height, ActionListener<TickBox> listener) {
		return tickBox(offset(x), offset(y), size(width), size(height), listener);
	}
}
