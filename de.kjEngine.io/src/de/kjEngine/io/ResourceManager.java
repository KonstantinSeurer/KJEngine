/**
 * 
 */
package de.kjEngine.io;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * @author konst
 *
 */
public class ResourceManager {

	private static Map<RL, BufferedImage> images = new HashMap<>();
	private static Map<RL, String> textResources = new HashMap<>();

	public static BufferedImage loadImage(RL rl, boolean cached) {
		if (cached) {
			BufferedImage image = images.get(rl);
			if (image == null) {
				image = loadImage(rl, false);
				images.put(rl, image);
			}
			return image;
		} else {
			try {
				return ImageIO.read(rl.openInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String loadTextResource(RL rl, boolean cached) {
		if (cached) {
			String data = textResources.get(rl);
			if (data == null) {
				data = loadTextResource(rl, false);
				textResources.put(rl, data);
			}
			return data;
		} else {
			StringBuilder sb = new StringBuilder();
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(rl.openInputStream()));
				String line;
				while ((line = r.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				}
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		}
	}

	public static void writeTextResource(RL rl, String data) {
		OutputStream out = null;
		try {
			out = rl.openOutputStream();
			out.write(data.getBytes());
		} catch (UnknownProtocolException e) {
			e.printStackTrace();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
