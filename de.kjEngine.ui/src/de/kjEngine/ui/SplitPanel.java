/**
 * 
 */
package de.kjEngine.ui;

import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.ParentSize;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.Size;
import de.kjEngine.ui.transform.TopRightParentOffset;

/**
 * @author konst
 *
 */
public class SplitPanel extends Slider {

	public final UI left, right;

	public SplitPanel(Offset x, Offset y, Size width, Size height, float rotation, Offset rotationPivotX, Offset rotationPivotY, Model defaultModel, Model hoverModel, Model pressModel,
			Orientation orientation, Size splitterSize) {
		super(x, y, width, height, rotation, rotationPivotX, rotationPivotY, defaultModel, hoverModel, pressModel, orientation,
				orientation == Orientation.HORIZONTAL ? splitterSize : new ParentSize(1f), orientation == Orientation.VERTICAL ? splitterSize : new ParentSize(1f));

		Size leftWidth, leftHeight;
		if (orientation == Orientation.HORIZONTAL) {
			leftWidth = new Size() {

				@Override
				public float getPixelWidth(UI ui, UI parent) {
					return buttonUI.getPixelX();
				}

				@Override
				public float getPixelHeight(UI ui, UI parent) {
					return 0;
				}
			};
			leftHeight = new ParentSize(1f);
		} else {
			leftWidth = new ParentSize(1f);
			leftHeight = new Size() {

				@Override
				public float getPixelWidth(UI ui, UI parent) {
					return 0;
				}

				@Override
				public float getPixelHeight(UI ui, UI parent) {
					return buttonUI.getPixelY();
				}
			};
		}
		left = new UI(new PixelOffset(), new PixelOffset(), leftWidth, leftHeight);
		add(left);

		Size rightWidth, rightHeight;
		if (orientation == Orientation.HORIZONTAL) {
			rightWidth = new Size() {

				@Override
				public float getPixelWidth(UI ui, UI parent) {
					return parent.getPixelWidth() - buttonUI.getPixelX() - buttonUI.getPixelWidth();
				}

				@Override
				public float getPixelHeight(UI ui, UI parent) {
					return 0;
				}
			};
			rightHeight = new ParentSize(1f);
		} else {
			rightWidth = new ParentSize(1f);
			rightHeight = new Size() {

				@Override
				public float getPixelWidth(UI ui, UI parent) {
					return 0;
				}

				@Override
				public float getPixelHeight(UI ui, UI parent) {
					return parent.getPixelHeight() - buttonUI.getPixelY() - buttonUI.getPixelHeight();
				}
			};
		}
		right = new UI(new TopRightParentOffset(), new TopRightParentOffset(), rightWidth, rightHeight);
		add(right);
	}

	@Override
	protected void change() {
	}
}
