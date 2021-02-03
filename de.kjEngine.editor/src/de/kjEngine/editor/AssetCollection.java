/**
 * 
 */
package de.kjEngine.editor;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.io.RL;
import de.kjEngine.ui.UIFactory;

/**
 * @author konst
 *
 */
public class AssetCollection extends Asset {

	public static final Texture2D ICON = Graphics.loadTexture(RL.create("jar://editor/de/kjEngine/editor/assetCollection.png"), SamplingMode.LINEAR, WrappingMode.CLAMP, false);

	public final List<Asset> assets = new ArrayList<>();

	public AssetCollection(UIFactory factory, String text) {
		super(factory, text, ICON, null);
		
		onClick = () -> {
			Main.setCurrentAssets(AssetCollection.this);
		};
	}

	public void add(Asset asset) {
		asset.setActive(isActive());
		assets.add(asset);
		asset.parentCollection = this;
		parent.add(asset);
	}
}
