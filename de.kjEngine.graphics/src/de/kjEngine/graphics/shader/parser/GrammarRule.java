/**
 * 
 */
package de.kjEngine.graphics.shader.parser;

import de.kjEngine.util.container.ReadArray;

/**
 * @author konst
 *
 */
public interface GrammarRule {
	
	public static final int NO_MATCH = -1;

	// don't use ReadArray.view to reduce the allocation count
	public ParseResult parse(ReadArray<Token> tokens, int offset, int length);
}
