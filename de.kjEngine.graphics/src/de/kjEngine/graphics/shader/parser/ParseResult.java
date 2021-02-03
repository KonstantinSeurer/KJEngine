/**
 * 
 */
package de.kjEngine.graphics.shader.parser;

/**
 * @author konst
 *
 */
public class ParseResult {
	
	public final boolean matches;
	public final int length;
	public final Object data;

	public ParseResult(boolean matches, int length, Object data) {
		this.matches = matches;
		this.length = length;
		this.data = data;
	}
}
