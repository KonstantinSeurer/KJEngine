package de.kjEngine.ui;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.GraphicsContext;
import de.kjEngine.ui.event.EventListener;
import de.kjEngine.ui.event.KeyEvent;
import de.kjEngine.ui.event.MouseButtonEvent;
import de.kjEngine.ui.event.MouseMoveEvent;
import de.kjEngine.ui.event.MouseWheelEvent;

/**
 * This handles the Display.
 * 
 * @author konst_df8d75v
 *
 */
public class Window {

	public static final int KEY_NONE = 0x00;

	public static final int KEY_ESCAPE = 0x01;
	public static final int KEY_1 = 0x02;
	public static final int KEY_2 = 0x03;
	public static final int KEY_3 = 0x04;
	public static final int KEY_4 = 0x05;
	public static final int KEY_5 = 0x06;
	public static final int KEY_6 = 0x07;
	public static final int KEY_7 = 0x08;
	public static final int KEY_8 = 0x09;
	public static final int KEY_9 = 0x0A;
	public static final int KEY_0 = 0x0B;
	public static final int KEY_MINUS = 0x0C; /* - on main keyboard */
	public static final int KEY_EQUALS = 0x0D;
	public static final int KEY_BACK = 0x0E; /* backspace */
	public static final int KEY_TAB = 0x0F;
	public static final int KEY_Q = 0x10;
	public static final int KEY_W = 0x11;
	public static final int KEY_E = 0x12;
	public static final int KEY_R = 0x13;
	public static final int KEY_T = 0x14;
	public static final int KEY_Y = 0x15;
	public static final int KEY_U = 0x16;
	public static final int KEY_I = 0x17;
	public static final int KEY_O = 0x18;
	public static final int KEY_P = 0x19;
	public static final int KEY_LBRACKET = 0x1A;
	public static final int KEY_RBRACKET = 0x1B;
	public static final int KEY_RETURN = 0x1C; /* Enter on main keyboard */
	public static final int KEY_LCONTROL = 0x1D;
	public static final int KEY_A = 0x1E;
	public static final int KEY_S = 0x1F;
	public static final int KEY_D = 0x20;
	public static final int KEY_F = 0x21;
	public static final int KEY_G = 0x22;
	public static final int KEY_H = 0x23;
	public static final int KEY_J = 0x24;
	public static final int KEY_K = 0x25;
	public static final int KEY_L = 0x26;
	public static final int KEY_SEMICOLON = 0x27;
	public static final int KEY_APOSTROPHE = 0x28;
	public static final int KEY_GRAVE = 0x29; /* accent grave */
	public static final int KEY_LSHIFT = 0x2A;
	public static final int KEY_BACKSLASH = 0x2B;
	public static final int KEY_Z = 0x2C;
	public static final int KEY_X = 0x2D;
	public static final int KEY_C = 0x2E;
	public static final int KEY_V = 0x2F;
	public static final int KEY_B = 0x30;
	public static final int KEY_N = 0x31;
	public static final int KEY_M = 0x32;
	public static final int KEY_COMMA = 0x33;
	public static final int KEY_PERIOD = 0x34; /* . on main keyboard */
	public static final int KEY_SLASH = 0x35; /* / on main keyboard */
	public static final int KEY_RSHIFT = 0x36;
	public static final int KEY_MULTIPLY = 0x37; /* * on numeric keypad */
	public static final int KEY_LMENU = 0x38; /* left Alt */
	public static final int KEY_SPACE = 0x39;
	public static final int KEY_CAPITAL = 0x3A;
	public static final int KEY_F1 = 0x3B;
	public static final int KEY_F2 = 0x3C;
	public static final int KEY_F3 = 0x3D;
	public static final int KEY_F4 = 0x3E;
	public static final int KEY_F5 = 0x3F;
	public static final int KEY_F6 = 0x40;
	public static final int KEY_F7 = 0x41;
	public static final int KEY_F8 = 0x42;
	public static final int KEY_F9 = 0x43;
	public static final int KEY_F10 = 0x44;
	public static final int KEY_NUMLOCK = 0x45;
	public static final int KEY_SCROLL = 0x46; /* Scroll Lock */
	public static final int KEY_NUMPAD7 = 0x47;
	public static final int KEY_NUMPAD8 = 0x48;
	public static final int KEY_NUMPAD9 = 0x49;
	public static final int KEY_SUBTRACT = 0x4A; /* - on numeric keypad */
	public static final int KEY_NUMPAD4 = 0x4B;
	public static final int KEY_NUMPAD5 = 0x4C;
	public static final int KEY_NUMPAD6 = 0x4D;
	public static final int KEY_ADD = 0x4E; /* + on numeric keypad */
	public static final int KEY_NUMPAD1 = 0x4F;
	public static final int KEY_NUMPAD2 = 0x50;
	public static final int KEY_NUMPAD3 = 0x51;
	public static final int KEY_NUMPAD0 = 0x52;
	public static final int KEY_DECIMAL = 0x53; /* . on numeric keypad */
	public static final int KEY_F11 = 0x57;
	public static final int KEY_F12 = 0x58;
	public static final int KEY_F13 = 0x64; /* (NEC PC98) */
	public static final int KEY_F14 = 0x65; /* (NEC PC98) */
	public static final int KEY_F15 = 0x66; /* (NEC PC98) */
	public static final int KEY_F16 = 0x67; /* Extended Function keys - (Mac) */
	public static final int KEY_F17 = 0x68;
	public static final int KEY_F18 = 0x69;
	public static final int KEY_KANA = 0x70; /* (Japanese keyboard) */
	public static final int KEY_F19 = 0x71; /* Extended Function keys - (Mac) */
	public static final int KEY_CONVERT = 0x79; /* (Japanese keyboard) */
	public static final int KEY_NOCONVERT = 0x7B; /* (Japanese keyboard) */
	public static final int KEY_YEN = 0x7D; /* (Japanese keyboard) */
	public static final int KEY_NUMPADEQUALS = 0x8D; /* = on numeric keypad (NEC PC98) */
	public static final int KEY_CIRCUMFLEX = 0x90; /* (Japanese keyboard) */
	public static final int KEY_AT = 0x91; /* (NEC PC98) */
	public static final int KEY_COLON = 0x92; /* (NEC PC98) */
	public static final int KEY_UNDERLINE = 0x93; /* (NEC PC98) */
	public static final int KEY_KANJI = 0x94; /* (Japanese keyboard) */
	public static final int KEY_STOP = 0x95; /* (NEC PC98) */
	public static final int KEY_AX = 0x96; /* (Japan AX) */
	public static final int KEY_UNLABELED = 0x97; /* (J3100) */
	public static final int KEY_NUMPADENTER = 0x9C; /* Enter on numeric keypad */
	public static final int KEY_RCONTROL = 0x9D;
	public static final int KEY_SECTION = 0xA7; /* Section symbol (Mac) */
	public static final int KEY_NUMPADCOMMA = 0xB3; /* , on numeric keypad (NEC PC98) */
	public static final int KEY_DIVIDE = 0xB5; /* / on numeric keypad */
	public static final int KEY_SYSRQ = 0xB7;
	public static final int KEY_RMENU = 0xB8; /* right Alt */
	public static final int KEY_FUNCTION = 0xC4; /* Function (Mac) */
	public static final int KEY_PAUSE = 0xC5; /* Pause */
	public static final int KEY_HOME = 0xC7; /* Home on arrow keypad */
	public static final int KEY_UP = 0xC8; /* UpArrow on arrow keypad */
	public static final int KEY_PRIOR = 0xC9; /* PgUp on arrow keypad */
	public static final int KEY_LEFT = 0xCB; /* LeftArrow on arrow keypad */
	public static final int KEY_RIGHT = 0xCD; /* RightArrow on arrow keypad */
	public static final int KEY_END = 0xCF; /* End on arrow keypad */
	public static final int KEY_DOWN = 0xD0; /* DownArrow on arrow keypad */
	public static final int KEY_NEXT = 0xD1; /* PgDn on arrow keypad */
	public static final int KEY_INSERT = 0xD2; /* Insert on arrow keypad */
	public static final int KEY_DELETE = 0xD3; /* Delete on arrow keypad */
	public static final int KEY_CLEAR = 0xDA; /* Clear key (Mac) */
	public static final int KEY_LMETA = 0xDB; /* Left Windows/Option key */
	public static final int KEY_RMETA = 0xDC; /* Right Windows/Option key */
	public static final int KEY_APPS = 0xDD; /* AppMenu key */
	public static final int KEY_POWER = 0xDE;
	public static final int KEY_SLEEP = 0xDF;

