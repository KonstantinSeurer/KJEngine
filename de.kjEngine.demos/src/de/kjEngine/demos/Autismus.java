/**
 * 
 */
package de.kjEngine.demos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author konst
 *
 */
public class Autismus {
	
	private static class IntPtr {
		int value;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File root = new File("../");
		Map<String, IntPtr> lines = new HashMap<>();
		scanFiles(root, lines);
		for (String extension : lines.keySet()) {
			System.out.println(extension + ": " + lines.get(extension).value + " lines");
		}
	}

	private static void scanFiles(File root, Map<String, IntPtr> target) {
		for (File file : root.listFiles()) {
			if (file.isDirectory()) {
				scanFiles(file, target);
			} else {
				String name = file.getName();
				String extension = null;
				if (name.contains(".")) {
					String[] pts = name.split("\\.");
					extension = pts[pts.length - 1];
				}
				if (extension != null && (extension.equals("java") || extension.equals("shader"))) {
					IntPtr lines = target.get(extension);
					if (lines == null) {
						lines = new IntPtr();
						target.put(extension, lines);
					}
					try {
						BufferedReader in = new BufferedReader(new FileReader(file));
						while (in.readLine() != null) {
							lines.value++;
						}
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
