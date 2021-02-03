/**
 * 
 */
package de.kjEngine.graphics.shader.parser;

import de.kjEngine.util.container.Array;
import de.kjEngine.util.container.ReadArray;

/**
 * @author konst
 *
 */
public class TokenRule implements GrammarRule {
	
	public Array<Integer> tokenTypes;

	public TokenRule(int... tokenTypes) {
		this.tokenTypes = new Array<>();
		for (int type : tokenTypes) {
			this.tokenTypes.add(type);
		}
	}

	@Override
	public ParseResult parse(ReadArray<Token> tokens, int offset, int length) {
		if (length < tokenTypes.length()) {
			return new ParseResult(false, 0, null);
		}
		for (int i = 0; i < tokenTypes.length(); i++) {
			if (tokenTypes.get(i) != tokens.get(offset + i).type) {
				return new ParseResult(false, 0, null);
			}
		}
		return new ParseResult(true, tokenTypes.length(), tokens.view(offset, tokenTypes.length()));
	}
}
