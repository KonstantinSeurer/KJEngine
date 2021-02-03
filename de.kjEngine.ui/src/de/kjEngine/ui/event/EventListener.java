package de.kjEngine.ui.event;

public interface EventListener {
	
	public void mousePressed(MouseButtonEvent e);
	public void mouseReleased(MouseButtonEvent e);
	
	public void mouseMoved(MouseMoveEvent e);
	
	public void mouseWheelMoved(MouseWheelEvent e);
	
	public void keyPressed(KeyEvent e);
	public void keyReleased(KeyEvent e);
}
