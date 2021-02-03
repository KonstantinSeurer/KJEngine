/**
 * 
 */
package de.kjEngine.graphics.shader.parser;

import java.util.Iterator;
import java.util.regex.Pattern;

import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class TokenStream implements Iterator<Token>, Iterable<Token> {

	private int position;
	private String source;
	private Array<Pattern> tokenTypes;
	private Token next;

	public TokenStream(String source, Array<String> tokenTypes) {
		this.source = source;
		this.tokenTypes = new Array<>();
		for (String s : tokenTypes) {
			this.tokenTypes.add(Pattern.compile(s));
		}
	}

	@Override
	public boolean hasNext() {
		if (next == null) {
			getNext();
		}
		return next != null;
	}

	@Override
	public Token next() {
		if (next == null) {
			getNext();
		}
		Token result = next;
		next = null;
		return result;
	}

	private void getNext() {
		int lastMatch = -1, lastMatchLength = -1;
		for (int length = 1; position + length <= source.length(); length++) {
			String substring = source.substring(position, position + length);
			for (int i = 0; i < tokenTypes.length(); i++) {
				if (tokenTypes.get(i).matcher(substring).matches()) {
					lastMatch = i;
					lastMatchLength = length;
					break;
				}
			}
		}
		if (lastMatch == -1) {
			return;
		}
		next = new Token(lastMatch, source.substring(position, position + lastMatchLength));
		position += lastMatchLength;
	}

	@Override
	public Iterator<Token> iterator() {
		return this;
	}
}
