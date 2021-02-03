/**
 * 
 */
package de.kjEngine.graphics.shader.parser;

/**
 * @author konst
 *
 */
public class ShaderGrammer {
	public static final ListRule FUNCTION_PARAMETER_LIST = new ListRule(null, new TokenRule(ShaderTokenStream.COMMA), 0, Integer.MAX_VALUE);

	public static final GrammarRule IDENTIFIER_EXPRESSION = new TokenRule(ShaderTokenStream.IDENTIFIER);
	public static final GrammarRule NUMBER_LITERAL_EXPRESSION = new TokenRule(ShaderTokenStream.NUMBER_LITERAL);
	public static final GrammarRule FUNCTION_EXPRESSION = new CompositeRule(new TokenRule(ShaderTokenStream.IDENTIFIER, ShaderTokenStream.OPEN_BRACKET), FUNCTION_PARAMETER_LIST,
			new TokenRule(ShaderTokenStream.CLOSED_BRACKET));
	public static final CompositeRule ADD_EXPRESSION = new CompositeRule(null, new TokenRule(ShaderTokenStream.ADD), null);
	public static final CompositeRule SUB_EXPRESSION = new CompositeRule(null, new TokenRule(ShaderTokenStream.SUB), null);
	public static final CompositeRule MUL_EXPRESSION = new CompositeRule(null, new TokenRule(ShaderTokenStream.MUL), null);
	public static final CompositeRule DIV_EXPRESSION = new CompositeRule(null, new TokenRule(ShaderTokenStream.DIV), null);

	public static final GrammarRule EXPRESSION = new OptionalRule(FUNCTION_EXPRESSION, IDENTIFIER_EXPRESSION, NUMBER_LITERAL_EXPRESSION);
	static {
		FUNCTION_PARAMETER_LIST.elementType = EXPRESSION;
		
		ADD_EXPRESSION.rules.set(0, EXPRESSION);
		ADD_EXPRESSION.rules.set(2, EXPRESSION);
		
		SUB_EXPRESSION.rules.set(0, EXPRESSION);
		SUB_EXPRESSION.rules.set(2, EXPRESSION);
		
		MUL_EXPRESSION.rules.set(0, EXPRESSION);
		MUL_EXPRESSION.rules.set(2, EXPRESSION);
		
		DIV_EXPRESSION.rules.set(0, EXPRESSION);
		DIV_EXPRESSION.rules.set(2, EXPRESSION);
	}

	public static final GrammarRule EXPRESSION_STATEMENT = new CompositeRule(EXPRESSION, new TokenRule(ShaderTokenStream.SEMICOLON));

	public static final GrammarRule VARIABLE_DECLARATION_STATEMENT = new TokenRule(ShaderTokenStream.IDENTIFIER, ShaderTokenStream.IDENTIFIER, ShaderTokenStream.SEMICOLON);
	public static final GrammarRule CONSTANT_DECLARATION_STATEMENT = new TokenRule(ShaderTokenStream.CONST, ShaderTokenStream.IDENTIFIER, ShaderTokenStream.IDENTIFIER, ShaderTokenStream.SEMICOLON);
	public static final GrammarRule VARIABLE_ASSIGNMENT_STATEMENT = new CompositeRule(new TokenRule(ShaderTokenStream.IDENTIFIER, ShaderTokenStream.EQUALS), EXPRESSION,
			new TokenRule(ShaderTokenStream.SEMICOLON));
	public static final GrammarRule VARIABLE_DECLARATION_AND_ASSIGNMENT_STATEMENT = new CompositeRule(
			new TokenRule(ShaderTokenStream.IDENTIFIER, ShaderTokenStream.IDENTIFIER, ShaderTokenStream.EQUALS), EXPRESSION, new TokenRule(ShaderTokenStream.SEMICOLON));
	public static final GrammarRule CONSTANT_DECLARATION_AND_ASSIGNMENT_STATEMENT = new CompositeRule(
			new TokenRule(ShaderTokenStream.CONST, ShaderTokenStream.IDENTIFIER, ShaderTokenStream.IDENTIFIER, ShaderTokenStream.EQUALS), EXPRESSION, new TokenRule(ShaderTokenStream.SEMICOLON));

	public static final GrammarRule STATEMENT = new OptionalRule(VARIABLE_DECLARATION_STATEMENT, CONSTANT_DECLARATION_STATEMENT, VARIABLE_ASSIGNMENT_STATEMENT,
			VARIABLE_DECLARATION_AND_ASSIGNMENT_STATEMENT, CONSTANT_DECLARATION_AND_ASSIGNMENT_STATEMENT, EXPRESSION_STATEMENT);

	public static final GrammarRule STATEMENT_LIST = new ListRule(STATEMENT, new NullRule(), 0, Integer.MAX_VALUE);

	public static final GrammarRule GRAMMAR = STATEMENT_LIST;
}
