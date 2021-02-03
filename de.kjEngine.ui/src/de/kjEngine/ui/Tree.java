/**
 * 
 */
package de.kjEngine.ui;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.component.Component;
import de.kjEngine.math.Vec2;
import de.kjEngine.ui.event.ActionListener;
import de.kjEngine.ui.event.MouseButtonEvent;
import de.kjEngine.ui.font.FontType;
import de.kjEngine.ui.font.TextComponent;
import de.kjEngine.ui.model.Model;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.ParentSize;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.Size;
import de.kjEngine.ui.transform.UIOffset;
import de.kjEngine.util.Time;

/**
 * @author konst
 *
 */
public class Tree extends UI {

	public static class Node extends UI {

		private String text;
		public final int indentation;
		private boolean open;

		public final List<Node> children = new ArrayList<>();
		public Node parentNode;

		private final Model buttonDefault, buttonHover, buttonPress;
		private final FontType font;
		private final float fontSize;
		private final ActionListener<Node> openListener;

		public Node(String text, Size height, Model buttonDefault, Model buttonHover, Model buttonPress, FontType font, float fontSize, boolean root, int indentation,
				ActionListener<Node> openListener) {
			super(new PixelOffset(), new UIOffset(), new ParentSize(1f), height);
			this.text = text;
			this.indentation = indentation;
			this.buttonDefault = buttonDefault;
			this.buttonHover = buttonHover;
			this.buttonPress = buttonPress;
			this.font = font;
			this.fontSize = fontSize;
			this.openListener = openListener;
			if (!root) {
				add(new ButtonComponent(buttonDefault, buttonHover, buttonPress) {

					long lastPress;

					@Override
					protected void press() {
						long currentTime = Time.nanos();
						if (Time.nanosToMillis(currentTime - lastPress) < 400f && !Node.this.children.isEmpty()) {
							setOpen(!open);
						}
						lastPress = currentTime;
						openListener.run(Node.this);
					}

					@Override
					public void mousePressed(MouseButtonEvent e) {
						if (e.isHandled() || !parent.intersects(Vec2.create(e.x, e.y))) {
							setState(State.DEFAULT);
						} else {
							super.mousePressed(e);
						}
					}

					@Override
					public void mouseReleased(MouseButtonEvent e) {
					}
				});
				StringBuilder textBuilder = new StringBuilder();
				for (int i = 0; i < indentation; i++) {
					textBuilder.append(' ');
				}
				textBuilder.append(text);
				add(new TextComponent(textBuilder.toString(), font, fontSize, false, false, true));
			}
			open = root;
		}

		public Node get(String text) {
			for (int i = 0; i < children.size(); i++) {
				Node child = children.get(i);
				if (child.text.equals(text)) {
					return child;
				}
			}
			return null;
		}

		public Node add(String text) {
			Node n = new Node(text, height, buttonDefault, buttonHover, buttonPress, font, fontSize, false, indentation + 2, openListener);
			n.setActive(open);
			parent.add(n);
			children.add(n);
			n.parentNode = this;
			return n;
		}

		public void remove(Node n) {
			children.remove(n);
			parent.remove(n);
			n.parentNode = null;
			for (Node child : n.children) {
				remove(child);
			}
		}

		public void setOpen(boolean open) {
			if (this.open == open) {
				return;
			}
			this.open = open; // TODO: fix
			for (Node child : children) {
				child.setActive(open);
				;
			}
		}

		public void m_setActive(boolean active) {
			setActive(active);
			for (Node child : children) {
				child.m_setActive(open & active);
			}
		}

		public boolean isOpen() {
			return open;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
			StringBuilder textBuilder = new StringBuilder();
			for (int i = 0; i < indentation; i++) {
				textBuilder.append(' ');
			}
			textBuilder.append(text);
			get(TextComponent.class).setText(textBuilder.toString());
		}
	}

	public final Node root;

	public Tree(Offset x, Offset y, Size width, Size height, float rotation, Offset rotationPivotX, Offset rotationPivotY, Size nodeHeight, Model buttonDefault, Model buttonHover, Model buttonPress,
			FontType font, float fontSize, ActionListener<Node> openListener) {
		super(x, y, width, height, rotation, rotationPivotX, rotationPivotY);

		root = new Node(null, nodeHeight, buttonDefault, buttonHover, buttonPress, font, fontSize, true, 0, openListener);
		add(root);

		add(new UIComponent<UI, UIComponent<UI, ?>>(Component.EARLY) {

			@Override
			protected void update(float delta) {
				float offset = getPixelHeight() / root.getPixelHeight() - 1f;
				layoutNodes(root.children, offset);
			}

			private float layoutNodes(List<Node> nodes, float y) {
				for (Node node : nodes) {
					node.y.value = y;
					y -= 1f;
					if (node.isOpen()) {
						y = layoutNodes(node.children, y);
					}
				}
				return y;
			}
		});
	}

	public float getLocalTreeHeight() {
		return getHeight(root.children) * root.getPixelHeight() / getPixelHeight();
	}

	private float getHeight(List<Node> nodes) {
		float height = 0f;
		height += nodes.size();
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			if (node.isOpen()) {
				height += getHeight(node.children);
			}
		}
		return height;
	}
}
