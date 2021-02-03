package de.kjEngine.ui.event;

public class KeyEvent extends Event {
	
	public final int key;
	public final char character;

	public KeyEvent(int key, char character) {
		this.key = key;
		this.character = character;
	}
}
