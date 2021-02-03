package de.kjEngine.ui.event;

public class MouseWheelEvent extends MouseEvent {
	
	public final int position, movement;

	public MouseWheelEvent(int x, int y, int position, int movement) {
		super(x, y);
		this.position = position;
		this.movement = movement;
	}
}
