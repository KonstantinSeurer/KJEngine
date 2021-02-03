package de.kjEngine.ui.event;

public class MouseButtonEvent extends MouseEvent {

	public final int button;

	public MouseButtonEvent(int x, int y, int button) {
		super(x, y);
		this.button = button;
	}
}
