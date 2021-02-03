/**
 * 
 */
package de.kjEngine.ui.event;

/**
 * @author konst
 *
 */
public class Event {
	
	private boolean handled;

	public Event() {
	}
	
	public void handle() {
		handled = true;
	}

	public boolean isHandled() {
		return handled;
	}
}
