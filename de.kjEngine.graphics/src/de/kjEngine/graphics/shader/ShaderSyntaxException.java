/**
 * 
 */
package de.kjEngine.graphics.shader;

/**
 * @author konst
 *
 */
public class ShaderSyntaxException extends ShaderCompilationException {
	private static final long serialVersionUID = -4192609342967082987L;

	public ShaderSyntaxException(int characterIndex) {
		super(characterIndex);
	}

	/**
	 * @param message
	 */
	public ShaderSyntaxException(String message, int characterIndex) {
		super(message, characterIndex);
	}

	/**
	 * @param cause
	 */
	public ShaderSyntaxException(Throwable cause, int characterIndex) {
		super(cause, characterIndex);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ShaderSyntaxException(String message, Throwable cause, int characterIndex) {
		super(message, cause, characterIndex);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ShaderSyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int characterIndex) {
		super(message, cause, enableSuppression, writableStackTrace, characterIndex);
	}
}
