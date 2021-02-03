/**
 * 
 */
package de.kjEngine.graphics.shader.parser.spirv;

import de.kjEngine.graphics.shader.FunctionSource;
import de.kjEngine.graphics.shader.PipelineSource;
import de.kjEngine.graphics.shader.ShaderCompilationException;
import de.kjEngine.graphics.shader.ShaderType;
import de.kjEngine.graphics.shader.parser.ParseResult;
import de.kjEngine.graphics.shader.parser.ShaderGrammer;
import de.kjEngine.graphics.shader.parser.ShaderTokenStream;
import de.kjEngine.graphics.shader.parser.Token;
import de.kjEngine.io.JarProtocolImplementation;
import de.kjEngine.io.RL;
import de.kjEngine.util.container.Array;
import de.kjEngine.util.container.ReadArray;

/**
 * @author konst
 *
 */
public class Test {

	public static void main(String[] args) {
		JarProtocolImplementation.registerClassLoader("graphics", Test.class);

		PipelineSource pipeline = null;
		try {
			pipeline = PipelineSource.parse(RL.create("jar://graphics/de/kjEngine/graphics/shader/parser/spirv/aces.shader"));
		} catch (ShaderCompilationException e) {
			e.printStackTrace();
		}

		FunctionSource main = pipeline.getShader(ShaderType.FRAGMENT).getMainFunction();

		System.out.println(main.getSource());
		System.out.println("########################################################");

		Array<Token> tokens = new Array<>(new ShaderTokenStream(main.getSource()));
		tokens.removeAll((token) -> {
			return token.type == ShaderTokenStream.SPACE;
		});
		for (Token t : tokens) {
			System.out.println(t);
		}
		System.out.println("########################################################");
		ParseResult tree = ShaderGrammer.GRAMMAR.parse(tokens, 0, tokens.length());
		print(tree, 0);
	}

	@SuppressWarnings("rawtypes")
	private static void print(ParseResult tree, int indentation) {
		if (tree.data instanceof ReadArray) {
			for (int i = 0; i < indentation; i++) {
				System.out.print("    ");
			}
			System.out.println("Array [" + ((ReadArray) tree.data).length() + "]:");
			for (Object element : (ReadArray) tree.data) {
				if (element instanceof ParseResult) {
					print((ParseResult) element, indentation + 1);
				} else {
					for (int i = 0; i < indentation + 1; i++) {
						System.out.print("    ");
					}
					System.out.println(element);
				}
			}
		} else if (tree.data instanceof ParseResult) {
			print((ParseResult) tree.data, indentation);
		} else {
			for (int i = 0; i < indentation; i++) {
				System.out.print("    ");
			}
			System.out.println(tree.data.getClass());
		}
	}
}
