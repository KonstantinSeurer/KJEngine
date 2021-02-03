/**
 * 
 */
package de.kjEngine.ui.event;

/**
 * @author konst
 *
 */
public class MouseMoveEvent extends MouseEvent {
	
	public final int prevX, prevY;

	public MouseMoveEvent(int x, int y, int prevX, int prevY) {
		super(x, y);
		this.prevX = prevX;
		this.prevY = prevY;
	}
}
