/**
 * 
 */
package de.kjEngine.graphics.shader.parser;

import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class ShaderTokenStream extends TokenStream {

	private static Array<String> tokenTypes = new Array<>();

	private static int token(String token) {
		tokenTypes.add(token);
		return tokenTypes.length() - 1;
	}

	public static final int SPACE = token("\\s+");

	public static final int OPEN_BRACKET = token("\\(");
	public static final int CLOSED_BRACKET = token("\\)");
	public static final int OPEN_SQUARE_BRACKET = token("\\[");
	public static final int CLOSED_SQUARE_BRACKET = token("\\]");
	public static final int OPEN_CURLY_BRACKET = token("\\{");
	public static final int CLOSED_CURLY_BRACKET = token("\\}");

	public static final int POINT = token("\\.");
	public static final int COMMA = token(",");
	public static final int SEMICOLON = token(";");

	public static final int EQUALS = token("=");

	public static final int ADD = token("\\+");
	public static final int SUB = token("\\-");
	public static final int MUL = token("\\*");
	public static final int DIV = token("/");

	public static final int CONST = token("const");

	public static final int IF = token("if");
	public static final int ELSE = token("else");
	public static final int SWITCH = token("switch");
	public static final int CASE = token("case");
	public static final int DEFAULT = token("default");
	public static final int FOR = token("for");
	public static final int WHILE = token("while");
	public static final int DO = token("do");
	public static final int BREAK = token("break");
	public static final int CONTINUE = token("continue");

	public static final int IDENTIFIER = token("[\\w_]+");
	public static final int NUMBER_LITERAL = token("[\\d\\.]+");

	public ShaderTokenStream(String source) {
		super(source, tokenTypes);
	}
}
