/**
 * 
 */
package de.kjEngine.editor;

import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.ui.ButtonComponent;
import de.kjEngine.ui.UI;
import de.kjEngine.ui.UIFactory;
import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.model.StandartMaterial;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.ParentMinusPixelSize;
import de.kjEngine.ui.transform.PixelSize;
import de.kjEngine.ui.transform.Size;
import de.kjEngine.ui.transform.TopRightPixelOffset;
import de.kjEngine.ui.transform.UIOffset;

/**
 * @author konst
 *
 */
public class Asset extends UI {

	public static final Size ASSET_WIDTH = new PixelSize(100f);
	public static final Size ASSET_HEIGHT = new PixelSize(120f);

	public static final Model DEFAULT = new Model(Model.getRectangle(), new StandartMaterial(Graphics.createTexture2D(1f, 1f, 1f, 0f)));
	public static final Model HOVER = new Model(Model.getRectangle(), new StandartMaterial(Graphics.createTexture2D(1f, 1f, 1f, 0.05f)));
	public static final Model PRESS = new Model(Model.getRectangle(), new StandartMaterial(Graphics.createTexture2D(1f, 1f, 1f, 0.1f)));

	public AssetCollection parentCollection;
	protected Runnable onClick;

	public Asset(UIFactory factory, String text, Texture2D icon, Runnable onClick) {
		super(new UIOffset(), new Offset() {
			
			@Override
			public float getPixelOffsetY(UI ui, UI parent) {
				return parent.getPixelHeight() - ui.getPixelHeight() * (value + 1f);
			}
			
			@Override
			public float getPixelOffsetX(UI ui, UI parent) {
				return 0;
			}
		}, ASSET_WIDTH, ASSET_HEIGHT);
		
		this.onClick = onClick;

		add(new ButtonComponent(DEFAULT, HOVER, PRESS) {

			@Override
			protected void press() {
				Asset.this.onClick.run();
			}
		});
		
		add(factory.image(new TopRightPixelOffset(5f), new TopRightPixelOffset(5f), new ParentMinusPixelSize(10f), new Size() {
			
			@Override
			public float getPixelWidth(UI ui, UI parent) {
				return 0;
			}
			
			@Override
			public float getPixelHeight(UI ui, UI parent) {
				return ui.getPixelWidth();
			}
		}, new StandartMaterial(icon)));
		
		add(factory.label("", "", "1pt", "20px", text, true, true, false));
	}
}