	/*
	 * public static final int STATE_ON = 0; public static final int STATE_OFF = 1;
	 * public static final int STATE_UNKNOWN = 2;
	 */
	public static final int KEYBOARD_SIZE = 256;

	private static final Map<Integer, Integer> KEY_CODE_LOOKUP = new HashMap<>();

	public static final int MOUSE_BUTTON_LEFT = 0;
	public static final int MOUSE_BUTTON_CENTER = 1;
	public static final int MOUSE_BUTTON_RIGHT = 2;
	
	static {
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_ESCAPE, Window.KEY_ESCAPE);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_1, Window.KEY_1);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_2, Window.KEY_2);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_3, Window.KEY_3);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_4, Window.KEY_4);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_5, Window.KEY_5);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_6, Window.KEY_6);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_7, Window.KEY_7);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_8, Window.KEY_8);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_9, Window.KEY_9);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_0, Window.KEY_0);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_MINUS, Window.KEY_MINUS);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_EQUAL, Window.KEY_EQUALS);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_BACKSPACE, Window.KEY_BACK);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_TAB, Window.KEY_TAB);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_Q, Window.KEY_Q);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_W, Window.KEY_W);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_E, Window.KEY_E);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_R, Window.KEY_R);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_T, Window.KEY_T);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_Y, Window.KEY_Y);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_U, Window.KEY_U);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_I, Window.KEY_I);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_O, Window.KEY_O);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_P, Window.KEY_P);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_LEFT_BRACKET, Window.KEY_LBRACKET);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_RIGHT_BRACKET, Window.KEY_RBRACKET);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_ENTER, Window.KEY_RETURN);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_LEFT_CONTROL, Window.KEY_LCONTROL);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_A, Window.KEY_A);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_S, Window.KEY_S);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_D, Window.KEY_D);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F, Window.KEY_F);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_G, Window.KEY_G);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_H, Window.KEY_H);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_J, Window.KEY_J);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_K, Window.KEY_K);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_L, Window.KEY_L);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_SEMICOLON, Window.KEY_SEMICOLON);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_APOSTROPHE, Window.KEY_APOSTROPHE);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_GRAVE_ACCENT, Window.KEY_GRAVE);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_LEFT_SHIFT, Window.KEY_LSHIFT);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_BACKSLASH, Window.KEY_BACKSLASH);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_Z, Window.KEY_Z);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_X, Window.KEY_X);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_C, Window.KEY_C);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_V, Window.KEY_V);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_B, Window.KEY_B);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_N, Window.KEY_N);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_M, Window.KEY_M);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_COMMA, Window.KEY_COMMA);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_PERIOD, Window.KEY_PERIOD);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_SLASH, Window.KEY_SLASH);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_RIGHT_SHIFT, Window.KEY_RSHIFT);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_MULTIPLY, Window.KEY_MULTIPLY);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_LEFT_ALT, Window.KEY_LMENU);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_SPACE, Window.KEY_SPACE);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_CAPS_LOCK, Window.KEY_CAPITAL);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F1, Window.KEY_F1);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F2, Window.KEY_F2);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F3, Window.KEY_F3);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F4, Window.KEY_F4);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F5, Window.KEY_F5);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F6, Window.KEY_F6);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F7, Window.KEY_F7);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F8, Window.KEY_F8);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F9, Window.KEY_F9);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F10, Window.KEY_F10);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_NUM_LOCK, Window.KEY_NUMLOCK);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_SCROLL_LOCK, Window.KEY_SCROLL);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_7, Window.KEY_NUMPAD7);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_8, Window.KEY_NUMPAD8);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_9, Window.KEY_NUMPAD9);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_SUBTRACT, Window.KEY_SUBTRACT);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_4, Window.KEY_NUMPAD4);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_5, Window.KEY_NUMPAD5);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_6, Window.KEY_NUMPAD6);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_ADD, Window.KEY_ADD);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_1, Window.KEY_NUMPAD1);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_2, Window.KEY_NUMPAD2);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_3, Window.KEY_NUMPAD3);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_0, Window.KEY_NUMPAD0);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_DECIMAL, Window.KEY_DECIMAL);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F11, Window.KEY_F11);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F12, Window.KEY_F12);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F13, Window.KEY_F13);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F14, Window.KEY_F14);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F15, Window.KEY_F15);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F16, Window.KEY_F16);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F17, Window.KEY_F17);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F18, Window.KEY_F18);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_F19, Window.KEY_F19);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_EQUAL, Window.KEY_NUMPADEQUALS);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_ENTER, Window.KEY_NUMPADENTER);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_RIGHT_CONTROL, Window.KEY_RCONTROL);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_KP_DIVIDE, Window.KEY_DIVIDE);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_RIGHT_ALT, Window.KEY_RMENU);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_PAUSE, Window.KEY_PAUSE);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_HOME, Window.KEY_HOME);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_UP, Window.KEY_UP);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_PAGE_UP, Window.KEY_PRIOR);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_LEFT, Window.KEY_LEFT);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_RIGHT, Window.KEY_RIGHT);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_END, Window.KEY_END);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_DOWN, Window.KEY_DOWN);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_PAGE_DOWN, Window.KEY_NEXT);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_INSERT, Window.KEY_INSERT);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_DELETE, Window.KEY_DELETE);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_LEFT_SUPER, Window.KEY_LMETA);
		KEY_CODE_LOOKUP.put(GLFW.GLFW_KEY_RIGHT_SUPER, Window.KEY_RMETA);
	}
	
	public static interface ResizeListener {
		void resize(int width, int height);
	}

	private static List<ResizeListener> resizeListeners = new CopyOnWriteArrayList<>();
	private static List<EventListener> eventListeners = new ArrayList<>();

	private static long window;
	private static int width, height;
	private static boolean resizable = false;
	private static String title = "GLFW window";

	private static int key_code;
	private static char key_char;
	private static boolean key_event, key_pressed;

	private static GLFWWindowSizeCallback sizeCallback = new GLFWWindowSizeCallback() {

		@Override
		public void invoke(long window, int width, int height) {
			Window.width = width;
			Window.height = height;
			
			for (int i = 0; i < resizeListeners.size(); i++) {
				resizeListeners.get(i).resize(width, height);
			}
		}
	};

	private static GLFWCharCallback charCallback = new GLFWCharCallback() {

		@Override
		public void invoke(long window, int codepoint) {
			key_char = (char) codepoint;
		}
	};

	private static GLFWKeyCallback keyCallback = new GLFWKeyCallback() {

		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (action == GLFW_PRESS) {
				Integer code = KEY_CODE_LOOKUP.get(key);
				if (code == null) {
					return;
				}
				key_code = code;
				key_pressed = true;
				key_event = true;
			} else if (action == GLFW_RELEASE) {
				Integer code = KEY_CODE_LOOKUP.get(key);
				if (code == null) {
					return;
				}
				key_code = code;
				key_pressed = false;
				key_event = true;
			}
		}
	};

	private static GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {

		@Override
		public void invoke(long window, int button, int action, int mods) {
			int actualButton = button;
			if (button == 2) {
				actualButton = MOUSE_BUTTON_CENTER;
			} else if (button == 1) {
				actualButton = MOUSE_BUTTON_RIGHT;
			}
			MouseButtonEvent e = new MouseButtonEvent(getMouseX(), getMouseY(), actualButton);
			if (action == GLFW_PRESS) {
				for (EventListener l : eventListeners) {
					l.mousePressed(e);
				}
			} else if (action == GLFW_RELEASE) {
				for (EventListener l : eventListeners) {
					l.mouseReleased(e);
				}
			}
		}
	};
	private static GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {

		@Override
		public void invoke(long window, double xoffset, double yoffset) {
			wheel += (int) yoffset;
			MouseWheelEvent e = new MouseWheelEvent(mouseX, mouseY, wheel, (int) yoffset);
			for (EventListener l : eventListeners) {
				l.mouseWheelMoved(e);
			}
		}
	};
	private static GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {

		@Override
		public void invoke(long window, double xpos, double ypos) {
			ypos = Window.height - (int) ypos;
			MouseMoveEvent e = new MouseMoveEvent((int) xpos, (int) ypos, mouseX, mouseY);
			mouseX = (int) xpos;
			mouseY = (int) ypos;
			for (EventListener l : eventListeners) {
				l.mouseMoved(e);
			}
		}
	};

	private static int wheel;
	private static int mouseX, mouseY;

	public static void create(int width, int height, String title, boolean resizable, GraphicsContext context) {
		Window.width = width;
		Window.height = height;
		Window.title = title;

		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
		glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);

		window = glfwCreateWindow(width, height, title, NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		context.init(window);
		Graphics.init(context);

		if (resizable) {
			glfwSetWindowSizeCallback(window, sizeCallback);
		}
		glfwSetKeyCallback(window, keyCallback);
		glfwSetCharCallback(window, charCallback);
		glfwSetMouseButtonCallback(window, mouseButtonCallback);
		glfwSetScrollCallback(window, scrollCallback);
		glfwSetCursorPosCallback(window, cursorPosCallback);
	}

	public static void update() {
		if (Graphics.getApi() == Graphics.API.OPENGL) {
			glfwSwapBuffers(window);
		} else {
			Graphics.getContext().swapBuffers();
		}
		glfwPollEvents();
		if (key_event) {
			if (key_pressed) {
				KeyEvent e = new KeyEvent(key_code, key_char);
				for (EventListener l : eventListeners) {
					l.keyPressed(e);
				}
			} else {
				KeyEvent e = new KeyEvent(key_code, key_char);
				for (EventListener l : eventListeners) {
					l.keyReleased(e);
				}
				key_char = 0;
			}
			key_event = false;
		}
	}

	public static void dispose() {
		Graphics.getContext().dispose();
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}

	public static float getAspect() {
		return ((float) getWidth() / (float) getHeight());
	}

	public static void setTitle(String title) {
		glfwSetWindowTitle(window, title);
		Window.title = title;
	}

	public static String getTitle() {
		return title;
	}

	public static boolean isCloseRequested() {
		return glfwWindowShouldClose(window);
	}

	public static void addResizeListener(ResizeListener l) {
		resizeListeners.add(l);
	}

	public static void removeResizeListener(ResizeListener l) {
		resizeListeners.remove(l);
	}

	public static boolean isKeyPressed(int key) {
		int glfw_key = 0;
		for (Entry<Integer, Integer> e : KEY_CODE_LOOKUP.entrySet()) {
			if (e.getValue() == key) {
				glfw_key = e.getKey();
			}
		}
		return glfwGetKey(window, glfw_key) == GLFW_PRESS;
	}

	public static int getMouseX() {
		return mouseX;
	}

	public static int getMouseY() {
		return mouseY;
	}

	public static boolean isMousePressed(int button) {
		int actualButton = button;
		if (button == MOUSE_BUTTON_CENTER) {
			actualButton = 2;
		} else if (button == MOUSE_BUTTON_RIGHT) {
			actualButton = 1;
		}
		return glfwGetMouseButton(window, actualButton) == GLFW_PRESS;
	}

	public static void addEventListener(EventListener l) {
		eventListeners.add(l);
	}

	public static void removeEventListener(EventListener l) {
		eventListeners.remove(l);
	}

	/**
	 * @return the resizable
	 */
	public static boolean isResizable() {
		return resizable;
	}
}
