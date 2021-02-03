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
@SuppressWarnings("rawtypes")
public class OptionalRule implements GrammarRule {
	
	public final Array<GrammarRule> options;

	public OptionalRule(GrammarRule... options) {
		this.options = new Array<>(options);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ParseResult parse(ReadArray tokens, int offset, int length) {
		for (int i = 0; i < options.length(); i++) {
			ParseResult match = options.get(i).parse(tokens, offset, length);
			if (match.matches) {
				return match;
			}
		}
		return new ParseResult(false, 0, null);
	}
}
