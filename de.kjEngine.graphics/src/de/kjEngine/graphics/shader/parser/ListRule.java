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
public class ListRule implements GrammarRule {

	public GrammarRule elementType;
	public GrammarRule seperator;
	public int minLength, maxLength;

	public ListRule(GrammarRule elementType, GrammarRule seperator, int minLength, int maxLength) {
		this.elementType = elementType;
		this.seperator = seperator;
		this.minLength = minLength;
		this.maxLength = maxLength;
	}

	@Override
	public ParseResult parse(ReadArray<Token> tokens, int offset, int length) {
		if (length < minLength) {
			return new ParseResult(false, 0, null);
		}
		int resultLength = 0;
		Array<ParseResult> result = new Array<>();
		for (int i = 0; i < minLength; i++) {
			ParseResult match1 = elementType.parse(tokens, offset + resultLength, length - resultLength);
			if (!match1.matches) {
				return new ParseResult(false, 0, null);
			}
			result.add(match1);
			resultLength += match1.length;
			if (i < minLength - 1) {
				ParseResult match2 = seperator.parse(tokens, offset + resultLength, length - resultLength);
				if (!match2.matches) {
					return new ParseResult(false, 0, null);
				}
				resultLength += match2.length;
			}
		}
		for (int i = minLength; i < maxLength; i++) {
			if (minLength > 0 || i > minLength) {
				ParseResult match1 = seperator.parse(tokens, offset + resultLength, length - resultLength);
				if (!match1.matches) {
					break;
				}
				resultLength += match1.length;
				if (resultLength > length) {
					resultLength -= match1.length;
					break;
				}
			}
			ParseResult match2 = elementType.parse(tokens, offset + resultLength, length - resultLength);
			if (!match2.matches) {

				break;
			}
			resultLength += match2.length;
			if (resultLength > length) {
				resultLength -= match2.length;
				break;
			}
			result.add(match2);
		}
		return new ParseResult(true, resultLength, result);
	}
}
