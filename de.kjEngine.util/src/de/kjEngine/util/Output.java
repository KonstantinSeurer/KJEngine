/**
 * 
 */
package de.kjEngine.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author konst
 *
 */
public class Output {
	
	public static enum Type {
		ERROR, WARNING, INFO, LOG
	}

	public static interface Handler {

		public static abstract class Provider {

			private Map<String, Handler> handlers = new HashMap<>();

			public Handler get(String id) {
				Handler h = handlers.get(id);
				if (h == null) {
					h = create();
					handlers.put(id, h);
				}
				return h;
			}

			protected abstract Handler create();
		}

		void print(String string);
	}

	private static boolean enabled = true;
	private static List<Handler.Provider> providers = new ArrayList<>();

	static {
		addHandlerProvider(new Handler.Provider() {

			@Override
			protected Handler create() {
				return new Handler() {

					@Override
					public void print(String string) {
						System.out.println(string);
					}
				};
			}
		});
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		Output.enabled = enabled;
	}

	public static void addHandlerProvider(Handler.Provider provider) {
		providers.add(provider);
	}

	public static void removeHandlerProvider(Handler.Provider provider) {
		providers.remove(provider);
	}

	public static void error(String format, Object... objects) {
		print(Type.ERROR, format, objects);
	}

	public static void warning(String format, Object... objects) {
		print(Type.WARNING, format, objects);
	}

	public static void info(String format, Object... objects) {
		print(Type.INFO, format, objects);
	}

	public static void log(String format, Object... objects) {
		print(Type.LOG, format, objects);
	}

	public static void print(Type context, String format, Object... objects) {
		if (!enabled) {
			return;
		}
		for (int i = 0; i < objects.length && format.contains("@"); i++) {
			Object o = objects[i];
			format = format.replaceFirst("@", o == null ? "null" : o.toString());
		}
		StackTraceElement[] stack = Thread.getAllStackTraces().get(Thread.currentThread());
		StackTraceElement e = stack[4];
		String string = e.getClassName() + "(line " + e.getLineNumber() + "): " + format;
		for (Handler.Provider provider : providers) {
			provider.get(e.getClassName()).print(string);
		}
	}
}
