package de.kjEngine.ui.event;

public class MouseEvent extends Event {
	
	public final int x, y;

	public MouseEvent(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
