/**
 * 
 */
package de.kjEngine.ui;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.component.Container;
import de.kjEngine.ui.event.EventListener;
import de.kjEngine.ui.event.KeyEvent;
import de.kjEngine.ui.event.MouseButtonEvent;
import de.kjEngine.ui.event.MouseMoveEvent;
import de.kjEngine.ui.event.MouseWheelEvent;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.WindowSize;

/**
 * @author konst
 *
 */
public class UIScene extends Container<UI, UIScene> implements EventListener {
	
	public final List<List<UIComponent<?, ?>>> layers = new ArrayList<>();
	
	public UIScene() {
		super(new UI(new PixelOffset(), new PixelOffset(), new WindowSize(1f), new WindowSize(1)));
	}
	
	public void add(UI ui) {
		root.add(ui);
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		layers.clear();
		addToLayer(root, 0);
	}

	private void addToLayer(UIComponent<?, ?> ui, int layer) {
		if (ui.isInactive()) {
			return;
		}
		if (layers.size() <= layer) {
			int count = layer - layers.size() + 1;
			for (int i = 0; i < count; i++) {
				layers.add(new ArrayList<>());
			}
		}
		layers.get(layer).add(ui);
		@SuppressWarnings("unchecked")
		List<UIComponent<?, ?>> children = ui.getAll(UIComponent.class);
		for (UIComponent<?, ?> child : children) {
			addToLayer(child, layer + 1);
		}
	}

	@Override
	public void mousePressed(MouseButtonEvent e) {
		for (int layerIndex = layers.size() - 1; layerIndex >= 0; layerIndex--) {
			List<UIComponent<?, ?>> layer = layers.get(layerIndex);
			for (int uiIndex = layer.size() - 1; uiIndex >= 0; uiIndex--) {
				layer.get(uiIndex).mousePressed(e);
			}
		}
	}

	@Override
	public void mouseReleased(MouseButtonEvent e) {
		for (int layerIndex = layers.size() - 1; layerIndex >= 0; layerIndex--) {
			List<UIComponent<?, ?>> layer = layers.get(layerIndex);
			for (int uiIndex = layer.size() - 1; uiIndex >= 0; uiIndex--) {
				layer.get(uiIndex).mouseReleased(e);
			}
		}
	}

	@Override
	public void mouseMoved(MouseMoveEvent e) {
		for (int layerIndex = layers.size() - 1; layerIndex >= 0; layerIndex--) {
			List<UIComponent<?, ?>> layer = layers.get(layerIndex);
			for (int uiIndex = layer.size() - 1; uiIndex >= 0; uiIndex--) {
				layer.get(uiIndex).mouseMoved(e);
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		for (int layerIndex = layers.size() - 1; layerIndex >= 0; layerIndex--) {
			List<UIComponent<?, ?>> layer = layers.get(layerIndex);
			for (int uiIndex = layer.size() - 1; uiIndex >= 0; uiIndex--) {
				layer.get(uiIndex).mouseWheelMoved(e);
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		for (int layerIndex = layers.size() - 1; layerIndex >= 0; layerIndex--) {
			List<UIComponent<?, ?>> layer = layers.get(layerIndex);
			for (int uiIndex = layer.size() - 1; uiIndex >= 0; uiIndex--) {
				layer.get(uiIndex).keyPressed(e);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for (int layerIndex = layers.size() - 1; layerIndex >= 0; layerIndex--) {
			List<UIComponent<?, ?>> layer = layers.get(layerIndex);
			for (int uiIndex = layer.size() - 1; uiIndex >= 0; uiIndex--) {
				layer.get(uiIndex).keyReleased(e);
			}
		}
	}
}
