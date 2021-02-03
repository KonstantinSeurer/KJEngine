/**
 * 
 */
package de.kjEngine.graphics.shader.parser;

/**
 * @author konst
 *
 */
public class Token {
	
	public final int type;
	public final String string;

	public Token(int type, String string) {
		this.type = type;
		this.string = string;
	}

	@Override
	public String toString() {
		return "[" + type + " " + string + "]";
	}
}
