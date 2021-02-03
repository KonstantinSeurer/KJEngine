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
public class CompositeRule implements GrammarRule {

	public final Array<GrammarRule> rules;

	public CompositeRule(GrammarRule... rules) {
		this.rules = new Array<>(rules);
	}

	@Override
	public ParseResult parse(ReadArray<Token> tokens, int offset, int length) {
		int resultLength = 0;
		Array<ParseResult> result = new Array<>();
		for (int i = 0; i < rules.length(); i++) {
			ParseResult match = rules.get(i).parse(tokens, offset + resultLength, length - resultLength);
			if (!match.matches) {
				return new ParseResult(false, 0, null);
			}
			result.add(match);
			resultLength += match.length;
		}
		return new ParseResult(true, resultLength, result);
	}
}
