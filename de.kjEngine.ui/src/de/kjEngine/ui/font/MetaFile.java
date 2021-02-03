package de.kjEngine.ui.font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceNotFoundException;
import de.kjEngine.io.UnknownProtocolException;

/**
 * Provides functionality for getting the values from a font file.
 * 
 * @author Karl
 *
 */
public class MetaFile {

	private static final int PAD_TOP = 0;
	private static final int PAD_LEFT = 1;
	private static final int PAD_BOTTOM = 2;
	private static final int PAD_RIGHT = 3;

	private static final int DESIRED_PADDING = 5;

	private static final String SPLITTER = " ";
	private static final String NUMBER_SEPARATOR = ",";

	private float spaceWidth;
	private int[] padding;
	private int paddingWidth;
	private int paddingHeight;
	private int maxCharacterHeight;

	private Map<Integer, CharacterData> metaData = new HashMap<Integer, CharacterData>();

	private BufferedReader reader;
	private Map<String, String> values = new HashMap<String, String>();

	/**
	 * Opens a font file in preparation for reading.
	 * 
	 * @param file - the font file.
	 */
	protected MetaFile(RL file) {
		openFile(file);
		loadPaddingData();
		loadLineSizes();
		int imageWidth = getValueOfVariable("scaleW");
		loadCharacterData(imageWidth);
		close();
	}

	protected float getSpaceWidth() {
		return spaceWidth;
	}

	protected CharacterData getCharacter(int ascii) {
		return metaData.get(ascii);
	}

	private boolean processNextLine() {
		values.clear();
		String line = null;
		try {
			line = reader.readLine();
		} catch (IOException e1) {
		}
		if (line == null) {
			return false;
		}
		for (String part : line.split(SPLITTER)) {
			String[] valuePairs = part.split("=");
			if (valuePairs.length == 2) {
				values.put(valuePairs[0], valuePairs[1]);
			}
		}
		return true;
	}

	private int getValueOfVariable(String variable) {
		if (values.get(variable) != null)
			return Integer.parseInt(values.get(variable));
		else
			return 0;
	}

	private int[] getValuesOfVariable(String variable) {
		String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
		int[] actualValues = new int[numbers.length];
		for (int i = 0; i < actualValues.length; i++) {
			actualValues[i] = Integer.parseInt(numbers[i]);
		}
		return actualValues;
	}

	private void close() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openFile(RL file) {
		try {
			reader = new BufferedReader(new InputStreamReader(file.openInputStream()));
		} catch (UnknownProtocolException e) {
			e.printStackTrace();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void loadPaddingData() {
		processNextLine();
		this.padding = getValuesOfVariable("padding");
		this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
		this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
	}

	private void loadLineSizes() {
		processNextLine();
	}

	private void loadCharacterData(int imageWidth) {
		processNextLine();
		processNextLine();
		while (processNextLine()) {
			CharacterData c = loadCharacter(imageWidth);
			if (c != null) {
				metaData.put(c.getId(), c);
			}
		}
	}

	private CharacterData loadCharacter(int imageSize) {
		int id = getValueOfVariable("id");
		if (id == ' ') {
			this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth);
			return null;
		}
		double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
		double yTex = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
		int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
		int height = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING));
		maxCharacterHeight = Math.max(maxCharacterHeight, height);
		double quadWidth = width;
		double quadHeight = height;
		double xTexSize = (double) width / imageSize;
		double yTexSize = (double) height / imageSize;
		double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING);
		double yOff = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING));
		double xAdvance = (getValueOfVariable("xadvance") - paddingWidth);
		return new CharacterData(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
	}

	public int getMaxCharacterHeight() {
		return maxCharacterHeight;
	}
}
