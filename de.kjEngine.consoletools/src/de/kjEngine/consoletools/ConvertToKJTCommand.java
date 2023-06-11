/**
 * 
 */
package de.kjEngine.consoletools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import de.kjEngine.graphics.KJT;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.io.RL;

/**
 * @author konst
 *
 */
public class ConvertToKJTCommand implements Command {

	@Override
	public void run(BufferedReader in, String[] args) {
		if (args.length == 0) {
			System.out.println("Wrong number of arguments!");
			return;
		}

		File file = new File(args[0]);

		if (!file.exists()) {
			System.out.println("File doesn't exist!");
			return;
		}

		if (file.isDirectory()) {
			if (args.length < 2) {
				System.out.println("Wrong number of arguments!");
				return;
			}

			String mode = args[1];

			switch (mode) {
			case "flat":
				convertFlat(file);
				break;
			case "recursive":
				convertRecursive(file);
				break;
			default:
				System.out.println("Unknown mode \"" + mode + "\"!");
			}
		} else {
			convert(file);
		}
	}

	private void convertRecursive(File file) {
		if (file.isFile()) {
			convert(file);
		} else {
			for (File child : file.listFiles()) {
				convertRecursive(child);
			}
		}
	}

	private void convertFlat(File file) {
		if (!file.isDirectory()) {
			System.out.println("\"" + file + "\" is no directory!");
			return;
		}

		for (File child : file.listFiles()) {
			if (child.isFile()) {
				convert(child);
			}
		}
	}

	private void convert(File file) {
		try {
			String name = file.getCanonicalPath();
			String nameWithoutExtension = name.substring(0, name.lastIndexOf("."));
			String newName = nameWithoutExtension + ".kjt";
			
			File newFile = new File(newName);

			RL src = RL.fromFile(file);
			RL dst = RL.fromFile(newFile);
			if (Texture2DData.getSupportedExtensions().contains(src.getExtension()) && !src.getExtension().equals("kjt")) {
				if (!newFile.exists()) {
					newFile.createNewFile();
					System.out.println("Create file \"" + newName + "\"");
				}
				
				KJT.write(dst, Texture2DData.create(src, SamplingMode.NEAREST, WrappingMode.CLAMP));
				
				System.out.println("Convert \"" + name + "\" to \"" + newName + "\"");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getSyntax() {
		return "file/folder [mode(flat/recursive)]";
	}
}
