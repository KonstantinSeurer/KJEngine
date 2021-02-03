/**
 * 
 */
package de.kjEngine.graphics.shader;

/**
 * @author konst
 *
 */
public class ShaderCompilationUtil {

	public static class ParseResult<T> {
		public boolean successful;
		public int endIndex;
		public T data;

		public ParseResult(boolean successful, int endIndex, T data) {
			this.successful = successful;
			this.endIndex = endIndex;
			this.data = data;
		}
	}

	public static int getLine(String source, int index) {
		int line = 1;
		for (int i = 0; i <= index; i++) {
			if (source.charAt(i) == '\n') {
				line++;
			}
		}
		return line;
	}

	public static ParseResult<ObjectSource> parseObject(String source, int startIndex) throws ShaderSyntaxException {
		String name = null, content = null;
		int sourceLength = source.length();
		int i = startIndex;
		for (; i < sourceLength; i++) {
			char c = source.charAt(i);
			if (c == '{') {
				name = source.substring(startIndex, i).trim();
				break;
			} else if (c == ';') {
				return new ParseResult<ObjectSource>(false, i, new ObjectSource(source.substring(startIndex, i), null));
			}
		}
		if (name == null) {
			return new ParseResult<ObjectSource>(false, i, null);
		}
		int contentStart = i;
		int level = 0;
		for (; i < sourceLength; i++) {
			char c = source.charAt(i);
			if (c == '{') {
				level++;
			} else if (c == '}') {
				level--;

				if (level == 0) {
					content = source.substring(contentStart + 1, i).trim();
					break;
				}
			}
		}
		if (level != 0) {
			throw new ShaderSyntaxException("missing closing bracket", i);
		}
		if (content == null) {
			System.out.println(name);
			return new ParseResult<ObjectSource>(false, i, null);
		}
		return new ParseResult<ObjectSource>(true, i, new ObjectSource(name, content));
	}

	public static String removeComments(String source) {
		StringBuilder sb = new StringBuilder();
		for (String line : source.split("\n")) {
			int commentIndex = line.indexOf("//");
			if (commentIndex == -1) {
				sb.append(line);
			} else {
				sb.append(line.substring(0, commentIndex));
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
