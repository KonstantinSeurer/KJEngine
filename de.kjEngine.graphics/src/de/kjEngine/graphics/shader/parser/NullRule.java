/**
 * 
 */
package de.kjEngine.graphics.shader.parser;

import de.kjEngine.util.container.ReadArray;

/**
 * @author konst
 *
 */
public class NullRule implements GrammarRule {

	public NullRule() {
	}

	@Override
	public ParseResult parse(ReadArray<Token> tokens, int offset, int length) {
		return new ParseResult(true, 0, null);
	}
}
